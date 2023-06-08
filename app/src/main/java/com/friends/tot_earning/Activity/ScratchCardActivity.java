package com.friends.tot_earning.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anupkumarpanwar.scratchview.ScratchView;
import com.friends.tot_earning.Model.User;
import com.friends.tot_earning.Model.WatchVideoModel;
import com.friends.tot_earning.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScratchCardActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    Dialog loadingDialog, scratchCardDialog,errorDialog;
    TextView coinsText,errorText;
    AppCompatButton getButton,okButton;
    ScratchView scratchView;
    RewardedAd mRewardedAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_card);
        firebaseFirestore=FirebaseFirestore.getInstance();

        ////////////////loading dialog
        loadingDialog = new Dialog(ScratchCardActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ////////////////loading dialog

        ////////////////Lucky box dialog
        scratchCardDialog = new Dialog(ScratchCardActivity.this);
        scratchCardDialog.setContentView(R.layout.dialog_scratch_card);
        scratchCardDialog.setCancelable(true);
        scratchCardDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        scratchCardDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getButton = scratchCardDialog.findViewById(R.id.get_button);
        coinsText = scratchCardDialog.findViewById(R.id.coins_text);
        ////////////////Lucky box dialog

        ////////////////Error dialog
        errorDialog = new Dialog(ScratchCardActivity.this);
        errorDialog.setContentView(R.layout.error_dialog);
        errorDialog.setCancelable(true);
        errorDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        errorDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        okButton = errorDialog.findViewById(R.id.button);
        errorText = errorDialog.findViewById(R.id.error_text);
        ////////////////Error dialog

        scratchView = findViewById(R.id.scratch_view);
        watchVideos();

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(ScratchCardActivity.this, "ca-app-pub-1356475100439807/1261887227",
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
                        Activity activityContext = ScratchCardActivity.this;
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

    private void watchVideos() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore.collection("USERS")
                .document(currentUser.getUid())
                .collection("USER_DATA")
                .document("SCRATCH_CARD")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String spinLimit = String.valueOf(task.getResult().getLong("limit"));

                            if (Long.parseLong(spinLimit) == 0) {
                                scratchView.setClickable(false);
                                errorDialog.show();

                                firebaseFirestore.collection("USERS")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .collection("USER_DATA")
                                        .document("SCRATCH_CARD")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    String spinLimit1 = String.valueOf(task.getResult().getLong("limit"));
                                                    scratchView.setClickable(false);

                                                    Map<String, Object> update = new HashMap<>();
                                                    update.put("limit", System.currentTimeMillis() + 86400000);
                                                    firebaseFirestore.collection("USERS")
                                                            .document(FirebaseAuth.getInstance().getUid())
                                                            .collection("USER_DATA")
                                                            .document("SCRATCH_CARD")
                                                            .update(update)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {

                                                                    scratchView.setClickable(false);
                                                                }
                                                            });
                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(ScratchCardActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                            } else if (Long.parseLong(spinLimit) == 1) {
                                scratchView.setClickable(true);

                                scratchView.setRevealListener(new ScratchView.IRevealListener() {
                                    @Override
                                    public void onRevealed(ScratchView scratchView) {
                                        getCoin();

                                        Map<String, Object> count = new HashMap<>();
                                        count.put("limit", Long.valueOf(spinLimit) - 1);
                                        firebaseFirestore.collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .collection("USER_DATA")
                                                .document("SCRATCH_CARD")
                                                .update(count)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            firebaseFirestore.collection("USERS")
                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                    .collection("USER_DATA")
                                                                    .document("SCRATCH_CARD")
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                String spinLimit1 = String.valueOf(task.getResult().getLong("limit"));
                                                                                scratchView.setClickable(false);

                                                                                Map<String, Object> update = new HashMap<>();
                                                                                update.put("limit", System.currentTimeMillis() + 86400000);
                                                                                firebaseFirestore.collection("USERS")
                                                                                        .document(FirebaseAuth.getInstance().getUid())
                                                                                        .collection("USER_DATA")
                                                                                        .document("SCRATCH_CARD")
                                                                                        .update(update)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {

                                                                                                scratchView.setClickable(false);
                                                                                            }
                                                                                        });
                                                                            } else {
                                                                                String error = task.getException().getMessage();
                                                                                Toast.makeText(ScratchCardActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(ScratchCardActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onRevealPercentChangedListener(ScratchView scratchView, float percent) {
                                        if (percent>=0.5) {
                                            Log.d("Reveal Percentage", "onRevealPercentChangedListener: " + String.valueOf(percent));
                                        }
                                    }
                                });


                            } else if (Long.parseLong(spinLimit) < System.currentTimeMillis() && Long.parseLong(spinLimit) > 1000) {

                                firebaseFirestore.collection("EARNING")
                                        .document("SCRATCH_CARD")
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        WatchVideoModel model = documentSnapshot.toObject(WatchVideoModel.class);

                                        Map<String, Object> limit = new HashMap<>();
                                        limit.put("limit", model.getLimit());
                                        firebaseFirestore.collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .collection("USER_DATA")
                                                .document("SCRATCH_CARD")
                                                .update(limit)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            firebaseFirestore.collection("USERS")
                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                    .collection("USER_DATA")
                                                                    .document("SCRATCH_CARD")
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                String spinLimit2 = String.valueOf(task.getResult().getLong("limit"));
                                                                                scratchView.setClickable(true);

                                                                                if (Long.parseLong(spinLimit2) == 1) {

                                                                                    scratchView.setClickable(true);

                                                                                    scratchView.setRevealListener(new ScratchView.IRevealListener() {
                                                                                        @Override
                                                                                        public void onRevealed(ScratchView scratchView) {
                                                                                            getCoin();

                                                                                            Map<String, Object> count = new HashMap<>();
                                                                                            count.put("limit", Long.valueOf(spinLimit2) - 1);
                                                                                            firebaseFirestore.collection("USERS")
                                                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                                                    .collection("USER_DATA")
                                                                                                    .document("SCRATCH_CARD")
                                                                                                    .update(count)
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                firebaseFirestore.collection("USERS")
                                                                                                                        .document(FirebaseAuth.getInstance().getUid())
                                                                                                                        .collection("USER_DATA")
                                                                                                                        .document("SCRATCH_CARD")
                                                                                                                        .get()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                    String spinLimit1 = String.valueOf(task.getResult().getLong("limit"));
                                                                                                                                    scratchView.setClickable(false);

                                                                                                                                    Map<String, Object> update = new HashMap<>();
                                                                                                                                    update.put("limit", System.currentTimeMillis() + 86400000);
                                                                                                                                    firebaseFirestore.collection("USERS")
                                                                                                                                            .document(FirebaseAuth.getInstance().getUid())
                                                                                                                                            .collection("USER_DATA")
                                                                                                                                            .document("SCRATCH_CARD")
                                                                                                                                            .update(update)
                                                                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                @Override
                                                                                                                                                public void onSuccess(Void unused) {
                                                                                                                                                    scratchView.setClickable(false);
                                                                                                                                                }
                                                                                                                                            });
                                                                                                                                } else {
                                                                                                                                    String error = task.getException().getMessage();
                                                                                                                                    Toast.makeText(ScratchCardActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                                                                }
                                                                                                                            }
                                                                                                                        });
                                                                                                            } else {
                                                                                                                String error = task.getException().getMessage();
                                                                                                                Toast.makeText(ScratchCardActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }

                                                                                        @Override
                                                                                        public void onRevealPercentChangedListener(ScratchView scratchView, float percent) {
                                                                                            if (percent>=0.5) {
                                                                                                Log.d("Reveal Percentage", "onRevealPercentChangedListener: " + String.valueOf(percent));
                                                                                            }
                                                                                        }
                                                                                    });

                                                                                } else {
                                                                                    scratchView.setRevealListener(new ScratchView.IRevealListener() {
                                                                                        @Override
                                                                                        public void onRevealed(ScratchView scratchView) {
                                                                                            getCoin();

                                                                                            Map<String, Object> count = new HashMap<>();
                                                                                            count.put("limit", Long.valueOf(spinLimit2) - 1);
                                                                                            firebaseFirestore.collection("USERS")
                                                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                                                    .collection("USER_DATA")
                                                                                                    .document("SCRATCH_CARD")
                                                                                                    .update(count)
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                firebaseFirestore.collection("USERS")
                                                                                                                        .document(FirebaseAuth.getInstance().getUid())
                                                                                                                        .collection("USER_DATA")
                                                                                                                        .document("SCRATCH_CARD")
                                                                                                                        .get()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                    String spinLimit1 = String.valueOf(task.getResult().getLong("limit"));
                                                                                                                                    scratchView.setClickable(false);
                                                                                                                                } else {
                                                                                                                                    String error = task.getException().getMessage();
                                                                                                                                    Toast.makeText(ScratchCardActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                                                                }
                                                                                                                            }
                                                                                                                        });
                                                                                                            } else {
                                                                                                                String error = task.getException().getMessage();
                                                                                                                Toast.makeText(ScratchCardActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }

                                                                                        @Override
                                                                                        public void onRevealPercentChangedListener(ScratchView scratchView, float percent) {
                                                                                            if (percent>=0.5) {
                                                                                                Log.d("Reveal Percentage", "onRevealPercentChangedListener: " + String.valueOf(percent));
                                                                                            }
                                                                                        }
                                                                                    });

                                                                                }
                                                                            } else {
                                                                                String error = task.getException().getMessage();
                                                                                Toast.makeText(ScratchCardActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(ScratchCardActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });

                            } else if (Long.parseLong(spinLimit) <= 100) {
                                scratchView.setClickable(true);

                                scratchView.setRevealListener(new ScratchView.IRevealListener() {
                                    @Override
                                    public void onRevealed(ScratchView scratchView) {
                                        Map<String, Object> count = new HashMap<>();
                                        count.put("limit", Long.valueOf(spinLimit) - 1);
                                        firebaseFirestore.collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .collection("USER_DATA")
                                                .document("SCRATCH_CARD")
                                                .update(count)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            firebaseFirestore.collection("USERS")
                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                    .collection("USER_DATA")
                                                                    .document("SCRATCH_CARD")
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                String spinLimit1 = String.valueOf(task.getResult().getLong("limit"));
                                                                                scratchView.setClickable(false);
                                                                            } else {
                                                                                String error = task.getException().getMessage();
                                                                                Toast.makeText(ScratchCardActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(ScratchCardActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onRevealPercentChangedListener(ScratchView scratchView, float percent) {
                                        if (percent>=0.5) {
                                            Log.d("Reveal Percentage", "onRevealPercentChangedListener: " + String.valueOf(percent));
                                        }
                                    }
                                });

                            } else if (Long.parseLong(spinLimit) > 1000) {
                                scratchView.setClickable(true);

                                scratchView.setRevealListener(new ScratchView.IRevealListener() {
                                    @Override
                                    public void onRevealed(ScratchView scratchView) {
                                        errorDialog.show();

                                        errorText.setText("Oops! Scratch card not be available. Try for next day");
                                        okButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                errorDialog.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onRevealPercentChangedListener(ScratchView scratchView, float percent) {
                                        if (percent>=0.5) {
                                            Log.d("Reveal Percentage", "onRevealPercentChangedListener: " + String.valueOf(percent));
                                        }
                                    }
                                });
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(ScratchCardActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getCoin() {

        firebaseFirestore.collection("EARNING")
                .document("SCRATCH_CARD")
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                WatchVideoModel model = documentSnapshot.toObject(WatchVideoModel.class);

                scratchCardDialog.show();
                Long randomNumber = Long.valueOf(model.getCoins());
                coinsText.setText("You got " + randomNumber + " coins in Scratch card");

                getButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scratchCardDialog.dismiss();
                        startActivity(new Intent(ScratchCardActivity.this,DashBoardActivity.class));


                        firebaseFirestore.collection("USERS")
                                .document(FirebaseAuth.getInstance().getUid())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        User user = documentSnapshot.toObject(User.class);

                                        Map<String, Object> time = new HashMap<>();
                                        time.put("coins", user.getCoins() + randomNumber);
                                        time.put("totalCoins", user.getTotalCoins() + randomNumber);


                                        firebaseFirestore.collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .update(time)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            String random = UUID.randomUUID().toString().substring(0, 28);
                                                            Map<String, Object> inviteRecord = new HashMap<>();
                                                            inviteRecord.put("date", System.currentTimeMillis());
                                                            inviteRecord.put("coins", FieldValue.increment(randomNumber));
                                                            inviteRecord.put("plusCoins", FieldValue.increment(randomNumber));
                                                            inviteRecord.put("source", "Scratch Card");

                                                            firebaseFirestore.collection("USERS")
                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                    .collection("RECORDS")
                                                                    .document(random)
                                                                    .set(inviteRecord)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            scratchCardDialog.dismiss();
                                                                            startActivity(new Intent(ScratchCardActivity.this, DashBoardActivity.class));
                                                                            Toast.makeText(ScratchCardActivity.this, randomNumber + " Coins added to your account", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });


                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(ScratchCardActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                        scratchCardDialog.dismiss();
                                                    }
                                                });
                                    }
                                });
                    }
                });

            }
        });
    }
}