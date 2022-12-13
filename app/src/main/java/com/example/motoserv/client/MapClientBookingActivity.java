package com.example.motoserv.client;

import static com.example.motoserv.BuildConfig.MAPS_API_KEY;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.motoserv.R;
import com.example.motoserv.providers.AuthProvider;
import com.example.motoserv.providers.ClientBookingProvider;
import com.example.motoserv.providers.DriverProvider;
import com.example.motoserv.providers.GeofireProvider;
import com.example.motoserv.providers.GoogleApiProvider;
import com.example.motoserv.providers.TokenProvider;
import com.example.motoserv.utils.DecodePoints;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapClientBookingActivity extends AppCompatActivity implements OnMapReadyCallback {

    SharedPreferences mPreferences;

    private GoogleMap mMap;

    private Marker mMarkerDriver;

    private PlacesClient mPlaces;
    private FusedLocationProviderClient mFusedLocation;

    private GeofireProvider mGeofireProvider;

    private AuthProvider mAuth;
    private ClientBookingProvider mClientBookingProvider;
    private TokenProvider mTokenProvider;
    private DriverProvider mDriverProvider;

    private GoogleApiProvider mGoogleApiProvider;

    private List<LatLng> mPolylineList;
    private PolylineOptions mPolylineOptions;
    private LatLng mOriginlatlng;
    private LatLng mDestinationLatlng;

    private LatLng mDriverLatLng;

    private ValueEventListener mEventListener;
    private ValueEventListener mListenerStatus;

    private boolean mIsFirstTime = true;

    private String mOrigin;
    private LatLng mOriginLocation;
    private String mDestination;
    private LatLng mDestinationLocation;

    private String mIdDriver;

    private TextView mTextViewDriverBooking;
    private TextView mTextViewOriginBooking;
    private TextView mTextViewDestinationBooking;
    private TextView mTextViewStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_client_booking);

        mAuth = new AuthProvider();

        mGeofireProvider = new GeofireProvider("drivers_working");
        mClientBookingProvider = new ClientBookingProvider();
        mTokenProvider = new TokenProvider();
        mGoogleApiProvider = new GoogleApiProvider();
        mDriverProvider = new DriverProvider();

        mTextViewDriverBooking = findViewById(R.id.text_view_name_driver_booking);
        mTextViewOriginBooking = findViewById(R.id.text_view_origin_driver_booking);
        mTextViewDestinationBooking = findViewById(R.id.text_view_destination_driver_booking);
        mTextViewStatus = findViewById(R.id.text_view_status);

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_client_booking);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(), MAPS_API_KEY);
        }

        getStatus();

        getClientBooking();
    }

    public void getStatus(){
        mListenerStatus = mClientBookingProvider.getStatus(mAuth.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String status = (String) snapshot.getValue();
                    assert status != null;
                    if (status.equals("accepted")){
                        mTextViewStatus.setText("Estado: aceptado");
                    }
                    if (status.equals("started")){
                        mTextViewStatus.setText("Estado: Viaje Iniciado");
                        startBooking();
                    }else if (status.equals("finished")){
                        mTextViewStatus.setText("Estado: Viaje Finalizado");
                        finishBooking();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void startBooking(){
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(mDestinationLatlng).title("DESTINO").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_blue)));

        drawRoute(mDestinationLatlng);
    }

    public void finishBooking(){
        Intent intent = new Intent(MapClientBookingActivity.this, PaymentClientActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEventListener != null){
            mGeofireProvider.getDriverLocation(mIdDriver).removeEventListener(mEventListener);
        }
        if (mListenerStatus != null){
            mClientBookingProvider.getStatus(mAuth.getId()).removeEventListener(mListenerStatus);
        }
    }

    private void getClientBooking(){
        mClientBookingProvider.getClientBooking(mAuth.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String destination = String.valueOf(snapshot.child("destination").getValue());
                    String origin = String.valueOf(snapshot.child("origin").getValue());
                    String idDriver = String.valueOf(snapshot.child("idDriver").getValue());
                    mIdDriver = idDriver;

                    double destinationLat = Double.parseDouble(snapshot.child("destinationLat").getValue().toString());
                    double destinationLng = Double.parseDouble(snapshot.child("destinationLng").getValue().toString());

                    double originLat = Double.parseDouble(snapshot.child("originLat").getValue().toString());
                    double originLng = Double.parseDouble(snapshot.child("originLng").getValue().toString());

                    mOriginlatlng = new LatLng(originLat, originLng);
                    mDestinationLatlng = new LatLng(destinationLat, destinationLng);

                    mTextViewOriginBooking.setText("Origen:" + origin);
                    mTextViewDestinationBooking.setText("Destino" + destination);

                    mMap.addMarker(new MarkerOptions().position(mOriginlatlng).title("AQUI").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red)));

                    //Obtener conductor para obtener su nbombre
                    getDriver(idDriver);

                    //Obtner posicion del conductor
                    getDriverLoaction(idDriver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getDriver(String idDriver){
        mDriverProvider.getDriver(idDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = (String) snapshot.child("name").getValue();
                    mTextViewDriverBooking.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getDriverLoaction(String idDriver){
        mEventListener = mGeofireProvider.getDriverLocation(idDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    double lat = Double.parseDouble(Objects.requireNonNull(snapshot.child("0").getValue()).toString());
                    double lng = Double.parseDouble(Objects.requireNonNull(snapshot.child("1").getValue()).toString());
                    
                    mDriverLatLng = new LatLng(lat, lng);

                    if (mMarkerDriver != null){
                        mMarkerDriver.remove();
                    }

                    mMarkerDriver = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                                        .title("Conductor")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_driver)));

                    if (mIsFirstTime){
                        mIsFirstTime = false;
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(mDriverLatLng)
                                        .zoom(14f)
                                        .build()
                        ));
                        //dibujar la ruta del conductor para el cliente
                        drawRoute(mOriginlatlng);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void drawRoute(LatLng latLng){
        mGoogleApiProvider.getDirections(mDriverLatLng, latLng).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polylines = route.getJSONObject("overview_polyline");
                    String points = polylines.getString("points");

                    //mPolylineList = PolyUtil.decode(points);
                    mPolylineList = DecodePoints.decodePoly(points);
                    mPolylineOptions = new PolylineOptions();
                    mPolylineOptions.color(Color.DKGRAY);
                    mPolylineOptions.width(8f);
                    mPolylineOptions.startCap(new SquareCap());
                    mPolylineOptions.jointType(JointType.ROUND);
                    mPolylineOptions.addAll(mPolylineList);
                    mMap.addPolyline(mPolylineOptions);

                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");
                    String distanceText = distance.getString("text");
                    String durationText = duration.getString("text");

                }catch (Exception e){
                    Log.d("error", "Error encontrado " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
}