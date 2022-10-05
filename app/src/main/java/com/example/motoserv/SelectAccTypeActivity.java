package com.example.motoserv;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SelectAccTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_acc_type);

        MyToolbar.show(this, "Selecciona el tipo de cuenta", false);

    }
}