package com.example.motoserv.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.motoserv.R;
import com.example.motoserv.models.ClientBooking;
import com.example.motoserv.models.FCMBody;
import com.example.motoserv.models.FCMResponse;
import com.example.motoserv.providers.AuthProvider;
import com.example.motoserv.providers.ClientBookingProvider;
import com.example.motoserv.providers.GeofireProvider;
import com.example.motoserv.providers.GoogleApiProvider;
import com.example.motoserv.providers.NotificationProvider;
import com.example.motoserv.providers.TokenProvider;
import com.example.motoserv.utils.DecodePoints;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
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
    private ClientBookingProvider mClientBookingProvider;
    private AuthProvider mAuthProvider;
    private GoogleApiProvider mGoogleApiProvider;

    private String mExtraOrigin;
    private String mExtraDestination;
    private double mExtraOriginLat;
    private double mExtraOriginLng;
    private double mExtraDestinationLat;
    private double mExtraDestinationLng;
    private LatLng mOriginLatLng;
    private LatLng mDestinationLatLng;

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

        mExtraOrigin = getIntent().getStringExtra("origin");
        mExtraDestination = getIntent().getStringExtra("destination");
        mExtraOriginLat = getIntent().getDoubleExtra("originLat", 0);
        mExtraOriginLng = getIntent().getDoubleExtra("originLng", 0);
        mExtraDestinationLat = getIntent().getDoubleExtra("destinationLat", 0);
        mExtraDestinationLng = getIntent().getDoubleExtra("destinationLng", 0);
        mOriginLatLng = new LatLng(mExtraOriginLat, mExtraOriginLng);
        mDestinationLatLng = new LatLng(mExtraDestinationLat, mExtraDestinationLng);

        mGeofireProvider = new GeofireProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();
        mClientBookingProvider = new ClientBookingProvider();
        mAuthProvider = new AuthProvider();
        mGoogleApiProvider = new GoogleApiProvider();

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
                    createClientBooking();
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

    private void createClientBooking(){

        mGoogleApiProvider.getDirections(mOriginLatLng, mDriverFoundLatLng).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polylines = route.getJSONObject("overview_polyline");
                    String points = polylines.getString("points");
                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");
                    String distanceText = distance.getString("text");
                    String durationText = duration.getString("text");

                    sendNotification(durationText, distanceText);

                }catch (Exception e){
                    Log.d("error", "Error encontrado " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void sendNotification(String time, String distance){
        mTokenProvider.getToken(mIdDriverFound).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String token = Objects.requireNonNull(snapshot.child("token").getValue()).toString();
                    Map<String, String> data = new HashMap<>();
                    data.put("title","Nuevo viaje a " + time + "de tu posicion");
                    data.put("body","Tienes una nueva solicitud de viaje en " + distance);
                    FCMBody fcmBody = new FCMBody(token, "high", data);

                    mNotificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                            if (response.body() != null){
                                if (response.body().getSuccess() == 1){
                                    ClientBooking clientBooking = new ClientBooking(
                                            mAuthProvider.getId(),
                                            mIdDriverFound,
                                            mExtraDestination,
                                            mExtraOrigin,
                                            time,
                                            distance,
                                            "Create",
                                            mExtraOriginLat,
                                            mExtraOriginLng,
                                            mExtraDestinationLat,
                                            mExtraDestinationLng
                                    );

                                    mClientBookingProvider.create(clientBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(RequestDriverActivity.this, "Client booking successful", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    //Toast.makeText(RequestDriverActivity.this, "Notificacion enviada", Toast.LENGTH_SHORT).show();
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