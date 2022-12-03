package com.example.motoserv.providers;

import com.example.motoserv.models.ClientBooking;
import com.example.motoserv.models.RideHistory;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RideHistoryProvider {

    private DatabaseReference mDatabase;

    public RideHistoryProvider(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("RideHistory");
    }

    public Task<Void> create(RideHistory rideHistory){
        return mDatabase.child(rideHistory.getIdRideHistory()).setValue(rideHistory);
    }

    public Task<Void> updateRateValueClient(String idRideHistory, float rateValue){
        Map<String, Object> map = new HashMap<>();
        map.put("rateClient", rateValue);
        return mDatabase.child(idRideHistory).updateChildren(map);
    }

    public Task<Void> updateRateValueDriver(String idRideHistory, float rateValue){
        Map<String, Object> map = new HashMap<>();
        map.put("rateDriver", rateValue);
        return mDatabase.child(idRideHistory).updateChildren(map);
    }

    public DatabaseReference getRideHistory(String idHistory){
        return mDatabase.child(idHistory);
    }
}
