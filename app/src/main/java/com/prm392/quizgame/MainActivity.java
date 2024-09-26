package com.prm392.quizgame;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prm392.quizgame.databinding.ActivityMainBinding;
import com.prm392.quizgame.fragment.LeaderBoardsFragment;
import com.prm392.quizgame.fragment.ProfileFragment;
import com.prm392.quizgame.fragment.WalletFragment;
import com.prm392.quizgame.model.Category;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;

import me.ibrahimsn.lib.OnItemSelectedListener;

public class MainActivity extends AppCompatActivity {
    // Tham chiếu tới Firebase Realtime Database
    private DatabaseReference database;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//        binding.bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
//            @Override
//            public boolean onItemSelect(int i) {
//                switch (i) {
//                    case 0:
//                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
//                        break;
//                    case 1:
//                        transaction.replace(R.id.content, new LeaderBoardsFragment());
//                        transaction.commit();
//                        break;
//                    case 2:
//                        transaction.replace(R.id.content, new WalletFragment());
//                        transaction.commit();
//                        break;
//                    case 3:
//                        transaction.replace(R.id.content, new ProfileFragment());
//                        transaction.commit();
//                        break;
//                }
//                return false;
//            }
//        });

        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category("","Mathematics","https://cdn-icons-png.flaticon.com/512/746/746960.png"));
        categories.add(new Category("","Science","https://cdn-icons-png.flaticon.com/512/647/647858.png"));
        categories.add(new Category("","History","https://cdn-icons-png.flaticon.com/512/5582/5582334.png"));
        categories.add(new Category("","Puzzle","https://cdn-icons-png.freepik.com/512/8317/8317697.png"));
        categories.add(new Category("","Drama","https://cdn-icons-png.flaticon.com/512/1048/1048962.png"));
        categories.add(new Category("","Drama","https://cdn-icons-png.flaticon.com/512/1048/1048962.png"));
        categories.add(new Category("","Drama","https://cdn-icons-png.flaticon.com/512/1048/1048962.png"));
        CategoryAdapter adapter = new CategoryAdapter(this,categories);
        binding.categoryList.setLayoutManager(new GridLayoutManager(this,2));
        binding.categoryList.setAdapter(adapter);
//        setContentView(R.layout.activity_main);

//        // Thiết lập khởi tạo giao diện (UI)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        // Khởi tạo Firebase Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        database = firebaseDatabase.getReference();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.wallet){
            Toast.makeText(this, "Wallet is clicked", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
