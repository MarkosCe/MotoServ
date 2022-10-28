package com.example.motoserv.providers;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeofireProvider {

    private DatabaseReference db;
    private GeoFire geoFire;

    public GeofireProvider(){
        db = FirebaseDatabase.getInstance().getReference().child("active_drivers");
        geoFire = new GeoFire(db);
    }

    public void saveLocation(String idDriver, LatLng latLng){
        geoFire.setLocation(idDriver, new GeoLocation(latLng.latitude, latLng.longitude));
    }

    public void removeLocation(String idDriver){
        geoFire.removeLocation(idDriver);
    }

    public GeoQuery getActiveDrivers(LatLng latLng){
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 1);
        geoQuery.removeAllListeners();
        return geoQuery;
    }
}