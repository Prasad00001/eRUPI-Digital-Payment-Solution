package com.project.eRupee;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Patterns;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    EditText nameET, aadhaarET, emailET, phoneET, passwordET;
    Button register;
    TextView login;
    boolean isNameValid, isEmailValid, isPhoneValid, isPasswordValid;
    TextInputLayout nameError, emailError, phoneError, passError;
    String name, aadhaar, email, phone, password;
    SharedPreferences sharedPreferences;
    FirebaseFirestore database;
    ProgressDialog progressDialog;
    TextView errorTV;
    String comesFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPreferences = getSharedPreferences(StaticClass.SHARED_PREFERENCES, MODE_PRIVATE);
        database = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);

        nameET = (EditText) findViewById(R.id.name);
        emailET = (EditText) findViewById(R.id.email);
        phoneET = (EditText) findViewById(R.id.phone);
        passwordET = (EditText) findViewById(R.id.password);
        aadhaarET = (EditText) findViewById(R.id.aadhaar);
        errorTV = findViewById(R.id.errorTV);

        register = (Button) findViewById(R.id.register);
        nameError = (TextInputLayout) findViewById(R.id.nameError);
        emailError = (TextInputLayout) findViewById(R.id.emailError);
        phoneError = (TextInputLayout) findViewById(R.id.phoneError);
        passError = (TextInputLayout) findViewById(R.id.passError);

        Intent i = getIntent();
        comesFrom = i.getStringExtra("comesFrom");

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEndUser(comesFrom);
            }
        });
    }

    public void writeDatabase(String comesFrom) {
        String node = "";
        if (comesFrom.equalsIgnoreCase("1")) {
            node = "distributors";
        } else {
            node = "users";
        }
        Map<String, Object> userReference = new HashMap<>();
        userReference.put("name", name);
        userReference.put("phone", phone);
        userReference.put("email", email);
        userReference.put("aadhaar", aadhaar);
        userReference.put("password", password);
        database.collection(node)
                .document(email)
                .set(userReference)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                                "Success",
                                Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(getApplicationContext(), CoreActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Error writing user",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void addEndUser(String comesFrom) {
        name = nameET.getText().toString();
        aadhaar = aadhaarET.getText().toString();
        email = emailET.getText().toString();
        phone = phoneET.getText().toString();
        password = passwordET.getText().toString();

        progressDialog.setMessage("Adding user...");
        progressDialog.show();
        writeDatabase(comesFrom);
    }

    public void displayErrorTV(int resourceID) {
        errorTV.setText(resourceID);
        errorTV.setVisibility(View.VISIBLE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                errorTV.setVisibility(View.GONE);
            }
        }, 1500);
    }
}