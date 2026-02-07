package moe.shizuku.manager.mrsk

import android.content.Context
import android.os.IBinder
import android.os.Parcel
import android.util.Log
import rikka.shizuku.Shizuku

/**
 * MRSK/Rei integration: get Shizuku Binder from reid (Murasaki service).
 * When running on Rei, reid registers io.murasaki.IMurasakiService and provides
 * getShizukuBinder() so Manager can use Shizuku without the Java server.
 */
object MRSKHelper {

    private const val TAG = "ShizukuMRSK"
    private const val MURASAKI_SERVICE_NAME = "io.murasaki.IMurasakiService"
    private const val DESCRIPTOR_MURASAKI = "io.murasaki.server.IMurasakiService"
    /** AIDL method id 30 = getShizukuBinder; transaction code = FIRST_CALL_TRANSACTION + 30 = 31 */
    private const val TRANSACTION_getShizukuBinder = 31

    /**
     * Try to get Shizuku Binder from reid's Murasaki service.
     * @return the Binder if reid is running and provides it, null otherwise
     */
    @JvmStatic
    fun getShizukuBinderFromReid(): IBinder? {
        val murasaki = getService(MURASAKI_SERVICE_NAME) ?: return null
        if (!murasaki.pingBinder()) return null
        return runCatching {
            val data = Parcel.obtain()
            val reply = Parcel.obtain()
            try {
                data.writeInterfaceToken(DESCRIPTOR_MURASAKI)
                val ok = murasaki.transact(TRANSACTION_getShizukuBinder, data, reply, 0)
                if (!ok) return@runCatching null
                reply.readException()
                return@runCatching reply.readStrongBinder()
            } finally {
                reply.recycle()
                data.recycle()
            }
        }.getOrElse { e ->
            Log.w(TAG, "getShizukuBinderFromReid", e)
            null
        }
    }

    /**
     * If we don't have a live Shizuku Binder, try to receive it from reid (MRSK).
     * Call early (e.g. Application.onCreate or before first Shizuku.pingBinder()).
     * @return true if Binder was received from reid and set to Shizuku
     */
    @JvmStatic
    fun tryReceiveBinderFromReid(context: Context): Boolean {
        if (Shizuku.pingBinder()) return false
        val binder = getShizukuBinderFromReid() ?: return false
        if (!binder.pingBinder()) return false
        Log.i(TAG, "Received Shizuku Binder from reid (MRSK)")
        Shizuku.onBinderReceived(binder, context.packageName)
        return true
    }

    /** @return true if Murasaki service is present (we're likely on Rei) */
    @JvmStatic
    fun isReidMurasakiAvailable(): Boolean {
        val service = getService(MURASAKI_SERVICE_NAME) ?: return false
        return service.pingBinder()
    }

    @JvmStatic
    private fun getService(name: String): IBinder? {
        return runCatching {
            val clazz = Class.forName("android.os.ServiceManager")
            val method = clazz.getMethod("getService", String::class.java)
            method.invoke(null, name) as? IBinder
        }.getOrNull()
    }
}
