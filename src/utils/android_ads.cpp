#include "utils/android_ads.hpp"
#ifdef ANDROID
#include "SDL_system.h"
#include "utils/log.hpp"
#include <jni.h>

static void callAdsMethod(const char* name)
{
    JNIEnv* env = (JNIEnv*)SDL_AndroidGetJNIEnv();
    if (!env) return;
    jobject activity = (jobject)SDL_AndroidGetActivity();
    if (!activity) return;
    jclass cls = env->GetObjectClass(activity);
    if (!cls) { env->DeleteLocalRef(activity); return; }
    jmethodID method = env->GetStaticMethodID(cls, name, "(Landroid/app/Activity;)V");
    if (method)
        env->CallStaticVoidMethod(cls, method, activity);
    else
        Log::warn("KaslikAds", "Could not find %s", name);
    env->DeleteLocalRef(cls);
    env->DeleteLocalRef(activity);
}

void kaslikShowRewardedAd() { callAdsMethod("showRewardedAd"); }
void kaslikShowInterstitialAd() { callAdsMethod("showInterstitialAd"); }
#endif
