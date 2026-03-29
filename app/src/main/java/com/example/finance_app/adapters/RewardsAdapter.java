package com.example.finance_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.finance_app.R;
import com.example.finance_app.models.Reward;

import java.util.List;
import java.util.Locale;

public class RewardsAdapter extends ArrayAdapter<Reward> {

    private double savingsGoal;
    private OnRewardRedeemedListener redeemListener;

    public interface OnRewardRedeemedListener {
        void onRewardRedeemed(Reward reward);
    }

    public RewardsAdapter(Context context, List<Reward> rewards, double savingsGoal, OnRewardRedeemedListener listener) {
        super(context, 0, rewards);
        this.savingsGoal = savingsGoal;
        this.redeemListener = listener;
    }

    public void updateSavingsGoal(double newGoal) {
        this.savingsGoal = newGoal;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_reward, parent, false);
        }

        Reward reward = getItem(position);

        TextView nameView = convertView.findViewById(R.id.reward_name);
        TextView priceView = convertView.findViewById(R.id.reward_price);

        if (reward != null) {
            nameView.setText(reward.getName());
            priceView.setText(String.format(Locale.US, "$%.2f", reward.getPrice()));

            convertView.setOnClickListener(v -> {
                showRedeemPopup(reward);
            });
        }

        return convertView;
    }

    private void showRedeemPopup(Reward reward) {
        double maxAllowedPrice = savingsGoal * 0.10;
        boolean canRedeem = reward.getPrice() <= maxAllowedPrice;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(reward.getName());

        if (canRedeem) {
            builder.setMessage(String.format(Locale.US, "Would you like to redeem this reward for $%.2f?\n\n(This is within your 10%% savings goal limit of $%.2f)",
                    reward.getPrice(), maxAllowedPrice));
            builder.setPositiveButton("Redeem", (dialog, which) -> {
                if (redeemListener != null) {
                    redeemListener.onRewardRedeemed(reward);
                }
            });
        } else {
            builder.setMessage(String.format(Locale.US, "You cannot redeem this reward.\n\nThe price ($%.2f) exceeds 10%% of your savings goal ($%.2f).",
                    reward.getPrice(), maxAllowedPrice));
        }

        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
