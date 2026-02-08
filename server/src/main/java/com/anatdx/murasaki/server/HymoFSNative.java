package com.anatdx.murasaki.server;

import androidx.annotation.Nullable;

/**
 * JNI bridge to HymoFS kernel interface (syscall + ioctl).
 * Protocol and ioctl definitions must match meta-hymo / HymoFS kernel.
 */
public final class HymoFSNative {

    public static final int EXPECTED_PROTOCOL_VERSION = 12;

    static {
        try {
            // Server started by starter passes -Dshizuku.library.path=<app lib dir>.
            // System.loadLibrary() uses java.library.path, which app_process does not set to that dir,
            // so we must load by full path when running as server process.
            String libPath = System.getProperty("shizuku.library.path");
            if (libPath != null && !libPath.isEmpty()) {
                System.load(libPath + "/libhymofs_jni.so");
            } else {
                System.loadLibrary("hymofs_jni");
            }
        } catch (Throwable t) {
            // Kernel may not have HymoFS, or lib not in path; native methods will return false / -1
        }
    }

    /** Returns kernel protocol version, or -1 if HymoFS not present. */
    public static native int getProtocolVersion();

    /** True if kernel has HymoFS and protocol version matches. */
    public static native boolean isAvailable();

    public static native boolean addRule(String src, String target, int type);

    public static native boolean deleteRule(String src);

    public static native boolean addMergeRule(String src, String target);

    public static native boolean clearRules();

    /** Returns multiline string of active rules, or null on error. */
    @Nullable
    public static native String getActiveRules();

    public static native boolean setEnabled(boolean enable);

    public static native boolean setMirrorPath(String path);

    public static native boolean setDebug(boolean enable);

    public static native boolean setStealth(boolean enable);

    /** Pass null to clear. */
    public static native boolean setUname(@Nullable String release, @Nullable String version);

    public static native boolean hidePath(String path);

    /** Reorder mnt_id (fix mount namespace). */
    public static native boolean fixMounts();

    private HymoFSNative() {}
}
