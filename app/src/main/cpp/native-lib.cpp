#include <jni.h>
#include <android/log.h>
#include <sys/types.h>
#include <dirent.h>
#include <string>

#define LOG_TAG "AppDetection"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

// Function to detect logger apps by package name
extern "C" JNIEXPORT jboolean JNICALL
Java_com_primeplay_faithflix_VPNUtils_isLoggerAppInstalled(JNIEnv *env, jobject /* this */) {
    jboolean loggerDetected = JNI_FALSE;
    const char* targetDir = "/data/data/";

    DIR* dir = opendir(targetDir);
    if (dir == NULL) {
        LOGI("Error opening directory: %s", targetDir);
        return JNI_FALSE;
    }

    struct dirent* entry;
    while ((entry = readdir(dir)) != NULL) {
        std::string packageName(entry->d_name);

        // Directly check for the specific package name
        if (packageName == "com.reqable.android") {
            LOGI("Logger app detected: %s", packageName.c_str());
            loggerDetected = JNI_TRUE;
            break;
        }
    }

    closedir(dir);
    return loggerDetected;
}
