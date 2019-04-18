package com.example.noq;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private static final int uniqueId = 702344;

    ZXingScannerView scannerView;

    NotificationApp nApp;

    private String userId;

    private JoinQueueTaskScan mJoinQueueTask = null;

    private NotificationManagerCompat nManager;

    private NotificationManager notificationManager;

    private ScanActivity parent = this;

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
//        mJoinQueueTask = new JoinQueueTaskScan(userId, placeId, parent);
//        mJoinQueueTask.execute((Void) null);

        createNotification(userId, placeId, parent.getApplicationContext());

        Intent intent = new Intent(parent.getApplicationContext(), NavActivity.class);

        startActivity(intent);
        Toast toast =  Toast.makeText(parent.getApplicationContext(), "You're successfully added to the queue.",
                Toast.LENGTH_LONG);
        toast.show();
        onBackPressed();
    }


//    @SuppressLint("ServiceCast")
//    public void sendNotification(String  userId, Intent intent){
//
////        Intent intent = new Intent(this, ScanActivity.class);
////        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        Notification notification = new NotificationCompat.Builder(this, NotificationApp.channel_ID)
//                .setSmallIcon(android.R.drawable.ic_menu_search)
//                .setContentTitle("Confirmation from NoQ")
//                .setContentText("Hello " + userId + " you have been added to waitlist ")
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setContentIntent(pendingIntent)
//                .build();
//
//        nManager.notify(uniqueId,notification);
//
//    }

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


    public void createNotification(String userId, String placeId, Context context) {
        final int NOTIFY_ID = 0; // ID of notification
        String id = "noqChannel"; // default_channel_id
        String title = "NoQ Channel"; // Default Channel
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Log.d("Checking notification",  "In here");
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notificationManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, ScanActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle("NoQ")                            // required
                    .setSmallIcon(android.R.drawable.ic_menu_search)   // required
                    .setContentText("Confirmation" ) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Hello " + userId + " You have been added to " + placeId + " queue!" ))
//                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        }
        else {
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, ScanActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle("Confirmation from NoQ")                            // required
                    .setSmallIcon(android.R.drawable.ic_menu_search)   // required
                    .setContentText("Confirmation" )
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Hello " + userId + " You have been added to " + placeId + " queue!" ))
//                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notificationManager.notify(NOTIFY_ID, notification);
    }


    public class JoinQueueTaskScan extends AsyncTask<Object, String, String> {

        private final String mEmail;
        private final String mPlaceId;
        private  final ScanActivity mActivity;

        JoinQueueTaskScan(String email, String placeId, ScanActivity activity) {
            mEmail = email;
            mPlaceId = placeId;
            this.mActivity = activity;
        }

        @Override
        protected String doInBackground(Object... objects) {
            HashMap<String, String> newUser = new HashMap<String, String>();
            newUser.put("userId", mEmail);
            newUser.put("placeId", mPlaceId);

            String returnData ="";

            String url = "https://noqueue-app.herokuapp.com/queues/addUser";
            PostUrl postUrl = new PostUrl();
            try {
                returnData = postUrl.postData(newUser, url);
                Log.d("Add to Queue success", returnData);

                runOnUiThread(new Runnable(){
                    public void run() {
                        Toast.makeText(mActivity.getApplicationContext(), "You're successfully added to the queue." , Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            return returnData;
        }

        @Override
        protected void onPostExecute(String result) {
            mJoinQueueTask = null;
            if(result == "Successful"){
                Log.d("Add User success", result);
            }
        }

    }
}
