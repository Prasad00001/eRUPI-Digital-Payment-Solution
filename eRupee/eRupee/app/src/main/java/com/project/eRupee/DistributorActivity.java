package com.project.eRupee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.eRupee.models.User;
import com.project.eRupee.models.Vouchers;

import java.util.ArrayList;
import java.util.Objects;

public class DistributorActivity extends AppCompatActivity {
    Button logoutBT, addUserBT, redeemBT;
    SharedPreferences sharedPreferences;
    private ArrayList<Vouchers> vouchersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor);
        sharedPreferences = getSharedPreferences(StaticClass.SHARED_PREFERENCES, MODE_PRIVATE);

        logoutBT = findViewById(R.id.logout);
        addUserBT = findViewById(R.id.userBT);
        redeemBT = findViewById(R.id.redeemBT);

        vouchersList.clear();
        addUserBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.putExtra("comesFrom", "2");
                startActivity(intent);
            }
        });

        redeemBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RedeemActivity.class));
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
                            Toast.makeText(DistributorActivity.this,
                                    "Error getting documents." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}