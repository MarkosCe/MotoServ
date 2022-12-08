package com.example.motoserv.client;

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
import com.example.motoserv.models.Client;
import com.example.motoserv.providers.ClientProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterClientActivity extends AppCompatActivity {

    TextInputEditText mTextInputUserName;
    RadioGroup mGender;

    SharedPreferences mPreferences;

    ClientProvider mClientProvider;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_client);

        MyToolbar.show(this, "Completar registro", false);

        mClientProvider = new ClientProvider();
        mAuth = FirebaseAuth.getInstance();
        mPreferences = getApplication().getSharedPreferences("preferences", MODE_PRIVATE);

        mTextInputUserName = findViewById(R.id.input_name_client);
        mGender = findViewById(R.id.groupbtn_gender_client);

        final Button button = findViewById(R.id.btn_empezar);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String id = mAuth.getCurrentUser().getUid();
                registerUserInfo(id);
            }
        });
    }

    void registerUserInfo(String id) {
        String userName = mTextInputUserName.getText().toString();
        if (!userName.equals("") && mGender.getCheckedRadioButtonId() != -1) {
            Client client = new Client();
            client.setId(id);
            client.setProvider(mPreferences.getString("provider", ""));
            client.setName(userName);
            client.setGender(getGender());
            create(client);
        }
    }

    void create(Client client){
        mClientProvider.create(client).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterClientActivity.this, "Registradooo cliente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterClientActivity.this, MapClientActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(RegisterClientActivity.this, "Algo salío mal cliente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getGender(){
        String gender = "";
        if (mGender.getCheckedRadioButtonId() == R.id.radio_hombre_client){
            gender = "Male";
        }else{
            gender = "Female";
        }
        return gender;
    }

}