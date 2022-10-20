package com.example.motoserv.providers;

import com.google.firebase.auth.FirebaseAuth;

public class AuthProvider {

    FirebaseAuth mAuth;

    public AuthProvider(){
        mAuth = FirebaseAuth.getInstance();
    }

    public String getId(){
        return mAuth.getCurrentUser().getUid();
    }
}
