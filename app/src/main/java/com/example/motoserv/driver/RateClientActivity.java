package com.example.motoserv.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motoserv.R;
import com.example.motoserv.models.ClientBooking;
import com.example.motoserv.models.RideHistory;
import com.example.motoserv.providers.ClientBookingProvider;
import com.example.motoserv.providers.RideHistoryProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class RateClientActivity extends AppCompatActivity {

    private TextView mTextViewOrigin;
    private TextView mTextViewDestination;

    private RatingBar mRatingBar;

    private Button mButtonRate;

    private RideHistory mRideHsitory;
    private RideHistoryProvider mRideHistoryProvider;
    private ClientBookingProvider mClientBookingProvider;

    private String mExtraClientId;

    private float mRateValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_client);

        mTextViewOrigin = findViewById(R.id.text_view_rate_origen_driver);
        mTextViewDestination = findViewById(R.id.text_view_rate_destination_driver);
        mRatingBar = findViewById(R.id.rating_bar_client);
        mButtonRate = findViewById(R.id.btn_rate_client);

        mExtraClientId = getIntent().getStringExtra("idClient");
        mClientBookingProvider = new ClientBookingProvider();
        mRideHistoryProvider = new RideHistoryProvider();

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float ratingValue, boolean b) {
                mRateValue = ratingValue;
            }
        });

        mButtonRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rate();
            }
        });

        getClientBooking();
    }

    private void getClientBooking(){
        mClientBookingProvider.getClientBooking(mExtraClientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    ClientBooking clientBooking = snapshot.getValue(ClientBooking.class);
                    assert clientBooking != null;
                    mTextViewOrigin.setText(clientBooking.getOrigin());
                    mTextViewDestination.setText(clientBooking.getDestination());

                    mRideHsitory = new RideHistory(
                            clientBooking.getIdRideHistory(),
                            clientBooking.getIdClient(),
                            clientBooking.getIdDriver(),
                            clientBooking.getDestination(),
                            clientBooking.getOrigin(),
                            clientBooking.getTime(),
                            clientBooking.getKm(),
                            clientBooking.getStatus(),
                            clientBooking.getOriginLat(),
                            clientBooking.getOriginLng(),
                            clientBooking.getDestinationLat(),
                            clientBooking.getDestinationLng()
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void rate(){
        if (mRateValue > 0){
            mRideHsitory.setRateClient(mRateValue);
            mRideHsitory.setTimestamp(new Date().getTime());
            mRideHistoryProvider.getRideHistory(mRideHsitory.getIdRideHistory()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        mRideHistoryProvider.updateRateValueClient(mRideHsitory.getIdRideHistory(), mRateValue);
                    }else {
                        mRideHistoryProvider.create(mRideHsitory).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(RateClientActivity.this, "Historial guardado", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RateClientActivity.this, MapDriverActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            Toast.makeText(this, "Califica al pasajero", Toast.LENGTH_SHORT).show();
        }
    }
}