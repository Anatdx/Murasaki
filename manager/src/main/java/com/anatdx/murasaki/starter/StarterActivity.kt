package com.anatdx.murasaki.starter

import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.anatdx.murasaki.R
import com.anatdx.murasaki.app.AppBarActivity
import com.anatdx.murasaki.databinding.StarterActivityBinding
import rikka.lifecycle.Resource
import rikka.lifecycle.Status
import rikka.lifecycle.viewModels
import com.anatdx.murasaki.Murasaki
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private class NotRootedException : Exception()

class StarterActivity : AppBarActivity() {

    private val viewModel by viewModels { ViewModel(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_24)

        val binding = StarterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.output.observe(this) {
            val output = it.data!!.trim()
            if (output.endsWith("info: shizuku_starter exit with 0")) {
                viewModel.appendOutput("")
                viewModel.appendOutput("Waiting for service...")

                Murasaki.addBinderReceivedListener(object : Murasaki.OnBinderReceivedListener {
                    override fun onBinderReceived() {
                        Murasaki.removeBinderReceivedListener(this)
                        viewModel.appendOutput("Service started, this window will be automatically closed in 3 seconds")

                        window?.decorView?.postDelayed({
                            if (!isFinishing) finish()
                        }, 3000)
                    }
                })
            } else if (it.status == Status.ERROR) {
                if (it.error is NotRootedException) {
                    MaterialAlertDialogBuilder(this)
                        .setMessage(R.string.start_with_root_failed)
                        .setPositiveButton(android.R.string.ok, null)
                        .show()
                }
            }
            binding.text1.text = output
        }
    }

    companion object {
        const val EXTRA_IS_ROOT = "${com.anatdx.murasaki.AppConstants.EXTRA}.IS_ROOT"
    }
}

private class ViewModel(context: android.content.Context) : androidx.lifecycle.ViewModel() {

    private val sb = StringBuilder()
    private val _output = androidx.lifecycle.MutableLiveData<Resource<StringBuilder>>()

    val output = _output as androidx.lifecycle.LiveData<Resource<StringBuilder>>

    init {
        try {
            startRoot()
        } catch (e: Throwable) {
            postResult(e)
        }
    }

    fun appendOutput(line: String) {
        sb.appendLine(line)
        postResult()
    }

    private fun postResult(throwable: Throwable? = null) {
        if (throwable == null)
            _output.postValue(Resource.success(sb))
        else
            _output.postValue(Resource.error(throwable, sb))
    }

    private fun startRoot() {
        sb.append("Starting with root...").append('\n').append('\n')
        postResult()

        GlobalScope.launch(Dispatchers.IO) {
            if (!com.topjohnwu.superuser.Shell.getShell().isRoot) {
                com.topjohnwu.superuser.Shell.getCachedShell()?.close()
                sb.append('\n').append("Can't open root shell, try again...").append('\n')

                postResult()
                if (!com.topjohnwu.superuser.Shell.getShell().isRoot) {
                    sb.append('\n').append("Still not :(").append('\n')
                    postResult(NotRootedException())
                    return@launch
                }
            }

            com.topjohnwu.superuser.Shell.cmd(Starter.internalCommand).to(object : com.topjohnwu.superuser.CallbackList<String?>() {
                override fun onAddElement(s: String?) {
                    sb.append(s).append('\n')
                    postResult()
                }
            }).submit {
                if (it.code != 0) {
                    sb.append('\n').append("Send this to developer may help solve the problem.")
                    postResult()
                }
            }
        }
    }
}
