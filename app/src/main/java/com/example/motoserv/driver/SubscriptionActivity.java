package com.example.motoserv.driver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.motoserv.MyToolbar;
import com.example.motoserv.R;

public class SubscriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        MyToolbar.show(this, "Suscripcion", true);
    }
}