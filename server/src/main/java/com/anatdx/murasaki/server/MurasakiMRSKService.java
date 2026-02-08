package com.anatdx.murasaki.server;

import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import com.anatdx.murasaki.server.util.Logger;

/**
 * MRSK Binder service: permission delegation and HymoFS kernel interface.
 * - requestPermissionForMurasaki: Rei/Murasaki permission flow.
 * - HymoFS transactions (2–15): direct JNI calls to HymoFS kernel (no netlink).
 */
public class MurasakiMRSKService extends Binder {

    private static final Logger LOGGER = new Logger("MurasakiMRSKService");

    private static final String DESCRIPTOR = "moe.shizuku.server.IShizukuMRSKService";
    private static final int TRANSACTION_requestPermissionForMurasaki = 1;
    private static final int TRANSACTION_hymofsIsAvailable = 2;
    private static final int TRANSACTION_hymofsGetProtocolVersion = 3;
    private static final int TRANSACTION_hymofsAddRule = 4;
    private static final int TRANSACTION_hymofsDeleteRule = 5;
    private static final int TRANSACTION_hymofsAddMergeRule = 6;
    private static final int TRANSACTION_hymofsClearRules = 7;
    private static final int TRANSACTION_hymofsGetActiveRules = 8;
    private static final int TRANSACTION_hymofsSetEnabled = 9;
    private static final int TRANSACTION_hymofsSetMirrorPath = 10;
    private static final int TRANSACTION_hymofsSetDebug = 11;
    private static final int TRANSACTION_hymofsSetStealth = 12;
    private static final int TRANSACTION_hymofsSetUname = 13;
    private static final int TRANSACTION_hymofsHidePath = 14;
    private static final int TRANSACTION_hymofsFixMounts = 15;

    private final MurasakiService murasakiService;

    public MurasakiMRSKService(@NonNull MurasakiService murasakiService) {
        this.murasakiService = murasakiService;
    }

    @Override
    protected boolean onTransact(int code, @NonNull Parcel data, Parcel reply, int flags) throws RemoteException {
        if (code == INTERFACE_TRANSACTION) {
            reply.writeString(DESCRIPTOR);
            return true;
        }
        if (code == TRANSACTION_requestPermissionForMurasaki) {
            data.enforceInterface(DESCRIPTOR);
            int uid = data.readInt();
            int pid = data.readInt();
            String packageName = data.readString();
            int requestCode = murasakiService.requestPermissionForMurasaki(uid, pid, packageName);
            reply.writeNoException();
            reply.writeInt(requestCode);
            return true;
        }
        // HymoFS: JNI to kernel (no netlink). All calls wrapped in try/catch so that
        // UnsatisfiedLinkError (e.g. lib not loaded in server process) does not crash Murasaki.
        if (code == TRANSACTION_hymofsIsAvailable) {
            data.enforceInterface(DESCRIPTOR);
            boolean result = false;
            try {
                result = HymoFSNative.isAvailable();
            } catch (Throwable t) {
                LOGGER.w(t, "HymoFS isAvailable");
            }
            reply.writeNoException();
            reply.writeByte((byte) (result ? 1 : 0));
            return true;
        }
        if (code == TRANSACTION_hymofsGetProtocolVersion) {
            data.enforceInterface(DESCRIPTOR);
            int result = -1;
            try {
                result = HymoFSNative.getProtocolVersion();
            } catch (Throwable t) {
                LOGGER.w(t, "HymoFS getProtocolVersion");
            }
            reply.writeNoException();
            reply.writeInt(result);
            return true;
        }
        if (code == TRANSACTION_hymofsAddRule) {
            data.enforceInterface(DESCRIPTOR);
            String src = data.readString();
            String target = data.readString();
            int type = data.readInt();
            boolean result = false;
            try {
                result = HymoFSNative.addRule(src, target, type);
            } catch (Throwable t) {
                LOGGER.w(t, "HymoFS addRule");
            }
            reply.writeNoException();
            reply.writeByte((byte) (result ? 1 : 0));
            return true;
        }
        if (code == TRANSACTION_hymofsDeleteRule) {
            data.enforceInterface(DESCRIPTOR);
            String src = data.readString();
            boolean result = false;
            try {
                result = HymoFSNative.deleteRule(src);
            } catch (Throwable t) {
                LOGGER.w(t, "HymoFS deleteRule");
            }
            reply.writeNoException();
            reply.writeByte((byte) (result ? 1 : 0));
            return true;
        }
        if (code == TRANSACTION_hymofsAddMergeRule) {
            data.enforceInterface(DESCRIPTOR);
            String src = data.readString();
            String target = data.readString();
            boolean result = false;
            try {
                result = HymoFSNative.addMergeRule(src, target);
            } catch (Throwable t) {
                LOGGER.w(t, "HymoFS addMergeRule");
            }
            reply.writeNoException();
            reply.writeByte((byte) (result ? 1 : 0));
            return true;
        }
        if (code == TRANSACTION_hymofsClearRules) {
            data.enforceInterface(DESCRIPTOR);
            boolean result = false;
            try {
                result = HymoFSNative.clearRules();
            } catch (Throwable t) {
                LOGGER.w(t, "HymoFS clearRules");
            }
            reply.writeNoException();
            reply.writeByte((byte) (result ? 1 : 0));
            return true;
        }
        if (code == TRANSACTION_hymofsGetActiveRules) {
            data.enforceInterface(DESCRIPTOR);
            String result = null;
            try {
                result = HymoFSNative.getActiveRules();
            } catch (Throwable t) {
                LOGGER.w(t, "HymoFS getActiveRules");
            }
            reply.writeNoException();
            reply.writeString(result);
            return true;
        }
        if (code == TRANSACTION_hymofsSetEnabled) {
            data.enforceInterface(DESCRIPTOR);
            boolean enable = data.readByte() != 0;
            boolean result = false;
            try {
                result = HymoFSNative.setEnabled(enable);
            } catch (Throwable t) {
                LOGGER.w(t, "HymoFS setEnabled");
            }
            reply.writeNoException();
            reply.writeByte((byte) (result ? 1 : 0));
            return true;
        }
        if (code == TRANSACTION_hymofsSetMirrorPath) {
            data.enforceInterface(DESCRIPTOR);
            String path = data.readString();
            boolean result = false;
            try {
                result = HymoFSNative.setMirrorPath(path);
            } catch (Throwable t) {
                LOGGER.w(t, "HymoFS setMirrorPath");
            }
            reply.writeNoException();
            reply.writeByte((byte) (result ? 1 : 0));
            return true;
        }
        if (code == TRANSACTION_hymofsSetDebug) {
            data.enforceInterface(DESCRIPTOR);
            boolean enable = data.readByte() != 0;
            boolean result = false;
            try {
                result = HymoFSNative.setDebug(enable);
            } catch (Throwable t) {
                LOGGER.w(t, "HymoFS setDebug");
            }
            reply.writeNoException();
            reply.writeByte((byte) (result ? 1 : 0));
            return true;
        }
        if (code == TRANSACTION_hymofsSetStealth) {
            data.enforceInterface(DESCRIPTOR);
            boolean enable = data.readByte() != 0;
            boolean result = false;
            try {
                result = HymoFSNative.setStealth(enable);
            } catch (Throwable t) {
                LOGGER.w(t, "HymoFS setStealth");
            }
            reply.writeNoException();
            reply.writeByte((byte) (result ? 1 : 0));
            return true;
        }
        if (code == TRANSACTION_hymofsSetUname) {
            data.enforceInterface(DESCRIPTOR);
            String release = data.readString();
            String version = data.readString();
            boolean result = false;
            try {
                result = HymoFSNative.setUname(release, version);
            } catch (Throwable t) {
                LOGGER.w(t, "HymoFS setUname");
            }
            reply.writeNoException();
            reply.writeByte((byte) (result ? 1 : 0));
            return true;
        }
        if (code == TRANSACTION_hymofsHidePath) {
            data.enforceInterface(DESCRIPTOR);
            String path = data.readString();
            boolean result = false;
            try {
                result = HymoFSNative.hidePath(path);
            } catch (Throwable t) {
                LOGGER.w(t, "HymoFS hidePath");
            }
            reply.writeNoException();
            reply.writeByte((byte) (result ? 1 : 0));
            return true;
        }
        if (code == TRANSACTION_hymofsFixMounts) {
            data.enforceInterface(DESCRIPTOR);
            boolean result = false;
            try {
                result = HymoFSNative.fixMounts();
            } catch (Throwable t) {
                LOGGER.w(t, "HymoFS fixMounts");
            }
            reply.writeNoException();
            reply.writeByte((byte) (result ? 1 : 0));
            return true;
        }
        return super.onTransact(code, data, reply, flags);
    }
}
