package com.b7anka.hollywoodtracker.Helpers;

import android.content.Context;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class AdsManager implements RewardedVideoAdListener
{
    public interface AfterRewardedVideoListener
    {
        void onRewardedVideoCompleted();
        void onRewardedVideoAdClosed();
        void onRewardedVideoFailedToLoad();
    }

    private SharedPreferencesManager preferencesManager;
    private Context context;
    private AfterRewardedVideoListener afterRewardedVideoListener;
    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd rewardedVideoAd;

    public AdsManager(Context context)
    {
        this.context = context;
        this.preferencesManager = new SharedPreferencesManager(this.context);
    }

    public AdsManager(Context context, AfterRewardedVideoListener afterRewardedVideoListener)
    {
        this(context);
        this.afterRewardedVideoListener = afterRewardedVideoListener;
    }

    public void showBannerAdds(AdView adView)
    {
        if(adView != null)
        {
            adView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
    }

    public void prepareInterstitialAd()
    {
        if(preferencesManager.getLastInterTimeStamp() < (System.currentTimeMillis()-Constants.TWO_MINUTES_IN_MILLIS))
        {
            this.mInterstitialAd = new InterstitialAd(this.context);
            this.mInterstitialAd .setAdUnitId(Constants.INTERSTITIAL_AD_ID);
            this.mInterstitialAd .loadAd(new AdRequest.Builder().build());

            mInterstitialAd.setAdListener(new AdListener() {

                @Override
                public void onAdFailedToLoad(int i)
                {
                    super.onAdFailedToLoad(i);
                }

                @Override
                public void onAdLoaded()
                {
                    super.onAdLoaded();
                    mInterstitialAd.show();
                }

                @Override
                public void onAdClosed()
                {
                    super.onAdClosed();
                }

            });
            preferencesManager.saveLastInterTimeStamp(System.currentTimeMillis());
        }
    }

    public void prepareRewardedVideoAd()
    {
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this.context);
        rewardedVideoAd.setRewardedVideoAdListener(this);
        rewardedVideoAd.loadAd(Constants.REWARDED_VIDEO_AD_ID,
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded()
    {
        rewardedVideoAd.show();
    }

    @Override
    public void onRewardedVideoAdOpened()
    {

    }

    @Override
    public void onRewardedVideoStarted()
    {

    }

    @Override
    public void onRewardedVideoAdClosed()
    {
        if(this.afterRewardedVideoListener != null)afterRewardedVideoListener.onRewardedVideoAdClosed();
    }

    @Override
    public void onRewarded(RewardItem rewardItem)
    {
        rewardedVideoAd.destroy(context);
        if(this.afterRewardedVideoListener != null)afterRewardedVideoListener.onRewardedVideoCompleted();
    }

    @Override
    public void onRewardedVideoAdLeftApplication()
    {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i)
    {
        if(this.afterRewardedVideoListener != null)afterRewardedVideoListener.onRewardedVideoFailedToLoad();
    }

    @Override
    public void onRewardedVideoCompleted()
    {

    }
}
