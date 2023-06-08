package com.friends.tot_earning.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.friends.tot_earning.Model.LuckyBoxModel;
import com.friends.tot_earning.Model.SpinWheelModel;
import com.friends.tot_earning.Model.User;
import com.friends.tot_earning.R;
import com.friends.tot_earning.databinding.ActivityLuckyBoxBinding;
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
import java.util.Random;
import java.util.UUID;

public class LuckyBoxActivity extends AppCompatActivity {

    ActivityLuckyBoxBinding binding;
    Dialog loadingDialog, errorDialog, luckyBoxDialog;
    AppCompatButton okButton, luckyDialogButton;
    TextView errorText, luckyTextDialog;
    FirebaseFirestore firebaseFirestore;
    LuckyBoxModel model;

    RewardedAd mRewardedAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLuckyBoxBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseFirestore = FirebaseFirestore.getInstance();

        ////////////////loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ////////////////loading dialog

        ////////////////Error dialog
        errorDialog = new Dialog(this);
        errorDialog.setContentView(R.layout.error_dialog);
        errorDialog.setCancelable(true);
        errorDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        errorDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        okButton = errorDialog.findViewById(R.id.button);
        errorText = errorDialog.findViewById(R.id.error_text);
        ////////////////Error dialog

        ////////////////Lucky box dialog
        luckyBoxDialog = new Dialog(this);
        luckyBoxDialog.setContentView(R.layout.dialog_lucky_box);
        luckyBoxDialog.setCancelable(true);
        luckyBoxDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        luckyBoxDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        luckyDialogButton = luckyBoxDialog.findViewById(R.id.lucky_box_button);
        luckyTextDialog = luckyBoxDialog.findViewById(R.id.lucky_box_text);
        ////////////////Lucky box dialog

        firebaseFirestore.collection("EARNING")
                .document("LUCKY_BOX")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        model = documentSnapshot.toObject(LuckyBoxModel.class);
                    }
                });

        final Animation animation = AnimationUtils.loadAnimation(LuckyBoxActivity.this, R.anim.animation_button);

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(LuckyBoxActivity.this, "ca-app-pub-1356475100439807/1261887227",
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
                        Activity activityContext = LuckyBoxActivity.this;
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
                .document("LUCKY_BOX")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String spinLimit = String.valueOf(task.getResult().getLong("limit"));


                            if (Long.parseLong(spinLimit) == 0) {
                                binding.spinCount.setText("0");
                                binding.getButton.setClickable(false);
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
                                                    binding.getButton.setClickable(false);

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
                                                                    binding.getButton.setClickable(false);
                                                                }
                                                            });
                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(LuckyBoxActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                            } else if (Long.parseLong(spinLimit) == 1) {
                                binding.spinCount.setText(spinLimit);
                                binding.getButton.setClickable(true);

                                binding.getButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        binding.getButton.setClickable(false);
                                        getCoin();

                                        Map<String, Object> count = new HashMap<>();
                                        count.put("limit", Long.valueOf(spinLimit) - 1);
                                        database.collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .collection("USER_DATA")
                                                .document("LUCKY_BOX")
                                                .update(count)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            database.collection("USERS")
                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                    .collection("USER_DATA")
                                                                    .document("LUCKY_BOX")
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                String spinLimit1 = String.valueOf(task.getResult().getLong("limit"));
                                                                                binding.spinCount.setText(spinLimit1);
                                                                                binding.getButton.setClickable(false);

                                                                                Map<String, Object> update = new HashMap<>();
                                                                                update.put("limit", System.currentTimeMillis() + 86400000);
                                                                                database.collection("USERS")
                                                                                        .document(FirebaseAuth.getInstance().getUid())
                                                                                        .collection("USER_DATA")
                                                                                        .document("LUCKY_BOX")
                                                                                        .update(update)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {
                                                                                                binding.spinCount.setText("0");
                                                                                                binding.getButton.setClickable(false);
                                                                                            }
                                                                                        });
                                                                            } else {
                                                                                String error = task.getException().getMessage();
                                                                                Toast.makeText(LuckyBoxActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(LuckyBoxActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });


                            } else if (Long.parseLong(spinLimit) < System.currentTimeMillis() && Long.parseLong(spinLimit) > 1000) {

                                database.collection("EARNING")
                                        .document("LUCKY_BOX")
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        SpinWheelModel model = documentSnapshot.toObject(SpinWheelModel.class);

                                        Map<String, Object> limit = new HashMap<>();
                                        limit.put("limit", model.getLimit());
                                        database.collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .collection("USER_DATA")
                                                .document("LUCKY_BOX")
                                                .update(limit)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            database.collection("USERS")
                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                    .collection("USER_DATA")
                                                                    .document("LUCKY_BOX")
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                String spinLimit2 = String.valueOf(task.getResult().getLong("limit"));
                                                                                binding.spinCount.setText(spinLimit2);
                                                                                binding.getButton.setClickable(true);

                                                                                if (Long.parseLong(spinLimit2) ==1){

                                                                                    binding.spinCount.setText(spinLimit2);
                                                                                    binding.getButton.setClickable(true);

                                                                                    binding.getButton.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            binding.getButton.setClickable(false);
                                                                                            getCoin();

                                                                                            Map<String, Object> count = new HashMap<>();
                                                                                            count.put("limit", Long.valueOf(spinLimit2) - 1);
                                                                                            database.collection("USERS")
                                                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                                                    .collection("USER_DATA")
                                                                                                    .document("LUCKY_BOX")
                                                                                                    .update(count)
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                database.collection("USERS")
                                                                                                                        .document(FirebaseAuth.getInstance().getUid())
                                                                                                                        .collection("USER_DATA")
                                                                                                                        .document("LUCKY_BOX")
                                                                                                                        .get()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                    String spinLimit1 = String.valueOf(task.getResult().getLong("limit"));
                                                                                                                                    binding.spinCount.setText(spinLimit1);
                                                                                                                                    binding.getButton.setClickable(false);

                                                                                                                                    Map<String, Object> update = new HashMap<>();
                                                                                                                                    update.put("limit", System.currentTimeMillis() + 86400000);
                                                                                                                                    database.collection("USERS")
                                                                                                                                            .document(FirebaseAuth.getInstance().getUid())
                                                                                                                                            .collection("USER_DATA")
                                                                                                                                            .document("LUCKY_BOX")
                                                                                                                                            .update(update)
                                                                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                @Override
                                                                                                                                                public void onSuccess(Void unused) {
                                                                                                                                                    binding.spinCount.setText("0");
                                                                                                                                                    binding.getButton.setClickable(false);
                                                                                                                                                }
                                                                                                                                            });
                                                                                                                                } else {
                                                                                                                                    String error = task.getException().getMessage();
                                                                                                                                    Toast.makeText(LuckyBoxActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                                                                }
                                                                                                                            }
                                                                                                                        });
                                                                                                            } else {
                                                                                                                String error = task.getException().getMessage();
                                                                                                                Toast.makeText(LuckyBoxActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    });

                                                                                }else {
                                                                                    binding.getButton.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            binding.getButton.setClickable(false);
                                                                                            getCoin();

                                                                                            Map<String, Object> count = new HashMap<>();
                                                                                            count.put("limit", Long.valueOf(spinLimit2) - 1);
                                                                                            database.collection("USERS")
                                                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                                                    .collection("USER_DATA")
                                                                                                    .document("LUCKY_BOX")
                                                                                                    .update(count)
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                database.collection("USERS")
                                                                                                                        .document(FirebaseAuth.getInstance().getUid())
                                                                                                                        .collection("USER_DATA")
                                                                                                                        .document("LUCKY_BOX")
                                                                                                                        .get()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                    String spinLimit1 = String.valueOf(task.getResult().getLong("limit"));
                                                                                                                                    binding.spinCount.setText(spinLimit1);
                                                                                                                                    binding.getButton.setClickable(false);
                                                                                                                                } else {
                                                                                                                                    String error = task.getException().getMessage();
                                                                                                                                    Toast.makeText(LuckyBoxActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                                                                }
                                                                                                                            }
                                                                                                                        });
                                                                                                            } else {
                                                                                                                String error = task.getException().getMessage();
                                                                                                                Toast.makeText(LuckyBoxActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    });
                                                                                }
                                                                            } else {
                                                                                String error = task.getException().getMessage();
                                                                                Toast.makeText(LuckyBoxActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(LuckyBoxActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });

                            } else if (Long.parseLong(spinLimit) <= 100 && Long.parseLong(spinLimit) >= 2) {
                                binding.spinCount.setText(spinLimit);
                                binding.getButton.setClickable(true);

                                binding.getButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        binding.getButton.setClickable(false);
                                        getCoin();

                                        Map<String, Object> count = new HashMap<>();
                                        count.put("limit", Long.valueOf(spinLimit) - 1);
                                        database.collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .collection("USER_DATA")
                                                .document("LUCKY_BOX")
                                                .update(count)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            database.collection("USERS")
                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                    .collection("USER_DATA")
                                                                    .document("LUCKY_BOX")
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                String spinLimit1 = String.valueOf(task.getResult().getLong("limit"));
                                                                                binding.spinCount.setText(spinLimit1);
                                                                                binding.getButton.setClickable(false);
                                                                            } else {
                                                                                String error = task.getException().getMessage();
                                                                                Toast.makeText(LuckyBoxActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(LuckyBoxActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });

                            } else if (Long.parseLong(spinLimit) > 1000) {
                                binding.getButton.setClickable(false);
                                binding.spinCount.setText("0");

                                errorDialog.show();

                                errorText.setText("Oops! Reward not available. Try for next day");
                                okButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        errorDialog.dismiss();
                                        startActivity(new Intent(LuckyBoxActivity.this, DashBoardActivity.class));
                                    }
                                });
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(LuckyBoxActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getCoin() {
        binding.luckyBoxClose.setVisibility(View.INVISIBLE);
        binding.luckyBox.setVisibility(View.VISIBLE);

        luckyBoxDialog.show();
        Random r = new Random();
        Long randomNumber = Long.valueOf(r.nextInt(50));
        luckyTextDialog.setText("You got " + randomNumber + " coins in lucky box");

        luckyDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                luckyBoxDialog.dismiss();
                startActivity(new Intent(LuckyBoxActivity.this,DashBoardActivity.class));


                firebaseFirestore.collection("USERS")
                        .document(FirebaseAuth.getInstance().getUid())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user = documentSnapshot.toObject(User.class);

                                Map<String, Object> time = new HashMap<>();
                                time.put("coins",user.getCoins() +randomNumber );
                                time.put("totalCoins", user.getTotalCoins() +randomNumber );


                                firebaseFirestore.collection("USERS")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .update(time)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                luckyBoxDialog.dismiss();
                                                if (task.isSuccessful()) {
                                                    String random = UUID.randomUUID().toString().substring(0, 28);
                                                    Map<String, Object> inviteRecord = new HashMap<>();
                                                    inviteRecord.put("date", System.currentTimeMillis());
                                                    inviteRecord.put("coins", FieldValue.increment(randomNumber));
                                                    inviteRecord.put("plusCoins", FieldValue.increment(randomNumber));
                                                    inviteRecord.put("source", "Lucky Box");

                                                    firebaseFirestore.collection("USERS")
                                                            .document(FirebaseAuth.getInstance().getUid())
                                                            .collection("RECORDS")
                                                            .document(random)
                                                            .set(inviteRecord)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    luckyBoxDialog.dismiss();
                                                                    Toast.makeText(LuckyBoxActivity.this, randomNumber + " Coins added to your account", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });


                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(LuckyBoxActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                                luckyBoxDialog.dismiss();
                                            }
                                        });
                            }
                        });
            }
        });


    }
}