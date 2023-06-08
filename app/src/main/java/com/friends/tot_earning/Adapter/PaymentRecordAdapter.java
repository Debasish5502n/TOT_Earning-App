package com.friends.tot_earning.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.friends.tot_earning.Model.PaymentRecordModel;
import com.friends.tot_earning.R;
import com.friends.tot_earning.databinding.PaymentRowDesignBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PaymentRecordAdapter extends RecyclerView.Adapter<PaymentRecordAdapter.LeaderboardViewHolder> {

    Context context;
    ArrayList<PaymentRecordModel> models;

    public PaymentRecordAdapter(Context context, ArrayList<PaymentRecordModel> users) {
        this.context = context;
        this.models = users;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.payment_row_design, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        PaymentRecordModel model = models.get(position);

        //  holder.binding.totalCoins.setText("You got "+String.valueOf(model.getCoins()+" coins in invitation bonus"));

        holder.binding.coins.setText(String.valueOf(model.getRedeemCoin()));
        holder.binding.number.setText(String.valueOf(model.getNumber()));
        holder.binding.process.setText(String.valueOf(model.getProcess()));
        holder.binding.sms.setText(String.valueOf(model.getSms()));

        DateFormat obj = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        // we create instance of the Date and pass milliseconds to the constructor
        Date res = new Date(model.getDate());

        holder.binding.date.setText("Date- " + obj.format(res));
        holder.binding.source.setText(model.getSource());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class LeaderboardViewHolder extends RecyclerView.ViewHolder {

        PaymentRowDesignBinding binding;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = PaymentRowDesignBinding.bind(itemView);
        }
    }
}
