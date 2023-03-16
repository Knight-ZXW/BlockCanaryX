//
// Created by Administrator on 2023/2/4.
//

#include "BlockCanary.h"
#include <jni.h>

#include <sys/types.h>
#include <sys/socket.h>
#include "logger.h"
#include "bytehook.h"
#include "TouchEventTracer.h"

static int currentTouchFd;
static bool inputHasSent;

void HookCallback(bytehook_stub_t task_stub, int status,
                  const char *caller_path_name, const char *sym_name,
                  void *new_func, void *prev_func, void *hooked_arg) {
    if (status) {
        LOGE("failed to hook: %s-%s-%d", caller_path_name, sym_name, status);
    }
}

ssize_t (*original_sendto)(int sockfd, const void *buf, size_t len, int flags,
                           const struct sockaddr *dest_addr, socklen_t addrlen);

ssize_t my_sendto(int sockfd, const void *buf, size_t len, int flags,
                  const struct sockaddr *dest_addr, socklen_t addrlen) {

    long ret = original_sendto(sockfd, buf, len, flags, dest_addr, addrlen);
    if (ret >= 0) {
        inputHasSent = true;
        TouchEventTracer::touchSendFinish(sockfd);
    }
    return ret;
}

void onTouchEventLag(int fd) {

}



static void nativeInitTouchEventLagDetective(JNIEnv *env,
                                             jclass, jint threshold) {
    bytehook_hook_all(
            "libinput.so$",
            "__sendto_chk",
            (void *)my_sendto,
            HookCallback,
            nullptr);


}


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *) {

}