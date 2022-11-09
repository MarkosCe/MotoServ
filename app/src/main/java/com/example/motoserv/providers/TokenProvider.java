package com.example.motoserv.providers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.motoserv.models.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class TokenProvider {

    DatabaseReference mDatabase;

    public TokenProvider(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("tokens");
    }

    public void create(String idUser){
        if (idUser == null)
            return;
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.d("Fetching FCM registration token failed", task.getException().toString());
                    return;
                }

                // Get new FCM registration token
                Token token = new Token(task.getResult());

                mDatabase.child(idUser).setValue(token);
            }
        });
    }

    public DatabaseReference getToken(String idUser){
        return mDatabase.child(idUser);
    }
}
