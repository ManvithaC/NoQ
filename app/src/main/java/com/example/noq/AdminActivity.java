package com.example.noq;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

public class AdminActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private PlaceCreateTask mAuthTask = null;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private EditText mPlaceName;
    private EditText mPlaceLatitude;
    private EditText mPlaceLongitude;
    private EditText mPlaceId;

    private View mAdminProgressView;
    private View mAdminFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mAdminProgressView = findViewById(R.id.admin_create_form);
        mAdminFormView = findViewById(R.id.admin_progress);

        Button mLocateMeButton = (Button) findViewById(R.id.locate_me_button);
        mLocateMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("button click", "locate button clicked");
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });

        Button mPlaceCreateButton = (Button) findViewById(R.id.event_create_button);
        mPlaceCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("button click", "event create button clicked");
                postPlaceDetailsToServer();
            }
        });

        mPlaceName = (EditText) findViewById(R.id.place_name);
        mPlaceLatitude = (EditText) findViewById(R.id.place_lat);
        mPlaceLongitude = (EditText) findViewById(R.id.place_long);
        mPlaceId = (EditText) findViewById(R.id.place_id);
    }

    private void postPlaceDetailsToServer(){
        if (mAuthTask != null) {
            return;
        }
        String placeName = mPlaceName.getText().toString();
        String placeLat = mPlaceLatitude.getText().toString();
        String placeLong = mPlaceLongitude.getText().toString();
        String placeId = mPlaceId.getText().toString();

        mAuthTask = new PlaceCreateTask(placeName, placeLat, placeLong, placeId );
        mAuthTask.execute((Void) null);
    }

    public class PlaceCreateTask extends AsyncTask<Object, String, String> {

        private final String mPlaceName;
        private final String mPlaceLatitude;
        private final String mPlaceLongitude;
        private final String mPlaceId;

        PlaceCreateTask(String placeName, String placeLat, String placeLong, String placeId) {
            mPlaceName = placeName;
            mPlaceLatitude = placeLat;
            mPlaceLongitude = placeLong;
            mPlaceId = placeId;
        }

        @Override
        protected String doInBackground(Object... objects) {
            HashMap<String, String> newPlace = new HashMap<String, String>();
            newPlace.put("email", LoginActivity.EMAIL);
            newPlace.put("name", mPlaceName);
            newPlace.put("placeId", mPlaceId);
            newPlace.put("latitude", mPlaceLatitude);
            newPlace.put("longitude", mPlaceLongitude);

            String returnData ="";

            String url = "https://noqueue-app.herokuapp.com/places";
            PostUrl postUrl = new PostUrl();
            try {
                returnData = postUrl.postData(newPlace, url);
                Log.d("post new place success", returnData);

                Snackbar.make(mAdminFormView, "Queue created successfully.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return returnData;
        }

        @Override
        protected void onPostExecute(String result) {
            mAuthTask = null;
            showProgress(false);

            if(result == "Successful"){
                Log.d("Place creation success", result);

                Toast toast =  Toast.makeText(getApplicationContext(), "Place creation successful.",
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mAdminFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mAdminFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAdminFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mAdminProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mAdminProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAdminProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mAdminProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mAdminFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_admin, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText("Please fill in the details: ");
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
