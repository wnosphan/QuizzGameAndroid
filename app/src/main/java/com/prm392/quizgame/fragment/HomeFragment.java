package com.prm392.quizgame.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.prm392.quizgame.CategoryAdapter;
import com.prm392.quizgame.MainActivity;
import com.prm392.quizgame.R;
import com.prm392.quizgame.SpinnerActivity;
import com.prm392.quizgame.databinding.FragmentHomeBinding;
import com.prm392.quizgame.model.Category;

import java.util.ArrayList;


public class HomeFragment extends Fragment {



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
     FragmentHomeBinding binding;
    FirebaseFirestore db ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        ArrayList<Category> categories = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        CategoryAdapter adapter = new CategoryAdapter(getContext(),categories);
        db.collection("category").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    Category category = documentSnapshot.toObject(Category.class);
                    category.setCategoryId(documentSnapshot.getId());
                    categories.add(category);
                }
                adapter.notifyDataSetChanged();
            }
        });

        binding.categoryList.setLayoutManager(new GridLayoutManager(getContext(),2));
        binding.categoryList.setAdapter(adapter);

        binding.btnSpinner.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), SpinnerActivity.class));
        });
        binding.btnInvite.setOnClickListener(view -> {
            // Giả sử bạn có đường dẫn tới bức ảnh trong biến imagePath
            String imagePath = "https://lh5.googleusercontent.com/qA4Xs4ZjXC5uXFyvVxE3ti2DaydWFzRqBXBQVCX89D3_Db-mqbuMasm5CNu56QgV4Kjm82cVvk8NLfT_O5rUSjw=w1280"; // Thay đổi với đường dẫn thực tế

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg"); // Hoặc "image/png" nếu là ảnh PNG
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imagePath));
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        });


        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}