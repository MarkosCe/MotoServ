package com.example.motoserv.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.motoserv.R;
import com.example.motoserv.models.FCMBody;
import com.example.motoserv.models.FCMResponse;
import com.example.motoserv.providers.GeofireProvider;
import com.example.motoserv.providers.NotificationProvider;
import com.example.motoserv.providers.TokenProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestDriverActivity extends AppCompatActivity {

    private LottieAnimationView mAnimationView;
    private TextView mTextViewLookingFor;
    private Button mButtonCancel;

    private GeofireProvider mGeofireProvider;
    private NotificationProvider mNotificationProvider;
    private TokenProvider mTokenProvider;

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
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();

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
                    sendNotification();
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

    private void sendNotification(){
        mTokenProvider.getToken(mIdDriverFound).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String token = Objects.requireNonNull(snapshot.child("token").getValue()).toString();
                    Map<String, String> data = new HashMap<>();
                    data.put("title","Nuevo viaje");
                    data.put("body","Tienes una nueva solicitud de viaje");
                    FCMBody fcmBody = new FCMBody(token, "high", data);

                    mNotificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                            if (response.body() != null){
                                if (response.body().getSuccess() == 1){
                                    Toast.makeText(RequestDriverActivity.this, "Notificacion enviada", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(RequestDriverActivity.this, "El envío de la notificacion falló", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(RequestDriverActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                            Log.d("Error", "Error " + t.getMessage());
                        }
                    });
                }else {
                    Toast.makeText(RequestDriverActivity.this, "No se pudo enviar la notificacion: Token de conductor no existe", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}