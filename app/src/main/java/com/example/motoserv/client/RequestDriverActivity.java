package com.example.motoserv.client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.motoserv.R;
import com.example.motoserv.providers.GeofireProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;

public class RequestDriverActivity extends AppCompatActivity {

    private LottieAnimationView mAnimationView;
    private TextView mTextViewLookingFor;
    private Button mButtonCancel;

    private GeofireProvider mGeofireProvider;

    private double mExtraOriginLat;
    private double mExtraOriginLng;
    private LatLng mOriginLatLng;

    private double mRadius = 0.1;
    private boolean mDriverFound = false;
    private String mIdDriverFound = "";
    private LatLng mDriverFoundLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_driver);

        mAnimationView = findViewById(R.id.animation_view);
        mTextViewLookingFor = findViewById(R.id.text_view_looking_for);
        mButtonCancel = findViewById(R.id.btn_cancel_viaje);

        mExtraOriginLat = getIntent().getDoubleExtra("originLat", 0);
        mExtraOriginLng = getIntent().getDoubleExtra("originLng", 0);
        mOriginLatLng = new LatLng(mExtraOriginLat, mExtraOriginLng);

        mGeofireProvider = new GeofireProvider();

        mAnimationView.playAnimation();

        getClosesDrivers();
    }

    private void getClosesDrivers(){
        mGeofireProvider.getActiveDrivers(mOriginLatLng, mRadius).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                if (!mDriverFound){
                    mDriverFound = true;
                    mIdDriverFound = key;
                    mDriverFoundLatLng = new LatLng(location.latitude, location.longitude);
                    mTextViewLookingFor.setText("Conductor encontrado\nEsperando respuesta");

                    Log.d("DRIVER", "ID:" + mIdDriverFound);
                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //ingresa cuando se termina de ejecutar el metodo getActiveDrivers
                if (!mDriverFound){
                    mRadius = mRadius + 0.1f;

                    if (mRadius > 5){
                        //ningun conductor disponible en un radio de 5km
                        mTextViewLookingFor.setText("Ningun conductor disponible");
                        Toast.makeText(RequestDriverActivity.this, "No se encontro un conductor", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        getClosesDrivers();
                    }
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
}