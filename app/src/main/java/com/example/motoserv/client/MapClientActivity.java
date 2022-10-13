package com.example.motoserv.client;

import androidx.annotation.NonNull;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

public class MapClientActivity extends AppCompatActivity implements OnMapReadyCallback {

    SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_client);

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_client);
        mapFragment.getMapAsync(this);

        // Get the Intent that started this activity and extract the string
        /*mPreferences = getApplicationContext().getSharedPreferences("typeProvider", MODE_PRIVATE);
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
        });*/

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
    }
}