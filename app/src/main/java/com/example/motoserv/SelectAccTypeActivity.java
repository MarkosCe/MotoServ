package com.example.motoserv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.motoserv.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SelectAccTypeActivity extends AppCompatActivity {

    private RadioButton mRbtnPass;
    private RadioButton mRbtnDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_acc_type);

        MyToolbar.show(this, "Selecciona el tipo de cuenta", false);

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
            Intent intent = new Intent(SelectAccTypeActivity.this, RegisterUserActivity.class);
            startActivity(intent);
        }else if (mRbtnDriver.isChecked()){
            Intent intent = new Intent(SelectAccTypeActivity.this, RegisterDriverActivity.class);
            startActivity(intent);
        }else {
            Toast.makeText(this, "Elige una opcion", Toast.LENGTH_SHORT).show();
        }

    }
}