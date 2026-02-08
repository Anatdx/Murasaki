#ifndef _LINUX_HYMO_MAGIC_H
#define _LINUX_HYMO_MAGIC_H

#include <stddef.h>
#include <stdint.h>
#include <sys/ioctl.h>

#define HYMO_MAGIC1 0x48594D4F  /* "HYMO" */
#define HYMO_MAGIC2 0x524F4F54  /* "ROOT" */
#define HYMO_PROTOCOL_VERSION 12

#define HYMO_MAX_LEN_PATHNAME 256
#define HYMO_FAKE_CMDLINE_SIZE 4096

#define HYMO_CMD_GET_FD 0x48021

struct hymo_syscall_arg {
    const char* src;
    const char* target;
    int type;
};

struct hymo_syscall_list_arg {
    char* buf;
    size_t size;
};

#define HYMO_UNAME_LEN 65
struct hymo_spoof_uname {
    char sysname[HYMO_UNAME_LEN];
    char nodename[HYMO_UNAME_LEN];
    char release[HYMO_UNAME_LEN];
    char version[HYMO_UNAME_LEN];
    char machine[HYMO_UNAME_LEN];
    char domainname[HYMO_UNAME_LEN];
    int err;
};

#define HYMO_IOC_MAGIC 'H'
#define HYMO_IOC_ADD_RULE _IOW(HYMO_IOC_MAGIC, 1, struct hymo_syscall_arg)
#define HYMO_IOC_DEL_RULE _IOW(HYMO_IOC_MAGIC, 2, struct hymo_syscall_arg)
#define HYMO_IOC_HIDE_RULE _IOW(HYMO_IOC_MAGIC, 3, struct hymo_syscall_arg)
#define HYMO_IOC_CLEAR_ALL _IO(HYMO_IOC_MAGIC, 5)
#define HYMO_IOC_GET_VERSION _IOR(HYMO_IOC_MAGIC, 6, int)
#define HYMO_IOC_LIST_RULES _IOWR(HYMO_IOC_MAGIC, 7, struct hymo_syscall_list_arg)
#define HYMO_IOC_SET_DEBUG _IOW(HYMO_IOC_MAGIC, 8, int)
#define HYMO_IOC_REORDER_MNT_ID _IO(HYMO_IOC_MAGIC, 9)
#define HYMO_IOC_SET_STEALTH _IOW(HYMO_IOC_MAGIC, 10, int)
#define HYMO_IOC_ADD_MERGE_RULE _IOW(HYMO_IOC_MAGIC, 12, struct hymo_syscall_arg)
#define HYMO_IOC_SET_MIRROR_PATH _IOW(HYMO_IOC_MAGIC, 14, struct hymo_syscall_arg)
#define HYMO_IOC_SET_UNAME _IOW(HYMO_IOC_MAGIC, 17, struct hymo_spoof_uname)
#define HYMO_IOC_SET_ENABLED _IOW(HYMO_IOC_MAGIC, 20, int)

#endif
