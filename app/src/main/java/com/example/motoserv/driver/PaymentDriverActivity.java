package com.example.motoserv.driver;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.motoserv.R;
import com.example.motoserv.providers.ClientBookingProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class PaymentDriverActivity extends AppCompatActivity {

    private LottieAnimationView mAnimationView;
    private TextView mTextViewMount;
    private TextView mTextViewStatus;
    private Button mButtonAccept;

    private ClientBookingProvider mClientBookingProvider;

    private ValueEventListener mListenerStatus;

    private String mExtraIdClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_driver);

        mAnimationView = findViewById(R.id.animation_view_driver);
        mTextViewMount = findViewById(R.id.text_view_mount);
        mTextViewStatus = findViewById(R.id.text_view_payment_status);
        mButtonAccept = findViewById(R.id.btn_accept_payment);

        mClientBookingProvider = new ClientBookingProvider();

        mExtraIdClient = getIntent().getStringExtra("idClient");

        mButtonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptPayment();
            }
        });

        getStatusPayment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListenerStatus != null) {
            mClientBookingProvider.getStatus(mExtraIdClient).removeEventListener(mListenerStatus);
        }
    }

    private void getStatusPayment(){
        mListenerStatus = mClientBookingProvider.getStatus(mExtraIdClient).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String status = (String) snapshot.getValue();
                    assert status != null;
                    if (status.equals("paid")){
                        updateUi();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUi(){
        mAnimationView.setAnimation(R.raw.dcoin);
        mTextViewStatus.setText("Pago recibido");
        mButtonAccept.setVisibility(View.VISIBLE);
    }

    private void acceptPayment(){
        Intent intent = new Intent(PaymentDriverActivity.this, RateClientActivity.class);
        intent.putExtra("idClient", mExtraIdClient);
        startActivity(intent);
        finish();
    }
}