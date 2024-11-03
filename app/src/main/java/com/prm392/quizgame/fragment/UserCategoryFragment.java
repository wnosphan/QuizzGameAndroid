package com.prm392.quizgame.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prm392.quizgame.R;
import com.prm392.quizgame.UserCategoryAdapter;
import com.prm392.quizgame.databinding.FragmentUserQuizBinding;
import com.prm392.quizgame.model.Category;

import java.util.ArrayList;
import java.util.List;

public class UserCategoryFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private FragmentUserQuizBinding binding;
    private ArrayList<Category> categories;
    private UserCategoryAdapter adapter;

    public UserCategoryFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCategoryOfUser();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserQuizBinding.inflate(inflater, container, false);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        // Initialize category list and adapter
        categories = new ArrayList<>();
        adapter = new UserCategoryAdapter(getContext(), categories);

        // Load categories from Firestore
//        loadCategoryOfUser();

        binding.cateList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.cateList.setAdapter(adapter);

        binding.createButton.setOnClickListener(v -> showCreateCategoryDialog());

        binding.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete the selected categories?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Call the method to delete selected categories
                        adapter.deleteSelectedCategories();
                        loadCategoryOfUser();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss(); // Dismiss the dialog on "No"
                    })
                    .show();
        });
        return binding.getRoot();
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    void loadCategoryOfUser() {
        categories.clear(); // Clear the current categories
        database.collection("quizofuser").document(auth.getCurrentUser().getUid()).collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        List<Category> categoryList = task.getResult().toObjects(Category.class);
                        if(categoryList.isEmpty()) {
                            binding.notiTitle.setText("No category found. Please create a new one.");
                            binding.notiTitle.setVisibility(View.VISIBLE);
                        } else {
                            binding.notiTitle.setVisibility(View.GONE);
                        }

                        categories.addAll(categoryList);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("TAG", "loadCategoryOfUser: " + task.getException().getMessage());
                    }
                });
    }

    private void showCreateCategoryDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_create_category, null);

        // Create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Create Category")
                .setView(dialogView)
                .setPositiveButton("Create", (dialog, i) -> {
                    EditText editTextCategoryName = dialogView.findViewById(R.id.editTextCategoryName);
                    String categoryName = editTextCategoryName.getText().toString().trim();

                    if (categoryName.isEmpty()) {
                        editTextCategoryName.setError("Category name is required");
                        return;
                    }
                    // Create category
                    createCategory(categoryName);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, i) -> dialog.dismiss());

        // Show the dialog
        builder.create().show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void createCategory(String categoryName) {
        String uid = auth.getCurrentUser().getUid();
        String categoryId = database.collection("quizofuser")
                .document(uid)
                .collection("categories")
                .document().getId(); // Generate unique ID

        Category category = new Category(categoryId, categoryName,
                "https://static.vecteezy.com/system/resources/previews/007/979/991/original/eps10-grey-folder-solid-icon-in-simple-flat-trendy-style-isolated-on-white-background-vector.jpg");

        database.collection("quizofuser")
                .document(uid)
                .collection("categories")
                .document(categoryId) // Use the generated ID
                .set(category)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Category " + categoryName + " added successfully.");
                    loadCategoryOfUser(); // Reload categories
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding category " + categoryName, e);
                });
    }
}
