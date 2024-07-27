package com.example.mytodo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomePage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView txtUserName;
    private ImageButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        txtUserName = findViewById(R.id.txtUserName);
        btnLogout = findViewById(R.id.btnLogout);
        BottomNavigationView bottomNavBar = findViewById(R.id.bottomNavBar);

        fetchUserName();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(HomePage.this, LoginPage.class));
                finish();
            }
        });

        bottomNavBar.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_tasks) {
                loadFragment(new TasksFragment());
                return true;
            } else if (item.getItemId() == R.id.navigation_calendar) {
                loadFragment(new CalendarFragment());
                return true;
            } else {
                return false;
            }
        });


        // Load default fragment
        if (savedInstanceState == null) {
            bottomNavBar.setSelectedItemId(R.id.navigation_tasks); // Change to the default fragment if needed
        }
    }

    private void fetchUserName() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userName = documentSnapshot.getString("userName");
                            txtUserName.setText("Hello, " + userName);
                        } else {
                            Toast.makeText(HomePage.this, "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(HomePage.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
