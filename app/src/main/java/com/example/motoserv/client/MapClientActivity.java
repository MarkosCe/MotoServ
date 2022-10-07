package com.example.motoserv.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.motoserv.LoginActivity;
import com.example.motoserv.R;
import com.example.motoserv.driver.MapDriverActivity;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class MapClientActivity extends AppCompatActivity {

    SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_client);

        // Get the Intent that started this activity and extract the string
        mPreferences = getApplicationContext().getSharedPreferences("typeProvider", MODE_PRIVATE);
        String provider= mPreferences.getString("provider", "notype");

        final Button mButtonLogOut = findViewById(R.id.btn_logout_c);

        mButtonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (provider != null) {
                    Toast.makeText(MapClientActivity.this, "No es nulo", Toast.LENGTH_SHORT).show();
                    if (provider.equals("FACEBOOK")) {
                        LoginManager.getInstance().logOut();
                    }
                }
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MapClientActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}