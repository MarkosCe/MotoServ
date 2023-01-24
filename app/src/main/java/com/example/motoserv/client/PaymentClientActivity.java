package com.example.motoserv.client;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.motoserv.R;
import com.example.motoserv.providers.AuthProvider;
import com.example.motoserv.providers.ClientBookingProvider;
import com.example.motoserv.providers.ClientProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class PaymentClientActivity extends AppCompatActivity {

    private Button mButtonPay;

    private AuthProvider mAuthProvider;
    private ClientBookingProvider mClientBookingProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_client);

        mButtonPay = findViewById(R.id.btn_pay_now);

        mAuthProvider = new AuthProvider();
        mClientBookingProvider = new ClientBookingProvider();

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                moveTaskToBack(true);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        mButtonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payNow();
            }
        });
    }

    private void payNow(){
        mClientBookingProvider.updateStatus(mAuthProvider.getId(), "paid").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mButtonPay.setEnabled(false);
                    Toast.makeText(PaymentClientActivity.this, "Pago registrado", Toast.LENGTH_SHORT).show();
                    updateUI();
                }else {
                    Toast.makeText(PaymentClientActivity.this, "Error: pago no registrado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUI(){
        Intent intent = new Intent(PaymentClientActivity.this, RateDriverActivity.class);
        startActivity(intent);
        finish();
    }
}