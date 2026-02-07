package rikka.shizuku.server;

public class ServerConstants {

    public static final int MANAGER_APP_NOT_FOUND = 50;

    public static final String PERMISSION = "moe.shizuku.manager.permission.API_V23";
    /**
     * Shizuku Manager package.
     * For Rei build: set to {@link #REI_MANAGER_APPLICATION_ID} so permission UI and
     * dispatchPermissionConfirmationResult use Rei (AuthorizeActivity).
     */
    public static final String MANAGER_APPLICATION_ID = "moe.shizuku.privileged.api";
    public static final String REQUEST_PERMISSION_ACTION = MANAGER_APPLICATION_ID + ".intent.action.REQUEST_PERMISSION";

    /** Rei Manager (MRSK): same role as Shizuku Manager for permission UI and dispatchPermissionConfirmationResult. */
    public static final String REI_MANAGER_APPLICATION_ID = "com.anatdx.rei";
    /** Rei AuthorizeActivity for MRSK permission confirmation. */
    public static final String REI_AUTHORIZE_ACTIVITY_CLASS = "com.anatdx.rei.ui.auth.AuthorizeActivity";
    /** Extra key: source = murasaki so Rei treats as Shizuku/Murasaki flow. */
    public static final String REI_EXTRA_SOURCE_MURASAKI = "murasaki";

    /** MRSK Binder service name when run by Shizuku server (reid can get this to delegate permission). */
    public static final String MRSK_SERVICE_NAME = "moe.shizuku.server.IShizukuMRSKService";
    /** Transaction code: requestPermissionForMurasaki(uid, pid, packageName) -> requestCode. */
    public static final int MRSK_TRANSACTION_requestPermissionForMurasaki = 1;

    public static final int BINDER_TRANSACTION_getApplications = 10001;
}
