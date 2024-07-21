package com.example.mytodo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AppDescription extends AppCompatActivity {


    //declaration
    private Button btnRedirectLogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_description);

        //initialization
        btnRedirectLogin = findViewById(R.id.btnRedirectLogin);

        btnRedirectLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(AppDescription.this, LoginPage.class);
                startActivity(loginIntent);
                finish();

            }
        });
    }
}