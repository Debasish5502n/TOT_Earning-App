package com.friends.tot_earning.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friends.tot_earning.Activity.IntroductionActivity;
import com.friends.tot_earning.Activity.InviteFriendsActivity;
import com.friends.tot_earning.Activity.LoginActivity;
import com.friends.tot_earning.Activity.RedemptionActivity;
import com.friends.tot_earning.Activity.TermsAndConditions;
import com.friends.tot_earning.Activity.UpdateUserActivity;
import com.friends.tot_earning.DataBase.DbQueries;
import com.bumptech.glide.Glide;
import com.friends.tot_earning.R;
import com.friends.tot_earning.databinding.FragmentAccountBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {

    FragmentAccountBinding binding;
    private final String CHECKEDITEM = "checked_item";

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Integer checkedItem;
    String selected;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAccountBinding.inflate(inflater, container, false);

        binding.fullName.setText(DbQueries.fullName);
        binding.email.setText(DbQueries.email);

        preferences=getActivity().getSharedPreferences("Them",MODE_PRIVATE);
        editor=preferences.edit();
        switch (getCheckedItem()) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
        }

        if (DbQueries.profileImage.equals("")) {
            Glide.with(getActivity()).load(R.drawable.avatarra).into(binding.profileImage);
        } else {
            try {
                Glide.with(getActivity()).load(DbQueries.profileImage).placeholder(R.drawable.avatarra).into(binding.profileImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        binding.updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), UpdateUserActivity.class);
                intent.putExtra("name",DbQueries.fullName);
                intent.putExtra("email",DbQueries.email);
                intent.putExtra("profile",DbQueries.profileImage);
                startActivity(intent);
            }
        });

        binding.logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });
        binding.wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), RedemptionActivity.class));
            }
        });
        binding.inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), InviteFriendsActivity.class));
            }
        });
        binding.dayNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog1();
            }
        });
        binding.rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse(
                        "https://play.google.com/store/apps/details?id=com.example.thugsoffacts"));
                intent1.setPackage("com.android.vending");
                startActivity(intent1);
            }
        });

        binding.customerSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] addresses={"thugsoffacts@gmail.com"};
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                //  intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                startActivity(Intent.createChooser(intent,"Email"));
            }
        });

        binding.instruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), IntroductionActivity.class));
            }
        });

        binding.termAndCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TermsAndConditions.class));
            }
        });
        return binding.getRoot();
    }

    private void showDialog1() {

        String[] thems = this.getResources().getStringArray(R.array.Them);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Select them");
        builder.setSingleChoiceItems(R.array.Them, getCheckedItem(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                selected = thems[i];
                checkedItem = i;
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                if (selected == null) {
                    selected = thems[i];
                    checkedItem = i;
                }
                switch (selected) {
                    case "System Default":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        break;
                    case "Light":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    case "Dark":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                    default:
                }
                setCheckedItem(checkedItem);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private int getCheckedItem() {
        return preferences.getInt(CHECKEDITEM, 0);
    }

    private void setCheckedItem(int i) {
        editor.putInt(CHECKEDITEM, i);
        editor.apply();
    }
}