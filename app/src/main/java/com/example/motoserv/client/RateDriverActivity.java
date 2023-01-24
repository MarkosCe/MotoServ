package com.example.motoserv.client;

import androidx.activity.OnBackPressedCallback;
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
import com.example.motoserv.driver.MapDriverActivity;
import com.example.motoserv.driver.RateClientActivity;
import com.example.motoserv.models.ClientBooking;
import com.example.motoserv.models.RideHistory;
import com.example.motoserv.providers.AuthProvider;
import com.example.motoserv.providers.ClientBookingProvider;
import com.example.motoserv.providers.RideHistoryProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class RateDriverActivity extends AppCompatActivity {

    private TextView mTextViewOrigin;
    private TextView mTextViewDestination;

    private RatingBar mRatingBar;

    private Button mButtonRate;

    private RideHistory mRideHsitory;
    private RideHistoryProvider mRideHistoryProvider;
    private ClientBookingProvider mClientBookingProvider;
    private AuthProvider mAuthProvider;

    private float mRateValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_driver);

        mTextViewOrigin = findViewById(R.id.text_view_rate_origin);
        mTextViewDestination = findViewById(R.id.text_view_rate_destination);
        mRatingBar = findViewById(R.id.rating_bar_driver);
        mButtonRate = findViewById(R.id.btn_rate_driver);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                moveTaskToBack(true);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        mClientBookingProvider = new ClientBookingProvider();
        mRideHistoryProvider = new RideHistoryProvider();
        mAuthProvider = new AuthProvider();

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
        mClientBookingProvider.getClientBooking(mAuthProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
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
            mRideHsitory.setRateDriver(mRateValue);
            mRideHsitory.setTimestamp(new Date().getTime());
            mRideHistoryProvider.getRideHistory(mRideHsitory.getIdRideHistory()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        mRideHistoryProvider.updateRateValueDriver(mRideHsitory.getIdRideHistory(), mRateValue).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(RateDriverActivity.this, "Historial actualizado", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RateDriverActivity.this, MapClientActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }else {
                        mRideHistoryProvider.create(mRideHsitory).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(RateDriverActivity.this, "Historial creado", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RateDriverActivity.this, MapClientActivity.class);
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