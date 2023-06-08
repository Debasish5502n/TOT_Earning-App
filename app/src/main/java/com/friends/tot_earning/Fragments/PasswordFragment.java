package com.friends.tot_earning.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.friends.tot_earning.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordFragment extends Fragment {

    EditText oldPassword, newPassword, conformNewPassword;
    Button saveBtn;
    Dialog loadingDialog;
    String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_password, container, false);
        oldPassword = view.findViewById(R.id.old_password);
        newPassword = view.findViewById(R.id.new_password);
        conformNewPassword = view.findViewById(R.id.new_conform_password);
        saveBtn = view.findViewById(R.id.save_btn);

        ////////////////loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.banner_slider_baground));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ////////////////loading dialog
        email = getArguments().getString("email");

        oldPassword.addTextChangedListener(new TextWatcher() {
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
        newPassword.addTextChangedListener(new TextWatcher() {
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
        conformNewPassword.addTextChangedListener(new TextWatcher() {
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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                checkemailandPassword();
            }
        });

        return view;
    }

    private void checkemailandPassword() {

        Drawable customErrorIcon = getResources().getDrawable(R.drawable.custom_error_icon);
        customErrorIcon.setBounds(0, 0, customErrorIcon.getIntrinsicWidth(), customErrorIcon.getIntrinsicHeight());

        if (newPassword.getText().toString().equals(conformNewPassword.getText().toString())) {

            AuthCredential credential = EmailAuthProvider
                    .getCredential(email, oldPassword.getText().toString());
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


            //       17 Prompt the user to re-provide their sign-in credentials

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    oldPassword.setText(null);
                                    newPassword.setText(null);
                                    conformNewPassword.setText(null);
                                    getActivity().finish();
                                    Toast.makeText(getContext(), "password updated successfully", Toast.LENGTH_SHORT).show();
                                }else {
                                    String error=task.getException().getMessage();
                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                }
                                loadingDialog.dismiss();
                            }
                        });
                    }else {
                        loadingDialog.dismiss();
                        String error=task.getException().getMessage();
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            loadingDialog.dismiss();
            conformNewPassword.setError("Doesn't match email!");
        }

    }

    private void checkedInput() {
        if (!TextUtils.isEmpty(oldPassword.getText()) && oldPassword.length() >= 8) {
            if (!TextUtils.isEmpty(newPassword.getText()) && newPassword.length() >= 8) {
                if (!TextUtils.isEmpty(conformNewPassword.getText()) && conformNewPassword.length() >= 8) {
                    saveBtn.setEnabled(true);
                    saveBtn.setTextColor(Color.rgb(255, 255, 255));
                } else {
                    saveBtn.setEnabled(false);
                    saveBtn.setTextColor(Color.argb(50, 255, 255, 255));
                }
            } else {
                saveBtn.setEnabled(false);
                saveBtn.setTextColor(Color.argb(50, 255, 255, 255));
            }
        } else {
            saveBtn.setEnabled(false);
            saveBtn.setTextColor(Color.argb(50, 255, 255, 255));
        }
    }
}
