package com.anatdx.murasaki.mrsk

import android.os.IBinder
import android.os.Parcel
import com.anatdx.murasaki.Murasaki
import com.anatdx.murasaki.MurasakiApiConstants
import com.anatdx.murasaki.server.ServerConstants
import java.util.concurrent.Executors

/**
 * 通过 MRSK Binder 查询 HymoFS 是否与内核对接成功。
 * 仅当 Murasaki 服务已运行且内核已打 HymoFS 补丁时返回对接成功。
 */
object HymoFSClient {

    private const val DESCRIPTOR = "moe.shizuku.server.IShizukuMRSKService"
    private const val TRANSACTION_hymofsIsAvailable = 2
    private const val TRANSACTION_hymofsGetProtocolVersion = 3

    private val io = Executors.newSingleThreadExecutor()

    data class Status(
        val serviceRunning: Boolean,
        val hymofsAvailable: Boolean,
        val protocolVersion: Int
    ) {
        val summary: String
            get() = when {
                !serviceRunning -> "Murasaki 服务未运行"
                !hymofsAvailable -> when {
                    protocolVersion < 0 -> "内核未支持 HymoFS"
                    else -> "协议版本不匹配 (内核: $protocolVersion)"
                }
                else -> "已对接 (协议 $protocolVersion)"
            }
    }

    /**
     * 获取 MRSK 服务 Binder。优先通过已收到的 Shizuku Binder 请求 getMRSKBinder；
     * 否则回退到 ServiceManager（如 Rei/reid 场景）。
     */
    @JvmStatic
    fun getMRSKBinder(): IBinder? {
        val shizuku = Murasaki.getBinder()
        if (shizuku != null && shizuku.pingBinder()) {
            val data = Parcel.obtain()
            val reply = Parcel.obtain()
            try {
                data.writeInterfaceToken(MurasakiApiConstants.BINDER_DESCRIPTOR)
                val ok = shizuku.transact(ServerConstants.BINDER_TRANSACTION_getMRSKBinder, data, reply, 0)
                if (ok) {
                    reply.readException()
                    return reply.readStrongBinder()
                }
            } catch (_: Exception) {
                // fall through to ServiceManager fallback
            } finally {
                data.recycle()
                reply.recycle()
            }
        }
        return runCatching {
            val clazz = Class.forName("android.os.ServiceManager")
            val method = clazz.getMethod("getService", String::class.java)
            method.invoke(null, ServerConstants.MRSK_SERVICE_NAME) as? IBinder
        }.getOrNull()
    }

    /**
     * 检测 HymoFS 对接状态（在后台线程执行，可于主线程调用）。
     */
    @JvmStatic
    fun checkStatus(callback: (Status) -> Unit) {
        io.submit {
            val status = checkStatusBlocking()
            callback(status)
        }
    }

    /**
     * 同步检测，应在后台线程调用。
     */
    @JvmStatic
    fun checkStatusBlocking(): Status {
        val mrsk = getMRSKBinder()
        if (mrsk == null || !mrsk.pingBinder()) {
            return Status(serviceRunning = false, hymofsAvailable = false, protocolVersion = -1)
        }
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        try {
            data.writeInterfaceToken(DESCRIPTOR)
            val okAvailable = mrsk.transact(TRANSACTION_hymofsIsAvailable, data, reply, 0)
            if (!okAvailable) {
                return Status(serviceRunning = true, hymofsAvailable = false, protocolVersion = -1)
            }
            reply.readException()
            val available = reply.readByte() != 0.toByte()

            val data2 = Parcel.obtain()
            val reply2 = Parcel.obtain()
            try {
                data2.writeInterfaceToken(DESCRIPTOR)
                mrsk.transact(TRANSACTION_hymofsGetProtocolVersion, data2, reply2, 0)
                reply2.readException()
                val version = reply2.readInt()
                return Status(serviceRunning = true, hymofsAvailable = available, protocolVersion = version)
            } finally {
                data2.recycle()
                reply2.recycle()
            }
        } catch (_: Exception) {
            return Status(serviceRunning = true, hymofsAvailable = false, protocolVersion = -1)
        } finally {
            data.recycle()
            reply.recycle()
        }
    }
}
