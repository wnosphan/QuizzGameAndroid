package com.prm392.quizgame;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prm392.quizgame.databinding.ActivityResultBinding;

import java.io.IOException;
import java.io.OutputStream;

public class ResultActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 1;
    ActivityResultBinding binding;
    int POINT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int correctAnswers = getIntent().getIntExtra("correct", 0);
        int totalQuestions = getIntent().getIntExtra("total", 0);
        int point = correctAnswers * POINT;
        binding.score.setText(String.format("%d/%d", correctAnswers, totalQuestions));
        binding.earnedCoins.setText(String.valueOf(point));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(FirebaseAuth.getInstance().getUid())
                .update("coins", FieldValue.increment(point));

        binding.shareBtn.setOnClickListener(view -> checkPermissionAndSaveScreenshot());
        binding.restartBtn.setOnClickListener(view -> goToHome());
    }

    private void checkPermissionAndSaveScreenshot() {
        // Không cần `WRITE_EXTERNAL_STORAGE` trên Android 10+
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
            } else {
                saveScreenshotToMediaStore(takeScreenshot());
            }
        } else {
            saveScreenshotToMediaStore(takeScreenshot());
        }
    }

    // Tạo ảnh chụp màn hình từ View
    private Bitmap takeScreenshot() {
        View rootView = getWindow().getDecorView().getRootView();
        Bitmap bitmap = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        rootView.draw(canvas);
        return bitmap;
    }


    // Lưu ảnh vào MediaStore
    private void saveScreenshotToMediaStore(Bitmap bitmap) {
        if (bitmap == null) {
            Toast.makeText(this, "Error creating screenshot", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentResolver resolver = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "screenshot_" + System.currentTimeMillis() + ".png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Screenshots");

        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try (OutputStream out = resolver.openOutputStream(uri)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Toast.makeText(this, "Saved screenshot to gallery", Toast.LENGTH_SHORT).show();

            // Gửi broadcast để cập nhật MediaStore và hiển thị ảnh ngay trong thư viện
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save screenshot", Toast.LENGTH_SHORT).show();
        }
    }


    private void restartQuiz() {
        Intent intent = new Intent(ResultActivity.this, QuizActivity.class);
        intent.putExtra("catId", getIntent().getStringExtra("catId")); // Truyền lại catId từ ResultActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void goToHome() {
        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveScreenshotToMediaStore(takeScreenshot());
            } else {
                Toast.makeText(this, "Permission denied to write external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
