package com.example.motoserv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

enum ProviderType {
    FACEBOOK,
    GOOGLE
}

public class RegisterUserActivity extends AppCompatActivity {

    SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        MyToolbar.show(this, "Completar perfil", false);

        mPreferences = getApplicationContext().getSharedPreferences("typeAccount", MODE_PRIVATE);

        String typeAccount = mPreferences.getString("account", "notype");

        Toast.makeText(this, typeAccount, Toast.LENGTH_SHORT).show();

        // Get the Intent that started this activity and extract the string
        /*Intent intent = getIntent();
        String provider = intent.getStringExtra(LoginActivity.EXTRA_PROVIDER);

        final Button mButtonLogOut = findViewById(R.id.salir);

        mButtonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (provider != null) {
                    Toast.makeText(RegisterUserActivity.this, "No es nulo", Toast.LENGTH_SHORT).show();
                    Log.d("msg", "no es nulo");
                    if (provider.equals(ProviderType.FACEBOOK.name())) {
                        LoginManager.getInstance().logOut();
                    }
                }
                FirebaseAuth.getInstance().signOut();
                onBackPressed();
            }
        }); */

    }
}