//
// Created by chenyichang on 2017/6/23.
//

#ifndef FEEDBACK_COMMON_H
#define FEEDBACK_COMMON_H

#include <jni.h>
#include <android/log.h>

#define NATIVE_TRACE ALOGD

#ifdef NATIVE_TRACE
#define  LOG_TAG    "UninstallInjector_c"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#else
#define  LOGD(...)
#define  LOGW(...)
#define  LOGE(...)
#endif


#endif //FEEDBACK_COMMON_H
