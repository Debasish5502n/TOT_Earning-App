package com.friends.tot_earning.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.friends.tot_earning.Fragments.SigniinFregment;
import com.friends.tot_earning.Fragments.SignupFragment;
import com.friends.tot_earning.R;

public class LoginActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    public static boolean onresetPassword=false;
    public static boolean signUpFragment=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        frameLayout=findViewById(R.id.frameLayout);

//        getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (signUpFragment){
            signUpFragment =false;
            setFragment(new SignupFragment());
        }else {
            setDefaultFregment(new SigniinFregment());
        }
    }

    private void setDefaultFregment(SigniinFregment fragment) {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            SigniinFregment.disableCloseBtn =false;
            SignupFragment.disableCloseBtn =false;
            if (onresetPassword) {
                onresetPassword =false;
                setDefaultFregment(new SigniinFregment());
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
}