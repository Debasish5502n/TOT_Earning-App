package com.friends.tot_earning.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.friends.tot_earning.Adapter.PaymentRecordAdapter;
import com.friends.tot_earning.DataBase.DbQueries;
import com.friends.tot_earning.Model.PaymentRecordModel;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RedemptionActivity extends AppCompatActivity {

    ConstraintLayout paytm,freeFirDiamond,googlePay,bank;
    Dialog loadingDialog, errorDialog, paytmDialog,dialogFreeFire,dialogGooglePay,dialogBankAccount;
    AppCompatButton okButton, paytmButton,freeFireButton,googlePayRedeemBtn,bankRedeemBtn;
    TextView errorText;
    EditText paytmEditText,freeFireEditText,googlePayEditText,accountNumber,reEnterNumber,accountName,ifscCode;
    FirebaseFirestore firebaseFirestore;
    String coins;
    TextView coinText, textView7;
    RecyclerView recyclerView;
    TextView noRecord;
    PaymentRecordAdapter adapter;
    RewardedAd mRewardedAd;
    String random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddemption);
        paytm = findViewById(R.id.paytm);
        firebaseFirestore = FirebaseFirestore.getInstance();
        textView7 = findViewById(R.id.textView7);
        coinText = findViewById(R.id.coin);
        freeFirDiamond = findViewById(R.id.free_fire_diamond);
        googlePay = findViewById(R.id.google_pay);
        bank = findViewById(R.id.bank);
        random = UUID.randomUUID().toString().substring(0, 28);

        ////////////////loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ////////////////loading dialog

        recyclerView = findViewById(R.id.recyclerView);
        noRecord = findViewById(R.id.no_record);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final ArrayList<PaymentRecordModel> models = new ArrayList<>();
        adapter=new PaymentRecordAdapter(RedemptionActivity.this,models);
        recyclerView.setAdapter(adapter);

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("PAYMENTS")
                .orderBy("date", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    PaymentRecordModel model = snapshot.toObject(PaymentRecordModel.class);
                    models.add(model);
                    recyclerView.setVisibility(View.VISIBLE);
                    noRecord.setVisibility(View.GONE);

                }

                adapter.notifyDataSetChanged();
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(RedemptionActivity.this, "ca-app-pub-1356475100439807/1261887227",
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
                        Activity activityContext = RedemptionActivity.this;
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
        paytmDialog = new Dialog(this);
        paytmDialog.setContentView(R.layout.dialog_paytm_redeem);
        paytmDialog.setCancelable(true);
        paytmDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        paytmDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paytmButton = paytmDialog.findViewById(R.id.paytm_redeem_btn);
        paytmEditText = paytmDialog.findViewById(R.id.peytm_edit_text);
        paytmButton.setEnabled(false);
        paytmButton.setTextColor(Color.argb(50, 255, 255, 255));
        ////////////////Lucky box dialog

        ////////////////Lucky box dialog
        dialogFreeFire = new Dialog(this);
        dialogFreeFire.setContentView(R.layout.dialog_free_fire_diamonds);
        dialogFreeFire.setCancelable(true);
        dialogFreeFire.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        dialogFreeFire.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        freeFireButton = dialogFreeFire.findViewById(R.id.free_fire_redeem_btn);
        freeFireEditText = dialogFreeFire.findViewById(R.id.free_fire_editText);
        freeFireButton.setEnabled(false);
        freeFireButton.setTextColor(Color.argb(50, 255, 255, 255));
        ////////////////Lucky box dialog

        ////////////////Lucky box dialog
        dialogGooglePay = new Dialog(this);
        dialogGooglePay.setContentView(R.layout.dialog_google_pay);
        dialogGooglePay.setCancelable(true);
        dialogGooglePay.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        dialogGooglePay.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        googlePayRedeemBtn = dialogGooglePay.findViewById(R.id.google_pay_redeem_btn);
        googlePayEditText = dialogGooglePay.findViewById(R.id.google_pay_editText);
        googlePayRedeemBtn.setEnabled(false);
        googlePayRedeemBtn.setTextColor(Color.argb(50, 255, 255, 255));
        ////////////////Lucky box dialog

        ////////////////Lucky box dialog
        dialogBankAccount = new Dialog(this);
        dialogBankAccount.setContentView(R.layout.dialog_bank_accounts);
        dialogBankAccount.setCancelable(true);
        dialogBankAccount.getWindow().setBackgroundDrawable(getDrawable(R.drawable.banner_slider_baground));
        dialogBankAccount.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bankRedeemBtn = dialogBankAccount.findViewById(R.id.continue_btn);
        accountNumber = dialogBankAccount.findViewById(R.id.account_number);
        reEnterNumber = dialogBankAccount.findViewById(R.id.re_enter_account_number);
        ifscCode = dialogBankAccount.findViewById(R.id.ifsc_code);
        accountName = dialogBankAccount.findViewById(R.id.account_holder_name);
        bankRedeemBtn.setEnabled(false);
        bankRedeemBtn.setTextColor(Color.argb(50, 255, 255, 255));
        ////////////////Lucky box dialog

        paytmRedeem();
        freeFireRedeem();
        googlePayRedeem();
        bankAccountRedeem();

        paytmEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(paytmEditText.getText())) {
                    paytmButton.setEnabled(false);
                    paytmButton.setTextColor(Color.argb(50, 255, 255, 255));
                    paytmEditText.setError("Enter paytm number");
                } else {
                    paytmButton.setEnabled(true);
                    paytmButton.setTextColor(Color.rgb(255, 255, 255));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        freeFireEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(freeFireEditText.getText())) {
                    freeFireButton.setEnabled(false);
                    freeFireButton.setTextColor(Color.argb(50, 255, 255, 255));
                    freeFireEditText.setError("Enter paytm number");
                } else {
                    freeFireButton.setEnabled(true);
                    freeFireButton.setTextColor(Color.rgb(255, 255, 255));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        googlePayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(googlePayEditText.getText())) {
                    googlePayRedeemBtn.setEnabled(false);
                    googlePayRedeemBtn.setTextColor(Color.argb(50, 255, 255, 255));
                    googlePayEditText.setError("Enter paytm number");
                } else {
                    googlePayRedeemBtn.setEnabled(true);
                    googlePayRedeemBtn.setTextColor(Color.rgb(255, 255, 255));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        reEnterNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkedInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        accountNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkedInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ifscCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkedInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        accountName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkedInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void bankAccountRedeem() {

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            coins = String.valueOf(task.getResult().getLong("coins"));
                            coinText.setText(coins);

                            firebaseFirestore.collection("EARNING")
                                    .document("REDEEM")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if (task.isSuccessful()) {
                                                Long limit = task.getResult().getLong("limit");
                                                String text = task.getResult().getString("text");
                                                textView7.setText(text);

                                                if (limit <= Long.valueOf(coins)) {
                                                    bank.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            dialogBankAccount.show();
                                                            bankRedeemBtn.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    dialogBankAccount.dismiss();
                                                                    loadingDialog.show();
                                                                    redemptionRecordBank(limit, accountNumber,ifscCode,accountName,"Bank account");
                                                                }
                                                            });
                                                        }
                                                    });
                                                } else {
                                                    bank.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            errorDialog.show();
                                                            errorText.setText("You do not have sufficient coin to redeem");
                                                        }
                                                    });
                                                }
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(RedemptionActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(RedemptionActivity.this, error, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void googlePayRedeem() {

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            coins = String.valueOf(task.getResult().getLong("coins"));
                            coinText.setText(coins);

                            firebaseFirestore.collection("EARNING")
                                    .document("REDEEM")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if (task.isSuccessful()) {
                                                Long limit = task.getResult().getLong("limit");
                                                String text = task.getResult().getString("text");
                                                textView7.setText(text);

                                                if (limit <= Long.valueOf(coins)) {
                                                    googlePay.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            dialogGooglePay.show();
                                                            googlePayRedeemBtn.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    dialogGooglePay.dismiss();
                                                                    loadingDialog.show();
                                                                    redemptionRecord(limit, googlePayEditText,"Google Pay");
                                                                }
                                                            });
                                                        }
                                                    });
                                                } else {
                                                    googlePay.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            errorDialog.show();
                                                            errorText.setText("You do not have sufficient coin to redeem");
                                                        }
                                                    });
                                                }
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(RedemptionActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(RedemptionActivity.this, error, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void freeFireRedeem() {

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            coins = String.valueOf(task.getResult().getLong("coins"));
                            coinText.setText(coins);

                            firebaseFirestore.collection("EARNING")
                                    .document("REDEEM")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if (task.isSuccessful()) {
                                                Long limit = task.getResult().getLong("limit");
                                                String text = task.getResult().getString("text");
                                                textView7.setText(text);

                                                if (limit <= Long.valueOf(coins)) {
                                                    freeFirDiamond.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            dialogFreeFire.show();
                                                            freeFireButton.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    dialogFreeFire.dismiss();
                                                                    loadingDialog.show();
                                                                    redemptionRecord(limit, freeFireEditText,"Free fire diamonds");
                                                                }
                                                            });
                                                        }
                                                    });
                                                } else {
                                                    freeFirDiamond.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            errorDialog.show();
                                                            errorText.setText("You do not have sufficient coin to redeem");
                                                        }
                                                    });
                                                }
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(RedemptionActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(RedemptionActivity.this, error, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void paytmRedeem() {

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            coins = String.valueOf(task.getResult().getLong("coins"));
                            coinText.setText(coins);

                            firebaseFirestore.collection("EARNING")
                                    .document("REDEEM")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if (task.isSuccessful()) {
                                                Long limit = task.getResult().getLong("limit");
                                                String text = task.getResult().getString("text");
                                                textView7.setText(text);

                                                if (limit <= Long.valueOf(coins)) {
                                                    paytm.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            paytmDialog.show();
                                                            paytmButton.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    paytmDialog.dismiss();
                                                                    loadingDialog.show();
                                                                    redemptionRecord(limit, paytmEditText,"Paytm");
                                                                }
                                                            });
                                                        }
                                                    });
                                                } else {
                                                    paytm.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            errorDialog.show();
                                                            errorText.setText("You do not have sufficient coin to redeem");
                                                        }
                                                    });
                                                }
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(RedemptionActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(RedemptionActivity.this, error, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void redemptionRecordBank(Long limit, EditText accountNumber,EditText ifscCode,EditText accountName,String source) {
        Map<String, Object> update = new HashMap<>();
        update.put("coins", Long.valueOf(coins) - limit);

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .update(update)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("redeemCoin", limit);
                            userInfo.put("name", DbQueries.fullName);
                            userInfo.put("email", DbQueries.email);
                            userInfo.put("uid", DbQueries.uid);
                            userInfo.put("number", accountNumber.getText().toString());
                            userInfo.put("ifscCode", ifscCode.getText().toString());
                            userInfo.put("accountHolderName", accountName.getText().toString());
                            userInfo.put("source", source);
                            userInfo.put("date", System.currentTimeMillis());
                            userInfo.put("process", "Pending");
                            userInfo.put("documentUid", random);
                            userInfo.put("sms", "You will receive your payment within 24hr");

                            firebaseFirestore.collection("PAYMENTS")
                                    .document(random)
                                    .set(userInfo)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Map<String, Object> userInfo = new HashMap<>();
                                                userInfo.put("redeemCoin", limit);
                                                userInfo.put("name", DbQueries.fullName);
                                                userInfo.put("email", DbQueries.email);
                                                userInfo.put("uid", DbQueries.uid);
                                                userInfo.put("number", accountNumber.getText().toString());
                                                userInfo.put("ifscCode", ifscCode.getText().toString());
                                                userInfo.put("accountHolderName", accountName.getText().toString());
                                                userInfo.put("source", source);
                                                userInfo.put("date", System.currentTimeMillis());
                                                userInfo.put("process", "Pending");
                                                userInfo.put("documentUid", random);
                                                userInfo.put("sms", "You will receive your payment within 24hr");

                                                firebaseFirestore.collection("USERS")
                                                        .document(FirebaseAuth.getInstance().getUid())
                                                        .collection("PAYMENTS")
                                                        .document(random)
                                                        .set(userInfo)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                String random = UUID.randomUUID().toString().substring(0, 28);
                                                                Map<String, Object> inviteRecord = new HashMap<>();
                                                                inviteRecord.put("date", System.currentTimeMillis());
                                                                inviteRecord.put("coins",  - limit);
                                                                inviteRecord.put("plusCoins",  - limit);
                                                                inviteRecord.put("source", source);

                                                                firebaseFirestore.collection("USERS")
                                                                        .document(FirebaseAuth.getInstance().getUid())
                                                                        .collection("RECORDS")
                                                                        .document(random)
                                                                        .set(inviteRecord)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                loadingDialog.dismiss();
                                                                                startActivity(new Intent(RedemptionActivity.this,DashBoardActivity.class));
                                                                                Toast.makeText(RedemptionActivity.this, "Your redeem request successfully finished", Toast.LENGTH_SHORT).show();
                                                                                coinText.setText(String.valueOf(Long.valueOf(coins) - limit));
                                                                            }
                                                                        });
                                                            }
                                                        });

                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(RedemptionActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(RedemptionActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void redemptionRecord(Long limit, EditText paytmNumber,String source) {
        Map<String, Object> update = new HashMap<>();
        update.put("coins", Long.valueOf(coins) - limit);

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .update(update)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("redeemCoin", limit);
                            userInfo.put("name", DbQueries.fullName);
                            userInfo.put("email", DbQueries.email);
                            userInfo.put("uid", DbQueries.uid);
                            userInfo.put("number", paytmNumber.getText().toString());
                            userInfo.put("source", source);
                            userInfo.put("date", System.currentTimeMillis());
                            userInfo.put("process", "Pending");
                            userInfo.put("documentUid", random);
                            userInfo.put("sms", "You will receive your payment within 24hr");

                            firebaseFirestore.collection("PAYMENTS")
                                    .document(random)
                                    .set(userInfo)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Map<String, Object> userInfo = new HashMap<>();
                                                userInfo.put("redeemCoin", limit);
                                                userInfo.put("name", DbQueries.fullName);
                                                userInfo.put("email", DbQueries.email);
                                                userInfo.put("uid", DbQueries.uid);
                                                userInfo.put("number", paytmNumber.getText().toString());
                                                userInfo.put("source", source);
                                                userInfo.put("date", System.currentTimeMillis());
                                                userInfo.put("process", "Pending");
                                                userInfo.put("documentUid", random);
                                                userInfo.put("sms", "You will receive your payment within 24hr");

                                                firebaseFirestore.collection("USERS")
                                                        .document(FirebaseAuth.getInstance().getUid())
                                                        .collection("PAYMENTS")
                                                        .document(random)
                                                        .set(userInfo)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                String random = UUID.randomUUID().toString().substring(0, 28);
                                                                Map<String, Object> inviteRecord = new HashMap<>();
                                                                inviteRecord.put("date", System.currentTimeMillis());
                                                                inviteRecord.put("coins",  - limit);
                                                                inviteRecord.put("plusCoins",  - limit);
                                                                inviteRecord.put("source", source);

                                                                firebaseFirestore.collection("USERS")
                                                                        .document(FirebaseAuth.getInstance().getUid())
                                                                        .collection("RECORDS")
                                                                        .document(random)
                                                                        .set(inviteRecord)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                loadingDialog.dismiss();
                                                                                startActivity(new Intent(RedemptionActivity.this,DashBoardActivity.class));
                                                                                Toast.makeText(RedemptionActivity.this, "Your redeem request successfully finished", Toast.LENGTH_SHORT).show();
                                                                                coinText.setText(String.valueOf(Long.valueOf(coins) - limit));
                                                                            }
                                                                        });
                                                            }
                                                        });

                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(RedemptionActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(RedemptionActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkedInput() {
        if (!TextUtils.isEmpty(accountNumber.getText())) {
            if (!TextUtils.isEmpty(reEnterNumber.getText())) {
                if (!TextUtils.isEmpty(ifscCode.getText())) {
                    if (!TextUtils.isEmpty(accountName.getText())) {
                        bankRedeemBtn.setEnabled(true);
                        bankRedeemBtn.setTextColor(Color.rgb(255, 255, 255));
                    } else {
                        bankRedeemBtn.setEnabled(false);
                        bankRedeemBtn.setTextColor(Color.argb(50, 255, 255, 255));
                    }
                } else {
                    bankRedeemBtn.setEnabled(false);
                    bankRedeemBtn.setTextColor(Color.argb(50, 255, 255, 255));
                }
            } else {
                bankRedeemBtn.setEnabled(false);
                bankRedeemBtn.setTextColor(Color.argb(50, 255, 255, 255));
            }
        } else {
            bankRedeemBtn.setEnabled(false);
            bankRedeemBtn.setTextColor(Color.argb(50, 255, 255, 255));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RedemptionActivity.this,DashBoardActivity.class));
    }
}