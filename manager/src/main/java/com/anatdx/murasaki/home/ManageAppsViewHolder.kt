package com.anatdx.murasaki.home

import android.content.Intent
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anatdx.murasaki.Helps
import com.anatdx.murasaki.R
import com.anatdx.murasaki.databinding.HomeItemContainerBinding
import com.anatdx.murasaki.databinding.HomeManageAppsItemBinding
import com.anatdx.murasaki.ktx.toHtml
import com.anatdx.murasaki.management.ApplicationManagementActivity
import com.anatdx.murasaki.model.ServiceStatus
import rikka.html.text.HtmlCompat
import rikka.recyclerview.BaseViewHolder
import rikka.recyclerview.BaseViewHolder.Creator

class ManageAppsViewHolder(private val binding: HomeManageAppsItemBinding, root: View) :
    BaseViewHolder<Pair<ServiceStatus, Int>>(root), View.OnClickListener {

    companion object {
        val CREATOR = Creator<Pair<ServiceStatus, Int>> { inflater: LayoutInflater, parent: ViewGroup? ->
            val outer = HomeItemContainerBinding.inflate(inflater, parent, false)
            val inner = HomeManageAppsItemBinding.inflate(inflater, outer.root, true)
            ManageAppsViewHolder(inner, outer.root)
        }
    }

    init {
        root.setOnClickListener(this)
    }

    private inline val title get() = binding.text1
    private inline val summary get() = binding.text2

    override fun onBind() {
        val context = itemView.context
        if (!data.first.isRunning) {
            itemView.isEnabled = false
            title.setText(R.string.home_app_management_title)
            summary.text = context.getString(
                R.string.home_status_service_not_running,
                context.getString(R.string.app_name)
            )
        } else {
            itemView.isEnabled = true
            title.text = context.resources.getQuantityString(
                R.plurals.home_app_management_authorized_apps_count,
                data.second,
                data.second
            )
            summary.text = context.getString(R.string.home_app_management_view_authorized_apps)
        }
    }

    override fun onClick(v: View) {
        v.context.startActivity(Intent(v.context, ApplicationManagementActivity::class.java))
    }
}
