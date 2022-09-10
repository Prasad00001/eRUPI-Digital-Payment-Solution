package com.project.eRupee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.eRupee.models.User;
import com.project.eRupee.models.Vouchers;

import java.util.ArrayList;
import java.util.Objects;

public class DistributeVoucherActivity extends AppCompatActivity {
    Button disVoucher;
    Context context;
    ProgressDialog progressDialog;
    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<Vouchers> vouchersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distribute_voucher);
        context = DistributeVoucherActivity.this;
        disVoucher = findViewById(R.id.disVoucher);
        progressDialog = new ProgressDialog(this);

        userList.clear();
        vouchersList.clear();
        getAllVouchers();
        getAllUsers();

        disVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();
            }
        });

    }

    public void showAlert() {

        new AlertDialog.Builder(context)
                .setTitle("Distribute Voucher")
                .setMessage("Are you sure you want to distribute voucher?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

//                        System.out.println(userList.size());
                        addVoucherToUser();
                        // Continue SMS Code
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void getAllUsers() {
        FirebaseFirestore.getInstance().collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document :
                                    Objects.requireNonNull(task.getResult())) {
                                //System.out.println(document);
                                User user = new User();
                                user.setName(document.getString("name"));
                                user.setPhone(document.getString("phone"));
                                user.setEmail(document.getString("email"));
                                user.setAadhaar(document.getString("aadhaar"));
                                user.setVoucher(document.getString("voucher"));
                                userList.add(user);
                            }
                        } else {
                            Toast.makeText(context,
                                    "Error getting documents." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void getAllVouchers() {
        FirebaseFirestore.getInstance().collection("vouchers").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document :
                                    Objects.requireNonNull(task.getResult())) {
                                //System.out.println(document);
                                Vouchers vouchers = new Vouchers();
                                vouchers.setAmount(document.getString("amount"));
                                vouchers.setCode(document.getString("code"));

                                vouchersList.add(vouchers);
                            }
                        } else {
                            Toast.makeText(DistributeVoucherActivity.this,
                                    "Error getting documents." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void addVoucherToUser() {
        int index = 0;
        while (index < userList.size()) {
            String code = vouchersList.get(index).getCode();
            updateUser(code, userList.get(index).getEmail(), vouchersList.get(index).getAmount());
            index++;
        }

        Toast.makeText(DistributeVoucherActivity.this, "Success", Toast.LENGTH_SHORT).show();

    }

    public void updateUser(String voucherCode, String userEmail, String amount) {

        FirebaseFirestore.getInstance().collection("users").document(userEmail).
                update("voucher", voucherCode, "amount", amount);

    }
}