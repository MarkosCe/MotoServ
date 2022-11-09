package com.example.motoserv.retrofit;

import com.example.motoserv.models.FCMBody;
import com.example.motoserv.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    //POST https://fcm.googleapis.com/fcm/send

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA9rov1Hc:APA91bH08QFav3JC1CyKfrx_wzxlEddRF64Aq1Pxlr-xAOODtG9G3ADEw4ryXgFmSFlm74RYL31_W5vfi_vavsuH0dmVHCvahl0l_hRlDiM8rvMGeeBBDn-yWFCGIueU8avW1bT-ikhD"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
