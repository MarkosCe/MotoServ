package com.example.motoserv.driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.motoserv.MyToolbar;
import com.example.motoserv.R;
import com.example.motoserv.adapters.RideHistoryAdapter;
import com.example.motoserv.models.RideHistory;
import com.example.motoserv.providers.AuthProvider;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RideHistoryDriverActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RideHistoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history_driver);

        MyToolbar.show(this, "Historial de viajes", true);

        mRecyclerView = findViewById(R.id.recycler_view_history_driver);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        mAdapter.stopListening();
    }
}