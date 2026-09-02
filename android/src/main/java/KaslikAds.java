package com.kaslikservices.kaslikkartrush;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;

/** Kaslik Kart Rush ad coordinator. Test ads are enabled for debug builds. */
public final class KaslikAds {
    private static final String TAG = "KaslikAds";
    private static final String UNITY_GAME_ID = "800365837";
    private static final String UNITY_INTERSTITIAL = "Interstitial_Android";
    private static final String UNITY_REWARDED = "Rewarded_Android";
    private static final String META_BANNER = "1849392019758918_1849398006424986";
    private static boolean initialized = false;
    private static boolean rewardedReady = false;
    private static boolean interstitialReady = false;
    private static FrameLayout metaContainer;

    private KaslikAds() {}

    public static void initialize(final Activity activity) {
        if (initialized) return;
        initialized = true;
        final boolean testMode = isDebug(activity);
        try {
            UnityAds.initialize(activity.getApplicationContext(), UNITY_GAME_ID, testMode,
                new IUnityAdsInitializationListener() {
                    @Override public void onInitializationComplete() {
                        loadRewarded();
                        loadInterstitial();
                    }
                    @Override public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                        Log.e(TAG, "Unity Ads init failed: " + error + " " + message);
                    }
                });
        } catch (Throwable t) {
            Log.e(TAG, "Unity Ads unavailable", t);
        }
        // Meta is optional: use reflection so the game still builds/runs if Meta changes its SDK.
        if (!testMode) addMetaBanner(activity);
    }

    private static boolean isDebug(Activity a) {
        return (a.getApplicationInfo().flags & android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    private static void loadRewarded() {
        try {
            UnityAds.load(UNITY_REWARDED, new IUnityAdsLoadListener() {
                @Override public void onUnityAdsAdLoaded(String placementId) { rewardedReady = true; }
                @Override public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                    rewardedReady = false; Log.w(TAG, "Rewarded load failed: " + error + " " + message);
                }
            });
        } catch (Throwable t) { Log.e(TAG, "Rewarded load error", t); }
    }

    private static void loadInterstitial() {
        try {
            UnityAds.load(UNITY_INTERSTITIAL, new IUnityAdsLoadListener() {
                @Override public void onUnityAdsAdLoaded(String placementId) { interstitialReady = true; }
                @Override public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                    interstitialReady = false; Log.w(TAG, "Interstitial load failed: " + error + " " + message);
                }
            });
        } catch (Throwable t) { Log.e(TAG, "Interstitial load error", t); }
    }

    public static void showRewarded(final Activity activity) {
        activity.runOnUiThread(() -> {
            if (!rewardedReady) {
                Toast.makeText(activity, "Bonus ad is loading. Try again in a moment.", Toast.LENGTH_SHORT).show();
                loadRewarded(); return;
            }
            rewardedReady = false;
            try {
                UnityAds.show(activity, UNITY_REWARDED, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                    @Override public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                        Toast.makeText(activity, "Ad unavailable right now.", Toast.LENGTH_SHORT).show(); loadRewarded();
                    }
                    @Override public void onUnityAdsShowStart(String placementId) {}
                    @Override public void onUnityAdsShowClick(String placementId) {}
                    @Override public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                        if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED)
                            Toast.makeText(activity, "Bonus unlocked! Thanks for supporting Kaslik Kart Rush.", Toast.LENGTH_LONG).show();
                        loadRewarded();
                    }
                });
            } catch (Throwable t) { Log.e(TAG, "Rewarded show error", t); loadRewarded(); }
        });
    }

    public static void showInterstitial(final Activity activity) {
        activity.runOnUiThread(() -> {
            if (!interstitialReady) { loadInterstitial(); return; }
            interstitialReady = false;
            try {
                UnityAds.show(activity, UNITY_INTERSTITIAL, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                    @Override public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) { loadInterstitial(); }
                    @Override public void onUnityAdsShowStart(String placementId) {}
                    @Override public void onUnityAdsShowClick(String placementId) {}
                    @Override public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) { loadInterstitial(); }
                });
            } catch (Throwable t) { Log.e(TAG, "Interstitial show error", t); loadInterstitial(); }
        });
    }

    private static void addMetaBanner(final Activity activity) {
        try {
            Class<?> adsClass = Class.forName("com.facebook.ads.AudienceNetworkAds");
            Object initializer = adsClass.getMethod("initialize", android.content.Context.class).invoke(null, activity.getApplicationContext());
            Class<?> adViewClass = Class.forName("com.facebook.ads.AdView");
            Class<?> adSizeClass = Class.forName("com.facebook.ads.AdSize");
            Object adSize = adSizeClass.getField("BANNER_HEIGHT_50").get(null);
            final Object adView = adViewClass.getConstructor(android.content.Context.class, String.class, adSizeClass)
                .newInstance(activity, META_BANNER, adSize);
            metaContainer = new FrameLayout(activity);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            metaContainer.setLayoutParams(lp);
            activity.addContentView(metaContainer, lp);
            metaContainer.addView((android.view.View)adView);
            adViewClass.getMethod("loadAd").invoke(adView);
        } catch (Throwable t) {
            Log.w(TAG, "Meta banner disabled/unavailable", t);
        }
    }
}
