package com.example.motoserv.providers;

import com.example.motoserv.models.Client;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ClientProvider {

    DatabaseReference db;

    public ClientProvider(){
        db = FirebaseDatabase.getInstance().getReference().child("Users").child("Clients");
    }

    public Task<Void> create(Client user){
        return db.child(user.getId()).setValue(user);
    }
}
