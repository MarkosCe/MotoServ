package com.example.motoserv.providers;

import static com.example.motoserv.BuildConfig.MAPS_API_KEY;

import com.example.motoserv.retrofit.IGoogleApi;
import com.example.motoserv.retrofit.RetrofitClient;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;

public class GoogleApiProvider {

    public GoogleApiProvider(){

    }

    public Call<String> getDirections(LatLng originLatLng, LatLng destinationLatLng){
        String baseUrl = "https://maps.googleapis.com";
        String query = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&" +
                "origin=" + originLatLng.latitude + "," + originLatLng.longitude + "&" +
                "destination=" + destinationLatLng.latitude + "," + destinationLatLng.longitude + "&" +
                "key=" + MAPS_API_KEY;

        return RetrofitClient.getClient(baseUrl).create(IGoogleApi.class).getDirections(baseUrl+query);
    }

}
