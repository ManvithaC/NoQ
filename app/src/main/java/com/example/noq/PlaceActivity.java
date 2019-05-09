package com.example.noq;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

public class PlaceActivity extends AppCompatActivity {
    private EditText mWaitTime;
    private TextView cafeAddress, cafeName;
    private Button mStartQueue;
    private String placeId, userId;
    private PlaceActivity parent = this;
    private StartQueueTask mStartQueueTask = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        cafeName = (TextView)findViewById(R.id.tvCafeName);
        cafeAddress = (TextView)findViewById(R.id.tvCafeAddress);
        mWaitTime = (EditText) findViewById(R.id.tv_address);
        mStartQueue = (Button) findViewById(R.id.btn_startQueue);

        placeId = getIntent().getStringExtra("placeId");
        String placeName = getIntent().getStringExtra("placeName");
        userId = getIntent().getStringExtra("email");

        cafeAddress.setText(placeId);
        cafeName.setText(placeName);

        mStartQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("button click", "start queue button clicked");
                startQueue();
            }
        });
    }


    private void startQueue() {
        String waitTime = mWaitTime.getText().toString();
        mStartQueueTask = new StartQueueTask(placeId, waitTime, parent);
        mStartQueueTask.execute((Void) null);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class StartQueueTask extends AsyncTask<Object, String, String> {

        private final String mPlaceId, mWaitTime;
        private  final PlaceActivity mActivity;

        StartQueueTask(String placeId, String waitTime, PlaceActivity activity) {
            mPlaceId = placeId;
            mWaitTime = waitTime;
            this.mActivity = activity;
        }

        @Override
        protected String doInBackground(Object... objects) {
            HashMap<String, String> newUser = new HashMap<String, String>();
            newUser.put("userId", mWaitTime);
            newUser.put("placeId", mPlaceId);

            String returnData ="";

            String url = "https://noqueue-app.herokuapp.com/queues";
            PostUrl postUrl = new PostUrl();
            try {
                returnData = postUrl.postData(newUser, url);
                Log.d("Start Queue success", returnData);

                runOnUiThread(new Runnable(){
                    public void run() {
                        Toast.makeText(mActivity.getApplicationContext(), "Today's queue started." , Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            return returnData;
        }

        @Override
        protected void onPostExecute(String result) {
            mStartQueueTask = null;

            mStartQueue.setText("QUEUE STARTED");

            if(result == "Successful"){
                Log.d("Start Queue success", result);
            }
        }

    }

}
