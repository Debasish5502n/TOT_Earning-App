package com.friends.tot_earning.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.friends.tot_earning.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.URLEncoder;

public class IntroductionActivity extends AppCompatActivity {

    WebView pdfview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        pdfview = (WebView) findViewById(R.id.viewpdf);
        pdfview.getSettings().setJavaScriptEnabled(true);

        FirebaseFirestore.getInstance()
                .collection("PDF")
                .document("pdfView")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String fileurl = String.valueOf(task.getResult().getString("pdf"));

                            final ProgressDialog pd = new ProgressDialog(IntroductionActivity.this);
                            pd.setTitle("PDF");
                            pd.setMessage("Opening....!!!");


                            pdfview.setWebViewClient(new WebViewClient() {
                                @Override
                                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                    super.onPageStarted(view, url, favicon);
                                    pd.show();
                                }

                                @Override
                                public void onPageFinished(WebView view, String url) {
                                    super.onPageFinished(view, url);
                                    pd.dismiss();
                                }
                            });

                            String url = "";
                            try {
                                url = URLEncoder.encode(fileurl, "UTF-8");
                            } catch (Exception ex) {
                            }

                            pdfview.loadUrl("http://docs.google.com/viewer?url=" + url + "&embedded=true");
                        } else {
                            String error = task.getResult().toString();
                            Toast.makeText(IntroductionActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}