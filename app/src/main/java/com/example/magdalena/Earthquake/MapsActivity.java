package com.example.magdalena.json1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

public class MapsActivity extends Activity implements OnMapReadyCallback {

    private final String TWIT_CONS_KEY = "rCm4fR9v2Dl98l2HQ8EpRVDOV";
    private final String TWIT_CONS_SEC_KEY = "OPBcIt7DVizheJa8C6vaSKW64BVHNPQFSUJ2Vx3HLfnAms5J6c";
    private ProgressDialog pDialog;

    // URL to get contacts JSON
    private static String url = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.geojson";


  //  private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    // JSON node keys
    private static final String TAG_PLACE = "place";
    private static final String TAG_TIME = "email";
    private static final String TAG_MAG = "mobile";
    ListView lv;


    // Hashmap for ListView
  //  private ArrayList<Tweet> tweets = new ArrayList<Tweet>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
       // setUpMapIfNeeded();

        //MAP
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lv = (ListView) findViewById(R.id.listView);
      //  setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, elements));


        Intent in = getIntent();

        // Get JSON values from previous intent
        String place = in.getStringExtra("place");
        System.out.println("pl" + place);
        long tim = in.getLongExtra("time", 0);
        System.out.println("ti" + tim);
        double mag = in.getDoubleExtra("mag", 0);
        System.out.println("ma" + mag);

        String time = Long.toString(tim);
        String magn = Double.toString(mag);

        new SearchOnTwitter().execute("eartquake");
    }

    class SearchOnTwitter extends AsyncTask<String, Void, Integer> {

        ArrayList<Tweet> tweets;
        final int SUCCESS = 0;
        final int FAILURE = SUCCESS + 1;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MapsActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setApplicationOnlyAuthEnabled(true);

                builder.setOAuthAuthenticationURL("https://api.twitter.com/oauth/request_token");
                builder.setOAuthAccessTokenURL("https://api.twitter.com/oauth/access_token");
                builder.setOAuthAuthorizationURL("https://api.twitter.com/oauth/authorize");
                builder.setOAuthRequestTokenURL("https://api.twitter.com/oauth/request_token");
                builder.setRestBaseURL("https://api.twitter.com/1.1/");
                builder.setOAuthConsumerKey(TWIT_CONS_KEY);
                builder.setOAuthConsumerSecret(TWIT_CONS_SEC_KEY);

                OAuth2Token token = new TwitterFactory(builder.build()).getInstance().getOAuth2Token();

                builder = new ConfigurationBuilder();
                builder.setApplicationOnlyAuthEnabled(true);
                builder.setOAuthConsumerKey(TWIT_CONS_KEY);
                builder.setOAuthConsumerSecret(TWIT_CONS_SEC_KEY);
                builder.setOAuth2TokenType(token.getTokenType());
                builder.setOAuth2AccessToken(token.getAccessToken());

                Twitter twitter = new TwitterFactory(builder.build()).getInstance();
                System.out.println("polaczylo sie");
                Query query = new Query(params[0]);
                // YOu can set the count of maximum records here
                query.setCount(10);
                QueryResult result;
                result = twitter.search(query);
                System.out.println("RESULT" + result);

                List<twitter4j.Status> tweets = result.getTweets();
                StringBuilder str = new StringBuilder();
                if (tweets != null) {
                    this.tweets = new ArrayList<Tweet>();
                    for (twitter4j.Status tweet : tweets) {
                        str.append("@" + tweet.getUser().getScreenName() + " - " + tweet.getText() + "\n");
                        System.out.println(str);
                        this.tweets.add(new Tweet("@" + tweet.getUser().getScreenName(), tweet.getText()));
                    }
                    return SUCCESS;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return FAILURE;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
          /*  ListAdapter adapter = new SimpleAdapter(
                    MapsActivity.this,  tweets,
                    R.layout.list_item2, new String[]{TAG_PLACE,
                    TAG_MAG}, new int[]{R.id.info,
                    R.id.author});*/

            lv.setAdapter(new TweetAdapter(MapsActivity.this, tweets));

       //     lv.setAdapter(adapter);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
     //   setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
 /*   private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.3030, 0.0732), 9));
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }*/

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link # m Map} is not null.
     */
  /*  private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }*/


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.3030, 0.0732), 9));
    }
}
