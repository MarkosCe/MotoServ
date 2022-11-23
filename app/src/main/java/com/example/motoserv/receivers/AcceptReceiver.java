package com.example.motoserv.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.motoserv.driver.MapDriverBookingActivity;
import com.example.motoserv.providers.AuthProvider;
import com.example.motoserv.providers.ClientBookingProvider;
import com.example.motoserv.providers.GeofireProvider;

public class AcceptReceiver extends BroadcastReceiver {

    private ClientBookingProvider clientBookingProvider;
    private GeofireProvider geofireProvider;
    private AuthProvider authProvider;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Se ejecuta cuando se presiona la accion de aceptar en la notificacion
        geofireProvider = new GeofireProvider("active_drivers");
        geofireProvider.removeLocation(authProvider.getId());

        String idClient = intent.getStringExtra("idClient");
        clientBookingProvider = new ClientBookingProvider();
        clientBookingProvider.updateStatus(idClient, "accepted");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);

        Intent intent1 = new Intent(context, MapDriverBookingActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setAction(Intent.ACTION_RUN);
        context.startActivity(intent1);
    }
}
