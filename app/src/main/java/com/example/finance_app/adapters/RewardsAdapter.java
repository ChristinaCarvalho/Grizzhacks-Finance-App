package com.example.finance_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.finance_app.R;
import com.example.finance_app.models.Reward;

import java.util.List;
import java.util.Locale;

public class RewardsAdapter extends ArrayAdapter<Reward> {

    public RewardsAdapter(Context context, List<Reward> rewards) {
        super(context, 0, rewards);
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
        }

        return convertView;
    }
}
