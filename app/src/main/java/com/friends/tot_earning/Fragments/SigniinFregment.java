package com.friends.tot_earning.Fragments;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.friends.tot_earning.Activity.DashBoardActivity;
import com.friends.tot_earning.Activity.LoginActivity;
import com.friends.tot_earning.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SigniinFregment extends Fragment {

    TextView dont_have_an_account;
    TextView password_reset;
    FrameLayout frameLayout;

    EditText email,password;
    AppCompatButton signin;
    ProgressBar progressBar;

    FirebaseAuth auth;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    public static boolean disableCloseBtn =false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_signiin_fregment, container, false);
        dont_have_an_account=view.findViewById(R.id.sign_up);
        frameLayout=getActivity().findViewById(R.id.frameLayout);

        email=view.findViewById(R.id.sign_in_email);
        password=view.findViewById(R.id.sign_in_password);
        signin=view.findViewById(R.id.sign_in_btn);
        progressBar=view.findViewById(R.id.sign_in_progressbar);
        password_reset=view.findViewById(R.id.sign_in_reset_password);

        auth= FirebaseAuth.getInstance();

        password_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.onresetPassword =true;
                setFragment(new password_reset_Fregment());
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

        dont_have_an_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignupFragment());
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkemailandPassword();
            }
        });
        return view;
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right,R.anim.slideout_from_left);
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
    private void checkedInput() {
        if (!TextUtils.isEmpty(email.getText())){
                if (!TextUtils.isEmpty(password.getText())){
                    signin.setEnabled(true);
                    signin.setTextColor(Color.rgb(255,255,255));
            }else {
                signin.setEnabled(false);
                signin.setTextColor(Color.argb(50,255,255,255));
            }
        }else {
            signin.setEnabled(false);
            signin.setTextColor(Color.argb(50,255,255,255));
        }
    }

    private void checkemailandPassword() {

        Drawable customErrorIcon=getResources().getDrawable(R.drawable.custom_error_icon);
        customErrorIcon.setBounds(0,0,customErrorIcon.getIntrinsicWidth(),customErrorIcon.getIntrinsicHeight());
        if (email.getText().toString().matches(emailPattern)){
            if (password.length() >=8){

                progressBar.setVisibility(View.VISIBLE);
                signin.setEnabled(false);
                signin.setTextColor(Color.argb(50,255,255,255));
                auth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){

                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(getActivity(), DashBoardActivity.class));
                                    disableCloseBtn =false;
                                    getActivity().finish();

                                }else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    signin.setEnabled(true);
                                    signin.setTextColor(Color.rgb(255,255,255));
                                    String error=task.getException().getMessage();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else {
                password.setError("Doesn't match email!");
            }
        }else {
            email.setError("Invalid Email!");
        }

    }
}