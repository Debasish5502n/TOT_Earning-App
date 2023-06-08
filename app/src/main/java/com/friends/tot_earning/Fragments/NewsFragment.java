package com.friends.tot_earning.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friends.tot_earning.Adapter.LeaderboardsAdapter;
import com.friends.tot_earning.Model.TopUsersModel;
import com.friends.tot_earning.Model.User;
import com.bumptech.glide.Glide;
import com.friends.tot_earning.R;
import com.friends.tot_earning.databinding.FragmentNewsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NewsFragment extends Fragment {
    FragmentNewsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNewsBinding.inflate(inflater, container, false);

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        final ArrayList<User> users = new ArrayList<>();
        final LeaderboardsAdapter adapter = new LeaderboardsAdapter(getContext(), users);

        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.showShimmerAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        database.collection("USERS")
                .orderBy("coins", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    User user = snapshot.toObject(User.class);

                    if (user.getTotalCoins() > 200) {
                        users.add(user);
                    }

                }
                binding.recyclerView.hideShimmerAdapter();
                adapter.notifyDataSetChanged();
            }
        });

        database.collection("TOP_3_USERS")
                .document("USER")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        TopUsersModel topUsersModel = documentSnapshot.toObject(TopUsersModel.class);

                        Glide.with(getContext()).load(topUsersModel.getUser_1_profile()).placeholder(R.drawable.avatarra).into(binding.profileImage1);
                        Glide.with(getContext()).load(topUsersModel.getUser_2_profile()).placeholder(R.drawable.avatarra).into(binding.profileImage2);
                        Glide.with(getContext()).load(topUsersModel.getUser_3_profile()).placeholder(R.drawable.avatarra).into(binding.profileImage3);

                        binding.image1Coin.setText(String.valueOf(topUsersModel.getUser_1_coin()));
                        binding.image2Coin.setText(String.valueOf(topUsersModel.getUser_2_coin()));
                        binding.image3Coin.setText(String.valueOf(topUsersModel.getUser_3_coin()));
                    }
                });

        return binding.getRoot();
    }

}