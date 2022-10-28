package com.example.motoserv.client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.motoserv.R;

public class RequestDriverActivity extends AppCompatActivity {

    private LottieAnimationView mAnimationView;
    private TextView mTextViewLookingFor;
    private Button mButtonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_driver);

        mAnimationView = findViewById(R.id.animation_view);
        mTextViewLookingFor = findViewById(R.id.text_view_looking_for);
        mButtonCancel = findViewById(R.id.btn_cancel_viaje);

        mAnimationView.playAnimation();
    }
}