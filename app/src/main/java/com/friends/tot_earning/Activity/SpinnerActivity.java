package com.friends.tot_earning.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.friends.tot_earning.Model.SpinWheelModel;
import com.friends.tot_earning.SpinWheel.LuckyWheelView;
import com.friends.tot_earning.SpinWheel.model.LuckyItem;
import com.friends.tot_earning.R;
import com.friends.tot_earning.databinding.ActivitySpinnerBinding;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class SpinnerActivity extends AppCompatActivity {

    ActivitySpinnerBinding binding;
    FirebaseFirestore database;
    SpinWheelModel model;
    String spinLimit;
    FirebaseUser currentUser;
    Dialog loadingDialog, errorDialog;
    AppCompatButton okButton;

    RewardedAd mRewardedAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpinnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        List<LuckyItem> data = new ArrayList<>();


        ////////////////loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ////////////////loading dialog

        ////////////////loading dialog
        errorDialog = new Dialog(this);
        errorDialog.setContentView(R.layout.error_dialog);
        errorDialog.setCancelable(true);
        errorDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        errorDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        okButton = errorDialog.findViewById(R.id.button);
        ////////////////loading dialog

        database.collection("EARNING")
                .document("SPIN_WHEEL")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        model = documentSnapshot.toObject(SpinWheelModel.class);

                        LuckyItem luckyItem1 = new LuckyItem();
                        luckyItem1.topText = String.valueOf(model.getCase_1());
                        luckyItem1.secondaryText = "COINS";
                        luckyItem1.textColor = Color.parseColor(String.valueOf(model.getCase_1_text_color()));
                        luckyItem1.color = Color.parseColor(String.valueOf(model.getCase_1_background()));
                        data.add(luckyItem1);

                        LuckyItem luckyItem2 = new LuckyItem();
                        luckyItem2.topText = String.valueOf(model.getCase_2());
                        luckyItem2.secondaryText = "COINS";
                        luckyItem2.color = Color.parseColor(String.valueOf(model.getCase_2_background()));
                        luckyItem2.textColor = Color.parseColor(String.valueOf(model.getCase_2_text_color()));
                        data.add(luckyItem2);

                        LuckyItem luckyItem3 = new LuckyItem();
                        luckyItem3.topText = String.valueOf(model.getCase_3());
                        luckyItem3.secondaryText = "COINS";
                        luckyItem3.textColor = Color.parseColor(String.valueOf(model.getCase_3_text_color()));
                        luckyItem3.color = Color.parseColor(String.valueOf(model.getCase_3_background()));
                        data.add(luckyItem3);

                        LuckyItem luckyItem4 = new LuckyItem();
                        luckyItem4.topText = String.valueOf(model.getCase_4());
                        luckyItem4.secondaryText = "COINS";
                        luckyItem4.color = Color.parseColor(String.valueOf(model.getCase_4_background()));
                        luckyItem4.textColor = Color.parseColor(String.valueOf(model.getCase_4_text_color()));
                        data.add(luckyItem4);

                        LuckyItem luckyItem5 = new LuckyItem();
                        luckyItem5.topText = String.valueOf(model.getCase_5());
                        luckyItem5.secondaryText = "COINS";
                        luckyItem5.textColor = Color.parseColor(String.valueOf(model.getCase_5_text_color()));
                        luckyItem5.color = Color.parseColor(String.valueOf(model.getCase_5_background()));
                        data.add(luckyItem5);

                        LuckyItem luckyItem6 = new LuckyItem();
                        luckyItem6.topText = String.valueOf(model.getCase_6());
                        luckyItem6.secondaryText = "COINS";
                        luckyItem6.color = Color.parseColor(String.valueOf(model.getCase_6_background()));
                        luckyItem6.textColor = Color.parseColor(String.valueOf(model.getCase_6_text_color()));
                        data.add(luckyItem6);

                        LuckyItem luckyItem7 = new LuckyItem();
                        luckyItem7.topText = String.valueOf(model.getCase_7());
                        luckyItem7.secondaryText = "COINS";
                        luckyItem7.textColor = Color.parseColor(String.valueOf(model.getCase_7_text_color()));
                        luckyItem7.color = Color.parseColor(String.valueOf(model.getCase_7_background()));
                        data.add(luckyItem7);

                        LuckyItem luckyItem8 = new LuckyItem();
                        luckyItem8.topText = String.valueOf(model.getCase_8());
                        luckyItem8.secondaryText = "COINS";
                        luckyItem8.color = Color.parseColor(String.valueOf(model.getCase_8_background()));
                        luckyItem8.textColor = Color.parseColor(String.valueOf(model.getCase_8_text_color()));
                        data.add(luckyItem8);


                        binding.wheelview.setData(data);
                        binding.wheelview.setRound(5);

                        binding.spinBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Random r = new Random();
                                int randomNumber = r.nextInt(8);

                                binding.wheelview.startLuckyWheelWithTargetIndex(randomNumber);

                            }
                        });

                        binding.wheelview.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
                            @Override
                            public void LuckyRoundItemSelected(int index) {
                                updateCash(index);

                            }
                        });
                    }

                    void updateCash(int index) {
                        long cash = 0;
                        switch (index) {
                            case 0:
                                cash = model.getCase_1();
                                break;
                            case 1:
                                cash = model.getCase_2();
                                break;
                            case 2:
                                cash = model.getCase_3();
                                break;
                            case 3:
                                cash = model.getCase_4();
                                break;
                            case 4:
                                cash = model.getCase_5();
                                break;
                            case 5:
                                cash = model.getCase_6();
                                break;
                            case 6:
                                cash = model.getCase_7();
                                break;
                            case 7:
                                cash = model.getCase_8();
                                break;
                        }

                        String random = UUID.randomUUID().toString().substring(0, 28);
                        Map<String, Object> inviteRecord = new HashMap<>();
                        inviteRecord.put("date", System.currentTimeMillis());
                        inviteRecord.put("coins", FieldValue.increment(cash));
                        inviteRecord.put("plusCoins", FieldValue.increment(cash));
                        inviteRecord.put("source", "Spin Wheel");

                        Map<String, Object> update = new HashMap<>();
                        update.put("coins", FieldValue.increment(cash));
                        update.put("totalCoins", FieldValue.increment(cash));
                        database
                                .collection("USERS")
                                .document(FirebaseAuth.getInstance().getUid())
                                .update(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                database.collection("USERS")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .collection("RECORDS")
                                        .document(random)
                                        .set(inviteRecord)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(SpinnerActivity.this, "Coins added in your account.", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SpinnerActivity.this,DashBoardActivity.class));
                                            }
                                        });

                            }
                        });

                    }
                });

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(SpinnerActivity.this, "ca-app-pub-1356475100439807/1261887227",
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
                        Activity activityContext = SpinnerActivity.this;
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
    protected void onStart() {
        super.onStart();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database.collection("USERS")
                .document(currentUser.getUid())
                .collection("USER_DATA")
                .document("SPIN_WHEEL")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String spinLimit = String.valueOf(task.getResult().getLong("limit"));


                            if (Long.parseLong(spinLimit) == 0) {
                                binding.spinCount.setText("0");
                                binding.spinBtn.setClickable(false);
                                errorDialog.show();

                                database.collection("USERS")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .collection("USER_DATA")
                                        .document("SPIN_WHEEL")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    String spinLimit1 = String.valueOf(task.getResult().getLong("limit"));
                                                    binding.spinCount.setText(spinLimit1);
                                                    binding.spinBtn.setClickable(false);

                                                    Map<String, Object> update = new HashMap<>();
                                                    update.put("limit", System.currentTimeMillis() + 86400000);
                                                    database.collection("USERS")
                                                            .document(FirebaseAuth.getInstance().getUid())
                                                            .collection("USER_DATA")
                                                            .document("SPIN_WHEEL")
                                                            .update(update)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    binding.spinCount.setText("0");
                                                                    binding.spinBtn.setClickable(false);
                                                                }
                                                            });
                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(SpinnerActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                            } else if (Long.parseLong(spinLimit) == 1) {
                                binding.spinCount.setText(spinLimit);
                                binding.spinBtn.setClickable(true);

                                binding.spinBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Random r = new Random();
                                        int randomNumber = r.nextInt(8);

                                        binding.wheelview.startLuckyWheelWithTargetIndex(randomNumber);

                                        Map<String, Object> count = new HashMap<>();
                                        count.put("limit", Long.valueOf(spinLimit) - 1);
                                        database.collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .collection("USER_DATA")
                                                .document("SPIN_WHEEL")
                                                .update(count)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            database.collection("USERS")
                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                    .collection("USER_DATA")
                                                                    .document("SPIN_WHEEL")
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                String spinLimit1 = String.valueOf(task.getResult().getLong("limit"));
                                                                                binding.spinCount.setText(spinLimit1);
                                                                                binding.spinBtn.setClickable(false);

                                                                                Map<String, Object> update = new HashMap<>();
                                                                                update.put("limit", System.currentTimeMillis() + 86400000);
                                                                                database.collection("USERS")
                                                                                        .document(FirebaseAuth.getInstance().getUid())
                                                                                        .collection("USER_DATA")
                                                                                        .document("SPIN_WHEEL")
                                                                                        .update(update)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {
                                                                                                binding.spinCount.setText("0");
                                                                                                binding.spinBtn.setClickable(false);
                                                                                            }
                                                                                        });
                                                                            } else {
                                                                                String error = task.getException().getMessage();
                                                                                Toast.makeText(SpinnerActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(SpinnerActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });


                            } else if (Long.parseLong(spinLimit) < System.currentTimeMillis() && Long.parseLong(spinLimit) > 1000) {

                                database.collection("EARNING")
                                        .document("SPIN_WHEEL")
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        SpinWheelModel model = documentSnapshot.toObject(SpinWheelModel.class);

                                        Map<String, Object> limit = new HashMap<>();
                                        limit.put("limit", model.getLimit());
                                        database.collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .collection("USER_DATA")
                                                .document("SPIN_WHEEL")
                                                .update(limit)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            database.collection("USERS")
                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                    .collection("USER_DATA")
                                                                    .document("SPIN_WHEEL")
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                String spinLimit2 = String.valueOf(task.getResult().getLong("limit"));
                                                                                binding.spinCount.setText(spinLimit2);
                                                                                binding.spinBtn.setClickable(true);

                                                                                binding.spinBtn.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        Random r = new Random();
                                                                                        int randomNumber = r.nextInt(8);

                                                                                        binding.wheelview.startLuckyWheelWithTargetIndex(randomNumber);

                                                                                        if (Long.parseLong(spinLimit2) == 1) {

                                                                                            Map<String, Object> count = new HashMap<>();
                                                                                            count.put("limit", Long.valueOf(spinLimit2) - 1);
                                                                                            database.collection("USERS")
                                                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                                                    .collection("USER_DATA")
                                                                                                    .document("SPIN_WHEEL")
                                                                                                    .update(count)
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                database.collection("USERS")
                                                                                                                        .document(FirebaseAuth.getInstance().getUid())
                                                                                                                        .collection("USER_DATA")
                                                                                                                        .document("SPIN_WHEEL")
                                                                                                                        .get()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                    String spinLimit1 = String.valueOf(task.getResult().getLong("limit"));
                                                                                                                                    binding.spinCount.setText(spinLimit1);
                                                                                                                                    binding.spinBtn.setClickable(false);

                                                                                                                                    Map<String, Object> update = new HashMap<>();
                                                                                                                                    update.put("limit", System.currentTimeMillis() + 86400000);
                                                                                                                                    database.collection("USERS")
                                                                                                                                            .document(FirebaseAuth.getInstance().getUid())
                                                                                                                                            .collection("USER_DATA")
                                                                                                                                            .document("SPIN_WHEEL")
                                                                                                                                            .update(update)
                                                                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                @Override
                                                                                                                                                public void onSuccess(Void unused) {
                                                                                                                                                    binding.spinCount.setText("0");
                                                                                                                                                    binding.spinBtn.setClickable(false);
                                                                                                                                                }
                                                                                                                                            });
                                                                                                                                } else {
                                                                                                                                    String error = task.getException().getMessage();
                                                                                                                                    Toast.makeText(SpinnerActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                                                                }
                                                                                                                            }
                                                                                                                        });
                                                                                                            } else {
                                                                                                                String error = task.getException().getMessage();
                                                                                                                Toast.makeText(SpinnerActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });

                                                                                        } else {
                                                                                            Map<String, Object> count = new HashMap<>();
                                                                                            count.put("limit", Long.valueOf(spinLimit2) - 1);
                                                                                            database.collection("USERS")
                                                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                                                    .collection("USER_DATA")
                                                                                                    .document("SPIN_WHEEL")
                                                                                                    .update(count)
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                database.collection("USERS")
                                                                                                                        .document(FirebaseAuth.getInstance().getUid())
                                                                                                                        .collection("USER_DATA")
                                                                                                                        .document("SPIN_WHEEL")
                                                                                                                        .get()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                    String spinLimit1 = String.valueOf(task.getResult().getLong("limit"));
                                                                                                                                    binding.spinCount.setText(spinLimit1);
                                                                                                                                    binding.spinBtn.setClickable(false);
                                                                                                                                } else {
                                                                                                                                    String error = task.getException().getMessage();
                                                                                                                                    Toast.makeText(SpinnerActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                                                                }
                                                                                                                            }
                                                                                                                        });
                                                                                                            } else {
                                                                                                                String error = task.getException().getMessage();
                                                                                                                Toast.makeText(SpinnerActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                });
                                                                            } else {
                                                                                String error = task.getException().getMessage();
                                                                                Toast.makeText(SpinnerActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(SpinnerActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });

                            } else if (Long.parseLong(spinLimit) <= 100) {
                                binding.spinCount.setText(spinLimit);
                                binding.spinBtn.setClickable(true);

                                binding.spinBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Random r = new Random();
                                        int randomNumber = r.nextInt(8);

                                        binding.wheelview.startLuckyWheelWithTargetIndex(randomNumber);

                                        Map<String, Object> count = new HashMap<>();
                                        count.put("limit", Long.valueOf(spinLimit) - 1);
                                        database.collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .collection("USER_DATA")
                                                .document("SPIN_WHEEL")
                                                .update(count)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            database.collection("USERS")
                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                    .collection("USER_DATA")
                                                                    .document("SPIN_WHEEL")
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                String spinLimit1 = String.valueOf(task.getResult().getLong("limit"));
                                                                                binding.spinCount.setText(spinLimit1);
                                                                                binding.spinBtn.setClickable(false);
                                                                            } else {
                                                                                String error = task.getException().getMessage();
                                                                                Toast.makeText(SpinnerActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(SpinnerActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });

                            } else if (Long.parseLong(spinLimit) > 1000) {
                                binding.spinBtn.setClickable(false);
                                binding.spinCount.setText("0");

                                errorDialog.show();
                                okButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        errorDialog.dismiss();
                                    }
                                });
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(SpinnerActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}