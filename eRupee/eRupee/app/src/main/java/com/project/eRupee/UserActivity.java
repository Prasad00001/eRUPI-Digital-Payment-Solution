package com.project.eRupee;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserActivity extends AppCompatActivity {
    Button logoutBT, redeemCodeBT;
    SharedPreferences sharedPreferences;
    TextView voucherCodeTV, nameTV, amount;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        sharedPreferences = getSharedPreferences(StaticClass.SHARED_PREFERENCES, MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);

        logoutBT = findViewById(R.id.logout);
        voucherCodeTV = findViewById(R.id.voucherCode);
        nameTV = findViewById(R.id.nameTV);
        redeemCodeBT = findViewById(R.id.redeemCodeBT);
        amount = findViewById(R.id.amount);

        getUser();

        redeemCodeBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String voucher = voucherCodeTV.getText().toString().split(" ")[1];
                System.out.println("VOUCHHHH" + voucher);
                Intent intent = new Intent(getApplicationContext(), QRActivity.class);
                intent.putExtra("QRdata", voucher);
                startActivity(intent);
            }
        });

        logoutBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        editSharedPreferences("0", "0");
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    public void editSharedPreferences(String userType, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(StaticClass.USER_TYPE, userType);
        editor.putString(StaticClass.EMAIL, email);
        editor.apply();
    }

    private void getUser() {

        String email = sharedPreferences.getString(StaticClass.EMAIL, "");
        DocumentReference documentReference =
                FirebaseFirestore.getInstance().collection("users")
                        .document(email);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        System.out.println(document.getData());

                        String voucher = document.getString("voucher");
                        String name = document.getString("name");
                        String vAmount = document.getString("amount");
                        //String voucher = document.getString("voucher");
                        voucherCodeTV.setText("Code: " + voucher);
                        nameTV.setText("Name: " + name);
                        amount.setText("Amount: " + vAmount + "/-");

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