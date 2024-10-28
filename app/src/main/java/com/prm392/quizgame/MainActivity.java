package com.prm392.quizgame;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.prm392.quizgame.databinding.ActivityMainBinding;
import com.prm392.quizgame.fragment.HomeFragment;
import com.prm392.quizgame.fragment.LeaderBoardsFragment;
import com.prm392.quizgame.fragment.ProfileFragment;
import com.prm392.quizgame.fragment.UserCategoryFragment;
import com.prm392.quizgame.fragment.WalletFragment;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import me.ibrahimsn.lib.OnItemSelectedListener;

public class MainActivity extends AppCompatActivity {
    // Tham chiếu tới Firebase Realtime Database

    ActivityMainBinding binding;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new HomeFragment());
        transaction.commit();
        binding.bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (i) {
                    case 0:
                        transaction.replace(R.id.content, new HomeFragment());
                        break;
                    case 1:
                        transaction.replace(R.id.content, new UserCategoryFragment());

                        break;
                    case 2:
                        transaction.replace(R.id.content, new LeaderBoardsFragment());

                        break;
                    case 3:
                        transaction.replace(R.id.content, new WalletFragment());

                        break;
                    case 4:
                        transaction.replace(R.id.content, new ProfileFragment());

                        break;
                }
                transaction.commit();
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.wallet) {
            Toast.makeText(this, "Wallet is clicked", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
