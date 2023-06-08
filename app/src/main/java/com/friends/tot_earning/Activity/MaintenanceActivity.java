package com.friends.tot_earning.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.friends.tot_earning.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MaintenanceActivity extends AppCompatActivity {

    AppCompatButton updateBtn;
    TextView updateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenace);
        updateBtn = findViewById(R.id.update_btn);
        updateText = findViewById(R.id.update_text);

        FirebaseFirestore.getInstance()
                .collection("EARNING")
                .document("Maintenance")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String text = String.valueOf(task.getResult().getString("text"));

                            updateText.setText(text);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(MaintenanceActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse(
                        "https://play.google.com/store/apps/details?id=com.example.thugsoffacts"));
                intent1.setPackage("com.android.vending");
                startActivity(intent1);
            }
        });
    }
}