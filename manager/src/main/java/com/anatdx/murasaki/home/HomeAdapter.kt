package com.anatdx.murasaki.home

import com.anatdx.murasaki.management.AppsViewModel
import com.anatdx.murasaki.utils.UserHandleCompat
import rikka.recyclerview.IdBasedRecyclerViewAdapter
import rikka.recyclerview.IndexCreatorPool

class HomeAdapter(private val homeModel: HomeViewModel, private val appsModel: AppsViewModel) :
    IdBasedRecyclerViewAdapter(ArrayList()) {

    init {
        updateData()
        setHasStableIds(true)
    }

    companion object {

        private const val ID_STATUS = 0L
        private const val ID_APPS = 1L
        private const val ID_HYMOFS = 5L
        private const val ID_START_ROOT = 3L
        private const val ID_LEARN_MORE = 6L
    }

    override fun onCreateCreatorPool(): IndexCreatorPool {
        return IndexCreatorPool()
    }

    fun updateData() {
        val status = homeModel.serviceStatus.value?.data ?: return
        val grantedCount = appsModel.grantedCount.value?.data ?: 0
        val permission = status.permission
        val isPrimaryUser = UserHandleCompat.myUserId() == 0

        clear()
        addItem(ServerStatusViewHolder.CREATOR, status, ID_STATUS)

        if (permission) {
            addItem(ManageAppsViewHolder.CREATOR, status to grantedCount, ID_APPS)
            addItem(HymoFSStatusViewHolder.CREATOR, Unit, ID_HYMOFS)
        }

        // Murasaki: only root start; no ADB / wireless ADB entries
        if (isPrimaryUser) {
            val rootRestart = status.isRunning && status.uid == 0
            addItem(StartRootViewHolder.CREATOR, rootRestart, ID_START_ROOT)
        }
        addItem(LearnMoreViewHolder.CREATOR, null, ID_LEARN_MORE)
        notifyDataSetChanged()
    }
}
