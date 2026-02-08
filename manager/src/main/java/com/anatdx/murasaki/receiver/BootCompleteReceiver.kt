package com.anatdx.murasaki.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.anatdx.murasaki.ShizukuSettings
import com.anatdx.murasaki.ShizukuSettings.LaunchMethod
import com.anatdx.murasaki.starter.Starter
import com.anatdx.murasaki.utils.UserHandleCompat
import com.topjohnwu.superuser.Shell
import com.anatdx.murasaki.Murasaki

/**
 * Murasaki: root-only, start on boot.
 */
class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_LOCKED_BOOT_COMPLETED != intent.action
            && Intent.ACTION_BOOT_COMPLETED != intent.action) {
            return
        }

        if (UserHandleCompat.myUserId() > 0 || Murasaki.pingBinder()) return

        if (ShizukuSettings.getLastLaunchMode() == LaunchMethod.ROOT) {
            rootStart(context)
        }
    }

    private fun rootStart(context: Context) {
        if (!Shell.getShell().isRoot) {
            Shell.getCachedShell()?.close()
            return
        }
        Shell.cmd(Starter.internalCommand).exec()
    }
}
