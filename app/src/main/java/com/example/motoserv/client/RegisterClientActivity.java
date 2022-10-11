package com.example.motoserv.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.motoserv.MyToolbar;
import com.example.motoserv.R;
import com.example.motoserv.driver.MapDriverActivity;
import com.example.motoserv.driver.RegisterDriverActivity;
import com.example.motoserv.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterClientActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        MyToolbar.show(this, "Completar registro", false);

        mAuth = FirebaseAuth.getInstance();

        final Button button = findViewById(R.id.btn_next_driver);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String id = mAuth.getCurrentUser().getUid();
                saveUser(id);
            }
        });
    }

    void saveUser(String id) {
        User user = new User();
        user.setName("Prueba");
        mDatabase.child("Users").child("Pasajeros").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterClientActivity.this, "Correcto", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterClientActivity.this, MapClientActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterClientActivity.this, "Algo falló", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}