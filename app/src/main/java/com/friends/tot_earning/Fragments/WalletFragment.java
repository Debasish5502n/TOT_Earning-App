package com.friends.tot_earning.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friends.tot_earning.Activity.RedemptionActivity;
import com.friends.tot_earning.DataBase.DbQueries;
import com.friends.tot_earning.Adapter.CoinsRecordAdapter;
import com.friends.tot_earning.Model.CoinsRecordModel;
import com.friends.tot_earning.databinding.FragmentWalletBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class WalletFragment extends Fragment {
    FragmentWalletBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWalletBinding.inflate(inflater, container, false);

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        final ArrayList<CoinsRecordModel> models = new ArrayList<>();
        final CoinsRecordAdapter adapter = new CoinsRecordAdapter(getContext(), models);

        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.coin.setText(DbQueries.coin);
        database.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("RECORDS")
                .orderBy("date", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    CoinsRecordModel model = snapshot.toObject(CoinsRecordModel.class);
                    models.add(model);

                }

                adapter.notifyDataSetChanged();
            }
        });

        binding.redeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), RedemptionActivity.class));
            }
        });

        return binding.getRoot();
    }

}