package com.friends.tot_earning.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.friends.tot_earning.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreenActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        auth = FirebaseAuth.getInstance();

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (auth.getCurrentUser() != null) {
                        FirebaseFirestore.getInstance()
                                .collection("EARNING")
                                .document("Maintenance")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            String Maintain = String.valueOf(task.getResult().getString("appMaintenance"));

                                            if (Maintain.equals("ON")){
                                                startActivity(new Intent(SplashScreenActivity.this, MaintenanceActivity.class));
                                                finish();
                                            }else {
                                                startActivity(new Intent(SplashScreenActivity.this, DashBoardActivity.class));
                                                finish();
                                            }
                                        }else {
                                            String error=task.getResult().toString();
                                            Toast.makeText(SplashScreenActivity.this, error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }
        };
        timer.start();

    }
}