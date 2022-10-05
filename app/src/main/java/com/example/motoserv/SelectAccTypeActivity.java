package com.example.motoserv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

public class SelectAccTypeActivity extends AppCompatActivity {

    SharedPreferences mPreferences;
    SharedPreferences.Editor editor;

    private RadioButton mRbtnPass;
    private RadioButton mRbtnDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_acc_type);

        MyToolbar.show(this, "Selecciona el tipo de cuenta", false);

        mPreferences = getApplication().getSharedPreferences("typeAccount", MODE_PRIVATE);
        editor = mPreferences.edit();

        mRbtnPass = findViewById(R.id.rbtn_type_pass);
        mRbtnDriver = findViewById(R.id.rbtn_type_driver);

        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        });

    }
    public void onRadioButtonClicked(View view) {

        if (mRbtnPass.isChecked()){
            editor.putString("account", "cliente");
            editor.apply();
            Intent intent = new Intent(SelectAccTypeActivity.this, RegisterUserActivity.class);
            startActivity(intent);
        }else if (mRbtnDriver.isChecked()){
            editor.putString("account", "driver");
            editor.apply();
            Intent intent = new Intent(SelectAccTypeActivity.this, RegisterDriverActivity.class);
            startActivity(intent);
        }else {
            Toast.makeText(this, "Elige una opcion", Toast.LENGTH_SHORT).show();
        }

    }
}