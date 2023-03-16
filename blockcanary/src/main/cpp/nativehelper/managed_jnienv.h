//
// Created by Administrator on 2023/2/4.
//

#ifndef BLOCKCANARYX_MANAGED_JNIENV_H
#define BLOCKCANARYX_MANAGED_JNIENV_H

#endif //BLOCKCANARYX_MANAGED_JNIENV_H

#include <jni.h>


namespace JniInvocation {

    void init(JavaVM *vm);
    JavaVM *getJavaVM();
    JNIEnv *getEnv();

}