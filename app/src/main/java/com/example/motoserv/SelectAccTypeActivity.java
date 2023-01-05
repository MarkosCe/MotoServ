package com.example.motoserv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.motoserv.client.MapClientActivity;
import com.example.motoserv.client.RegisterClientActivity;
import com.example.motoserv.driver.MapDriverActivity;
import com.example.motoserv.driver.RegisterDriverActivity;

public class SelectAccTypeActivity extends AppCompatActivity {

    private RadioButton mRbtnPass;
    private RadioButton mRbtnDriver;

    SharedPreferences mPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_acc_type);

        MyToolbar.show(this, "Selecciona el tipo de cuenta", false);

        mRbtnPass = findViewById(R.id.rbtn_type_pass);
        mRbtnDriver = findViewById(R.id.rbtn_type_driver);

        mPreferences = getApplication().getSharedPreferences("preferences", MODE_PRIVATE);
        editor = mPreferences.edit();

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
            //editor.putString("typeAcc", "Client");
            //editor.apply();
            Intent intent = new Intent(SelectAccTypeActivity.this, RegisterClientActivity.class);
            intent.putExtra("typeAcc", "Client");
            startActivity(intent);
            finish();
        }else if (mRbtnDriver.isChecked()){
            //editor.putString("typeAcc", "Driver");
            //editor.apply();
            Intent intent = new Intent(SelectAccTypeActivity.this, RegisterDriverActivity.class);
            intent.putExtra("typeAcc", "Driver");
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(this, "Elige una opcion", Toast.LENGTH_SHORT).show();
        }

    }
}