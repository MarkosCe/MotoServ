package com.example.motoserv.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.motoserv.LoginActivity;
import com.example.motoserv.MyToolbar;
import com.example.motoserv.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.auth.FirebaseAuth;

public class HomeDriverActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_driver);

        MyToolbar.show(this, "Home", false);

        // Get the Intent that started this activity and extract the string
        mPreferences = getApplicationContext().getSharedPreferences("typeProvider", MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driver_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout){
            Toast.makeText(this, "Salir", Toast.LENGTH_SHORT).show();
            logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut(){
        //disconnect();
        String provider= mPreferences.getString("provider", "notype");
        if (provider != null) {
            Toast.makeText(HomeDriverActivity.this, "No es nulo", Toast.LENGTH_SHORT).show();
            if (provider.equals("FACEBOOK")) {
                LoginManager.getInstance().logOut();
            }
        }
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(HomeDriverActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}