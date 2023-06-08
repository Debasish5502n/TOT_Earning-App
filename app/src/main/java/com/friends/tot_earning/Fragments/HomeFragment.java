package com.friends.tot_earning.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.friends.tot_earning.Activity.DashBoardActivity;
import com.friends.tot_earning.Activity.InviteFriendsActivity;
import com.friends.tot_earning.Activity.LuckyBoxActivity;
import com.friends.tot_earning.Activity.QuestionsActivity;
import com.friends.tot_earning.Activity.ScratchCardActivity;
import com.friends.tot_earning.Activity.SpinnerActivity;
import com.friends.tot_earning.DataBase.DbQueries;
import com.friends.tot_earning.Model.User;
import com.friends.tot_earning.Model.WatchVideoModel;
import com.friends.tot_earning.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    FirebaseAuth auth;
    Dialog loadingDialog, luckyBoxDialog, errorDialog;
    ImageView day1_image, day2_image, day3_image, day4_image, day5_image, day6_image, day7_image;
    ImageView day1_image_background, day2_image_background, day3_image_background, day4_image_background, day5_image_background, day6_image_background, day7_image_background;
    TextView day_1_text, day_2_text, day_3_text, day_4_text, day_5_text, day_6_text, day_7_text;

    FirebaseFirestore firebaseFirestore;
    FirebaseUser currentUser;
    User user;

    String limit1, limit2, limit3, limit4, limit5, limit6, limit7;
    String maintainSpinWheel, maintainLuckyBox, maintainPlayQuiz, maintainScratchCard;
    TextView coins, fullName, email;
    CircleImageView profileImage;
    AppCompatButton luckyDialogButton, okButton;
    TextView luckyTextDialog, errorText;

    ConstraintLayout spinWheel, luckyBox, playQuiz, watchVideo, inviteFriends, scratchCard;

    private InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;
    AdRequest adRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ////////////////loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.banner_slider_baground));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ////////////////loading dialog

        ////////////////Lucky box dialog
        luckyBoxDialog = new Dialog(getContext());
        luckyBoxDialog.setContentView(R.layout.dialog_lucky_box);
        luckyBoxDialog.setCancelable(true);
        luckyBoxDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.banner_slider_baground));
        luckyBoxDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        luckyDialogButton = luckyBoxDialog.findViewById(R.id.lucky_box_button);
        luckyTextDialog = luckyBoxDialog.findViewById(R.id.lucky_box_text);
        ////////////////Lucky box dialog

        ////////////////Error dialog
        errorDialog = new Dialog(getContext());
        errorDialog.setContentView(R.layout.error_dialog);
        errorDialog.setCancelable(true);
        errorDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.banner_slider_baground));
        errorDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        okButton = errorDialog.findViewById(R.id.button);
        errorText = errorDialog.findViewById(R.id.error_text);
        ////////////////Error dialog

        day1_image = view.findViewById(R.id.day1_img);
        day2_image = view.findViewById(R.id.day2_img);
        day3_image = view.findViewById(R.id.day3_img);
        day4_image = view.findViewById(R.id.day4_img);
        day5_image = view.findViewById(R.id.day5_img);
        day6_image = view.findViewById(R.id.day6_img);
        day7_image = view.findViewById(R.id.day7_img);

        day1_image_background = view.findViewById(R.id.day1_background_gray);
        day2_image_background = view.findViewById(R.id.day2_background_gray);
        day3_image_background = view.findViewById(R.id.day3_background_gray);
        day4_image_background = view.findViewById(R.id.day4_background_gray);
        day5_image_background = view.findViewById(R.id.day5_background_gray);
        day6_image_background = view.findViewById(R.id.day6_background_gray);
        day7_image_background = view.findViewById(R.id.day7_background_gray);

        day_1_text = view.findViewById(R.id.day_1_text);
        day_2_text = view.findViewById(R.id.day_2_text);
        day_3_text = view.findViewById(R.id.day_3_text);
        day_4_text = view.findViewById(R.id.day_4_text);
        day_5_text = view.findViewById(R.id.day_5_text);
        day_6_text = view.findViewById(R.id.day_6_text);
        day_7_text = view.findViewById(R.id.day_7_text);

        spinWheel = view.findViewById(R.id.spin_wheel);
        luckyBox = view.findViewById(R.id.lucky_box);
        playQuiz = view.findViewById(R.id.play_quiz);
        watchVideo = view.findViewById(R.id.watch_video);
        inviteFriends = view.findViewById(R.id.invite_earn);
        scratchCard = view.findViewById(R.id.survey);

        fullName = view.findViewById(R.id.full_name);
        email = view.findViewById(R.id.email);
        profileImage = view.findViewById(R.id.profile_image);
        coins = view.findViewById(R.id.coins);

        final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.animation_button);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        adRequest = new AdRequest.Builder().build();

        watchVideos();

        firebaseFirestore.collection("EARNING")
                .document("CHECK_IN")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            limit1 = String.valueOf(task.getResult().getLong("limit1"));
                            limit2 = String.valueOf(task.getResult().getLong("limit2"));
                            limit3 = String.valueOf(task.getResult().getLong("limit3"));
                            limit4 = String.valueOf(task.getResult().getLong("limit4"));
                            limit5 = String.valueOf(task.getResult().getLong("limit5"));
                            limit6 = String.valueOf(task.getResult().getLong("limit6"));
                            limit7 = String.valueOf(task.getResult().getLong("limit7"));

                            day_1_text.setText(String.valueOf(limit1));
                            day_2_text.setText(String.valueOf(limit2));
                            day_3_text.setText(String.valueOf(limit3));
                            day_4_text.setText(String.valueOf(limit4));
                            day_5_text.setText(String.valueOf(limit5));
                            day_6_text.setText(String.valueOf(limit6));
                            day_7_text.setText(String.valueOf(limit7));

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        firebaseFirestore.collection("EARNING")
                .document("Maintenance")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            maintainSpinWheel = String.valueOf(task.getResult().getString("spinWheel"));
                            maintainScratchCard = String.valueOf(task.getResult().getString("scratchCard"));
                            maintainLuckyBox = String.valueOf(task.getResult().getString("luckyBox"));
                            maintainPlayQuiz = String.valueOf(task.getResult().getString("playQuiz"));

                            if (maintainSpinWheel.equals("ON")) {
                                spinWheel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (mInterstitialAd != null) {
                                            mInterstitialAd.show(getActivity());
                                        } else {
                                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                                        }
                                        startActivity(new Intent(getContext(), SpinnerActivity.class));
                                    }
                                });
                            } else {
                                spinWheel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        errorDialog.show();
                                        errorText.setText("Currently spin wheel unavailable. please wait spin wheel will be available within 24hrs.");
                                        okButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                errorDialog.dismiss();
                                            }
                                        });
                                    }
                                });
                            }

                            if (maintainScratchCard.equals("ON")) {
                                scratchCard.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (mInterstitialAd != null) {
                                            mInterstitialAd.show(getActivity());
                                        } else {
                                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                                        }
                                        startActivity(new Intent(getContext(), ScratchCardActivity.class));
                                    }
                                });
                            } else {
                                scratchCard.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        errorDialog.show();
                                        errorText.setText("Currently scratch card unavailable. please wait scratch card will be available within 24hrs.");
                                        okButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                errorDialog.dismiss();
                                            }
                                        });
                                    }
                                });
                            }

                            if (maintainLuckyBox.equals("ON")) {
                                luckyBox.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (mInterstitialAd != null) {
                                            mInterstitialAd.show(getActivity());
                                        } else {
                                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                                        }
                                        startActivity(new Intent(getContext(), LuckyBoxActivity.class));
                                    }
                                });
                            } else {
                                luckyBox.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        errorDialog.show();
                                        errorText.setText("Currently lucky box unavailable. please wait lucky box will be available within 24hrs.");
                                        okButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                errorDialog.dismiss();
                                            }
                                        });
                                    }
                                });
                            }

                            if (maintainPlayQuiz.equals("ON")) {
                                playQuiz.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (mInterstitialAd != null) {
                                            mInterstitialAd.show(getActivity());
                                        } else {
                                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                                        }
                                        startActivity(new Intent(getContext(), QuestionsActivity.class));
                                    }
                                });
                            } else {
                                playQuiz.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        errorDialog.show();
                                        errorText.setText("Currently play quiz unavailable. please wait play quiz will be available within 24hrs.");
                                        okButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                errorDialog.dismiss();
                                            }
                                        });
                                    }
                                });
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        firebaseFirestore.collection("USERS")
                .document(currentUser.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);

                DbQueries.fullName = user.getName();
                DbQueries.email = user.getEmail();
                DbQueries.profileImage = user.getProfile();
                DbQueries.coin = String.valueOf(user.getCoins());
                DbQueries.uid = String.valueOf(user.getUid());

                fullName.setText("Hii " + DbQueries.fullName);
                coins.setText(String.valueOf(user.getCoins()));
                email.setText(DbQueries.email);
                if (user.getProfile().equals("")) {
                    Glide.with(getActivity()).load(R.drawable.avatarra).into(profileImage);
                } else {
                    try {
                        Glide.with(getActivity()).load(DbQueries.profileImage).placeholder(R.drawable.avatarra).into(profileImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                loadingDialog.dismiss();
            }
        });

        InterstitialAd.load(getContext(), "ca-app-pub-1356475100439807/2853621752", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });

        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(getActivity());
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
                startActivity(new Intent(getContext(), InviteFriendsActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        firebaseFirestore.collection("USERS")
                .document(currentUser.getUid())
                .collection("USER_DATA")
                .document("DAYS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String day1, day2, day3, day4, day5, day6, day7;
                            day1 = String.valueOf(task.getResult().getLong("day1"));
                            day2 = String.valueOf(task.getResult().getLong("day2"));
                            day3 = String.valueOf(task.getResult().getLong("day3"));
                            day4 = String.valueOf(task.getResult().getLong("day4"));
                            day5 = String.valueOf(task.getResult().getLong("day5"));
                            day6 = String.valueOf(task.getResult().getLong("day6"));
                            day7 = String.valueOf(task.getResult().getLong("day7"));

                            if (Long.parseLong(day1) > System.currentTimeMillis()) {
                                day1_image.setVisibility(View.VISIBLE);

                                day1_image_background.setVisibility(View.GONE);
                                day1_image.setImageResource(R.drawable.right_circle_png);
                            } else if (Long.parseLong(day1) == 0) {
                                day1_image.setVisibility(View.GONE);

                                day1_image_background.setVisibility(View.VISIBLE);
                            } else if (Long.parseLong(day1) == 1) {
                                day1_image.setVisibility(View.VISIBLE);

                                day1_image.setImageResource(R.drawable.right_circle_png);

                                day1_image_background.setVisibility(View.GONE);

                                day1_image.setClickable(false);
                                day2_image.setClickable(false);
                                day3_image.setClickable(false);
                                day4_image.setClickable(false);
                                day5_image.setClickable(false);
                                day6_image.setClickable(false);
                                day7_image.setClickable(false);
                            } else if (Long.parseLong(day1) < System.currentTimeMillis()) {
                                Map<String, Object> time = new HashMap<>();
                                time.put("day2", 0);
                                time.put("day3", 0);
                                time.put("day4", 0);
                                time.put("day5", 0);
                                time.put("day6", 0);
                                time.put("day7", 0);
                                firebaseFirestore.collection("USERS")
                                        .document(currentUser.getUid())
                                        .collection("USER_DATA")
                                        .document("DAYS")
                                        .update(time)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                day1_image.setVisibility(View.VISIBLE);

                                day1_image_background.setVisibility(View.GONE);

                                Map<String, Object> update = new HashMap<>();
                                update.put("day2", System.currentTimeMillis() + 86400000);
                                update.put("day1", 1);

                                day1_image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        day1_image.setImageResource(R.drawable.right_circle_png);

                                        firebaseFirestore.collection("USERS")
                                                .document(currentUser.getUid())
                                                .collection("USER_DATA")
                                                .document("DAYS")
                                                .update(update)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Map<String, Object> coins = new HashMap<>();
                                                            coins.put("coins", user.getCoins() + Long.parseLong(limit1));
                                                            coins.put("totalCoins", user.getTotalCoins() + Long.parseLong(limit1));
                                                            firebaseFirestore.collection("USERS")
                                                                    .document(currentUser.getUid())
                                                                    .update(coins)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            checkInCoin_day_1();
                                                                        }
                                                                    });
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }

                            if (Long.parseLong(day2) == 0) {
                                day2_image.setVisibility(View.GONE);

                                day2_image_background.setVisibility(View.VISIBLE);
                            } else if (Long.parseLong(day2) == 1) {
                                day2_image.setVisibility(View.VISIBLE);

                                day2_image.setImageResource(R.drawable.right_circle_png);

                                day2_image_background.setVisibility(View.GONE);

                                day1_image.setClickable(false);
                                day2_image.setClickable(false);
                                day3_image.setClickable(false);
                                day4_image.setClickable(false);
                                day5_image.setClickable(false);
                                day6_image.setClickable(false);
                                day7_image.setClickable(false);
                            } else if (Long.parseLong(day2) < System.currentTimeMillis()) {
                                day2_image.setVisibility(View.VISIBLE);

                                day2_image_background.setVisibility(View.GONE);

                                Map<String, Object> update = new HashMap<>();
                                update.put("day3", System.currentTimeMillis() + 86400000);
                                update.put("day2", 1);

                                day2_image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        day2_image.setImageResource(R.drawable.right_circle_png);

                                        firebaseFirestore.collection("USERS")
                                                .document(currentUser.getUid())
                                                .collection("USER_DATA")
                                                .document("DAYS")
                                                .update(update)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Map<String, Object> coins = new HashMap<>();
                                                            coins.put("coins", user.getCoins() + Long.parseLong(limit2));
                                                            coins.put("totalCoins", user.getTotalCoins() + Long.parseLong(limit2));
                                                            firebaseFirestore.collection("USERS")
                                                                    .document(currentUser.getUid())
                                                                    .update(coins)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            checkInCoin_day_2();
                                                                        }
                                                                    });
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }

                            if (Long.parseLong(day3) == 0) {
                                day3_image.setVisibility(View.GONE);

                                day3_image_background.setVisibility(View.VISIBLE);
                            } else if (Long.parseLong(day3) == 1) {
                                day3_image.setVisibility(View.VISIBLE);

                                day3_image.setImageResource(R.drawable.right_circle_png);

                                day3_image_background.setVisibility(View.GONE);

                                day1_image.setClickable(false);
                                day2_image.setClickable(false);
                                day3_image.setClickable(false);
                                day4_image.setClickable(false);
                                day5_image.setClickable(false);
                                day6_image.setClickable(false);
                                day7_image.setClickable(false);
                            } else if (Long.parseLong(day3) < System.currentTimeMillis()) {
                                day3_image.setVisibility(View.VISIBLE);

                                day3_image_background.setVisibility(View.GONE);

                                Map<String, Object> update = new HashMap<>();
                                update.put("day4", System.currentTimeMillis() + 86400000);
                                update.put("day3", 1);

                                day3_image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        day3_image.setImageResource(R.drawable.right_circle_png);

                                        firebaseFirestore.collection("USERS")
                                                .document(currentUser.getUid())
                                                .collection("USER_DATA")
                                                .document("DAYS")
                                                .update(update)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Map<String, Object> coins = new HashMap<>();
                                                            coins.put("coins", user.getCoins() + Long.parseLong(limit3));
                                                            coins.put("totalCoins", user.getTotalCoins() + Long.parseLong(limit3));
                                                            firebaseFirestore.collection("USERS")
                                                                    .document(currentUser.getUid())
                                                                    .update(coins)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            checkInCoin_day_3();
                                                                        }
                                                                    });
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }

                            if (Long.parseLong(day4) == 0) {
                                day4_image.setVisibility(View.GONE);

                                day4_image_background.setVisibility(View.VISIBLE);
                            } else if (Long.parseLong(day4) == 1) {
                                day4_image.setVisibility(View.VISIBLE);

                                day4_image.setImageResource(R.drawable.right_circle_png);

                                day4_image_background.setVisibility(View.GONE);

                                day1_image.setClickable(false);
                                day2_image.setClickable(false);
                                day3_image.setClickable(false);
                                day4_image.setClickable(false);
                                day5_image.setClickable(false);
                                day6_image.setClickable(false);
                                day7_image.setClickable(false);
                            } else if (Long.parseLong(day4) < System.currentTimeMillis()) {
                                day4_image.setVisibility(View.VISIBLE);

                                day4_image_background.setVisibility(View.GONE);

                                Map<String, Object> update = new HashMap<>();
                                update.put("day5", System.currentTimeMillis() + 86400000);
                                update.put("day4", 1);

                                day4_image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        day4_image.setImageResource(R.drawable.right_circle_png);

                                        firebaseFirestore.collection("USERS")
                                                .document(currentUser.getUid())
                                                .collection("USER_DATA")
                                                .document("DAYS")
                                                .update(update)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Map<String, Object> coins = new HashMap<>();
                                                            coins.put("coins", user.getCoins() + Long.parseLong(limit4));
                                                            coins.put("totalCoins", user.getTotalCoins() + Long.parseLong(limit4));
                                                            firebaseFirestore.collection("USERS")
                                                                    .document(currentUser.getUid())
                                                                    .update(coins)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            checkInCoin_day_4();
                                                                        }
                                                                    });
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }

                            if (Long.parseLong(day5) == 0) {
                                day5_image.setVisibility(View.GONE);

                                day5_image_background.setVisibility(View.VISIBLE);
                            } else if (Long.parseLong(day5) == 1) {
                                day5_image.setVisibility(View.VISIBLE);

                                day5_image.setImageResource(R.drawable.right_circle_png);

                                day5_image_background.setVisibility(View.GONE);

                                day1_image.setClickable(false);
                                day2_image.setClickable(false);
                                day3_image.setClickable(false);
                                day4_image.setClickable(false);
                                day5_image.setClickable(false);
                                day6_image.setClickable(false);
                                day7_image.setClickable(false);
                            } else if (Long.parseLong(day5) < System.currentTimeMillis()) {
                                day5_image.setVisibility(View.VISIBLE);

                                day5_image_background.setVisibility(View.GONE);

                                Map<String, Object> update = new HashMap<>();
                                update.put("day6", System.currentTimeMillis() + 86400000);
                                update.put("day5", 1);

                                day5_image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        day5_image.setImageResource(R.drawable.right_circle_png);

                                        firebaseFirestore.collection("USERS")
                                                .document(currentUser.getUid())
                                                .collection("USER_DATA")
                                                .document("DAYS")
                                                .update(update)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Map<String, Object> coins = new HashMap<>();
                                                            coins.put("coins", user.getCoins() + Long.parseLong(limit5));
                                                            coins.put("totalCoins", user.getTotalCoins() + Long.parseLong(limit5));
                                                            firebaseFirestore.collection("USERS")
                                                                    .document(currentUser.getUid())
                                                                    .update(coins)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            checkInCoin_day_5();
                                                                        }
                                                                    });
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }

                            if (Long.parseLong(day6) == 0) {
                                day6_image.setVisibility(View.GONE);

                                day6_image_background.setVisibility(View.VISIBLE);
                            } else if (Long.parseLong(day6) == 1) {
                                day6_image.setVisibility(View.VISIBLE);

                                day6_image.setImageResource(R.drawable.right_circle_png);

                                day6_image_background.setVisibility(View.GONE);

                                day1_image.setClickable(false);
                                day2_image.setClickable(false);
                                day3_image.setClickable(false);
                                day4_image.setClickable(false);
                                day5_image.setClickable(false);
                                day6_image.setClickable(false);
                                day7_image.setClickable(false);
                            } else if (Long.parseLong(day6) < System.currentTimeMillis()) {
                                day6_image.setVisibility(View.VISIBLE);

                                day6_image_background.setVisibility(View.GONE);

                                Map<String, Object> update = new HashMap<>();
                                update.put("day7", System.currentTimeMillis() + 86400000);
                                update.put("day6", 1);

                                day6_image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        day6_image.setImageResource(R.drawable.right_circle_png);

                                        firebaseFirestore.collection("USERS")
                                                .document(currentUser.getUid())
                                                .collection("USER_DATA")
                                                .document("DAYS")
                                                .update(update)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Map<String, Object> coins = new HashMap<>();
                                                            coins.put("coins", user.getCoins() + Long.parseLong(limit6));
                                                            coins.put("totalCoins", user.getTotalCoins() + Long.parseLong(limit6));
                                                            firebaseFirestore.collection("USERS")
                                                                    .document(currentUser.getUid())
                                                                    .update(coins)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            checkInCoin_day_6();
                                                                        }
                                                                    });
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }

                            if (Long.parseLong(day7) == 0) {
                                day7_image.setVisibility(View.GONE);

                                day7_image_background.setVisibility(View.VISIBLE);
                            } else if (Long.parseLong(day7) == 1) {
                                day7_image.setVisibility(View.VISIBLE);

                                day7_image.setImageResource(R.drawable.right_circle_png);

                                day7_image_background.setVisibility(View.GONE);

                                day1_image.setClickable(false);
                                day2_image.setClickable(false);
                                day3_image.setClickable(false);
                                day4_image.setClickable(false);
                                day5_image.setClickable(false);
                                day6_image.setClickable(false);
                                day7_image.setClickable(false);
                            } else if (Long.parseLong(day7) < System.currentTimeMillis()) {
                                day7_image.setVisibility(View.VISIBLE);

                                day7_image_background.setVisibility(View.GONE);

                                Map<String, Object> update = new HashMap<>();
                                update.put("day1", System.currentTimeMillis() + 86400000);
                                update.put("day7", 1);

                                day7_image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        day7_image.setImageResource(R.drawable.right_circle_png);

                                        firebaseFirestore.collection("USERS")
                                                .document(currentUser.getUid())
                                                .collection("USER_DATA")
                                                .document("DAYS")
                                                .update(update)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Map<String, Object> coins = new HashMap<>();
                                                            coins.put("coins", user.getCoins() + Long.parseLong(limit7));
                                                            coins.put("totalCoins", user.getTotalCoins() + Long.parseLong(limit7));
                                                            firebaseFirestore.collection("USERS")
                                                                    .document(currentUser.getUid())
                                                                    .update(coins)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            checkInCoin_day_7();
                                                                        }
                                                                    });
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }

                        }
                    }
                });
    }

    private void checkInCoin_day_1() {
        String random = UUID.randomUUID().toString().substring(0, 28);
        Map<String, Object> inviteRecord = new HashMap<>();
        inviteRecord.put("date", System.currentTimeMillis());
        inviteRecord.put("coins", Long.valueOf(limit1));
        inviteRecord.put("plusCoins", Long.valueOf(limit1));
        inviteRecord.put("source", "Check in");

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("RECORDS")
                .document(random)
                .set(inviteRecord)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), limit1 + " Coins added to your account", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkInCoin_day_2() {
        String random = UUID.randomUUID().toString().substring(0, 28);
        Map<String, Object> inviteRecord = new HashMap<>();
        inviteRecord.put("date", System.currentTimeMillis());
        inviteRecord.put("coins", Long.valueOf(limit2));
        inviteRecord.put("plusCoins", Long.valueOf(limit2));
        inviteRecord.put("source", "Check in");

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("RECORDS")
                .document(random)
                .set(inviteRecord)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), limit2 + " Coins added to your account", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkInCoin_day_3() {
        String random = UUID.randomUUID().toString().substring(0, 28);
        Map<String, Object> inviteRecord = new HashMap<>();
        inviteRecord.put("date", System.currentTimeMillis());
        inviteRecord.put("coins", Long.valueOf(limit3));
        inviteRecord.put("plusCoins", Long.valueOf(limit3));
        inviteRecord.put("source", "Check in");

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("RECORDS")
                .document(random)
                .set(inviteRecord)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), limit3 + " Coins added to your account", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkInCoin_day_4() {
        String random = UUID.randomUUID().toString().substring(0, 28);
        Map<String, Object> inviteRecord = new HashMap<>();
        inviteRecord.put("date", System.currentTimeMillis());
        inviteRecord.put("coins", Long.valueOf(limit4));
        inviteRecord.put("plusCoins", Long.valueOf(limit4));
        inviteRecord.put("source", "Check in");

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("RECORDS")
                .document(random)
                .set(inviteRecord)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), limit4 + " Coins added to your account", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkInCoin_day_5() {
        String random = UUID.randomUUID().toString().substring(0, 28);
        Map<String, Object> inviteRecord = new HashMap<>();
        inviteRecord.put("date", System.currentTimeMillis());
        inviteRecord.put("coins", Long.valueOf(limit5));
        inviteRecord.put("plusCoins", Long.valueOf(limit5));
        inviteRecord.put("source", "Check in");

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("RECORDS")
                .document(random)
                .set(inviteRecord)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), limit5 + " Coins added to your account", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkInCoin_day_6() {
        String random = UUID.randomUUID().toString().substring(0, 28);
        Map<String, Object> inviteRecord = new HashMap<>();
        inviteRecord.put("date", System.currentTimeMillis());
        inviteRecord.put("coins", Long.valueOf(limit6));
        inviteRecord.put("plusCoins", Long.valueOf(limit6));
        inviteRecord.put("source", "Check in");

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("RECORDS")
                .document(random)
                .set(inviteRecord)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), limit6 + " Coins added to your account", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkInCoin_day_7() {
        String random = UUID.randomUUID().toString().substring(0, 28);
        Map<String, Object> inviteRecord = new HashMap<>();
        inviteRecord.put("date", System.currentTimeMillis());
        inviteRecord.put("coins", Long.valueOf(limit7));
        inviteRecord.put("plusCoins", Long.valueOf(limit7));
        inviteRecord.put("source", "Check in");

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("RECORDS")
                .document(random)
                .set(inviteRecord)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), limit7 + " Coins added to your account", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void watchVideos() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore.collection("USERS")
                .document(currentUser.getUid())
                .collection("USER_DATA")
                .document("WATCH_VIDEO")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String spinLimit = String.valueOf(task.getResult().getLong("limit"));


                            if (Long.parseLong(spinLimit) == 0) {
                                watchVideo.setClickable(false);
                                errorDialog.show();

                                firebaseFirestore.collection("USERS")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .collection("USER_DATA")
                                        .document("WATCH_VIDEO")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    String spinLimit1 = String.valueOf(task.getResult().getLong("limit"));
                                                    watchVideo.setClickable(false);

                                                    Map<String, Object> update = new HashMap<>();
                                                    update.put("limit", System.currentTimeMillis() + 86400000);
                                                    firebaseFirestore.collection("USERS")
                                                            .document(FirebaseAuth.getInstance().getUid())
                                                            .collection("USER_DATA")
                                                            .document("WATCH_VIDEO")
                                                            .update(update)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {

                                                                    watchVideo.setClickable(false);
                                                                }
                                                            });
                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                            } else if (Long.parseLong(spinLimit) == 1) {
                                watchVideo.setClickable(true);

                                watchVideo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        loadingDialog.show();
                                        
                                        RewardedAd.load(getContext(), "ca-app-pub-1356475100439807/1261887227",
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

                                        Thread timer = new Thread() {
                                            public void run() {
                                                try {
                                                    sleep(2000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                } finally {
                                                    loadingDialog.dismiss();
                                                    if (mRewardedAd != null) {
                                                        Activity activityContext = getActivity();
                                                        activityContext.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                                                                    @Override
                                                                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                                                        // Handle the reward.
                                                                        int rewardAmount = rewardItem.getAmount();
                                                                        String rewardType = rewardItem.getType();

                                                                        getCoin();

                                                                        Map<String, Object> count = new HashMap<>();
                                                                        count.put("limit", Long.valueOf(spinLimit) - 1);
                                                                        firebaseFirestore.collection("USERS")
                                                                                .document(FirebaseAuth.getInstance().getUid())
                                                                                .collection("USER_DATA")
                                                                                .document("WATCH_VIDEO")
                                                                                .update(count)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            firebaseFirestore.collection("USERS")
                                                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                                                    .collection("USER_DATA")
                                                                                                    .document("WATCH_VIDEO")
                                                                                                    .get()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                String spinLimit1 = String.valueOf(task.getResult().getLong("limit"));
                                                                                                                watchVideo.setClickable(false);

                                                                                                                Map<String, Object> update = new HashMap<>();
                                                                                                                update.put("limit", System.currentTimeMillis() + 86400000);
                                                                                                                firebaseFirestore.collection("USERS")
                                                                                                                        .document(FirebaseAuth.getInstance().getUid())
                                                                                                                        .collection("USER_DATA")
                                                                                                                        .document("WATCH_VIDEO")
                                                                                                                        .update(update)
                                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(Void unused) {

                                                                                                                                watchVideo.setClickable(false);
                                                                                                                            }
                                                                                                                        });
                                                                                                            } else {
                                                                                                                String error = task.getException().getMessage();
                                                                                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        } else {
                                                                                            String error = task.getException().getMessage();
                                                                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                });

                                                            }
                                                        });

                                                    } else {
                                                       //   Toast.makeText(getContext(), "ads not available", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        };
                                        timer.start();
                                    }
                                });

                            } else if (Long.parseLong(spinLimit) < System.currentTimeMillis() && Long.parseLong(spinLimit) > 1000) {

                                firebaseFirestore.collection("EARNING")
                                        .document("WATCH_VIDEO")
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        WatchVideoModel model = documentSnapshot.toObject(WatchVideoModel.class);

                                        Map<String, Object> limit = new HashMap<>();
                                        limit.put("limit", model.getLimit());
                                        firebaseFirestore.collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .collection("USER_DATA")
                                                .document("WATCH_VIDEO")
                                                .update(limit)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            firebaseFirestore.collection("USERS")
                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                    .collection("USER_DATA")
                                                                    .document("WATCH_VIDEO")
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                String spinLimit2 = String.valueOf(task.getResult().getLong("limit"));
                                                                                watchVideo.setClickable(true);

                                                                                if (Long.parseLong(spinLimit2) == 1) {

                                                                                    watchVideo.setClickable(true);

                                                                                    watchVideo.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            loadingDialog.show();

                                                                                            RewardedAd.load(getContext(), "ca-app-pub-1356475100439807/1261887227",
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

                                                                                            Thread timer = new Thread() {
                                                                                                public void run() {
                                                                                                    try {
                                                                                                        sleep(2000);
                                                                                                    } catch (InterruptedException e) {
                                                                                                        e.printStackTrace();
                                                                                                    } finally {
                                                                                                        loadingDialog.dismiss();
                                                                                                        if (mRewardedAd != null) {
                                                                                                            Activity activityContext = getActivity();
                                                                                                            activityContext.runOnUiThread(new Runnable() {
                                                                                                                @Override
                                                                                                                public void run() {
                                                                                                                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                                                                                                                        @Override
                                                                                                                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                                                                                                            // Handle the reward.
                                                                                                                            int rewardAmount = rewardItem.getAmount();
                                                                                                                            String rewardType = rewardItem.getType();

                                                                                                                            getCoin();

                                                                                                                            Map<String, Object> count = new HashMap<>();
                                                                                                                            count.put("limit", Long.valueOf(spinLimit) - 1);
                                                                                                                            firebaseFirestore.collection("USERS")
                                                                                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                                                                                    .collection("USER_DATA")
                                                                                                                                    .document("WATCH_VIDEO")
                                                                                                                                    .update(count)
                                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                        @Override
                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                                firebaseFirestore.collection("USERS")
                                                                                                                                                        .document(FirebaseAuth.getInstance().getUid())
                                                                                                                                                        .collection("USER_DATA")
                                                                                                                                                        .document("WATCH_VIDEO")
                                                                                                                                                        .get()
                                                                                                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                                                            @Override
                                                                                                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                                                    String spinLimit1 = String.valueOf(task.getResult().getLong("limit"));
                                                                                                                                                                    watchVideo.setClickable(false);

                                                                                                                                                                    Map<String, Object> update = new HashMap<>();
                                                                                                                                                                    update.put("limit", System.currentTimeMillis() + 86400000);
                                                                                                                                                                    firebaseFirestore.collection("USERS")
                                                                                                                                                                            .document(FirebaseAuth.getInstance().getUid())
                                                                                                                                                                            .collection("USER_DATA")
                                                                                                                                                                            .document("WATCH_VIDEO")
                                                                                                                                                                            .update(update)
                                                                                                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                                                @Override
                                                                                                                                                                                public void onSuccess(Void unused) {

                                                                                                                                                                                    watchVideo.setClickable(false);
                                                                                                                                                                                }
                                                                                                                                                                            });
                                                                                                                                                                } else {
                                                                                                                                                                    String error = task.getException().getMessage();
                                                                                                                                                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                                                                                                }
                                                                                                                                                            }
                                                                                                                                                        });
                                                                                                                                            } else {
                                                                                                                                                String error = task.getException().getMessage();
                                                                                                                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    });
                                                                                                                        }
                                                                                                                    });

                                                                                                                }
                                                                                                            });

                                                                                                        } else {
                                                                                                          //  Toast.makeText(getContext(), "ads not available", Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            };
                                                                                            timer.start();
                                                                                        }
                                                                                    });

                                                                                } else {
                                                                                    watchVideo.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            loadingDialog.show();

                                                                                            RewardedAd.load(getContext(), "ca-app-pub-1356475100439807/1261887227",
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

                                                                                            Thread timer = new Thread() {
                                                                                                public void run() {
                                                                                                    try {
                                                                                                        sleep(2000);
                                                                                                    } catch (InterruptedException e) {
                                                                                                        e.printStackTrace();
                                                                                                    } finally {
                                                                                                        loadingDialog.dismiss();
                                                                                                        if (mRewardedAd != null) {
                                                                                                            Activity activityContext = getActivity();
                                                                                                            activityContext.runOnUiThread(new Runnable() {
                                                                                                                @Override
                                                                                                                public void run() {
                                                                                                                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                                                                                                                        @Override
                                                                                                                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                                                                                                            // Handle the reward.
                                                                                                                            int rewardAmount = rewardItem.getAmount();
                                                                                                                            String rewardType = rewardItem.getType();

                                                                                                                            getCoin();

                                                                                                                            Map<String, Object> count = new HashMap<>();
                                                                                                                            count.put("limit", Long.valueOf(spinLimit2) - 1);
                                                                                                                            firebaseFirestore.collection("USERS")
                                                                                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                                                                                    .collection("USER_DATA")
                                                                                                                                    .document("WATCH_VIDEO")
                                                                                                                                    .update(count)
                                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                        @Override
                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                                firebaseFirestore.collection("USERS")
                                                                                                                                                        .document(FirebaseAuth.getInstance().getUid())
                                                                                                                                                        .collection("USER_DATA")
                                                                                                                                                        .document("WATCH_VIDEO")
                                                                                                                                                        .get()
                                                                                                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                                                            @Override
                                                                                                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                                                    String spinLimit1 = String.valueOf(task.getResult().getLong("limit"));
                                                                                                                                                                    watchVideo.setClickable(false);
                                                                                                                                                                } else {
                                                                                                                                                                    String error = task.getException().getMessage();
                                                                                                                                                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                                                                                                }
                                                                                                                                                            }
                                                                                                                                                        });
                                                                                                                                            } else {
                                                                                                                                                String error = task.getException().getMessage();
                                                                                                                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    });
                                                                                                                        }
                                                                                                                    });
                                                                                                                }
                                                                                                            });

                                                                                                        } else {
                                                                                                          //    Toast.makeText(getContext(), "Ads not available", Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            };
                                                                                            timer.start();

                                                                                        }
                                                                                    });
                                                                                }
                                                                            } else {
                                                                                String error = task.getException().getMessage();
                                                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });

                            } else if (Long.parseLong(spinLimit) <= 100) {
                                watchVideo.setClickable(true);

                                watchVideo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        loadingDialog.show();

                                        RewardedAd.load(getContext(), "ca-app-pub-1356475100439807/1261887227",
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

                                        Thread timer = new Thread() {
                                            public void run() {
                                                try {
                                                    sleep(2000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                } finally {
                                                    loadingDialog.dismiss();
                                                    if (mRewardedAd != null) {
                                                        Activity activityContext = getActivity();
                                                        activityContext.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                                                                    @Override
                                                                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                                                        // Handle the reward.
                                                                        int rewardAmount = rewardItem.getAmount();
                                                                        String rewardType = rewardItem.getType();

                                                                        getCoin();

                                                                        Map<String, Object> count = new HashMap<>();
                                                                        count.put("limit", Long.valueOf(spinLimit) - 1);
                                                                        firebaseFirestore.collection("USERS")
                                                                                .document(FirebaseAuth.getInstance().getUid())
                                                                                .collection("USER_DATA")
                                                                                .document("WATCH_VIDEO")
                                                                                .update(count)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            firebaseFirestore.collection("USERS")
                                                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                                                    .collection("USER_DATA")
                                                                                                    .document("WATCH_VIDEO")
                                                                                                    .get()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                String spinLimit1 = String.valueOf(task.getResult().getLong("limit"));
                                                                                                                watchVideo.setClickable(false);
                                                                                                            } else {
                                                                                                                String error = task.getException().getMessage();
                                                                                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        } else {
                                                                                            String error = task.getException().getMessage();
                                                                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                });
                                                            }
                                                        });

                                                    } else {
                                                       // Toast.makeText(getContext(), "Ads not available", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        };
                                        timer.start();

                                    }
                                });
                            } else if (Long.parseLong(spinLimit) > 1000) {
                                watchVideo.setClickable(true);

                                watchVideo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        errorDialog.show();

                                        errorText.setText("Oops! Video not available. Try for next day");
                                        okButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                errorDialog.dismiss();
                                            }
                                        });
                                    }
                                });
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
    }

    private void getCoin() {

        firebaseFirestore.collection("EARNING")
                .document("WATCH_VIDEO")
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                WatchVideoModel model = documentSnapshot.toObject(WatchVideoModel.class);

                luckyBoxDialog.show();
                Long randomNumber = Long.valueOf(model.getCoins());
                luckyTextDialog.setText("You got " + randomNumber + " coins in watch video");

                luckyDialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        luckyBoxDialog.dismiss();
                        startActivity(new Intent(getContext(), DashBoardActivity.class));


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
                                                            inviteRecord.put("source", "Watch Video");

                                                            firebaseFirestore.collection("USERS")
                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                    .collection("RECORDS")
                                                                    .document(random)
                                                                    .set(inviteRecord)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            Toast.makeText(getContext(), randomNumber + " Coins added to your account", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });


                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                        }
                                                        luckyBoxDialog.dismiss();
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