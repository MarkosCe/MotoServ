package com.example.motoserv.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.motoserv.MyToolbar;
import com.example.motoserv.R;
import com.example.motoserv.adapters.RideHistoryAdapter;
import com.example.motoserv.models.RideHistory;
import com.example.motoserv.providers.AuthProvider;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RideHistoryDriverActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RideHistoryAdapter mAdapter;

    ImageView mImageView;

    private boolean hasListening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history_driver);

        MyToolbar.show(this, "Historial de viajes", true);

        mImageView = findViewById(R.id.img_not_found);
        mRecyclerView = findViewById(R.id.recycler_view_history_driver);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    /*
    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
        AuthProvider mAuthProvider = new AuthProvider();
        Query query = FirebaseDatabase.getInstance().getReference()
                                    .child("RideHistory").orderByChild("idDriver")
                                    .equalTo(mAuthProvider.getId());

        Task<DataSnapshot> snapshotTask = query.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Toast.makeText(RideHistoryDriverActivity.this, "exist", Toast.LENGTH_SHORT).show();
                    mImageView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    FirebaseRecyclerOptions<RideHistory> options = new FirebaseRecyclerOptions.Builder<RideHistory>()
                            .setQuery(query, RideHistory.class).build();
                    mAdapter = new RideHistoryAdapter(options);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.startListening();
                    hasListening = true;
                }else {
                    Toast.makeText(RideHistoryDriverActivity.this, "Not found", Toast.LENGTH_SHORT).show();
                    mRecyclerView.setVisibility(View.GONE);
                    mImageView.setVisibility(View.VISIBLE);
                }
            }
        });
    } */

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
        AuthProvider mAuthProvider = new AuthProvider();
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("RideHistory").orderByChild("idDriver")
                .equalTo(mAuthProvider.getId());

        FirebaseRecyclerOptions<RideHistory> options = new FirebaseRecyclerOptions.Builder<RideHistory>()
                .setQuery(query, RideHistory.class).build();
        mAdapter = new RideHistoryAdapter(options);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (hasListening)
            mAdapter.stopListening();
    }
}