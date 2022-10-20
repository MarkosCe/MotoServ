package com.example.motoserv.providers;

import com.example.motoserv.models.Driver;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverProvider {

    DatabaseReference db;

    public DriverProvider(){
        db = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
    }

    public Task<Void> create(Driver user){
        return db.child(user.getId()).setValue(user);
    }
}
