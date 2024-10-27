package com.prm392.quizgame;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prm392.quizgame.SpinWheel.LuckyWheelView;
import com.prm392.quizgame.SpinWheel.model.LuckyItem;
import com.prm392.quizgame.databinding.ActivitySpinnerBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpinnerActivity extends AppCompatActivity {

    private ActivitySpinnerBinding binding;
    private final List<LuckyItem> luckyItems = new ArrayList<>();
    FirebaseFirestore database;
    private int changeSpin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpinnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();
        getChangeSpin();

        initializeLuckyItems();
        setupLuckyWheel();
        setupSpinButton();
        binding.btnBackToHome.setOnClickListener(view -> finish());
    }

    private void getChangeSpin() {
        database.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    changeSpin = documentSnapshot.getLong("changeSpin").intValue();
                    binding.tvSpinChange.setText(String.valueOf("Spin Change: " +changeSpin)); // Update TextView here
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load spin count.", Toast.LENGTH_SHORT).show());
    }

    private void setChangeSpin() {
        changeSpin--; // Decrement spin count locally
        binding.tvSpinChange.setText(String.valueOf("Spin Change: " + changeSpin)); // Update TextView
        database.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .update("changeSpin", changeSpin)
//                .addOnSuccessListener(aVoid -> Toast.makeText(SpinnerActivity.this, "Spin count updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(SpinnerActivity.this, "Failed to update spin count", Toast.LENGTH_SHORT).show());
    }

    private void initializeLuckyItems() {
        luckyItems.add(createLuckyItem("5", "COINS", Color.parseColor("#212121"), Color.parseColor("#eceff1")));
        luckyItems.add(createLuckyItem("10", "COINS", Color.parseColor("#ffffff"), Color.parseColor("#00cf00")));
        luckyItems.add(createLuckyItem("15", "COINS", Color.parseColor("#212121"), Color.parseColor("#eceff1")));
        luckyItems.add(createLuckyItem("20", "COINS", Color.parseColor("#ffffff"), Color.parseColor("#7f00d9")));
        luckyItems.add(createLuckyItem("25", "COINS", Color.parseColor("#212121"), Color.parseColor("#eceff1")));
        luckyItems.add(createLuckyItem("30", "COINS", Color.parseColor("#ffffff"), Color.parseColor("#dc0000")));
        luckyItems.add(createLuckyItem("35", "COINS", Color.parseColor("#212121"), Color.parseColor("#eceff1")));
        luckyItems.add(createLuckyItem("0", "COINS", Color.parseColor("#ffffff"), Color.parseColor("#008bff")));
    }

    private LuckyItem createLuckyItem(String topText, String secondaryText, int textColor, int color) {
        LuckyItem luckyItem = new LuckyItem();
        luckyItem.topText = topText;
        luckyItem.secondaryText = secondaryText;
        luckyItem.textColor = textColor;
        luckyItem.color = color;
        return luckyItem;
    }

    private void setupLuckyWheel() {
        binding.wheelview.setData(luckyItems);
        binding.wheelview.setRound(5);
        binding.wheelview.setLuckyRoundItemSelectedListener(this::onItemSelected);
    }

    private void setupSpinButton() {
        binding.spinBtn.setOnClickListener(view -> {
            if (changeSpin > 0) { // Check if spins are available
                int randomIndex = new Random().nextInt(luckyItems.size());
                binding.wheelview.startLuckyWheelWithTargetIndex(randomIndex);
                setChangeSpin(); // Decrement spins after each spin
            } else {
                Toast.makeText(this, "No spins left. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onItemSelected(int index) {
        long coins = getCoinAmount(index);
        updateCoinsInAccount(coins);
    }

    private long getCoinAmount(int index) {
        switch (index) {
            case 0:
                return 5;
            case 1:
                return 10;
            case 2:
                return 15;
            case 3:
                return 20;
            case 4:
                return 25;
            case 5:
                return 30;
            case 6:
                return 35;
            default:
                return 0;
        }
    }
    private void showResultPopup(long coins) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Congratulations!");
        builder.setMessage("You won " + coins + " coins!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void updateCoinsInAccount(long coins) {
        String userId = FirebaseAuth.getInstance().getUid();

        if (userId != null) {
            database.collection("users")
                    .document(userId)
                    .update("coins", FieldValue.increment(coins))
                    .addOnSuccessListener(aVoid -> {
                        showResultPopup(coins);
                        Toast.makeText(SpinnerActivity.this, "Coins added to your account.", Toast.LENGTH_SHORT).show();
//                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(SpinnerActivity.this, "Failed to update coins. Please try again.", Toast.LENGTH_SHORT).show()
                    );
        } else {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
        }
    }
}
