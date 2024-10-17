package com.prm392.quizgame;

import static com.prm392.quizgame.R.*;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.prm392.quizgame.databinding.ActivityMainBinding;
import com.prm392.quizgame.databinding.ActivityQuizBinding;
import com.prm392.quizgame.model.Question;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {


    ActivityQuizBinding binding;
    ArrayList<Question> questions;
    int index = 0;
    Question question;
    CountDownTimer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_quiz);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        questions = new ArrayList<>();
        questions.add(new Question("What is the capital of India","New Delhi","Mumbai","Kolkata","Chennai","Kolkata"));
        questions.add(new Question("What is the capital of USA","New York","Washington DC","Los Angeles","Chicago","Washington DC"));

        setNextQuestion();
        resetTimer();
        // Nhận dữ liệu categoryId từ Intent
        String categoryId = getIntent().getStringExtra("catId");
    }
    void setNextQuestion() {
        // Dừng bộ đếm của câu hỏi trước
        if (timer != null) {
            timer.cancel();  // Hủy timer cũ trước khi khởi tạo timer mới
        }

        if (index < questions.size()) {
            // Khởi động lại bộ đếm cho câu hỏi mới
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
            textView.setBackground(getResources().getDrawable(drawable.option_right));
        } else {
            showAnswer();
            textView.setBackground(getResources().getDrawable(drawable.option_wrong));
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
    }
    public void onClick(View view) {
        if (view.getId() == id.option_1) {
            checkAnswer(binding.option1);
        } else if (view.getId() == id.option_2) {
            checkAnswer(binding.option2);
        } else if (view.getId() == id.option_3) {
            checkAnswer(binding.option3);
        } else if (view.getId() == id.option_4) {
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
                Toast.makeText(this, "Quiz Finished", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (view.getId() == R.id.quizBtn) {
            finish();  // Thoát quiz khi nhấn "Quit"
        }
    }

}