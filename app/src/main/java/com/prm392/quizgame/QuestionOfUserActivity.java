package com.prm392.quizgame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prm392.quizgame.model.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionOfUserActivity extends AppCompatActivity {

    private RecyclerView recyclerViewQuestion;
    private QuestionAdapter questionAdapter;
    private List<Question> questionList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_of_user);
        Button btnCreateQuestion = findViewById(R.id.btnCreateQuestion);
        Button btnStartQuiz = findViewById(R.id.btnStartQuiz);
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerViewQuestion = findViewById(R.id.recyclerViewQuestion);
        recyclerViewQuestion.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter and set it to RecyclerView
        questionAdapter = new QuestionAdapter(this, questionList);
        recyclerViewQuestion.setAdapter(questionAdapter);

        btnCreateQuestion.setOnClickListener(v -> showCreateQuestionDialog());


        // Get category ID from the intent
        String catId = getIntent().getStringExtra("catId");

        if (catId != null) {
            loadQuestions(catId);
        }

        btnStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionOfUserActivity.this, UserQuizActivity.class);
                intent.putExtra("catId", catId);
                startActivity(intent);
            }
        });

    }

    private void loadQuestions(String catId) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("quizofuser")
                .document(uid)
                .collection("categories")
                .document(catId)
                .collection("questions")  // Assuming questions are stored in this sub-collection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        questionList.clear();  // Clear the list before adding new items
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Question question = document.toObject(Question.class);
                            questionList.add(question);
                        }
                        questionAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Error getting questions: ", task.getException());
                    }
                });
    }

    private void showCreateQuestionDialog() {
        // Inflate the dialog layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_question, null);
        builder.setView(dialogView);

        EditText etQuestion = dialogView.findViewById(R.id.etQuestion);
        EditText etOptionA = dialogView.findViewById(R.id.etOptionA);
        EditText etOptionB = dialogView.findViewById(R.id.etOptionB);
        EditText etOptionC = dialogView.findViewById(R.id.etOptionC);
        EditText etOptionD = dialogView.findViewById(R.id.etOptionD);
        EditText etCorrectAnswer = dialogView.findViewById(R.id.etCorrectAnswer);
        builder.setTitle("Create Question");
        builder.setPositiveButton("Create", (dialog, which) -> {
            // Retrieve input values
            String questionText = etQuestion.getText().toString().trim();
            String optionA = etOptionA.getText().toString().trim();
            String optionB = etOptionB.getText().toString().trim();
            String optionC = etOptionC.getText().toString().trim();
            String optionD = etOptionD.getText().toString().trim();
            String correctAnswer = etCorrectAnswer.getText().toString().trim();

            if (questionText.isEmpty() || optionA.isEmpty() || optionB.isEmpty() ||
                    optionC.isEmpty() || optionD.isEmpty() || correctAnswer.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create Question object
            Question newQuestion = new Question(questionText, optionA, optionB, optionC, optionD, correctAnswer);

            // Save to Firestore
            saveQuestionToFirestore(newQuestion);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveQuestionToFirestore(Question question) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String catId = getIntent().getStringExtra("catId");  // Assuming you pass category ID from previous activity

        db.collection("quizofuser")
                .document(uid)
                .collection("categories")
                .document(catId)
                .collection("questions")
                .add(question)
                .addOnSuccessListener(documentReference -> {
                    questionList.add(question);
                    questionAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Question added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error adding question", e);
                    Toast.makeText(this, "Failed to add question", Toast.LENGTH_SHORT).show();
                });
    }

}
