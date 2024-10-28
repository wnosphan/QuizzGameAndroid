package com.prm392.quizgame;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prm392.quizgame.databinding.ActivityQuizBinding;
import com.prm392.quizgame.model.Question;

import java.util.ArrayList;
import java.util.Random;

public class UserQuizActivity extends AppCompatActivity {


    ActivityQuizBinding binding;
    ArrayList<Question> questions;
    int index = 0;
    Question question;
    CountDownTimer timer;
    FirebaseFirestore db;
    FirebaseAuth auth;
    int correctAnswers = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String categoryId = getIntent().getStringExtra("catId");

        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.getRoot().setVisibility(View.GONE);

        questions = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Load câu hỏi từ Firebase
        loadQuestionsOfUserFromFireBase(auth.getCurrentUser().getUid(),categoryId);
    }


    void loadQuestionsOfUserFromFireBase(String uid, String catId) {
        db.collection("quizofuser")
                .document(uid)
                .collection("categories")
                .document(catId)
                .collection("questions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        questions.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                Question question = document.toObject(Question.class);
                                questions.add(question);
                            }
                        }
                        // Check if there are questions, then start quiz or show alert
                        if (questions.size() > 0) {
                            binding.getRoot().setVisibility(View.VISIBLE);
                            showStartQuizDialog();
                        } else {
                            showNoQuestionsAlert();
                        }
                    } else {
                        Log.e("Firestore", "Error getting questions: ", task.getException());
                    }
                });
    }

    void showStartQuizDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Start Quiz")
                .setMessage("Do you want to start the quiz?")
                .setPositiveButton("Start", (dialog, which) -> startQuiz())
                .setNegativeButton("Cancel", (dialog, which) -> finish())
                .show();
    }

    void showNoQuestionsAlert() {
        new AlertDialog.Builder(this)
                .setTitle("No Questions Available")
                .setMessage("No questions found for this quiz. Please try again later.")
                .setPositiveButton("OK", (dialog, which) -> finish())
                .show();
    }

    void addQuestionsToList(QuerySnapshot queryDocumentSnapshots) {
        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
            Question question = snapshot.toObject(Question.class);
            questions.add(question);
        }
    }

    void startQuiz() {
        if (questions.size() > 0) {
            resetTimer();
            setNextQuestion();
        } else {
            Toast.makeText(this, "No questions found!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    void setNextQuestion() {
        if (timer != null) {
            timer.cancel(); // Dừng bộ đếm hiện tại
        }

        if (index < questions.size()) {
            resetTimer();
            timer.start();

            // Thiết lập câu hỏi và các tùy chọn trả lời
            binding.questionCounter.setText((index + 1) + "/" + questions.size());
            question = questions.get(index);
            binding.question.setText(question.getQuestion());
            binding.option1.setText(question.getOptionA());
            binding.option2.setText(question.getOptionB());
            binding.option3.setText(question.getOptionC());
            binding.option4.setText(question.getOptionD());
        }
    }



    public void checkAnswer(TextView textView){
        String answer = textView.getText().toString();
        if(answer.equals(question.getCorrectAns())){
            correctAnswers++;
            showAnswer();
            textView.setBackground(getResources().getDrawable(R.drawable.option_right));

        } else {
            showAnswer();
            textView.setBackground(getResources().getDrawable(R.drawable.option_wrong));
        }

    }
    void resetTimer(){
        timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.timer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                // Khi hết giờ thì tự động hiển thị câu trả lời đúng
                showAnswer();
                // Tự động chuyển sang câu hỏi tiếp theo sau khi hết giờ
                index++;
                if (index < questions.size()) {
                    reset();
                    setNextQuestion();
                } else {
                    Toast.makeText(UserQuizActivity.this, "Quiz Finished", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        };
    }



    void showAnswer(){
        if(question.getCorrectAns().equals(binding.option1.getText().toString())){
            binding.option1.setBackground(getResources().getDrawable(R.drawable.option_right));
        } else if(question.getCorrectAns().equals(binding.option2.getText().toString())){
            binding.option2.setBackground(getResources().getDrawable(R.drawable.option_right));
        } else if(question.getCorrectAns().equals(binding.option3.getText().toString())){
            binding.option3.setBackground(getResources().getDrawable(R.drawable.option_right));
        } else if(question.getCorrectAns().equals(binding.option4.getText().toString())){
            binding.option4.setBackground(getResources().getDrawable(R.drawable.option_right));
        }
    }
    void reset(){
        binding.option1.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option2.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option3.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option4.setBackground(getResources().getDrawable(R.drawable.option_unselected));
    }
    public void onClick(View view) {
        if (view.getId() == R.id.option_1) {
            checkAnswer(binding.option1);
        } else if (view.getId() == R.id.option_2) {
            checkAnswer(binding.option2);
        } else if (view.getId() == R.id.option_3) {
            checkAnswer(binding.option3);
        } else if (view.getId() == R.id.option_4) {
            checkAnswer(binding.option4);
        }

        // Dừng bộ đếm ngay sau khi người dùng chọn câu trả lời
        if (timer != null) {
            timer.cancel();  // Hủy bộ đếm thời gian hiện tại
        }

        showAnswer();  // Hiển thị đáp án đúng sau khi người dùng chọn câu trả lời

        // Chuyển sang câu hỏi tiếp theo khi nhấn nút "Next"
        if (view.getId() == R.id.nextBtn) {
            index++;
            if (index < questions.size()) {
                reset();  // Đặt lại giao diện của các tùy chọn
                setNextQuestion();  // Thiết lập câu hỏi mới và khởi động lại timer
            } else {
                Intent intent = new Intent(UserQuizActivity.this, ResultActivity.class);

                intent.putExtra("correct", correctAnswers);
                intent.putExtra("total", questions.size());
//                Toast.makeText(this, "Quiz Finished", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        } else if (view.getId() == R.id.quizBtn) {
            finish();  // Thoát quiz khi nhấn "Quit"
        }
    }
}