package com.example.motoserv.driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.motoserv.LoginActivity;
import com.example.motoserv.MyToolbar;
import com.example.motoserv.R;
import com.example.motoserv.client.MapClientActivity;
import com.example.motoserv.providers.AuthProvider;
import com.example.motoserv.providers.GeofireProvider;
import com.example.motoserv.providers.TokenProvider;
import com.facebook.login.LoginManager;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MapDriverActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Button mButtonConnect;
    private boolean isConnect = false;

    private FloatingActionButton mFloatingButton;
    private FloatingActionButton mFloatingButtonNotify;

    private GoogleMap mMap;

    private Marker mMarker;

    private FusedLocationProviderClient mFusedLocation;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private LatLng mCurrentLocation;
    private GeofireProvider mGeofireProvider;
    private AuthProvider mAuthProvider;
    private TokenProvider mTokenProvider;

    private ValueEventListener mEventListener;

    private boolean isCameraMove = false;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_driver);

        MyToolbar.show(this, "Mapa", false);

        mGeofireProvider = new GeofireProvider("active_drivers");
        mAuthProvider = new AuthProvider();
        mTokenProvider = new TokenProvider();

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_driver);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        mFloatingButton = findViewById(R.id.floating_home);
        mFloatingButtonNotify = findViewById(R.id.floating_notifications);
        mButtonConnect = findViewById(R.id.btn_connect_driver);

        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHome();
            }
        });

        mFloatingButtonNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNotifications();
            }
        });

        mButtonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnect){
                    disconnect();
                }else{
                    startLocation();
                }
            }
        });

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (getApplicationContext() != null){

                        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                        if (mMarker != null){
                            mMarker.remove();
                        }
                        mMarker = mMap.addMarker(new MarkerOptions().position(
                                        new LatLng(location.getLatitude(), location.getLongitude())
                                ).title("Tu estás aquí")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_driver)));
                        //localizacion en tiempo real
                        if (isCameraMove){
                            isCameraMove = false;
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                                    new CameraPosition.Builder()
                                            .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                            .zoom(16f)
                                            .build()
                            ));
                        }
                        updateLocation();
                    }
                }
            }
        };

        //generate token
        generateToken();

        //verificar si el conductor esta trabajando
        isDriverWorking();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEventListener != null){
            mGeofireProvider.isDriverWorking(mAuthProvider.getId()).removeEventListener(mEventListener);
        }
    }

    private void showNotifications(){
        //
    }

    private void goToHome(){
        Intent intent = new Intent(MapDriverActivity.this, HomeDriverActivity.class);
        intent.putExtra("map", true);
        startActivity(intent);
    }

    private void isDriverWorking(){
        mEventListener = mGeofireProvider.isDriverWorking(mAuthProvider.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    disconnect();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateLocation(){
        if (mAuthProvider.existSession() && mCurrentLocation != null) {
            mGeofireProvider.saveLocation(mAuthProvider.getId(), mCurrentLocation);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        /*LocationRequest.Builder builder = new LocationRequest.Builder(10000)
                .setIntervalMillis(10000)
                .setMinUpdateIntervalMillis(5000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateDistanceMeters(5);*/

        /*LocationRequest.Builder builder= new LocationRequest.Builder(mLocationRequest);
        builder.build();*/


        mLocationRequest = LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(5);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    if (gpsActive()){
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    }else {
                        showAlertDialog();
                    }
                }else {
                    checkLocationPermisions();
                }
            }else {
                checkLocationPermisions();
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActive()){
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }else {
            showAlertDialog();
        }
    }

    private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Activa el GPS para continuar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }

    private boolean gpsActive(){
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isActive = true;
        }
        return isActive;
    }

    private void disconnect(){
        if (mFusedLocation != null){
            mButtonConnect.setText("Conectar");
            isConnect = false;
            mFusedLocation.removeLocationUpdates(mLocationCallback);
            if (mAuthProvider.existSession()){
                mGeofireProvider.removeLocation(mAuthProvider.getId());
            }
        }else {
            Toast.makeText(this, "No te puedes desconectar", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                if (gpsActive()){
                    mButtonConnect.setText("Desconectar");
                    isConnect = true;
                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                }else {
                    showAlertDialog();
                }
            }else {
                checkLocationPermisions();
            }
        }else {
            if (gpsActive()){
                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            }else {
                showAlertDialog();
            }
        }
    }

    private void checkLocationPermisions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this)
                        .setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicacion requiere de los permisos para poder utilizarse")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapDriverActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            }else {
                ActivityCompat.requestPermissions(MapDriverActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    private void generateToken(){
        mTokenProvider.create(mAuthProvider.getId());
    }
}