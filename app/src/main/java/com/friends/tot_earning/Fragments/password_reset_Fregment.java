package com.friends.tot_earning.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.friends.tot_earning.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class password_reset_Fregment extends Fragment {

    FirebaseAuth auth;
    EditText resetEmail;
    Button resetButton;
    TextView goBack;

    LinearLayout linearLayout;
    ImageView emailIcon;
    TextView emailText;
    ProgressBar progressBar;

    FrameLayout frameLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_password_reset__fregment, container, false);
        auth= FirebaseAuth.getInstance();
        resetButton=view.findViewById(R.id.reset_btn);
        resetEmail=view.findViewById(R.id.reset_email);
        goBack=view.findViewById(R.id.reset_go_back);
        frameLayout=getActivity().findViewById(R.id.frameLayout);

        linearLayout=view.findViewById(R.id.linearLayout);
        emailIcon=view.findViewById(R.id.reset_email_icon);
        emailText=view.findViewById(R.id.reset_email_text);
        progressBar=view.findViewById(R.id.progressBar);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SigniinFregment());
            }
        });

        resetEmail.addTextChangedListener(new TextWatcher() {
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

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emailText.setText("successfully reset link sent to your registered email");
                TransitionManager.beginDelayedTransition(linearLayout);
                emailText.setVisibility(View.GONE);

                TransitionManager.beginDelayedTransition(linearLayout);
                emailIcon.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                resetButton.setEnabled(false);
                resetButton.setTextColor(Color.argb(50,255,255,255));
                auth.sendPasswordResetEmail(resetEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    Glide.with(emailIcon).load(R.drawable.greem_email).into(emailIcon);
                                    emailText.setTextColor(getResources().getColor(R.color.successGreen));
                                    TransitionManager.beginDelayedTransition(linearLayout);
                                    emailText.setVisibility(View.VISIBLE);
                                    emailIcon.setVisibility(View.VISIBLE);
                                }else {
                                    String error=task.getException().getMessage();
                                    Glide.with(emailIcon).load(R.drawable.red_email).into(emailIcon);

                                    emailText.setText(error);
                                    emailText.setTextColor(getResources().getColor(R.color.red));
                                    TransitionManager.beginDelayedTransition(linearLayout);
                                    emailText.setVisibility(View.VISIBLE);
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                                resetButton.setEnabled(true);
                                resetButton.setTextColor(Color.rgb(255,255,255));
                            }
                        });
            }
        });

        return view;
    }
    private void setFragment(SigniinFregment fragment) {
        FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left,R.anim.slideout_from_right);
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
    private void checkedInput() {
        if (!TextUtils.isEmpty(resetEmail.getText())){
                resetButton.setEnabled(true);
                resetButton.setTextColor(Color.rgb(255,255,255));
        }else {
            resetButton.setEnabled(false);
            resetButton.setTextColor(Color.argb(50,255,255,255));
        }
    }
}