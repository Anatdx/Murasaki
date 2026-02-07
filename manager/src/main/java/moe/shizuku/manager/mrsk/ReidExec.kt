package moe.shizuku.manager.mrsk

import com.topjohnwu.superuser.Shell

/**
 * Run reid/daemon commands via root shell when on Rei (MRSK).
 * Use when [MRSKHelper.isReidMurasakiAvailable] and you need allowlist/daemon control
 * without depending on Rei app's Murasaki API.
 */
object ReidExec {

    private const val REID_PATH = "/data/adb/reid"
    private const val DEFAULT_TIMEOUT_MS = 15_000L

    data class Result(val exitCode: Int, val output: String)

    /** Run reid with given args (e.g. "allowlist", "grant", uid.toString(), pkg). */
    @JvmStatic
    fun execReid(args: List<String>, timeoutMs: Long = DEFAULT_TIMEOUT_MS): Result {
        if (args.isEmpty()) return Result(-1, "empty args")
        val escaped = args.joinToString(" ") { shellEscape(it) }
        val cmd = "if [ -x $REID_PATH ]; then $REID_PATH $escaped; else echo no_reid; exit 127; fi"
        return runShellSu(cmd, timeoutMs)
    }

    /** Convenience: allowlist grant uid for package. */
    @JvmStatic
    fun allowlistGrant(uid: Int, packageName: String, timeoutMs: Long = DEFAULT_TIMEOUT_MS): Result =
        execReid(listOf("allowlist", "grant", uid.toString(), packageName), timeoutMs)

    /** Convenience: allowlist revoke uid. */
    @JvmStatic
    fun allowlistRevoke(uid: Int, timeoutMs: Long = DEFAULT_TIMEOUT_MS): Result =
        execReid(listOf("allowlist", "revoke", uid.toString()), timeoutMs)

    private fun shellEscape(s: String): String {
        val safe = s.replace("'", "'\\''")
        return "'$safe'"
    }

    private fun runShellSu(cmd: String, timeoutMs: Long): Result {
        return try {
            // Shell is already root (libsu); run script directly
            val result = Shell.cmd(cmd).exec()
            val exitCode = result.code
            val output = (result.out + result.err).joinToString("\n").trim()
            Result(exitCode, output)
        } catch (e: Exception) {
            Result(-1, e.message ?: e.javaClass.simpleName)
        }
    }
}
