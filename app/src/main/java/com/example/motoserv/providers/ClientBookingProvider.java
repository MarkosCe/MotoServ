package com.example.motoserv.providers;

import com.example.motoserv.models.ClientBooking;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ClientBookingProvider {

    private DatabaseReference mDatabase;

    public ClientBookingProvider(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ClientBooking");
    }

    public Task<Void> create(ClientBooking clientBooking){
        return mDatabase.child(clientBooking.getIdClient()).setValue(clientBooking);
    }

    public Task<Void> updateStatus(String idClientBooking, String status){
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        return mDatabase.child(idClientBooking).updateChildren(map);
    }

    public Task<Void> updateIdRideHistory(String idClientBooking){
        String id = mDatabase.push().getKey();  //genera un id unico en la base de datos
        Map<String, Object> map = new HashMap<>();
        map.put("idRideHistory", id);
        return mDatabase.child(idClientBooking).updateChildren(map);
    }

    public DatabaseReference getStatus(String idClientBooking) {
        return mDatabase.child(idClientBooking).child("status");
    }

    public DatabaseReference getClientBooking(String idClientBooking){
        return mDatabase.child(idClientBooking);
    }

    public Task<Void> delete(String idClient){
        return mDatabase.child(idClient).removeValue();
    }
}
