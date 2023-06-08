package com.friends.tot_earning.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.friends.tot_earning.Model.PlayQuizModel;
import com.friends.tot_earning.Model.QuestionModel;
import com.friends.tot_earning.Model.User;
import com.friends.tot_earning.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuestionsActivity extends AppCompatActivity {
    public static final String FILE_NAME = "QUIZZER";
    public static final String KEY_NAME = "QUESTIONS";

    int count = 0;
    TextView questions, questions_indicator;
    AppCompatButton share_btn, next_btn;
    LinearLayout options_contaner;
    FloatingActionButton book_mark;
    public List<QuestionModel> list;
    int position = 0;
    int Score = 0;
    ArrayList<QuestionModel> bookmarkList;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Gson gson;
    int matchQuestionPosition;
    FirebaseFirestore firebaseFirestore;
    Dialog errorDialog, loadingDialog, playQuizDialog;
    AppCompatButton okButton, playQuizButton;
    TextView errorText, playQuizText;

    RewardedAd mRewardedAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        firebaseFirestore = FirebaseFirestore.getInstance();

        questions = findViewById(R.id.questions);
        questions_indicator = findViewById(R.id.question_indicator);
        share_btn = findViewById(R.id.share_btn);
        next_btn = findViewById(R.id.next_btn);
        options_contaner = findViewById(R.id.options_contaner);
        book_mark = findViewById(R.id.book_mark);

        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();

        ////////////////error dialog
        errorDialog = new Dialog(this);
        errorDialog.setContentView(R.layout.error_dialog);
        errorDialog.setCancelable(true);
        errorDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        errorDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        okButton = errorDialog.findViewById(R.id.button);
        errorText = errorDialog.findViewById(R.id.error_text);
        ////////////////error dialog

        ////////////////loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ////////////////loading dialog

        ////////////////Play Quiz dialog
        playQuizDialog = new Dialog(this);
        playQuizDialog.setContentView(R.layout.dialog_play_quiz);
        playQuizDialog.setCancelable(false);
        playQuizDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        playQuizDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        playQuizButton = playQuizDialog.findViewById(R.id.play_quiz_button);
        playQuizText = playQuizDialog.findViewById(R.id.play_quiz_error_text);
        ////////////////Play Quiz dialog

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(QuestionsActivity.this, "ca-app-pub-1356475100439807/1261887227",
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


        getBookmark();
        book_mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelMatch()) {
                    bookmarkList.remove(matchQuestionPosition);
                    // book_mark.setImageDrawable(getDrawable(R.drawable.book_mark));
                } else {
                    bookmarkList.add(list.get(position));
                    //  book_mark.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                }
            }
        });


        list = new ArrayList<>();

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA")
                .document("PLAY_QUIZ")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String spinLimit = String.valueOf(task.getResult().getLong("limit"));

                            if (Long.parseLong(spinLimit) == 1) {

                                firebaseFirestore.collection("EARNING").document("MCQ").collection("questions")
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                    QuestionModel list1 = snapshot.toObject(QuestionModel.class);
                                                    list.add(list1);

                                                }
                                                if (list.size() > 0) {
                                                    for (int i = 0; i < 4; i++) {
                                                        options_contaner.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                checkAnswer((Button) v);
                                                            }
                                                        });
                                                    }
                                                    playAnim(questions, 0, list.get(position).getQuestion());
                                                    next_btn.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            next_btn.setEnabled(true);
                                                            next_btn.setAlpha(0.7f);
                                                            enableOption(true);
                                                            position++;
                                                            if (position == list.size()) {
                                                                Map<String, Object> update = new HashMap<>();
                                                                update.put("limit", System.currentTimeMillis() + 86400000);
                                                                firebaseFirestore.collection("USERS")
                                                                        .document(FirebaseAuth.getInstance().getUid())
                                                                        .collection("USER_DATA")
                                                                        .document("PLAY_QUIZ")
                                                                        .update(update)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                            }
                                                                        });

                                                                int firstScore = list.size() - Score;
                                                                Long totalScore = Long.valueOf((Score - firstScore) * 10);
                                                                String random = UUID.randomUUID().toString().substring(0, 28);
                                                                Map<String, Object> inviteRecord = new HashMap<>();
                                                                inviteRecord.put("date", System.currentTimeMillis());
                                                                inviteRecord.put("coins", totalScore);
                                                                inviteRecord.put("plusCoins", totalScore);
                                                                inviteRecord.put("source", "Play Quiz");

                                                                firebaseFirestore.collection("USERS")
                                                                        .document(FirebaseAuth.getInstance().getUid())
                                                                        .collection("RECORDS")
                                                                        .document(random)
                                                                        .set(inviteRecord)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                Toast.makeText(QuestionsActivity.this, totalScore + " Coins added to your account", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });

                                                                //Score Acticity
                                                                Thread timer = new Thread(){
                                                                    public void run(){
                                                                        try{
                                                                            sleep(0000);
                                                                        } catch (InterruptedException e){
                                                                            e.printStackTrace();
                                                                        }finally{
                                                                            if (mRewardedAd != null) {
                                                                                Activity activityContext = QuestionsActivity.this;
                                                                                activityContext.runOnUiThread(new Runnable() {
                                                                                    @Override public void run() {
                                                                                        mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                                                                                            @Override
                                                                                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                                                                                // Handle the reward.
                                                                                                int rewardAmount = rewardItem.getAmount();
                                                                                                String rewardType = rewardItem.getType();
                                                                                                Intent intent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                                                                                                intent.putExtra("total", list.size());
                                                                                                intent.putExtra("score", Score);
                                                                                                startActivity(intent);
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

                                                                return;
                                                            }
                                                            count = 0;
                                                            playAnim(questions, 0, list.get(position).getQuestion());
                                                        }
                                                    });

                                                } else {
                                                    finish();
                                                    Toast.makeText(getApplicationContext(), "No Questions", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            } else if (Long.parseLong(spinLimit) < System.currentTimeMillis() && Long.parseLong(spinLimit) > 1) {

                                Map<String, Object> limit = new HashMap<>();
                                limit.put("limit", 1);
                                firebaseFirestore.collection("USERS")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .collection("USER_DATA")
                                        .document("PLAY_QUIZ")
                                        .update(limit)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    firebaseFirestore.collection("USERS")
                                                            .document(FirebaseAuth.getInstance().getUid())
                                                            .collection("USER_DATA")
                                                            .document("PLAY_QUIZ")
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        String spinLimit2 = String.valueOf(task.getResult().getLong("limit"));

                                                                        if (Long.parseLong(spinLimit2) == 1) {

                                                                            firebaseFirestore.collection("EARNING").document("MCQ").collection("questions")
                                                                                    .get()
                                                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                        @Override
                                                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                                                                QuestionModel list1 = snapshot.toObject(QuestionModel.class);
                                                                                                list.add(list1);
                                                                                                loadingDialog.dismiss();

                                                                                            }
                                                                                            if (list.size() > 0) {
                                                                                                for (int i = 0; i < 4; i++) {
                                                                                                    options_contaner.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                                                                                                        @Override
                                                                                                        public void onClick(View v) {
                                                                                                            checkAnswer((Button) v);
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                                playAnim(questions, 0, list.get(position).getQuestion());
                                                                                                next_btn.setOnClickListener(new View.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(View v) {
                                                                                                        next_btn.setEnabled(true);
                                                                                                        next_btn.setAlpha(0.7f);
                                                                                                        enableOption(true);
                                                                                                        position++;
                                                                                                        if (position == list.size()) {
                                                                                                            Map<String, Object> update = new HashMap<>();
                                                                                                            update.put("limit", System.currentTimeMillis() + 86400000);
                                                                                                            firebaseFirestore.collection("USERS")
                                                                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                                                                    .collection("USER_DATA")
                                                                                                                    .document("PLAY_QUIZ")
                                                                                                                    .update(update)
                                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onSuccess(Void unused) {

                                                                                                                        }
                                                                                                                    });

                                                                                                            int firstScore = list.size() - Score;
                                                                                                            Long totalScore = Long.valueOf((Score - firstScore) * 10);
                                                                                                            String random = UUID.randomUUID().toString().substring(0, 28);
                                                                                                            Map<String, Object> inviteRecord = new HashMap<>();
                                                                                                            inviteRecord.put("date", System.currentTimeMillis());
                                                                                                            inviteRecord.put("coins", totalScore);
                                                                                                            inviteRecord.put("plusCoins", totalScore);
                                                                                                            inviteRecord.put("source", "Play Quiz");

                                                                                                            firebaseFirestore.collection("USERS")
                                                                                                                    .document(FirebaseAuth.getInstance().getUid())
                                                                                                                    .collection("RECORDS")
                                                                                                                    .document(random)
                                                                                                                    .set(inviteRecord)
                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                            Toast.makeText(QuestionsActivity.this, totalScore + " Coins added to your account", Toast.LENGTH_SHORT).show();
                                                                                                                        }
                                                                                                                    });


                                                                                                            //Score Acticity

                                                                                                            Thread timer = new Thread(){
                                                                                                                public void run(){
                                                                                                                    try{
                                                                                                                        sleep(0000);
                                                                                                                    } catch (InterruptedException e){
                                                                                                                        e.printStackTrace();
                                                                                                                    }finally{
                                                                                                                        if (mRewardedAd != null) {
                                                                                                                            Activity activityContext = QuestionsActivity.this;
                                                                                                                            activityContext.runOnUiThread(new Runnable() {
                                                                                                                                @Override public void run() {
                                                                                                                                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                                                                                                                                        @Override
                                                                                                                                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                                                                                                                            // Handle the reward.
                                                                                                                                            int rewardAmount = rewardItem.getAmount();
                                                                                                                                            String rewardType = rewardItem.getType();
                                                                                                                                            Intent intent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                                                                                                                                            intent.putExtra("total", list.size());
                                                                                                                                            intent.putExtra("score", Score);
                                                                                                                                            startActivity(intent);
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

                                                                                                            return;
                                                                                                        }
                                                                                                        count = 0;
                                                                                                        playAnim(questions, 0, list.get(position).getQuestion());
                                                                                                    }
                                                                                                });
                                                                                            } else {
                                                                                                finish();
                                                                                                Toast.makeText(getApplicationContext(), "No Questions", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    } else {
                                                                        String error = task.getException().getMessage();
                                                                        Toast.makeText(QuestionsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(QuestionsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                            } else {
                                loadingDialog.dismiss();
                                errorDialog.show();
                                Thread timer = new Thread(){
                                    public void run(){
                                        try{
                                            sleep(2000);
                                        } catch (InterruptedException e){
                                            e.printStackTrace();
                                        }finally{
                                            if (mRewardedAd != null) {
                                                Activity activityContext = QuestionsActivity.this;
                                                activityContext.runOnUiThread(new Runnable() {
                                                    @Override public void run() {
                                                        mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                                                            @Override
                                                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                                                // Handle the reward.
                                                                errorDialog.dismiss();
                                                                int rewardAmount = rewardItem.getAmount();
                                                                String rewardType = rewardItem.getType();

                                                                errorText.setText("Oops! Question not available. Try for next day");
                                                                okButton.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        errorDialog.dismiss();
                                                                        startActivity(new Intent(QuestionsActivity.this, DashBoardActivity.class));
                                                                    }
                                                                });
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
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(QuestionsActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA")
                .document("PLAY_QUIZ")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String spinLimit = String.valueOf(task.getResult().getLong("limit"));

                            if (Long.parseLong(spinLimit) == 1) {

                                Map<String, Object> update = new HashMap<>();
                                update.put("limit", System.currentTimeMillis() + 86400000);
                                firebaseFirestore.collection("USERS")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .collection("USER_DATA")
                                        .document("PLAY_QUIZ")
                                        .update(update)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                firebaseFirestore.collection("USERS")
                                                        .document(FirebaseAuth.getInstance().getUid())
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()){
                                                                    Long illegalActivity=task.getResult().getLong("illegalActivity");

                                                                    Map<String, Object> update = new HashMap<>();
                                                                    update.put("illegalActivity",Long.valueOf(illegalActivity) +1);
                                                                    firebaseFirestore.collection("USERS")
                                                                            .document(FirebaseAuth.getInstance().getUid())
                                                                            .update(update)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {

                                                                                }
                                                                            });
                                                                }else {
                                                                    String error = task.getException().getMessage();
                                                                    Toast.makeText(QuestionsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(QuestionsActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        storeBookmark();

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA")
                .document("PLAY_QUIZ")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            String spinLimit = String.valueOf(task.getResult().getLong("limit"));

                            if (Long.parseLong(spinLimit) == 1) {


                                playQuizDialog.show();
                                playQuizText.setText("Oops! Question not available. Try for next day");
                                playQuizButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        errorDialog.dismiss();
                                        startActivity(new Intent(QuestionsActivity.this, DashBoardActivity.class));

                                        Map<String, Object> update = new HashMap<>();
                                        update.put("limit", System.currentTimeMillis() + 86400000);
                                        firebaseFirestore.collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .collection("USER_DATA")
                                                .document("PLAY_QUIZ")
                                                .update(update)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                    }
                                                });
                                    }
                                });

                                Map<String, Object> update = new HashMap<>();
                                update.put("limit", System.currentTimeMillis() + 86400000);
                                firebaseFirestore.collection("USERS")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .collection("USER_DATA")
                                        .document("PLAY_QUIZ")
                                        .update(update)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                firebaseFirestore.collection("USERS")
                                                        .document(FirebaseAuth.getInstance().getUid())
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()){
                                                                    Long illegalActivity=task.getResult().getLong("illegalActivity");

                                                                    Map<String, Object> update = new HashMap<>();
                                                                    update.put("illegalActivity",Long.valueOf(illegalActivity) +1);
                                                                    firebaseFirestore.collection("USERS")
                                                                            .document(FirebaseAuth.getInstance().getUid())
                                                                            .update(update)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {

                                                                                }
                                                                            });
                                                                }else {
                                                                    String error = task.getException().getMessage();
                                                                    Toast.makeText(QuestionsActivity.this, error, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA")
                .document("PLAY_QUIZ")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String spinLimit = String.valueOf(task.getResult().getLong("limit"));

                            if (Long.parseLong(spinLimit) == 1) {

                                Map<String, Object> update = new HashMap<>();
                                update.put("limit", System.currentTimeMillis() + 86400000);
                                firebaseFirestore.collection("USERS")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .collection("USER_DATA")
                                        .document("PLAY_QUIZ")
                                        .update(update)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                firebaseFirestore.collection("USERS")
                                                        .document(FirebaseAuth.getInstance().getUid())
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()){
                                                                    Long illegalActivity=task.getResult().getLong("illegalActivity");

                                                                    Map<String, Object> update = new HashMap<>();
                                                                    update.put("illegalActivity",Long.valueOf(illegalActivity) +1);
                                                                    firebaseFirestore.collection("USERS")
                                                                            .document(FirebaseAuth.getInstance().getUid())
                                                                            .update(update)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {

                                                                                }
                                                                            });
                                                                }else {
                                                                    String error = task.getException().getMessage();
                                                                    Toast.makeText(QuestionsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(QuestionsActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void playAnim(View view, int value, final String data) {
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                try {
                    if (value == 0 && count < 4) {
                        String option = "";
                        if (count == 0) {
                            option = list.get(position).getOptionA();
                        } else if (count == 1) {
                            option = list.get(position).getOptionB();
                        } else if (count == 2) {
                            option = list.get(position).getOptionC();
                        } else if (count == 3) {
                            option = list.get(position).getOptionD();
                        }
                        playAnim(options_contaner.getChildAt(count), 0, option);
                        count++;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (value == 0) {
                    try {
                        ((TextView) view).setText(data);
                        questions_indicator.setText(position + 1 + "/" + list.size());
                        if (modelMatch()) {
                            //  book_mark.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                        } else {
                            //  book_mark.setImageDrawable(getDrawable(R.drawable.book_mark));
                        }
                    } catch (ClassCastException e) {
                        ((Button) view).setText(data);
                    }
                    view.setTag(data);
                    playAnim(view, 1, data);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void checkAnswer(Button selectedOption) {
        try {
            enableOption(false);
            next_btn.setEnabled(true);
            next_btn.setAlpha(1);
            if (selectedOption.getText().toString().equals(list.get(position).getCorrectANS())) {
                //correct Answer
                Score++;
                selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C36500")));
                plusCoin();
            } else {
                //incorrect Answer
                selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C36500")));
                Button correctOption = (Button) options_contaner.findViewWithTag(list.get(position).getCorrectANS());
            //    correctOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00FF04")));
                lossCoin();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void enableOption(boolean enable) {
        for (int i = 0; i < 4; i++) {
            options_contaner.getChildAt(i).setEnabled(enable);
            if (enable) {
                options_contaner.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C8C8C8")));
            }
        }
    }

    private void getBookmark() {

        String json = preferences.getString(KEY_NAME, "");

        Type type = new TypeToken<List<QuestionModel>>() {
        }.getType();

        bookmarkList = gson.fromJson(json, type);

        if (bookmarkList == null) {
            bookmarkList = new ArrayList<>();
        }
    }

    private boolean modelMatch() {
        boolean matched = false;
        int i = 0;
        for (QuestionModel model : bookmarkList) {
            if (model.getQuestion().equals(list.get(position).getQuestion()) &&
                    model.getCorrectANS().equals(list.get(position).getCorrectANS())) {
                matched = true;
                matchQuestionPosition = i;
            }
            i++;
        }
        return matched;
    }

    private void storeBookmark() {
        String json = gson.toJson(bookmarkList);
        editor.putString(KEY_NAME, json);
        editor.commit();
    }

    private void plusCoin() {
        firebaseFirestore.collection("EARNING")
                .document("MCQ")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        PlayQuizModel model = documentSnapshot.toObject(PlayQuizModel.class);


                        firebaseFirestore.collection("USERS")
                                .document(FirebaseAuth.getInstance().getUid())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        User user = documentSnapshot.toObject(User.class);

                                        Map<String, Object> time = new HashMap<>();
                                        time.put("coins", user.getCoins() + model.getCoin());
                                        time.put("totalCoins", user.getTotalCoins() + model.getCoin());


                                        firebaseFirestore.collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .update(time)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {


                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(QuestionsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    private void lossCoin() {
        firebaseFirestore.collection("EARNING")
                .document("MCQ")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        PlayQuizModel model = documentSnapshot.toObject(PlayQuizModel.class);


                        firebaseFirestore.collection("USERS")
                                .document(FirebaseAuth.getInstance().getUid())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        User user = documentSnapshot.toObject(User.class);

                                        Map<String, Object> time = new HashMap<>();
                                        time.put("coins", user.getCoins() - model.getCoin());
                                        time.put("totalCoins", user.getTotalCoins() - model.getCoin());


                                        firebaseFirestore.collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .update(time)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {


                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(QuestionsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

}