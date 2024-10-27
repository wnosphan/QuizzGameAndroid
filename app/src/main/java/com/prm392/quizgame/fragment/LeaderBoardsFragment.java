package com.prm392.quizgame.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.prm392.quizgame.LeaderboardsAdapter;
import com.prm392.quizgame.R;
import com.prm392.quizgame.databinding.FragmentLeaderBoardsBinding;
import com.prm392.quizgame.model.User;

import java.util.ArrayList;

public class LeaderBoardsFragment extends Fragment {

    public LeaderBoardsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentLeaderBoardsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLeaderBoardsBinding.inflate(inflater,container,false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<User> users = new ArrayList<>();
        LeaderboardsAdapter adapter = new LeaderboardsAdapter(getContext(),users);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        db.collection("users")
                .orderBy("coins", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){
                            User user = snapshot.toObject(User.class);
                            users.add(user);
                        }
                        adapter.notifyDataSetChanged();
                        
                    }
                });


        return binding.getRoot();
    }
}