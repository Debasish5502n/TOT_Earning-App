package com.friends.tot_earning.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.friends.tot_earning.Fragments.PasswordFragment;
import com.friends.tot_earning.Fragments.UserInfoFragment;
import com.friends.tot_earning.R;
import com.google.android.material.tabs.TabLayout;


public class UpdateUserActivity extends AppCompatActivity {

    TabLayout tableLayout;
    FrameLayout frameLayout;
    UserInfoFragment userInfoFragment;
    PasswordFragment passwordFragment;
    String name, email, profilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        tableLayout = findViewById(R.id.tab_layout);
        frameLayout = findViewById(R.id.frameLayout);
        userInfoFragment = new UserInfoFragment();
        passwordFragment = new PasswordFragment();

        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        profilePhoto = getIntent().getStringExtra("profile");

        tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    setFragment(userInfoFragment, true);
                }
                if (tab.getPosition() == 1) {
                    setFragment(passwordFragment, false);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tableLayout.getTabAt(0).select();
        setFragment(userInfoFragment, true);
    }

    private void setFragment(Fragment fragment, boolean setBundle) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        if (setBundle) {
            bundle.putString("name", name);
            bundle.putString("email", email);
            bundle.putString("photo", profilePhoto);
            fragment.setArguments(bundle);
        }else {
            bundle.putString("email", email);
            fragment.setArguments(bundle);
        }
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

}