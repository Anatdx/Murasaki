package com.anatdx.murasaki.shell

import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Parcel
import com.anatdx.murasaki.utils.Logger.LOGGER
import com.anatdx.murasaki.Murasaki

object ShellBinderRequestHandler {

    fun handleRequest(context: Context, intent: Intent): Boolean {
        if (intent.action != "rikka.shizuku.intent.action.REQUEST_BINDER") {
            return false
        }

        val binder = intent.getBundleExtra("data")?.getBinder("binder") ?: return false
        val murasakiBinder = Murasaki.getBinder()
        if (murasakiBinder == null) {
            LOGGER.w("Binder not received or Murasaki service not running")
        }

        val data = Parcel.obtain()
        return try {
            data.writeStrongBinder(murasakiBinder)
            data.writeString(context.applicationInfo.sourceDir)
            binder.transact(1, data, null, IBinder.FLAG_ONEWAY)
            true
        } catch (e: Throwable) {
            e.printStackTrace()
            false
        } finally {
            data.recycle()
        }
    }
}
