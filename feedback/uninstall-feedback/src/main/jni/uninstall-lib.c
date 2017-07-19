#include <android/log.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/inotify.h>
#include <unistd.h>
#include "common.h"

//http://blog.csdn.net/delmoremiao/article/details/52032832

void setDaemonProcessName(JNIEnv *env, jstring processName_) {

    do {
        jclass class_id = (*env)->FindClass(env,
                                            "com/hobby/uninstall_feedback/AppProcessEntry");

        LOGD("class_id = %d", class_id == NULL);
        if (class_id == NULL) {
            break;
        }

        jmethodID method_id = (*env)->GetStaticMethodID(env, class_id, "setProcessName",
                                                        "(Ljava/lang/String;)V");

        LOGD("method_id = %d", method_id == NULL);

        if (method_id == NULL) {
            break;
        }

        if (processName_ != NULL) {

            (*env)->CallStaticVoidMethod(env, class_id, method_id, processName_);
        }


    } while (JNI_FALSE);

}


JNIEXPORT jint JNICALL
Java_com_hobby_uninstall_1feedback_UninstallInjector_openUrlWhenUninstallNative(JNIEnv *env,
                                                                                jclass type,
                                                                                jstring dirStr_,
                                                                                jstring data_,
                                                                                jstring brand_,
                                                                                jint apiLevel,
                                                                                jstring processName_) {
    const char *dirStr = (*env)->GetStringUTFChars(env, dirStr_, 0);
    const char *data = (*env)->GetStringUTFChars(env, data_, 0);
    const char *brand = (*env)->GetStringUTFChars(env, brand_, 0);
    const char *processName = (*env)->GetStringUTFChars(env, processName_, 0);

    // TODO
    const int api_level = apiLevel;

    LOGD("dirStr = %s, data = %s, brand = %s, process = %s", dirStr, data, brand, processName);

    pid_t pid = fork();

    LOGD("pid = %d", pid);

    //目前在Android 5.0系统上会把fork出来的进程放到一个进程组里，
    //当程序主进程挂掉后，也会把整个进程组杀掉,因此用fork的方式也无法在Android5.0及以上系统实现守护进程.

    //xref: /frameworks/base/services/core/java/com/android/server/am/ProcessRecord.java
    if (0 == pid) {

        LOGD("enter uninstall inject pid = %d", pid);
        setDaemonProcessName(env, processName_);

        while (JNI_TRUE) {

            LOGD("while JNI_TRUE");
            int fileDescriptor = inotify_init();
            if (fileDescriptor < 0) {
                //inotify_init failed
                exit(1);
            }

            int watchDescriptor;
            watchDescriptor = inotify_add_watch(fileDescriptor, dirStr, IN_DELETE);
            if (watchDescriptor < 0) {
                //inotify_add_watch failed
                exit(1);
            }

            void *p_buf = malloc(sizeof(struct inotify_event));
            if (p_buf == NULL) {
                exit(1);
            }

            LOGD("Start Observer");

            read(fileDescriptor, p_buf, sizeof(struct inotify_event));

            LOGD("Delete");

            free(p_buf);

            inotify_rm_watch(fileDescriptor, IN_DELETE);

            LOGD("rm watch");

            sleep(2);

            // 两秒后再判断目录是否存在，如果还存在，有可能是覆盖安装
            if (!access(dirStr, 0)) {
                // 覆盖安装什么都不用做，重新监听目录删除
                LOGD("replace install");
            } else {
                // 不是覆盖安装，应该是真被删除了
                LOGD("will be startup intent");
                break;
            }

        }

        //4.1以上的系统需要加上 --user 0
        //区分Android 4.x之前的版本 和 Android 4.x之后的版本的API变化
        LOGD("execlp");
        if (api_level > 16) {
            execlp("am", "am", "start", "--user", "0", "-a", "android.intent.action.VIEW", "-d",
                   data, (char *) NULL);
        } else {
            execlp("am", "am", "start", "-a", "android.intent.action.VIEW", "-d", data,
                   (char *) NULL);
        }
        LOGD("execlp end");

    }

    (*env)->ReleaseStringUTFChars(env, dirStr_, dirStr);
    (*env)->ReleaseStringUTFChars(env, data_, data);
    (*env)->ReleaseStringUTFChars(env, brand_, brand);
    (*env)->ReleaseStringUTFChars(env, processName_, processName);

    return pid;
}