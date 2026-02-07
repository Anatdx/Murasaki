package rikka.shizuku.server;

import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import rikka.shizuku.server.util.Logger;

/**
 * MRSK Binder service: allows reid/Murasaki to delegate permission request to Shizuku server.
 * When Shizuku server runs with Rei as Manager, this service is registered so that
 * permission flow (show Rei AuthorizeActivity, then dispatchPermissionConfirmationResult)
 * stays inside Shizuku Java and works correctly.
 */
public class ShizukuMRSKService extends Binder {

    private static final Logger LOGGER = new Logger("ShizukuMRSKService");

    private static final String DESCRIPTOR = "moe.shizuku.server.IShizukuMRSKService";
    private static final int TRANSACTION_requestPermissionForMurasaki = 1;

    private final ShizukuService shizukuService;

    public ShizukuMRSKService(@NonNull ShizukuService shizukuService) {
        this.shizukuService = shizukuService;
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
            int requestCode = shizukuService.requestPermissionForMurasaki(uid, pid, packageName);
            reply.writeNoException();
            reply.writeInt(requestCode);
            return true;
        }
        return super.onTransact(code, data, reply, flags);
    }
}
