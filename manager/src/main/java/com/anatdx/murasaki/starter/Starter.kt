package com.anatdx.murasaki.starter

import com.anatdx.murasaki.application
import java.io.File

object Starter {

    // Root start JNI (libshizuku.so) removed - path kept for root start command; will fail at runtime if so missing
    private val starterFile = File(application.applicationInfo.nativeLibraryDir, "libshizuku.so")

    val userCommand: String = starterFile.absolutePath

    val internalCommand = "$userCommand --apk=${application.applicationInfo.sourceDir}"
}
