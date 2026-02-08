package com.anatdx.murasaki.management

import android.content.pm.PackageInfo
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Job
import com.anatdx.murasaki.Helps
import com.anatdx.murasaki.R
import com.anatdx.murasaki.authorization.AuthorizationManager
import com.anatdx.murasaki.databinding.AppListEmptyBinding
import com.anatdx.murasaki.databinding.AppListItemBinding
import com.anatdx.murasaki.ktx.toHtml
import com.anatdx.murasaki.utils.AppIconCache
import com.anatdx.murasaki.utils.ShizukuSystemApis
import com.anatdx.murasaki.utils.UserHandleCompat
import rikka.html.text.HtmlCompat
import rikka.recyclerview.BaseViewHolder
import rikka.recyclerview.BaseViewHolder.Creator
import com.anatdx.murasaki.Murasaki

class EmptyViewHolder(private val binding: AppListEmptyBinding) : BaseViewHolder<Any>(binding.root) {

    companion object {
        @JvmField
        val CREATOR = Creator<Any> { inflater: LayoutInflater, parent: ViewGroup? -> EmptyViewHolder(AppListEmptyBinding.inflate(inflater, parent, false)) }
    }

}
