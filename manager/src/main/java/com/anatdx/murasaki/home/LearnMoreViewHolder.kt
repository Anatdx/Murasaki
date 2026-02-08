package com.anatdx.murasaki.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anatdx.murasaki.Helps
import com.anatdx.murasaki.databinding.HomeItemContainerBinding
import com.anatdx.murasaki.databinding.HomeLearnMoreBinding
import com.anatdx.murasaki.utils.CustomTabsHelper
import rikka.recyclerview.BaseViewHolder
import rikka.recyclerview.BaseViewHolder.Creator

class LearnMoreViewHolder(binding: HomeLearnMoreBinding, root: View) : BaseViewHolder<Any?>(root) {

    companion object {
        val CREATOR = Creator<Any> { inflater: LayoutInflater, parent: ViewGroup? ->
            val outer = HomeItemContainerBinding.inflate(inflater, parent, false)
            val inner = HomeLearnMoreBinding.inflate(inflater, outer.root, true)
            LearnMoreViewHolder(inner, outer.root)
        }
    }

    init {
        root.setOnClickListener { v: View -> CustomTabsHelper.launchUrlOrCopy(v.context, Helps.HOME.get()) }
    }
}
