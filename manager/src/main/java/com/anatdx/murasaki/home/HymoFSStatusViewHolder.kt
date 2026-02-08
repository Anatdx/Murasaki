package com.anatdx.murasaki.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.anatdx.murasaki.R
import com.anatdx.murasaki.databinding.HomeHymofsStatusBinding
import com.anatdx.murasaki.databinding.HomeItemContainerBinding
import com.anatdx.murasaki.mrsk.HymoFSClient
import rikka.recyclerview.BaseViewHolder
import rikka.recyclerview.BaseViewHolder.Creator

/**
 * 首页「HymoFS 对接状态」一行：点击后通过 MRSK 检测内核 HymoFS 是否对接成功，并更新副标题与 Toast。
 */
class HymoFSStatusViewHolder(
    private val binding: HomeHymofsStatusBinding,
    root: View
) : BaseViewHolder<Unit>(root) {

    companion object {
        val CREATOR = Creator<Unit> { inflater: LayoutInflater, parent: ViewGroup? ->
            val outer = HomeItemContainerBinding.inflate(inflater, parent, false)
            val inner = HomeHymofsStatusBinding.inflate(inflater, outer.root, true)
            HymoFSStatusViewHolder(inner, outer.root)
        }
    }

    init {
        root.setOnClickListener {
            binding.text2.text = root.context.getString(R.string.home_hymofs_status_checking)
            HymoFSClient.checkStatus { status ->
                root.post {
                    binding.text2.text = status.summary
                    Toast.makeText(root.context, status.summary, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBind() {
        binding.text2.text = itemView.context.getString(R.string.home_hymofs_status_tap_to_check)
    }
}
