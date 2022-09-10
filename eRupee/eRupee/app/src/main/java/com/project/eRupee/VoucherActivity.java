package com.project.eRupee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class    VoucherActivity extends AppCompatActivity {
    FirebaseFirestore database;
    ProgressDialog progressDialog;
    EditText countET, amountET;
    Button generateBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vohucher);
        progressDialog = new ProgressDialog(this);
        database = FirebaseFirestore.getInstance();
        countET = findViewById(R.id.count);
        amountET = findViewById(R.id.amount);
        generateBT = findViewById(R.id.generate);

        generateBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < Integer.parseInt(countET.getText().toString()); i++) {
                    progressDialog.setMessage("Adding Vouchers...");
                    progressDialog.show();
                    GenerateVoucher();
                }
            }
        });
    }

    private void GenerateVoucher() {
        StringBuilder result = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int charactersLength = characters.length();
        for (int i = 0; i < 12; i++) {
            result.append(characters.charAt((int) Math.floor(Math.random() * charactersLength)));
        }
        writeDatabase(result.toString());
    }

    public void writeDatabase(String voucherCode) {
        Map<String, Object> codeReference = new HashMap<>();
        codeReference.put("code", voucherCode);
        codeReference.put("amount", amountET.getText().toString());
//        userReference.put("phone", phone);
//        userReference.put("email", email);
//        userReference.put("aadhaar", aadhaar);
//        userReference.put("password", password);
        database.collection("vouchers")
                .document(voucherCode)
                .set(codeReference)
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
}