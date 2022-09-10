package com.project.eRupee;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    Button login;
    boolean isEmailValid, isPasswordValid;
    TextInputLayout emailError, passError;
    private FirebaseAuth firebaseAuth;
    EditText emailET, passwordET;
    TextView errorTV;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    FirebaseFirestore database;
    RadioButton userButton;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(StaticClass.SHARED_PREFERENCES, MODE_PRIVATE);
        errorTV = findViewById(R.id.errorTV);
        progressDialog = new ProgressDialog(this);
        emailET = (EditText) findViewById(R.id.email);
        passwordET = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        emailError = (TextInputLayout) findViewById(R.id.emailError);
        passError = (TextInputLayout) findViewById(R.id.passError);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetValidation();
            }
        });
    }

    public void SetValidation() {
        // Check for a valid email address.
        if (emailET.getText().toString().isEmpty()) {
            emailError.setError(getResources().getString(R.string.email_error));
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailET.getText().toString()).matches()) {
            emailError.setError(getResources().getString(R.string.error_invalid_email));
            isEmailValid = false;
        } else {
            isEmailValid = true;
            emailError.setErrorEnabled(false);
        }

        // Check for a valid password.
        if (passwordET.getText().toString().isEmpty()) {
            passError.setError(getResources().getString(R.string.password_error));
            isPasswordValid = false;
        } else if (passwordET.getText().length() < 6) {
            passError.setError(getResources().getString(R.string.error_invalid_password));
            isPasswordValid = false;
        } else {
            isPasswordValid = true;
            passError.setErrorEnabled(false);
        }

        if (isEmailValid && isPasswordValid) {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            userButton = (RadioButton) findViewById(selectedId);
            Toast.makeText(LoginActivity.this, userButton.getText(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(), "Successfully", Toast.LENGTH_SHORT).show();
            String userType = "";
            if (userButton.getText().toString().contains("Admin")) {
                userType = "1"; //Admin
            } else if (userButton.getText().toString().contains("Distributor")) {
                userType = "2";  //Distributor
            } else {
                userType = "3";    //End User
            }

            login(emailET.getText().toString(), passwordET.getText().toString(), userType);
        }

    }

    public void login(final String email, final String password, final String userType) {
        progressDialog.setMessage("Login...");
        progressDialog.show();
        if (userType.equalsIgnoreCase("1")) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
//                            getDataByDocument();
                                Toast.makeText(getApplicationContext(), "Successfully Logged In", Toast.LENGTH_SHORT).show();
                                if (email.equalsIgnoreCase("admin@erupee.gov.in")) {
                                    editSharedPreferences(userType, email);
                                    Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                                    startActivity(intent);
                                }
                            } else {
                                progressDialog.dismiss();
                                displayErrorTV(R.string.authentication_failed);
                            }
                        }
                    });
        } else if (userType.equalsIgnoreCase("2")) {
            DocumentReference documentReference =
                    database.collection("distributors")
                            .document(email);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            System.out.println(task.getResult().get("password"));
                            String DBPassword = task.getResult().get("password").toString();

                            if (DBPassword.equals(password)) {
                                editSharedPreferences(userType, email);
                                startActivity(new Intent(getApplicationContext(), DistributorActivity.class));
                            } else {
                                progressDialog.dismiss();
                                displayErrorTV(R.string.authentication_failed);
                                Toast.makeText(getApplicationContext(),
                                        "Wrong credentials",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    "get failed with " + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                                "get failed with " + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            DocumentReference documentReference =
                    database.collection("users")
                            .document(email);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            System.out.println(task.getResult().get("password"));
                            String DBPassword = task.getResult().get("password").toString();

                            if (DBPassword.equals(password)) {
                                editSharedPreferences(userType, email);
                                startActivity(new Intent(getApplicationContext(), UserActivity.class));
                            } else {
                                progressDialog.dismiss();
                                displayErrorTV(R.string.authentication_failed);
                                Toast.makeText(getApplicationContext(),
                                        "Wrong credentials",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    "get failed with " + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                                "get failed with " + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void editSharedPreferences(String userType, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(StaticClass.USER_TYPE, userType);
        editor.putString(StaticClass.EMAIL, email);
        editor.apply();
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