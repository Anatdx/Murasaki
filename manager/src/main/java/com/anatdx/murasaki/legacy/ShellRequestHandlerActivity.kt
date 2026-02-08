package com.anatdx.murasaki.legacy

import android.os.Bundle
import android.widget.Toast
import com.anatdx.murasaki.app.AppActivity
import com.anatdx.murasaki.shell.ShellBinderRequestHandler

class ShellRequestHandlerActivity : AppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ShellBinderRequestHandler.handleRequest(this, intent)
        finish()
    }
}
