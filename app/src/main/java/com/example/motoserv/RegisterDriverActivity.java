package com.example.motoserv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class RegisterDriverActivity extends AppCompatActivity {

    SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);

        mPreferences = getApplicationContext().getSharedPreferences("typeAccount", MODE_PRIVATE);

        String typeAccount = mPreferences.getString("account", "notype");

        Toast.makeText(this, typeAccount, Toast.LENGTH_SHORT).show();

    }
}