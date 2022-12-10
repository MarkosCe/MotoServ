package com.example.motoserv.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.motoserv.R;
import com.example.motoserv.channel.NotificacionHelper;
import com.example.motoserv.receivers.AcceptReceiver;
import com.example.motoserv.receivers.CancelReceiver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {

    private static final int NOTIFICATION_CODE = 500;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        RemoteMessage.Notification notification = message.getNotification();
        Map<String, String> data = message.getData();
        String title = data.get("title");
        String body = data.get("body");

        if (title != null){
            if (title.contains("NUEVO VIAJE")){
                String idClient = data.get("idClient");
                showNotificationActions(title, body, idClient);
            }else if (title.contains("VIAJE CANCELADO")){
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(2);
                showNotification(title, body);
            }else {
                showNotification(title, body);
            }
        }
    }

    private void showNotification(String title, String body){

        PendingIntent intent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificacionHelper notificacionHelper = new NotificacionHelper(getBaseContext());

        Notification.Builder builder = notificacionHelper.getNotification(title, body, intent);
        notificacionHelper.getManager().notify(1, builder.build());
    }

    private void showNotificationActions(String title, String body, String idClient){

        //Notification action: aceptar
        Intent acceptIntent = new Intent(this, AcceptReceiver.class);
        acceptIntent.putExtra("idClient", idClient);
        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification.Action acceptAction = new Notification.Action.Builder(
                R.mipmap.ic_launcher,
                "Aceptar",
                acceptPendingIntent
        ).build();
        /*NotificationCompat.Action acceptAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Aceptar",
                acceptPendingIntent
        ).build();*/

        //Notification action: rechazar
        Intent cancelIntent = new Intent(this, CancelReceiver.class);
        cancelIntent.putExtra("idClient", idClient);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification.Action cancelAction = new Notification.Action.Builder(
                R.mipmap.ic_launcher,
                "Rechazar",
                cancelPendingIntent
        ).build();

        NotificacionHelper notificacionHelper = new NotificacionHelper(getBaseContext());

        Notification.Builder builder = notificacionHelper.getNotificationActions(title, body, acceptAction, cancelAction);
        notificacionHelper.getManager().notify(2, builder.build());
    }
}
