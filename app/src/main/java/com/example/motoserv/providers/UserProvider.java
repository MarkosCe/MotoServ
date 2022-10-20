package com.example.motoserv.providers;

import com.example.motoserv.models.Client;
import com.example.motoserv.models.Driver;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UserProvider {

    DatabaseReference db;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public UserProvider(){
        db = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public Task<Void> create(Driver user, String typeAcc){
        return db.child(typeAcc).child(user.getId()).setValue(user);
    }

    public Task<Void> create(Client user, String typeAcc){
        return db.child(typeAcc).child(user.getId()).setValue(user);
    }

    public Task<Void> update(Driver user){
        Map<String, Object> map = new HashMap<>();
        map.put("name", user.getName());
        map.put("gender", user.getGender());
        return db.child(user.getId()).updateChildren(map);
    }

}
