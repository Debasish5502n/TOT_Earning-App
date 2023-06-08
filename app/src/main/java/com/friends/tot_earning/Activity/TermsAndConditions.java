package com.friends.tot_earning.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.friends.tot_earning.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class TermsAndConditions extends AppCompatActivity {

    RewardedAd mRewardedAd;
    Dialog loadingDialog;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        webView=findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://bajpura.blogspot.com/2022/06/thugs-of-facts-privacy-policy.html");
        ////////////////loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ////////////////loading dialog

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(TermsAndConditions.this, "ca-app-pub-1356475100439807/1261887227",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        super.onAdLoaded(rewardedAd);
                        mRewardedAd = rewardedAd;
                    }
                });

        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }finally{
                    loadingDialog.dismiss();
                    if (mRewardedAd != null) {
                        Activity activityContext = TermsAndConditions.this;
                        activityContext.runOnUiThread(new Runnable() {
                            @Override public void run() {
                                mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                                    @Override
                                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                        // Handle the reward.
                                        int rewardAmount = rewardItem.getAmount();
                                        String rewardType = rewardItem.getType();

                                    }
                                });
                            }
                        });

                    } else {
                        //  Toast.makeText(QuestionsActivity.this, "Not add ads", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        timer.start();
    }
}