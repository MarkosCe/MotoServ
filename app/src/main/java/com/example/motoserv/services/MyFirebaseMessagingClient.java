package com.example.motoserv.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.motoserv.channel.NotificacionHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {

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
            showNotification(title, body);
        }
    }

    private void showNotification(String title, String body){

        PendingIntent intent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(), PendingIntent.FLAG_ONE_SHOT);

        NotificacionHelper notificacionHelper = new NotificacionHelper(getBaseContext());

        Notification.Builder builder = notificacionHelper.getNotification(title, body, intent);
        notificacionHelper.getManager().notify(1, builder.build());
    }
}
