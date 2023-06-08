package com.friends.tot_earning.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.friends.tot_earning.Model.CoinsRecordModel;
import com.friends.tot_earning.R;
import com.friends.tot_earning.databinding.RowUserCoinsRecordBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CoinsRecordAdapter extends RecyclerView.Adapter<CoinsRecordAdapter.LeaderboardViewHolder> {

    Context context;
    ArrayList<CoinsRecordModel> models;

    public CoinsRecordAdapter(Context context, ArrayList<CoinsRecordModel> users) {
        this.context = context;
        this.models = users;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_user_coins_record, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        CoinsRecordModel model = models.get(position);

      //  holder.binding.totalCoins.setText("You got "+String.valueOf(model.getCoins()+" coins in invitation bonus"));
        if (model.getPlusCoins() <0){
            holder.binding.plusCoins.setText(String.valueOf( model.getPlusCoins()));
            holder.binding.plusCoins.setTextColor(Color.parseColor("#ff0000"));
        }else {
            holder.binding.plusCoins.setText(String.valueOf("+ " + model.getPlusCoins()));
        }

        DateFormat obj = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        // we create instance of the Date and pass milliseconds to the constructor
        Date res = new Date(model.getDate());

        holder.binding.totalCoins.setText("Date- "+obj.format(res));
        holder.binding.source.setText(model.getSource());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class LeaderboardViewHolder extends RecyclerView.ViewHolder {

        RowUserCoinsRecordBinding binding;
        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowUserCoinsRecordBinding.bind(itemView);
        }
    }
}
