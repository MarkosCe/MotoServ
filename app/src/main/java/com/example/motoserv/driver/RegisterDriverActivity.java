package com.example.motoserv.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.motoserv.MyToolbar;
import com.example.motoserv.R;
import com.example.motoserv.models.Driver;
import com.example.motoserv.providers.DriverProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterDriverActivity extends AppCompatActivity {

    TextInputEditText mTextInputUserName;
    RadioGroup mGender;

    SharedPreferences mPreferences;

    DriverProvider mDriverProvider;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);

        MyToolbar.show(this, "Completar registro", false);

        mDriverProvider = new DriverProvider();

        mPreferences = getApplication().getSharedPreferences("preferences", MODE_PRIVATE);

        mTextInputUserName = findViewById(R.id.input_user_name);
        mGender = findViewById(R.id.group_button_gender);

        mAuth = FirebaseAuth.getInstance();

        final Button button = findViewById(R.id.btn_listo);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String id = mAuth.getCurrentUser().getUid();
                registerUserInfo(id);
            }
        });
    }

    void registerUserInfo(String id){
        String userName = mTextInputUserName.getText().toString();
        if (!userName.equals("") && mGender.getCheckedRadioButtonId() != -1){
            Driver driver = new Driver();
            driver.setId(id);
            driver.setProvider(mPreferences.getString("provider",""));
            driver.setName(userName);
            driver.setGender(getGender());
            create(driver);
        }else {
            Toast.makeText(RegisterDriverActivity.this, "Completar informacion", Toast.LENGTH_SHORT).show();
        }
    }

    void create(Driver driver){
        mDriverProvider.create(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterDriverActivity.this, "Registradooo", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterDriverActivity.this, RegisterDriverTwoActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(RegisterDriverActivity.this, "Algo salío mal", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getGender(){
        String gender = "";
        if (mGender.getCheckedRadioButtonId() == R.id.radio_hombre){
            gender = "Male";
        }else{
            gender = "Female";
        }
        return gender;
    }
}