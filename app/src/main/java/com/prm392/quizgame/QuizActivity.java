package com.prm392.quizgame;

import static com.prm392.quizgame.R.*;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.prm392.quizgame.databinding.ActivityMainBinding;
import com.prm392.quizgame.databinding.ActivityQuizBinding;
import com.prm392.quizgame.model.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {


    ActivityQuizBinding binding;
    ArrayList<Question> questions;
    int index = 0;
    Question question;
    CountDownTimer timer;
    FirebaseFirestore db;
    FirebaseAuth auth;
    int correctAnswers = 0;
    private boolean isFiftyFiftyUsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         String categoryId = getIntent().getStringExtra("catId");
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        questions = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // Load câu hỏi từ Firebase
        loadQuestionsFromFirebase(categoryId);
    }

    void loadQuestionsFromFirebase(String categoryId) {
        Random random = new Random();
        final int rand = random.nextInt(2);

        db.collection("category").document(categoryId)
                .collection("question").whereLessThanOrEqualTo("index", rand)
                .orderBy("index").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            db.collection("category").document(categoryId)
                                    .collection("question").whereGreaterThanOrEqualTo("index", rand)
                                    .orderBy("index").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                Question question = snapshot.toObject(Question.class);
                                                questions.add(question);
                                            }
                                            setNextQuestion();
                                        }
                                    });
                        } else {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                Question question = snapshot.toObject(Question.class);
                                questions.add(question);
                            }
                            setNextQuestion();
                        }
                    }
                });

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
            textView.setBackground(getResources().getDrawable(drawable.option_right));

        } else {
            showAnswer();
            textView.setBackground(getResources().getDrawable(drawable.option_wrong));
        }
        binding.imageView4.setVisibility(View.VISIBLE);

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
                    Toast.makeText(QuizActivity.this, "Quiz Finished", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        };
    }



    void showAnswer(){
        if(question.getCorrectAns().equals(binding.option1.getText().toString())){
            binding.option1.setBackground(getResources().getDrawable(drawable.option_right));
        } else if(question.getCorrectAns().equals(binding.option2.getText().toString())){
            binding.option2.setBackground(getResources().getDrawable(drawable.option_right));
        } else if(question.getCorrectAns().equals(binding.option3.getText().toString())){
            binding.option3.setBackground(getResources().getDrawable(drawable.option_right));
        } else if(question.getCorrectAns().equals(binding.option4.getText().toString())){
            binding.option4.setBackground(getResources().getDrawable(drawable.option_right));
        }
    }
    void reset(){
        binding.option1.setBackground(getResources().getDrawable(drawable.option_unselected));
        binding.option2.setBackground(getResources().getDrawable(drawable.option_unselected));
        binding.option3.setBackground(getResources().getDrawable(drawable.option_unselected));
        binding.option4.setBackground(getResources().getDrawable(drawable.option_unselected));

        binding.option1.setVisibility(View.VISIBLE);
        binding.option2.setVisibility(View.VISIBLE);
        binding.option3.setVisibility(View.VISIBLE);
        binding.option4.setVisibility(View.VISIBLE);

        binding.descriptionTextView.setVisibility(View.INVISIBLE);
        binding.imageView4.setVisibility(View.INVISIBLE);

    }
    public void onClick(View view) {
        // Xử lý nút 50:50
        if (view.getId() == R.id.imageView3 && !isFiftyFiftyUsed) {
            isFiftyFiftyUsed = true; // Đánh dấu rằng nút 50:50 đã được sử dụng
            binding.imageView3.setVisibility(View.INVISIBLE); // Ẩn nút 50:50
            // Lấy danh sách các tùy chọn sai ngẫu nhiên
            List<TextView> options = new ArrayList<>(Arrays.asList(binding.option1, binding.option2, binding.option3, binding.option4));
            options.removeIf(option -> option.getText().toString().equals(question.getCorrectAns())); // Loại bỏ đáp án đúng

            // Chọn 2 tùy chọn sai để ẩn
            Collections.shuffle(options); // Trộn ngẫu nhiên danh sách đáp án sai
            options.get(0).setVisibility(View.INVISIBLE); // Ẩn đáp án sai đầu tiên
            options.get(1).setVisibility(View.INVISIBLE); // Ẩn đáp án sai thứ hai

            // Thoát khỏi phương thức sau khi xử lý nút 50:50
            return;
        }

        // Xử lý các nút đáp án
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
            timer.cancel();
        }

        showAnswer(); // Hiển thị đáp án đúng sau khi người dùng chọn câu trả lời
        if (view.getId() == R.id.imageView4) {
            binding.descriptionTextView.setText(question.getDescription()); // Hiển thị giải thích
            binding.descriptionTextView.setVisibility(View.VISIBLE); // Hiển thị TextView giải thích
        }
        // Chuyển sang câu hỏi tiếp theo khi nhấn nút "Next"
        if (view.getId() == R.id.nextBtn) {
            index++;
            if (index < questions.size()) {
                reset(); // Đặt lại giao diện của các tùy chọn
                setNextQuestion(); // Thiết lập câu hỏi mới và khởi động lại timer
            } else {
                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                intent.putExtra("correct", correctAnswers);
                intent.putExtra("total", questions.size());
                startActivity(intent);
            }
        } else if (view.getId() == R.id.quizBtn) {
            finish(); // Thoát quiz khi nhấn "Quit"
        }
    }



}