package com.project.eRupee;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;
import com.project.eRupee.models.Vouchers;

import java.util.ArrayList;
import java.util.Objects;

public class RedeemActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    Button redeemBT;
    EditText voucherCode;
    private ArrayList<Vouchers> vouchersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        redeemBT = findViewById(R.id.redeemBT);
        voucherCode = findViewById(R.id.voucherCode);
        vouchersList.clear();
        getAllVouchers();

        redeemBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matchCode(voucherCode.getText().toString().trim());
            }
        });

        mCodeScanner = new CodeScanner(this, scannerView);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RedeemActivity.this, result.getText(), Toast.LENGTH_SHORT).show();

                        matchCode(result.getText());
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    public void matchCode(String code) {

        int index = 0;
        boolean matches = false;

        while (index < vouchersList.size()) {
            String vCode = vouchersList.get(index).getCode();
            if (vCode.equalsIgnoreCase(code)) {
                matches = true;
            }
            index++;
        }

        if (matches) {
            Toast.makeText(RedeemActivity.this,
                    "Redeemed Successfully",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RedeemActivity.this,
                    "Voucher code does not exists",
                    Toast.LENGTH_SHORT).show();
        }
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
                            Toast.makeText(RedeemActivity.this,
                                    "Error getting documents." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}