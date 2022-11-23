package com.example.motoserv.client;

import static com.example.motoserv.BuildConfig.MAPS_API_KEY;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.example.motoserv.R;
import com.example.motoserv.providers.AuthProvider;
import com.example.motoserv.providers.GeofireProvider;
import com.example.motoserv.providers.TokenProvider;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;

import java.util.ArrayList;
import java.util.List;

public class MapClientBookingActivity extends AppCompatActivity implements OnMapReadyCallback {

    SharedPreferences mPreferences;

    private GoogleMap mMap;

    private Marker mMarker;

    private PlacesClient mPlaces;
    private FusedLocationProviderClient mFusedLocation;

    private GeofireProvider mGeofireProvider;
    private LatLng mCurrentLocation;

    private AuthProvider mAuth;
    private TokenProvider mTokenProvider;

    private List<Marker> mDriversMarkers = new ArrayList<>();
    private boolean mIsFirstTime = true;

    private String mOrigin;
    private LatLng mOriginLocation;
    private String mDestination;
    private LatLng mDestinationLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_client_booking);

        mAuth = new AuthProvider();

        mGeofireProvider = new GeofireProvider("drivers_working");

        mTokenProvider = new TokenProvider();

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_client_booking);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(), MAPS_API_KEY);
        }
        mPlaces = Places.createClient(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
}