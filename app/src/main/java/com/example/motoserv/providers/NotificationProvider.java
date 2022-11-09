package com.example.motoserv.providers;

import com.example.motoserv.models.FCMBody;
import com.example.motoserv.models.FCMResponse;
import com.example.motoserv.retrofit.IFCMApi;
import com.example.motoserv.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    public NotificationProvider(){

    }

    public Call<FCMResponse> sendNotification(FCMBody body){
        String url = "https://fcm.googleapis.com";
        return RetrofitClient.getClientObject(url).create(IFCMApi.class).send(body);
    }

}
