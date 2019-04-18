package com.example.noq;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.design.widget.CoordinatorLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private GetPlacesTask mRunGetPlacesTask = null;

    private  String placesData = null;

    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRunGetPlacesTask = new GetPlacesTask();
        mRunGetPlacesTask.execute((Void) null);

        mEmail = getIntent().getStringExtra("email");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        TextView username = (TextView)findViewById(R.id.username);
        username.setText("Sup', "+ getIntent().getStringExtra("firstname")+" "
                +getIntent().getStringExtra("lastname"));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Log.d("settings", "D");
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);

            intent.putExtra("firstname", getIntent().getStringExtra("firstname"));
            intent.putExtra("lastname", getIntent().getStringExtra("lastname"));

            startActivity(intent);

        } else if (id == R.id.nav_logout) {

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);

            Toast toast =  Toast.makeText(getApplicationContext(), "Bye. See ya later!",
                    Toast.LENGTH_LONG);
            toast.show();
        } else if (id == R.id.nav_scan) {

            Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
            intent.putExtra("email", getIntent().getStringExtra("email"));
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class GetPlacesTask extends AsyncTask<Object, String, String> {
        private RecyclerView recyclerView;
        private RecyclerView.Adapter mAdapter;
        private RecyclerView.LayoutManager layoutManager;

        public CoordinatorLayout coordinator_layout;

        public void GetPlacesTask(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        protected String doInBackground(Object... objects) {

            String returnData ="";

            String url = "https://noqueue-app.herokuapp.com/places";
            DownloadUrl downloadUrl = new DownloadUrl();
            try {
                returnData = downloadUrl.readUrl(url);
                Log.d("Response places data", returnData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("data from Places API", returnData);
            return returnData;
        }

        @SuppressLint("CutPasteId")
        @Override
        protected void onPostExecute(String result) {
            Log.d("Result", result);
            DownloadUrl downloadUrl = new DownloadUrl();
            mRunGetPlacesTask = null;
            int get_places_responseCode = downloadUrl.getResponseCode();
            if(get_places_responseCode == 200){
                placesData = result;
                Log.d("placesData", placesData == null ? "null" : placesData.toString());

                coordinator_layout = findViewById(R.id.coordinator_layout);

                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                layoutManager = new LinearLayoutManager(NavActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                ArrayList<String> allPlaces = new ArrayList<>();

                try {
                    JSONArray jsonArray = new JSONArray(result);
                    Log.d("jsonObject", jsonArray.toString());
                    for(int i=0; i < jsonArray.length(); i++) {
                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                        String placeDetails = jsonobject.getString("name") + ":"
                                + jsonobject.getString("placeId") + ":"
                                + mEmail ;
                        allPlaces.add(placeDetails);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter = new RecyclerAdapter((ArrayList<String>)allPlaces);
                recyclerView.setAdapter(mAdapter);
            }
            if(get_places_responseCode == 401){
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Something seems to be wrong!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

        }
    }
}
