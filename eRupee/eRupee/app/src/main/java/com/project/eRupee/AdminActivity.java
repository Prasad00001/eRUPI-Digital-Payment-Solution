package com.project.eRupee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    Button logoutBT, distributorBT, voucherBT, userBT, disVoucher;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        logoutBT = findViewById(R.id.logout);
        distributorBT = findViewById(R.id.distributor);
        voucherBT = findViewById(R.id.voucher);
        userBT = findViewById(R.id.userBT);
        disVoucher = findViewById(R.id.disVoucher);
        sharedPreferences = getSharedPreferences(StaticClass.SHARED_PREFERENCES, MODE_PRIVATE);

        logoutBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        distributorBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.putExtra("comesFrom", "1");
                startActivity(intent);
            }
        });

        userBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.putExtra("comesFrom", "2");
                startActivity(intent);
            }
        });


        voucherBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), VoucherActivity.class));
            }
        });

        disVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DistributeVoucherActivity.class));
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
}