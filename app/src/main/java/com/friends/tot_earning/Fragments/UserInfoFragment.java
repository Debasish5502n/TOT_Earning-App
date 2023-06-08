package com.friends.tot_earning.Fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
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

import com.bumptech.glide.Glide;
import com.friends.tot_earning.DataBase.DbQueries;
import com.friends.tot_earning.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoFragment extends Fragment {

    CircleImageView imageView;
    Button updateImage, removeImage;
    EditText emailField, nameField;
    Uri filePath;
    boolean updatePhoto=false;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    Dialog loadingDialog, passwordDialog;
    EditText password;
    AppCompatButton saveBtn,updateBtn;
    String name, email, photo;
    FirebaseStorage storage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        imageView = view.findViewById(R.id.circleImageView);
        updateImage = view.findViewById(R.id.update_image);
        removeImage = view.findViewById(R.id.remove);
        updateBtn = view.findViewById(R.id.update_btn);
        emailField = view.findViewById(R.id.email);
        nameField = view.findViewById(R.id.name);

        ////////////////loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.banner_slider_baground));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ////////////////loading dialog

        ////////////////password dialog
        passwordDialog = new Dialog(getContext());
        passwordDialog.setContentView(R.layout.password_conformation_dialog);
        passwordDialog.setCancelable(true);
        passwordDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.banner_slider_baground));
        passwordDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        saveBtn = passwordDialog.findViewById(R.id.done_btn);
        password = passwordDialog.findViewById(R.id.password);
        ////////////////password dialog

        name = getArguments().getString("name");
        email = getArguments().getString("email");
        photo = getArguments().getString("photo");

        Glide.with(getContext()).load(photo).placeholder(R.drawable.avatarra).into(imageView);
        nameField.setText(name);
        emailField.setText(email);

        storage=FirebaseStorage.getInstance();

        emailField.addTextChangedListener(new TextWatcher() {
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
        nameField.addTextChangedListener(new TextWatcher() {
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

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(getActivity())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();

                            }
                        })
                        .check();
            }
        });

        removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filePath=null;
                updatePhoto=true;
                Glide.with(getContext()).load(R.drawable.avatarra).into(imageView);
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkemailandPassword();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            filePath = data.getData();
            updatePhoto=true;
            Glide.with(getContext()).load(filePath).into(imageView);

        }
    }

    private void checkemailandPassword() {

        Drawable customErrorIcon = getResources().getDrawable(R.drawable.custom_error_icon);
        customErrorIcon.setBounds(0, 0, customErrorIcon.getIntrinsicWidth(), customErrorIcon.getIntrinsicHeight());
        if (emailField.getText().toString().matches(emailPattern)) {
            ////
            if (emailField.getText().toString().toLowerCase().trim().equals(email.toLowerCase().trim())) {
                loadingDialog.show();
                updatePhoto();
            } else {
                passwordDialog.show();
                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingDialog.show();
                        String userPassword = password.getText().toString();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        passwordDialog.dismiss();

                        AuthCredential credential = EmailAuthProvider
                                .getCredential(email, userPassword);


                        //       17 Prompt the user to re-provide their sign-in credentials

                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    user.updateEmail(emailField.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                /////////update Photo
                                                updatePhoto();
                                                /////////update Photo
                                            }else {
                                                loadingDialog.dismiss();
                                                String error=task.getException().getMessage();
                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else {
                                    loadingDialog.dismiss();
                                    String error=task.getException().getMessage();
                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        } else {
            emailField.setError("Invalid Email!");
        }

    }

    private void updatePhoto(){
        if (updatePhoto){
            if (filePath !=null){
                StorageReference reference = storage.getReference().child("profiles").child(FirebaseAuth.getInstance().getUid());
                reference.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (task.isSuccessful()){
                                        filePath=uri;
                                        DbQueries.profileImage = uri.toString();
                                        Glide.with(getContext()).load(DbQueries.profileImage).into(imageView);

                                        Map<String,Object> updateProfile=new HashMap<>();
                                        updateProfile.put("email",emailField.getText().toString());
                                        updateProfile.put("name",nameField.getText().toString());
                                        updateProfile.put("profile",DbQueries.profileImage);

                                        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                                                .update(updateProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    if (updateProfile.size() >1){
                                                        DbQueries.email=emailField.getText().toString().trim();
                                                        DbQueries.fullName=nameField.getText().toString().trim();
                                                    }else {
                                                        DbQueries.fullName=nameField.getText().toString().trim();
                                                    }
                                                    getActivity().finish();
                                                    Toast.makeText(getContext(), "Successfully updated", Toast.LENGTH_SHORT).show();
                                                }else {
                                                    String error=task.getException().getMessage();
                                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                }
                                                loadingDialog.dismiss();
                                            }
                                        });

                                    }else {
                                        loadingDialog.dismiss();
                                        DbQueries.profileImage ="";
                                        String error=task.getException().getMessage();
                                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                    }
                });
            }else {///remove photo
                DbQueries.profileImage ="";

                Map<String,Object> updateProfile=new HashMap<>();
                updateProfile.put("email",emailField.getText().toString());
                updateProfile.put("name",nameField.getText().toString());
                updateProfile.put("profile","");

                FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                        .update(updateProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            if (updateProfile.size() >1){
                                DbQueries.email=emailField.getText().toString().trim();
                                DbQueries.fullName=nameField.getText().toString().trim();
                            }else {
                                DbQueries.fullName=nameField.getText().toString().trim();
                            }
                            getActivity().finish();
                            Toast.makeText(getContext(), "Successfully updated", Toast.LENGTH_SHORT).show();
                        }else {
                            String error=task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
            }
        }else {
            Map<String,Object> updateProfile=new HashMap<>();
            updateProfile.put("name",nameField.getText().toString());
            updateProfile.put("email",emailField.getText().toString());

            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                    .update(updateProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        if (updateProfile.size() >1){
                            DbQueries.email=emailField.getText().toString().trim();
                            DbQueries.fullName=nameField.getText().toString().trim();
                        }else {
                            DbQueries.fullName=nameField.getText().toString().trim();
                        }
                        getActivity().finish();
                        Toast.makeText(getContext(), "Successfully updated", Toast.LENGTH_SHORT).show();
                    }else {
                        String error=task.getException().getMessage();
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();
                }
            });
        }
    }

    private void checkedInput() {
        if (!TextUtils.isEmpty(emailField.getText())) {
            if (!TextUtils.isEmpty(nameField.getText())) {
                updateBtn.setEnabled(true);
                updateBtn.setTextColor(Color.rgb(255, 255, 255));

            } else {
                updateBtn.setEnabled(false);
                updateBtn.setTextColor(Color.argb(50, 255, 255, 255));
            }
        } else {
            updateBtn.setEnabled(false);
            updateBtn.setTextColor(Color.argb(50, 255, 255, 255));
        }
    }
}