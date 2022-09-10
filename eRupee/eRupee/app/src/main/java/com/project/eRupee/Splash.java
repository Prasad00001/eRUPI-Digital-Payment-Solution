package com.project.eRupee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Splash extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore database;
    SharedPreferences sharedPreferences;
    String userType = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(StaticClass.SHARED_PREFERENCES, MODE_PRIVATE);

        userType = sharedPreferences.getString(StaticClass.USER_TYPE, "0");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkEntry();
            }
        }, 1000);
    }

    public void checkEntry() {
        if (userType.equalsIgnoreCase("1")) {
            startActivity(new Intent(getApplicationContext(), AdminActivity.class));
        } else if (userType.equalsIgnoreCase("2")) {
            startActivity(new Intent(getApplicationContext(), DistributorActivity.class));
        } else if (userType.equalsIgnoreCase("3")) {
            startActivity(new Intent(getApplicationContext(), UserActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
    }
}
