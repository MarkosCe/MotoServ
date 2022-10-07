package com.example.motoserv.driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.motoserv.LoginActivity;
import com.example.motoserv.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class MapDriverActivity extends AppCompatActivity {

    SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_driver);

        // Get the Intent that started this activity and extract the string
        mPreferences = getApplicationContext().getSharedPreferences("typeProvider", MODE_PRIVATE);
        String provider= mPreferences.getString("provider", "notype");

        final Button mButtonLogOut = findViewById(R.id.btn_logout);

        mButtonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (provider != null) {
                    Toast.makeText(MapDriverActivity.this, "No es nulo", Toast.LENGTH_SHORT).show();
                    if (provider.equals("FACEBOOK")) {
                        LoginManager.getInstance().logOut();
                    }
                }
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MapDriverActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}