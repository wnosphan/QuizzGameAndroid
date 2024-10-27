package com.prm392.quizgame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.prm392.quizgame.model.Category;

import java.util.ArrayList;
import java.util.List;

public class UserCategoryAdapter extends RecyclerView.Adapter<UserCategoryAdapter.UserCategoryViewHolder> {
    Context context;
    ArrayList<Category> categories;
    private List<Category> selectedCategories = new ArrayList<>();

    public UserCategoryAdapter(Context context, ArrayList<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public UserCategoryAdapter.UserCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, null);
        return new UserCategoryAdapter.UserCategoryViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull UserCategoryAdapter.UserCategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.textView.setText(category.getCategoryName());
        Glide.with(context).load(category.getCategoryImage()).into(holder.imageView);

        if (selectedCategories.contains(category)) {
            holder.liCate.setBackgroundColor(context.getResources().getColor(R.color.selected_background)); // Change to your desired color
        } else {
            holder.liCate.setBackgroundColor(context.getResources().getColor(android.R.color.transparent)); // Reset to default background
        }

        holder.cateSelect.setChecked(selectedCategories.contains(category));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QuestionOfUserActivity.class);
                intent.putExtra("catId", category.getCategoryId());
                context.startActivity(intent);
            }
        });


        // Handle checkbox click
        holder.cateSelect.setOnClickListener(v -> {
            if (holder.cateSelect.isChecked()) {
                selectedCategories.add(category);
            } else {
                selectedCategories.remove(category);
            }
            notifyItemChanged(position); // Update the item to reflect the new state
        });

        // Long click to select for delete
        holder.itemView.setOnLongClickListener(v -> {
            holder.cateSelect.setChecked(!holder.cateSelect.isChecked());
            if (holder.cateSelect.isChecked()) {
                selectedCategories.add(category);
            } else {
                selectedCategories.remove(category);
            }
            notifyItemChanged(position); // Update the item to reflect the new state
            return true; // Consume the long click event
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteSelectedCategories() {
        if (selectedCategories.isEmpty()) {
            return; // No categories to delete
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        // Create a batch to perform multiple delete operations
        WriteBatch batch = database.batch();

        for (Category category : selectedCategories) {
            DocumentReference categoryRef = database.collection("quizofuser")
                    .document(uid)
                    .collection("categories")
                    .document(category.getCategoryId());
            batch.delete(categoryRef); // Queue the deletion of the category
        }

        // Commit the batch
        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    // Remove from local list and clear selection
                    categories.removeAll(selectedCategories);
                    selectedCategories.clear();
                    notifyDataSetChanged(); // Notify the adapter that the data has changed
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error deleting categories", e);
                });
    }

    public List<Category> getSelectedCategories() {
        return selectedCategories;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class UserCategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        CheckBox cateSelect;
        LinearLayout liCate;

        public UserCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.category);
            cateSelect = itemView.findViewById(R.id.cateSelect);
            liCate = itemView.findViewById(R.id.liCate);
        }
    }
}
