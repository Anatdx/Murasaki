/*
 * JNI bridge for HymoFS kernel interface.
 * Mirrors meta-hymo/src/mount/hymofs.cpp (syscall + ioctl only).
 */
#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include <sys/ioctl.h>
#include <sys/syscall.h>
#include <unistd.h>

#include "hymo_magic.h"

#define LIST_RULES_BUF_SIZE (16 * 1024)

static int s_hymo_fd = -1;

static int get_anon_fd(void) {
    if (s_hymo_fd >= 0)
        return s_hymo_fd;
    int fd = (int) syscall(SYS_reboot, (long) HYMO_MAGIC1, (long) HYMO_MAGIC2,
                           (long) HYMO_CMD_GET_FD, 0L);
    if (fd < 0)
        return -1;
    s_hymo_fd = fd;
    return fd;
}

static int hymo_ioctl(unsigned long cmd, void* arg) {
    int fd = get_anon_fd();
    if (fd < 0)
        return -1;
    return ioctl(fd, cmd, arg);
}

JNIEXPORT jint JNICALL
Java_rikka_shizuku_server_HymoFSNative_getProtocolVersion(JNIEnv* env, jclass clazz) {
    int version = 0;
    if (hymo_ioctl(HYMO_IOC_GET_VERSION, &version) != 0)
        return -1;
    return (jint) version;
}

JNIEXPORT jboolean JNICALL
Java_rikka_shizuku_server_HymoFSNative_isAvailable(JNIEnv* env, jclass clazz) {
    int ver = Java_rikka_shizuku_server_HymoFSNative_getProtocolVersion(env, clazz);
    return (ver == HYMO_PROTOCOL_VERSION) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_rikka_shizuku_server_HymoFSNative_addRule(JNIEnv* env, jclass clazz,
                                                jstring src_java, jstring target_java,
                                                jint type) {
    const char* src = (*env)->GetStringUTFChars(env, src_java, NULL);
    const char* target = target_java ? (*env)->GetStringUTFChars(env, target_java, NULL) : NULL;
    struct hymo_syscall_arg arg = {
            .src = src,
            .target = target,
            .type = (int) type
    };
    int ret = hymo_ioctl(HYMO_IOC_ADD_RULE, &arg);
    (*env)->ReleaseStringUTFChars(env, src_java, src);
    if (target_java)
        (*env)->ReleaseStringUTFChars(env, target_java, target);
    return (ret == 0) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_rikka_shizuku_server_HymoFSNative_deleteRule(JNIEnv* env, jclass clazz, jstring src_java) {
    const char* src = (*env)->GetStringUTFChars(env, src_java, NULL);
    struct hymo_syscall_arg arg = {.src = src, .target = NULL, .type = 0};
    int ret = hymo_ioctl(HYMO_IOC_DEL_RULE, &arg);
    (*env)->ReleaseStringUTFChars(env, src_java, src);
    return (ret == 0) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_rikka_shizuku_server_HymoFSNative_addMergeRule(JNIEnv* env, jclass clazz,
                                                    jstring src_java, jstring target_java) {
    const char* src = (*env)->GetStringUTFChars(env, src_java, NULL);
    const char* target = (*env)->GetStringUTFChars(env, target_java, NULL);
    struct hymo_syscall_arg arg = {.src = src, .target = target, .type = 0};
    int ret = hymo_ioctl(HYMO_IOC_ADD_MERGE_RULE, &arg);
    (*env)->ReleaseStringUTFChars(env, src_java, src);
    (*env)->ReleaseStringUTFChars(env, target_java, target);
    return (ret == 0) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_rikka_shizuku_server_HymoFSNative_clearRules(JNIEnv* env, jclass clazz) {
    return (hymo_ioctl(HYMO_IOC_CLEAR_ALL, NULL) == 0) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jstring JNICALL
Java_rikka_shizuku_server_HymoFSNative_getActiveRules(JNIEnv* env, jclass clazz) {
    char* buf = (char*) malloc(LIST_RULES_BUF_SIZE);
    if (!buf)
        return NULL;
    memset(buf, 0, LIST_RULES_BUF_SIZE);
    struct hymo_syscall_list_arg arg = {.buf = buf, .size = LIST_RULES_BUF_SIZE};
    int ret = hymo_ioctl(HYMO_IOC_LIST_RULES, &arg);
    jstring result = NULL;
    if (ret == 0)
        result = (*env)->NewStringUTF(env, buf);
    free(buf);
    return result;
}

JNIEXPORT jboolean JNICALL
Java_rikka_shizuku_server_HymoFSNative_setEnabled(JNIEnv* env, jclass clazz, jboolean enable) {
    int val = enable ? 1 : 0;
    return (hymo_ioctl(HYMO_IOC_SET_ENABLED, &val) == 0) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_rikka_shizuku_server_HymoFSNative_setMirrorPath(JNIEnv* env, jclass clazz, jstring path_java) {
    const char* path = (*env)->GetStringUTFChars(env, path_java, NULL);
    struct hymo_syscall_arg arg = {.src = path, .target = NULL, .type = 0};
    int ret = hymo_ioctl(HYMO_IOC_SET_MIRROR_PATH, &arg);
    (*env)->ReleaseStringUTFChars(env, path_java, path);
    return (ret == 0) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_rikka_shizuku_server_HymoFSNative_setDebug(JNIEnv* env, jclass clazz, jboolean enable) {
    int val = enable ? 1 : 0;
    return (hymo_ioctl(HYMO_IOC_SET_DEBUG, &val) == 0) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_rikka_shizuku_server_HymoFSNative_setStealth(JNIEnv* env, jclass clazz, jboolean enable) {
    int val = enable ? 1 : 0;
    return (hymo_ioctl(HYMO_IOC_SET_STEALTH, &val) == 0) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_rikka_shizuku_server_HymoFSNative_setUname(JNIEnv* env, jclass clazz,
                                                 jstring release_java, jstring version_java) {
    struct hymo_spoof_uname u;
    memset(&u, 0, sizeof(u));
    if (release_java) {
        const char* r = (*env)->GetStringUTFChars(env, release_java, NULL);
        strncpy(u.release, r, HYMO_UNAME_LEN - 1);
        u.release[HYMO_UNAME_LEN - 1] = '\0';
        (*env)->ReleaseStringUTFChars(env, release_java, r);
    }
    if (version_java) {
        const char* v = (*env)->GetStringUTFChars(env, version_java, NULL);
        strncpy(u.version, v, HYMO_UNAME_LEN - 1);
        u.version[HYMO_UNAME_LEN - 1] = '\0';
        (*env)->ReleaseStringUTFChars(env, version_java, v);
    }
    return (hymo_ioctl(HYMO_IOC_SET_UNAME, &u) == 0) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_rikka_shizuku_server_HymoFSNative_hidePath(JNIEnv* env, jclass clazz, jstring path_java) {
    const char* path = (*env)->GetStringUTFChars(env, path_java, NULL);
    struct hymo_syscall_arg arg = {.src = path, .target = NULL, .type = 0};
    int ret = hymo_ioctl(HYMO_IOC_HIDE_RULE, &arg);
    (*env)->ReleaseStringUTFChars(env, path_java, path);
    return (ret == 0) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_rikka_shizuku_server_HymoFSNative_fixMounts(JNIEnv* env, jclass clazz) {
    return (hymo_ioctl(HYMO_IOC_REORDER_MNT_ID, NULL) == 0) ? JNI_TRUE : JNI_FALSE;
}
