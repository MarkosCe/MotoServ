package com.example.motoserv.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.motoserv.MyToolbar;
import com.example.motoserv.R;
import com.example.motoserv.providers.GoogleApiProvider;
import com.example.motoserv.utils.DecodePoints;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailRequestActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private double mExtraOriginLat;
    private double mExtraOriginLng;
    private double mExtraDestinationLat;
    private double mExtraDestinationLng;
    private String mExtraOrigin;
    private String mExtraDestination;

    private LatLng mOriginlatlng;
    private LatLng mDestinationLatlng;

    TextView mTextViewOrigin;
    TextView mTextViewDestination;
    TextView mTextViewTime;
    TextView mTextViewDistance;

    Button mButtonRequest;

    private GoogleApiProvider mGoogleApiProvider;

    private List<LatLng> mPolylineList;
    private PolylineOptions mPolylineOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_request);

        MyToolbar.show(this, "Tus datos", true);

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_client_request);
        mapFragment.getMapAsync(this);

        mExtraOriginLat = getIntent().getDoubleExtra("origin_lat", 0);
        mExtraOriginLng = getIntent().getDoubleExtra("origin_lng", 0);
        mExtraDestinationLat = getIntent().getDoubleExtra("destination_lat", 0);
        mExtraDestinationLng = getIntent().getDoubleExtra("destination_lng", 0);
        mExtraOrigin = getIntent().getStringExtra("origin");
        mExtraDestination = getIntent().getStringExtra("destination");

        mOriginlatlng = new LatLng(mExtraOriginLat, mExtraOriginLng);
        mDestinationLatlng = new LatLng(mExtraDestinationLat, mExtraDestinationLng);

        mTextViewOrigin = findViewById(R.id.text_view_origin);
        mTextViewDestination = findViewById(R.id.text_view_destination);
        mTextViewTime = findViewById(R.id.text_view_time);
        mTextViewDistance = findViewById(R.id.text_view_distance);
        mButtonRequest = findViewById(R.id.btn_request_now);

        mTextViewOrigin.setText(mExtraOrigin);
        mTextViewDestination.setText(mExtraDestination);

        mButtonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRequestDriver();
            }
        });

        mGoogleApiProvider = new GoogleApiProvider();
    }

    private void goToRequestDriver(){
        Intent intent = new Intent(DetailRequestActivity.this, RequestDriverActivity.class);
        intent.putExtra("originLat", mOriginlatlng.latitude);
        intent.putExtra("originLng", mOriginlatlng.longitude);
        intent.putExtra("origin", mExtraOrigin);
        intent.putExtra("destination", mExtraDestination);
        intent.putExtra("destinationLat", mDestinationLatlng.latitude);
        intent.putExtra("destinationLng", mDestinationLatlng.longitude);
        startActivity(intent);
        finish();
    }

    private void drawRoute(){
        mGoogleApiProvider.getDirections(mOriginlatlng, mDestinationLatlng).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polylines = route.getJSONObject("overview_polyline");
                    String points = polylines.getString("points");

                    /*JSONObject jsonObject = new JSONObject(response.body());
                    JSONObject job = jsonObject.getJSONObject("rates");
                    JSONObject jsonObject1 = job.getJSONObject("USD");
                    String amount = jsonObject1.getString("rate_for_amount");*/

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

                    mTextViewTime.setText(durationText);
                    mTextViewDistance.setText(distanceText);

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

        mMap.addMarker(new MarkerOptions().position(mOriginlatlng).title("Origen").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_red)));
        mMap.addMarker(new MarkerOptions().position(mDestinationLatlng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_blue)));

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(mOriginlatlng)
                        .zoom(14f)
                        .build()
        ));

        drawRoute();
    }
}