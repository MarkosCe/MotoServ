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

    public boolean existSession(){
        boolean exist = false;
        if (mAuth.getCurrentUser() != null){
            exist = true;
        }
        return exist;
    }
}
