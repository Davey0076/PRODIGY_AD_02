package com.example.mytodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class StartScreen extends AppCompatActivity {

    //declaration
    private ImageView checklistImage;
    private ProgressBar progressStart;

    private int progressStatus = 0;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_scren);

        //initialization
        checklistImage = findViewById(R.id.checklistImage);
        progressStart = findViewById(R.id.progressStart);

        // Simulate loading process
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 100) {
                    progressStatus++;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressStart.setProgress(progressStatus);
                        }
                    });
                    try {
                        // Sleep for 30 milliseconds to simulate progress update
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // When progress is complete, start the main activity
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(StartScreen.this, AppDescription.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).start();
    }


    }
