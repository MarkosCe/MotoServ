package com.example.motoserv.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.motoserv.providers.ClientBookingProvider;

public class CancelReceiver extends BroadcastReceiver {

    private ClientBookingProvider clientBookingProvider;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Se ejecuta cuando se presiona la accion de cancelar en la notificacion

        String idClient = intent.getStringExtra("idClient");
        clientBookingProvider = new ClientBookingProvider();
        clientBookingProvider.updateStatus(idClient, "cancelled");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);
    }
}
