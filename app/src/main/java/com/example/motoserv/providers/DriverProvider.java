package com.example.motoserv.providers;

import com.example.motoserv.models.Driver;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DriverProvider {

    DatabaseReference db;

    public DriverProvider(){
        db = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
    }

    public Task<Void> create(Driver user){
        return db.child(user.getId()).setValue(user);
    }

    public Task<Void> updateImageProfile(String id, String uri){
        Map<String, Object> map = new HashMap<>();
        map.put("image", uri);
        return db.child(id).updateChildren(map);
    }

    public Task<Void> updateUsername(String id, String name){
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        return db.child(id).updateChildren(map);
    }

    public Task<Void> updateBrandVehicle(String id, String brand){
        Map<String, Object> map = new HashMap<>();
        map.put("brand_vehicle", brand);
        return db.child(id).updateChildren(map);
    }

    public Task<Void> updatePlateVehicle(String id, String plate){
        Map<String, Object> map = new HashMap<>();
        map.put("plate_vehicle", plate);
        return db.child(id).updateChildren(map);
    }

    public DatabaseReference getDriver(String idDriver){
        return db.child(idDriver);
    }

}
