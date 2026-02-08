package com.anatdx.murasaki.home

import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.anatdx.murasaki.BuildConfig
import com.anatdx.murasaki.Manifest
import com.anatdx.murasaki.application
import com.anatdx.murasaki.model.ServiceStatus
import com.anatdx.murasaki.mrsk.MRSKHelper
import com.anatdx.murasaki.utils.Logger.LOGGER
import com.anatdx.murasaki.utils.ShizukuSystemApis
import rikka.lifecycle.Resource
import com.anatdx.murasaki.Murasaki

class HomeViewModel : ViewModel() {

    private val _serviceStatus = MutableLiveData<Resource<ServiceStatus>>()
    val serviceStatus = _serviceStatus as LiveData<Resource<ServiceStatus>>

    private fun load(): ServiceStatus {
        if (!Murasaki.pingBinder()) {
            return ServiceStatus()
        }

        val uid = Murasaki.getUid()
        val apiVersion = Murasaki.getVersion()
        val patchVersion = Murasaki.getServerPatchVersion().let { if (it < 0) 0 else it }
        val seContext = if (apiVersion >= 6) {
            try {
                Murasaki.getSELinuxContext()
            } catch (tr: Throwable) {
                LOGGER.w(tr, "getSELinuxContext")
                null
            }
        } else null
        val permissionTest =
            Murasaki.checkRemotePermission("android.permission.GRANT_RUNTIME_PERMISSIONS") == PackageManager.PERMISSION_GRANTED

        // Before a526d6bb, server will not exit on uninstall, manager installed later will get not permission
        // Run a random remote transaction here, report no permission as not running
        ShizukuSystemApis.checkPermission(Manifest.permission.API_V23, BuildConfig.APPLICATION_ID, 0)
        return ServiceStatus(uid, apiVersion, patchVersion, seContext, permissionTest)
    }

    fun reload() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // If no Binder yet, try MRSK (reid) so Rei users get Shizuku without Java server
                if (!Murasaki.pingBinder()) {
                    MRSKHelper.tryReceiveBinderFromReid(application)
                }
                val status = load()
                _serviceStatus.postValue(Resource.success(status))
            } catch (e: CancellationException) {

            } catch (e: Throwable) {
                _serviceStatus.postValue(Resource.error(e, ServiceStatus()))
            }
        }
    }
}
