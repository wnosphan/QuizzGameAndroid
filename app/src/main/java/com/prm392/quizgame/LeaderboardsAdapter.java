package com.prm392.quizgame;

import static androidx.recyclerview.widget.RecyclerView.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392.quizgame.databinding.RowLeaderboardsBinding;
import com.prm392.quizgame.model.User;

import java.util.ArrayList;

public class LeaderboardsAdapter extends  RecyclerView.Adapter<LeaderboardsAdapter.LeaderboardsViewHolder> {
    Context context;
    ArrayList<User> users;
    public LeaderboardsAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }
    @NonNull
    @Override
    public LeaderboardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_leaderboards,parent,false);
        return new LeaderboardsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardsViewHolder holder, int position) {
     User user = users.get(position);
        holder.binding.textView10.setText(user.getName());
        holder.binding.textView12.setText(String.valueOf(user.getCoins()));
        holder.binding.textView7.setText(String.format("#%d",position+1));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class LeaderboardsViewHolder extends RecyclerView.ViewHolder {
        RowLeaderboardsBinding binding;
        public LeaderboardsViewHolder(@NonNull View itemView) {

            super(itemView);
            binding = RowLeaderboardsBinding.bind(itemView);
        }
    }
}
