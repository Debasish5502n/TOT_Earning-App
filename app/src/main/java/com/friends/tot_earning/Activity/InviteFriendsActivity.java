package com.friends.tot_earning.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.friends.tot_earning.BuildConfig;
import com.friends.tot_earning.DataBase.DbQueries;
import com.friends.tot_earning.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class InviteFriendsActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView referCode, referCodeText;
    AppCompatButton inviteFriends;
    FirebaseFirestore database;

    RewardedAd mRewardedAd;
    Dialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);
        referCode = findViewById(R.id.refer_code);
        referCodeText = findViewById(R.id.invitation_text);
        inviteFriends = findViewById(R.id.button2);
        database = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Invite Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        ////////////////loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ////////////////loading dialog

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(InviteFriendsActivity.this, "ca-app-pub-1356475100439807/1261887227",
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

        database.collection("EARNING")
                .document("INVITE_BONUS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String spinLimit1 = String.valueOf(task.getResult().getLong("coins"));
                            referCodeText.setText("Invitation bonus "+spinLimit1+" coins will be added to both the accounts");
                        }
                    }
                });

        referCode.setText(DbQueries.uid);
        referCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", referCode.getText().toString());
                clipboard.setPrimaryClip(clip);

                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(400);

                Toast.makeText(InviteFriendsActivity.this, "Refer code copied", Toast.LENGTH_SHORT).show();
            }
        });

        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String sharesms = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n"
                        +"Your refer code is- "+ "\n" +referCode.getText().toString()
                        + "\n\n" +"Note fake users will be not accepted otherwise you added fake user then our team will be suspended your account.";
                intent.putExtra(Intent.EXTRA_TEXT, sharesms);
                startActivity(Intent.createChooser(intent, "Share by"));
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
                        Activity activityContext = InviteFriendsActivity.this;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}