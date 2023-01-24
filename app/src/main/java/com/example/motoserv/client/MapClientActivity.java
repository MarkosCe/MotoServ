package com.example.motoserv.client;

import static com.example.motoserv.BuildConfig.MAPS_API_KEY;

import androidx.activity.OnBackPressedCallback;
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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.example.motoserv.LoginActivity;
import com.example.motoserv.MyToolbar;
import com.example.motoserv.R;
import com.example.motoserv.driver.MapDriverActivity;
import com.example.motoserv.providers.AuthProvider;
import com.example.motoserv.providers.GeofireProvider;
import com.example.motoserv.providers.TokenProvider;
import com.facebook.login.LoginManager;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapClientActivity extends AppCompatActivity implements OnMapReadyCallback {

    SharedPreferences mPreferences;

    private GoogleMap mMap;

    private Marker mMarker;

    FusedLocationProviderClient mFusedLocation;
    LocationRequest mLocationRequest;
    //LocationCallback mLocationCallback;

    private GeofireProvider mGeofireProvider;
    private LatLng mCurrentLocation;

    private AuthProvider mAuth;
    private TokenProvider mTokenProvider;

    private List<Marker> mDriversMarkers = new ArrayList<>();
    private boolean mIsFirstTime = true;

    private PlacesClient mPlaces;
    private AutocompleteSupportFragment mAutocomplete;
    private AutocompleteSupportFragment mAutocompleteDestination;

    private String mOrigin;
    private LatLng mOriginLocation;
    private String mDestination;
    private LatLng mDestinationLocation;

    private GoogleMap.OnCameraIdleListener mCameraListener;

    Button mButtonRequestDriver;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null){

                    mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    //localizacion en tiempo real
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(16f)
                                    .build()
                    ));
                    if (mIsFirstTime){
                        mIsFirstTime = false;
                        getActiveDrivers();
                        limitSearch();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_client);

        MyToolbar.show(this, "Client Map", false);

        mAuth = new AuthProvider();

        mGeofireProvider = new GeofireProvider("active_drivers");

        mTokenProvider = new TokenProvider();

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                moveTaskToBack(true);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_client);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        mButtonRequestDriver = findViewById(R.id.btn_request_driver);
        mButtonRequestDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestDriver();
            }
        });

        if (!Places.isInitialized()){
           Places.initialize(getApplicationContext(), MAPS_API_KEY);
        }
        mPlaces = Places.createClient(this);
        instanceAutocompleteOrigin();
        instanceAutocompleteDestination();
        onCameraMove();

        //generate token
        generateToken();

        // Get the Intent that started this activity and extract the string
        mPreferences = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE);
    }

    private void requestDriver(){
        if (mOriginLocation != null && mDestinationLocation != null){
            Intent intent = new Intent(MapClientActivity.this, DetailRequestActivity.class);
            intent.putExtra("origin_lat", mOriginLocation.latitude);
            intent.putExtra("origin_lng", mOriginLocation.longitude);
            intent.putExtra("destination_lat", mDestinationLocation.latitude);
            intent.putExtra("destination_lng", mDestinationLocation.longitude);
            intent.putExtra("origin", mOrigin);
            intent.putExtra("destination", mDestination);
            startActivity(intent);
        }else {
            Toast.makeText(this, "Selecciona tu origen y tu destino", Toast.LENGTH_SHORT).show();
        }
    }

    private void instanceAutocompleteOrigin(){
        mAutocomplete = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.places_autocomplete_origin);
        mAutocomplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutocomplete.setHint("Origen");
        mAutocomplete.setText("");
        mAutocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {

            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mOrigin = place.getName();
                mOriginLocation = place.getLatLng();
                Log.d("PLACE", "Name" + mOrigin);
                Log.d("PLACE", "Lat" + mOriginLocation.latitude);
                Log.d("PLACE", "Lng" + mOriginLocation.longitude);
            }
        });
    }

    private void instanceAutocompleteDestination(){
        mAutocompleteDestination = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.places_autocomplete_destination);
        mAutocompleteDestination.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutocompleteDestination.setHint("Destino");
        mAutocompleteDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {

            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mDestination = place.getName();
                mDestinationLocation = place.getLatLng();
                Log.d("PLACE", "Name" + mDestination);
                Log.d("PLACE", "Lat" + mDestinationLocation.latitude);
                Log.d("PLACE", "Lng" + mDestinationLocation.longitude);
            }
        });
    }

    private void limitSearch(){
        LatLng northside = SphericalUtil.computeOffset(mCurrentLocation, 1000, 0);
        LatLng southside = SphericalUtil.computeOffset(mCurrentLocation, 1000, 180);
        mAutocomplete.setCountry("MEX");
        mAutocomplete.setLocationBias(RectangularBounds.newInstance(southside, northside));
        mAutocompleteDestination.setCountry("MEX");
        mAutocompleteDestination.setLocationBias(RectangularBounds.newInstance(southside, northside));
    }

    private void onCameraMove(){
        mCameraListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                try {
                    Geocoder geocoder = new Geocoder(MapClientActivity.this);
                    mOriginLocation = mMap.getCameraPosition().target;
                    List<Address> addressList = geocoder.getFromLocation(mOriginLocation.latitude, mOriginLocation.longitude, 1);
                    String city = addressList.get(0).getLocality();
                    String country = addressList.get(0).getCountryName();
                    String address = addressList.get(0).getAddressLine(0);
                    mOrigin = address + " " + city;
                    mAutocomplete.setText(address + " " + city);
                }catch (Exception e){
                    Log.d("Error", "Mensaje de error" + e.getMessage());
                }
            }
        };
    }

    private void getActiveDrivers(){
        mGeofireProvider.getActiveDrivers(mCurrentLocation, 1).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                // Se añaden los marcadores de los conductores que se van conectando
                for (Marker marker: mDriversMarkers){
                    if (marker.getTag() != null){
                        if (marker.getTag().equals(key)){
                            return;
                        }
                    }
                }

                LatLng driverLocation = new LatLng(location.latitude, location.longitude);
                Marker marker = mMap.addMarker(new MarkerOptions().position(driverLocation).title("Disponible").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_driver)));
                marker.setTag(key);
                mDriversMarkers.add(marker);

            }

            @Override
            public void onKeyExited(String key) {
                //Cuando un conductor se desconecta
                for (Marker marker: mDriversMarkers){
                    if (marker.getTag() != null){
                        if (marker.getTag().equals(key)){
                            marker.remove();
                            mDriversMarkers.remove(marker);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                //Actualizar la posicion de cada conductor
                for (Marker marker: mDriversMarkers){
                    if (marker.getTag() != null){
                        if (marker.getTag().equals(key)){
                            marker.setPosition(new LatLng(location.latitude, location.longitude));
                        }
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    //@SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnCameraIdleListener(mCameraListener);

        /*LocationRequest.Builder builder = new LocationRequest.Builder(10000)
                .setIntervalMillis(10000)
                .setMinUpdateIntervalMillis(5000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateDistanceMeters(5);*/

        /*LocationRequest.Builder builder= new LocationRequest.Builder(mLocationRequest);
        builder.build();*/
        mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY)
                .setIntervalMillis(10000)
                .setMinUpdateIntervalMillis(5000)
                .setMinUpdateDistanceMeters(5)
                .build();

        /*mLocationRequest = LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(5);*/

        Toast.makeText(this, "0nmapready", Toast.LENGTH_SHORT).show();
        startLocation();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    if (gpsActive()){
                        Toast.makeText(this, "gpsmove", Toast.LENGTH_SHORT).show();
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
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
            mMap.setMyLocationEnabled(true);
        }else if (requestCode == SETTINGS_REQUEST_CODE && !gpsActive()){
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

    @SuppressLint("MissingPermission")
    private void startLocation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                if (gpsActive()){
                    Toast.makeText(this, "startlocation", Toast.LENGTH_SHORT).show();
                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    mMap.setMyLocationEnabled(true);
                    Toast.makeText(this, "endstartlocation", Toast.LENGTH_SHORT).show();
                }else {
                    showAlertDialog();
                }
            }else {
                checkLocationPermisions();
            }
        }else {
            if (gpsActive()){
                Toast.makeText(this, "startl0ca", Toast.LENGTH_SHORT).show();
                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
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
                                ActivityCompat.requestPermissions(MapClientActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            }else {
                ActivityCompat.requestPermissions(MapClientActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.client_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_logout){
            logOut();
        }

        return super.onOptionsItemSelected(item);
    }

    void logOut(){
        String provider= mPreferences.getString("provider", null);
        if (provider != null) {
            Toast.makeText(MapClientActivity.this, "No es nulo", Toast.LENGTH_SHORT).show();
            if (provider.equals("FACEBOOK")) {
                LoginManager.getInstance().logOut();
            }
        }
        FirebaseAuth.getInstance().signOut();
        if (mPreferences.edit().clear().commit()) {
            Toast.makeText(this, "Borrado", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MapClientActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void generateToken(){
        mTokenProvider.create(mAuth.getId());
    }
}