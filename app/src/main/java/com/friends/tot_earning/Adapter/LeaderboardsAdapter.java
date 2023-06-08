package com.friends.tot_earning.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.friends.tot_earning.Model.User;
import com.friends.tot_earning.R;
import com.friends.tot_earning.databinding.RowLeaderboardsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LeaderboardsAdapter extends RecyclerView.Adapter<LeaderboardsAdapter.LeaderboardViewHolder> {

    Context context;
    ArrayList<User> users;

    public LeaderboardsAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_leaderboards, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        User user = users.get(position);

        holder.binding.name.setText(user.getName());
        holder.binding.coins.setText(String.valueOf(user.getTotalCoins()));
        holder.binding.index.setText(String.format("#%d", position+1));

        Glide.with(context)
                .load(user.getProfile())
                .placeholder(R.drawable.avatarra)
                .into(holder.binding.profileImage);

        Map<String,Object> top3user=new HashMap<>();
        top3user.put("user_1_coin",users.get(0).getTotalCoins());
        top3user.put("user_2_coin",users.get(1).getTotalCoins());
        top3user.put("user_3_coin",users.get(2).getTotalCoins());
        top3user.put("user_1_profile",users.get(0).getProfile());
        top3user.put("user_2_profile",users.get(1).getProfile());
        top3user.put("user_3_profile",users.get(2).getProfile());

        FirebaseFirestore.getInstance()
                .collection("TOP_3_USERS")
                .document("USER")
                .update(top3user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class LeaderboardViewHolder extends RecyclerView.ViewHolder {

        RowLeaderboardsBinding binding;
        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowLeaderboardsBinding.bind(itemView);
        }
    }
}
