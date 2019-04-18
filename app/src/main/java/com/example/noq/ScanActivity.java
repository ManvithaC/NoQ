package com.example.noq;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.example.noq.QueueActivity;

import com.google.zxing.Result;

import java.util.Random;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private static final int uniqueId = 702344;

    ZXingScannerView scannerView;

    NotificationApp nApp;

    private String userId;

    private QueueActivity.JoinQueueTask mJoinQueueTask = null;

    private NotificationManagerCompat nManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        nManager = NotificationManagerCompat.from(this);
    }

    @Override
    public void handleResult(Result result) {
//        MainActivity.resultTextView.setText(result.getText());
        Log.i("Result from scanner", result.getText());

        final String placeId = result.getText();
        userId = getIntent().getStringExtra("email");
        Log.i("userID", userId);
        mJoinQueueTask = new QueueActivity().new JoinQueueTask(userId, placeId);
        mJoinQueueTask.execute((Void) null);

        Intent intent = new Intent(getApplicationContext(), NavActivity.class);

        sendNotification(userId, intent);
//        nApp = new NotificationApp();
//        Notification builder = nApp.getNotification(userId);
//        nApp.getManager().notify(new Random().nextInt(), builder);

        startActivity(intent);
        Toast toast =  Toast.makeText(getApplicationContext(), "Done!",
                Toast.LENGTH_LONG);
        toast.show();
        onBackPressed();
    }


    @SuppressLint("ServiceCast")
    public void sendNotification(String  userId, Intent intent){

//        Intent intent = new Intent(this, ScanActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(this, NotificationApp.channel_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Confirmation from NoQ")
                .setContentText("Hello " + userId + " you have been added to waitlist ")
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .build();

        nManager.notify(uniqueId,notification);

    }

    @Override
    protected void onPause() {
        super.onPause();

        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();

        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}
