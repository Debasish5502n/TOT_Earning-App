package com.friends.tot_earning.Fragments;


import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

//import com.example.bbkart.Activity.MainActivity;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.FirebaseFirestore;

import com.friends.tot_earning.Activity.DashBoardActivity;
import com.friends.tot_earning.Model.User;
import com.bumptech.glide.Glide;
import com.friends.tot_earning.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupFragment extends Fragment {

    TextView already_have_an_account;
    FrameLayout frameLayout;

    EditText email, name, password, conform_password, phoneNumber, refer;
    AppCompatButton signup;
    ProgressBar progressBar;
    CircleImageView profileImage;
    Uri selectedImage;

    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    public static boolean disableCloseBtn = false;
    User user;
    FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup_report, container, false);
        already_have_an_account = view.findViewById(R.id.already_have_an_account);
        frameLayout = getActivity().findViewById(R.id.frameLayout);

        email = view.findViewById(R.id.sign_up_email);
        name = view.findViewById(R.id.sign_up_name);
        password = view.findViewById(R.id.sign_up_password);
        conform_password = view.findViewById(R.id.sign_up_conform_password);
        signup = view.findViewById(R.id.sign_up_ptn);
        phoneNumber = view.findViewById(R.id.sign_up_number);
        progressBar = view.findViewById(R.id.sign_up_progressbar);
        profileImage = view.findViewById(R.id.profile_image);
        refer = view.findViewById(R.id.sign_up_referCode);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(getActivity())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                // permission is granted, open the camera
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent, 45);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                // check for permanent denial of permission
                                if (response.isPermanentlyDenied()) {
                                    // navigate user to app settings
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        email.addTextChangedListener(new TextWatcher() {
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
        name.addTextChangedListener(new TextWatcher() {
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
        password.addTextChangedListener(new TextWatcher() {
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
        phoneNumber.addTextChangedListener(new TextWatcher() {
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
        conform_password.addTextChangedListener(new TextWatcher() {
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
        refer.addTextChangedListener(new TextWatcher() {
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
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkemailandPassword();
            }
        });

        already_have_an_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SigniinFregment());
            }
        });
        return view;
    }

    private void checkemailandPassword() {

        Drawable customErrorIcon = getResources().getDrawable(R.drawable.custom_error_icon);
        customErrorIcon.setBounds(0, 0, customErrorIcon.getIntrinsicWidth(), customErrorIcon.getIntrinsicHeight());
        if (email.getText().toString().matches(emailPattern)) {
            if (password.getText().toString().equals(conform_password.getText().toString())) {

                progressBar.setVisibility(View.VISIBLE);
                signup.setEnabled(false);
                signup.setTextColor(Color.argb(50, 255, 255, 255));
                auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    if (selectedImage != null) {
                                        StorageReference reference = FirebaseStorage.getInstance().getReference().child("profiles").child(auth.getUid());
                                        reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {

                                                            String fullName = name.getText().toString();
                                                            String emailAddress = email.getText().toString();
                                                            String phone = phoneNumber.getText().toString();
                                                            String referCode = refer.getText().toString();
                                                            String uid = auth.getUid();
                                                            String profile = uri.toString();
                                                            long coins = 25;
                                                            long totalCoins = 25;
                                                            long date = System.currentTimeMillis();
                                                            long illegalActivity = 0;
                                                            String unBanned = "unBanned";

                                                            user = new User(fullName, emailAddress, profile, referCode, coins, totalCoins, phone, uid, date,illegalActivity,unBanned);
                                                            firebaseFirestore
                                                                    .collection("USERS")
                                                                    .document(auth.getUid())
                                                                    .set(user)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {

                                                                                referCoin();
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
                                        });

                                    } else {
                                        String fullName = name.getText().toString();
                                        String emailAddress = email.getText().toString();
                                        String phone = phoneNumber.getText().toString();
                                        String referCode = refer.getText().toString();
                                        String uid = auth.getUid();
                                        String profile = "";
                                        long coins = 25;
                                        long totalCoins = 25;
                                        long date = System.currentTimeMillis();
                                        long illegalActivity = 0;
                                        String unBanned = "unBanned";

                                        user = new User(fullName, emailAddress, profile, referCode, coins, totalCoins, phone, uid, date,illegalActivity,unBanned);
                                        firebaseFirestore
                                                .collection("USERS")
                                                .document(auth.getUid())
                                                .set(user)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            referCoin();
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                } else {

                                }
                            }
                        });
            } else {
                conform_password.setError("Doesn't match email!");
            }
        } else {
            email.setError("Invalid Email!");
        }

    }

    private void setFragment(SigniinFregment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void checkedInput() {
        if (!TextUtils.isEmpty(email.getText())) {
            if (!TextUtils.isEmpty(name.getText())) {
                if (!TextUtils.isEmpty(password.getText()) && password.length() >= 8) {
                    if (!TextUtils.isEmpty(conform_password.getText()) && conform_password.length() >= 8) {
                        signup.setEnabled(true);
                        signup.setTextColor(Color.rgb(255, 255, 255));
                    } else {
                        signup.setEnabled(false);
                        signup.setTextColor(Color.argb(50, 255, 255, 255));
                    }
                } else {
                    signup.setEnabled(false);
                    signup.setTextColor(Color.argb(50, 255, 255, 255));
                }
            } else {
                signup.setEnabled(false);
                signup.setTextColor(Color.argb(50, 255, 255, 255));
            }
        } else {
            signup.setEnabled(false);
            signup.setTextColor(Color.argb(50, 255, 255, 255));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (data.getData() != null) {
                profileImage.setImageURI(data.getData());
                Glide
                        .with(getContext())
                        .load(data.getData())
                        .into(profileImage);
                selectedImage = data.getData();
            }
        }
    }

    private void checkIn() {
        Map<Object, Long> time = new HashMap<>();
        time.put("day1", (long) System.currentTimeMillis());
        time.put("day2", (long) 0);
        time.put("day3", (long) 0);
        time.put("day4", (long) 0);
        time.put("day5", (long) 0);
        time.put("day6", (long) 0);
        time.put("day7", (long) 0);

        Map<Object, Long> spinWheel = new HashMap<>();
        spinWheel.put("limit", System.currentTimeMillis());

        Map<Object, Long> luckyBox = new HashMap<>();
        luckyBox.put("limit", System.currentTimeMillis());

        Map<Object, Long> playQuiz = new HashMap<>();
        playQuiz.put("limit", System.currentTimeMillis());

        Map<Object, Long> watchVideo = new HashMap<>();
        watchVideo.put("limit", System.currentTimeMillis());

        Map<Object, Long> scratchCard = new HashMap<>();
        scratchCard.put("limit", System.currentTimeMillis());

        List<String> documentsName = new ArrayList<>();
        documentsName.add(new String("SPIN_WHEEL"));
        documentsName.add(new String("LUCKY_BOX"));
        documentsName.add(new String("PLAY_QUIZ"));
        documentsName.add(new String("WATCH_VIDEO"));
        documentsName.add(new String("SCRATCH_CARD"));
        documentsName.add(new String("DAYS"));

        List<Map<Object, Long>> documentFields = new ArrayList<>();
        documentFields.add(spinWheel);
        documentFields.add(luckyBox);
        documentFields.add(playQuiz);
        documentFields.add(watchVideo);
        documentFields.add(scratchCard);
        documentFields.add(time);

        for (int x = 0; x < documentsName.size(); x++) {

            int finalX = x;
            firebaseFirestore.collection("USERS")
                    .document(auth.getUid())
                    .collection("USER_DATA")
                    .document(documentsName.get(x))
                    .set(documentFields.get(x))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (finalX == documentsName.size() - 1) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    userCoinRecords();
                                    disableCloseBtn = false;
                                }
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                signup.setEnabled(true);
                                signup.setTextColor(Color.rgb(255, 255, 255));
                                String error = task.getException().getMessage();
                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void userCoinRecords() {
        if (!refer.getText().toString().equals("")) {
            if (refer.length() >= 20) {

                firebaseFirestore.collection("EARNING")
                        .document("INVITE_BONUS")
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String inviteCoin = String.valueOf(task.getResult().getLong("coins"));

                        Map<String, Object> records = new HashMap<>();
                        records.put("date", System.currentTimeMillis());
                        records.put("coins", Long.valueOf(inviteCoin));
                        records.put("plusCoins", Long.valueOf(inviteCoin));
                        records.put("source", "Sign up bonus");

                        firebaseFirestore.collection("USERS")
                                .document(auth.getUid())
                                .collection("RECORDS")
                                .document("besrberb788ntr7rt84rt")
                                .set(records)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            String random = UUID.randomUUID().toString().substring(0, 28);
                                            Map<String, Object> inviteRecord = new HashMap<>();
                                            inviteRecord.put("date", System.currentTimeMillis());
                                            inviteRecord.put("coins", Long.valueOf(inviteCoin));
                                            inviteRecord.put("plusCoins", Long.valueOf(inviteCoin));
                                            inviteRecord.put("source", "Invitation Bonus");

                                            firebaseFirestore.collection("USERS")
                                                    .document(refer.getText().toString())
                                                    .collection("RECORDS")
                                                    .document(random)
                                                    .set(inviteRecord)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Map<String, Object> user = new HashMap<>();
                                                                user.put("coins", Long.valueOf(inviteCoin));
                                                                user.put("totalCoins", Long.valueOf(inviteCoin));

                                                                firebaseFirestore
                                                                        .collection("USERS")
                                                                        .document(auth.getUid())
                                                                        .update(user)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {

                                                                                    Map<String, Object> inviteUser = new HashMap<>();
                                                                                    inviteUser.put("name", name.getText().toString());
                                                                                    inviteUser.put("email", email.getText().toString());
                                                                                    inviteUser.put("uid", auth.getUid());
                                                                                    inviteUser.put("date", Long.valueOf(System.currentTimeMillis()));
                                                                                    firebaseFirestore.collection("USERS")
                                                                                            .document(refer.getText().toString())
                                                                                            .collection("INVITATION")
                                                                                            .document(auth.getUid())
                                                                                            .set(inviteUser)
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (task.isSuccessful()) {

                                                                                                        startActivity(new Intent(getActivity(), DashBoardActivity.class));
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

            } else {
                Map<String, Object> records = new HashMap<>();
                records.put("date", System.currentTimeMillis());
                records.put("coins", 25);
                records.put("plusCoins", 25);
                records.put("source", "Sign up bonus");

                firebaseFirestore.collection("USERS")
                        .document(auth.getUid())
                        .collection("RECORDS")
                        .document("besrberb788ntr7rt84rt")
                        .set(records)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(getActivity(), DashBoardActivity.class));
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        } else {
            Map<String, Object> records = new HashMap<>();
            records.put("date", System.currentTimeMillis());
            records.put("coins", 25);
            records.put("plusCoins", 25);
            records.put("source", "Sign up bonus");

            firebaseFirestore.collection("USERS")
                    .document(auth.getUid())
                    .collection("RECORDS")
                    .document("besrberb788ntr7rt84rt")
                    .set(records)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(getActivity(), DashBoardActivity.class));
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    private void referCoin() {
        if (!refer.getText().toString().equals("")) {
            if (refer.length() >= 20) {
                firebaseFirestore.collection("USERS")
                        .document(refer.getText().toString())
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User updateCoin = documentSnapshot.toObject(User.class);

                        //binding.currentCoins.setText(user.getCoins() + "");

                        firebaseFirestore.collection("EARNING")
                                .document("INVITE_BONUS")
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                String inviteCoin = String.valueOf(task.getResult().getLong("coins"));

                                Map<String, Object> update = new HashMap<>();
                                update.put("coins", Long.valueOf(updateCoin.getCoins()) + Long.valueOf(inviteCoin));
                                update.put("totalCoins", Long.valueOf(updateCoin.getTotalCoins()) + Long.valueOf(inviteCoin));

                                firebaseFirestore
                                        .collection("USERS")
                                        .document(refer.getText().toString())
                                        .update(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        checkIn();

                                    }
                                });
                            }
                        });
                    }
                });
            } else {
                checkIn();
            }

        } else {
            checkIn();

        }
    }

}