package com.friends.tot_earning.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.friends.tot_earning.R;


public class ScoreActivity extends AppCompatActivity {

    TextView total,score;
    AppCompatButton done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        total=findViewById(R.id.total);
        score=findViewById(R.id.score);
        done=findViewById(R.id.done_btn);

        total.setText("OUT OF "+String.valueOf(getIntent().getIntExtra("total",0)));
        score.setText(String.valueOf(getIntent().getIntExtra("score",0)));

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScoreActivity.this, DashBoardActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ScoreActivity.this, DashBoardActivity.class));
    }
}