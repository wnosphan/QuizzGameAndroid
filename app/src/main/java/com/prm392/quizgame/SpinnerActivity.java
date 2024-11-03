package com.prm392.quizgame;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import java.util.Date;
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
        checkAndStartCountdown();
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
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load spin count.", Toast.LENGTH_SHORT).show());
    }

    private void setChangeSpin() {
        changeSpin--; // Decrement spin count locally
        database.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .update("changeSpin", changeSpin)
//                .addOnSuccessListener(aVoid -> Toast.makeText(SpinnerActivity.this, "Spin count updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(SpinnerActivity.this, "Failed to update spin count", Toast.LENGTH_SHORT).show());
    }

    private void initializeLuckyItems() {
        luckyItems.add(createLuckyItem("500", "COINS", Color.parseColor("#212121"), Color.parseColor("#eceff1")));
        luckyItems.add(createLuckyItem("1000", "COINS", Color.parseColor("#ffffff"), Color.parseColor("#00cf00")));
        luckyItems.add(createLuckyItem("1500", "COINS", Color.parseColor("#212121"), Color.parseColor("#eceff1")));
        luckyItems.add(createLuckyItem("2000", "COINS", Color.parseColor("#ffffff"), Color.parseColor("#7f00d9")));
        luckyItems.add(createLuckyItem("2500", "COINS", Color.parseColor("#212121"), Color.parseColor("#eceff1")));
        luckyItems.add(createLuckyItem("3000", "COINS", Color.parseColor("#ffffff"), Color.parseColor("#dc0000")));
        luckyItems.add(createLuckyItem("3500", "COINS", Color.parseColor("#212121"), Color.parseColor("#eceff1")));
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
            if (changeSpin > 0) {
                checkSpinAvailability(); // Kiểm tra thời gian chờ trước mỗi lượt spin
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
                return 500;
            case 1:
                return 1000;
            case 2:
                return 1500;
            case 3:
                return 2000;
            case 4:
                return 2500;
            case 5:
                return 3000;
            case 6:
                return 3500;
            default:
                return 0;
        }
    }
    private void showResultPopup(long coins) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        builder.setView(customLayout);

        TextView title = customLayout.findViewById(R.id.dialog_title);
        TextView message = customLayout.findViewById(R.id.dialog_message);
        Button okButton = customLayout.findViewById(R.id.btn_ok);

        message.setText("You won " + coins + " coins!");

        // Tạo dialog
        AlertDialog dialog = builder.create();

        okButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show(); // Hiện dialog
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
    private void checkSpinAvailability() {
        String userId = FirebaseAuth.getInstance().getUid();
        database.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Date lastSpinDate = documentSnapshot.getDate("lastSpinTime");

                    long lastSpinTime = (lastSpinDate != null) ? lastSpinDate.getTime() : 0;
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastSpinTime >= 86400000) { // 24 hours in milliseconds
                        startSpin(); // Nếu đủ 24 giờ, cho phép spin
                    } else {
                        Toast.makeText(this, "Please wait 24 hours for the next spin.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startSpin() {
        int randomIndex = new Random().nextInt(luckyItems.size());
        binding.wheelview.startLuckyWheelWithTargetIndex(randomIndex);
        setChangeSpin();
        updateLastSpinTime();
         startCountdown(86400000);
    }

    private void updateLastSpinTime() {
        String userId = FirebaseAuth.getInstance().getUid();
        database.collection("users").document(userId)
                .update("lastSpinTime", FieldValue.serverTimestamp())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update spin time", Toast.LENGTH_SHORT).show());
    }

    private void checkAndStartCountdown() {
        database.collection("users").document(FirebaseAuth.getInstance().getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Date lastSpinDate = documentSnapshot.getDate("lastSpinTime");
                    long lastSpinTime = (lastSpinDate != null) ? lastSpinDate.getTime() : 0;
                    long currentTime = System.currentTimeMillis();
                    long timeDiff = 86400000 - (currentTime - lastSpinTime); // 24 giờ trừ đi thời gian đã trôi qua

                    if (timeDiff > 0) {
                        startCountdown(timeDiff);
                    } else {
                        binding.tvSpinChange.setText("You can spin now!");
                    }
                });
    }

    private void startCountdown(long millisUntilFinished) {
        new CountDownTimer(millisUntilFinished, 1000) {
            public void onTick(long millisUntilFinished) {
                long hours = millisUntilFinished / (1000 * 60 * 60);
                long minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60);
                long seconds = (millisUntilFinished % (1000 * 60)) / 1000;
                binding.tvSpinChange.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            }

            public void onFinish() {
                changeSpin++;
                binding.tvSpinChange.setText("You can spin now!");
                database.collection("users")
                        .document(FirebaseAuth.getInstance().getUid())
                        .update("changeSpin", changeSpin )
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(SpinnerActivity.this, "Spin count updated", Toast.LENGTH_SHORT).show();
                            setChangeSpin();
                        });
            }
        }.start();
    }


}
