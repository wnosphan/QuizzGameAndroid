package com.prm392.quizgame.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prm392.quizgame.R;
import com.prm392.quizgame.databinding.FragmentWalletBinding;
import com.prm392.quizgame.model.User;
import com.prm392.quizgame.model.WithdrawRequest;

public class WalletFragment extends Fragment {


    public WalletFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    FragmentWalletBinding binding;
    FirebaseFirestore db ;
    User user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWalletBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment

        db = FirebaseFirestore.getInstance();
        db.collection("users").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                 user = documentSnapshot.toObject(User.class);
                binding.currentCoins.setText(String.valueOf(user.getCoins()));

            }
        });

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.getCoins()>=10000){
                    String uid = FirebaseAuth.getInstance().getUid();
                    String payPalEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    WithdrawRequest request = new WithdrawRequest( uid,payPalEmail,user.getName());

                    db.collection("withdraws").document(uid).set(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            db.collection("users").document(FirebaseAuth.getInstance().getUid()).update("isPremium",user.isPremium());

                            Toast.makeText(getContext(), "You are Premium User", Toast.LENGTH_SHORT).show();
                            binding.premiumIcon.setVisibility(View.VISIBLE);
                            binding.textView.setVisibility(View.INVISIBLE);
                        }
                    });
                }else {
                    Toast.makeText(getContext(), "You need at least 10000 coins to upgrade Premium", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkIsPremium();
        return binding.getRoot();
    }
  void checkIsPremium(){
        db.collection("users").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                boolean isPremium = documentSnapshot.getBoolean("isPremium");

                if(isPremium==true){
                    binding.premiumIcon.setVisibility(View.VISIBLE);
                    binding.textView.setVisibility(View.INVISIBLE);
                    binding.sendBtn.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}