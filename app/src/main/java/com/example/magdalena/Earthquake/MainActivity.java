package com.example.magdalena.json1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import static android.graphics.Color.*;

public class MainActivity extends ListActivity {

    private ProgressDialog pDialog;

    // URL to get contacts JSON
    private static String url = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.geojson";

    //ID to select 20
    private static String TAG_ID;
    // JSON Node names

    private static final String TAG_PROP = "properties";
    private static final String TAG_TYPE = "type";
    private static final String TAG_FEATURES = "features"; //contacts
    private static final String TAG_MAG = "mag"; //id
    private static final String TAG_DETAIL = "detail";
    private static final String TAG_PLACE = "place";//name
    private static final String TAG_TIME = "time";//name
    private static final String TAG_TSUNAMI = "tsunami";
    private static final String TAG_GEOMETRY = "geometry";
    private static final String TAG_COOR = "coordinates";

    // contacts JSONArray
    JSONArray features = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = new ArrayList<HashMap<String, String>>();

        ListView lv = getListView();

      /*  TextView messageText = (TextView) findViewById(R.id.place);
          if(Integer.parseInt(TAG_MAG) > 2) {
             messageText.setTextColor(RED);
          }*/

        // Listview on item click listener
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String PL = ((TextView) view.findViewById(R.id.place))
                        .getText().toString();
                String TIM = ((TextView) view.findViewById(R.id.time))
                        .getText().toString();
                String MAGN = ((TextView) view.findViewById(R.id.mag))
                        .getText().toString();
                long czas= Long.parseLong(TIM);
                double mag = Double.parseDouble(MAGN);


                // Starting single contact activity
                Intent in = new Intent(getApplicationContext(),
                        MapsActivity.class);
                in.putExtra("place", PL);
                in.putExtra("time", czas);
                in.putExtra("mag",mag );
                startActivity(in);

            }
        });

        // Calling async task to get json
        new GetContacts().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                System.out.println("if");
                try {
                    System.out.println("try");
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    System.out.println("po obdzekcie");
                    // Getting JSON Array node

                    features = jsonObj.getJSONArray(TAG_FEATURES); //opisanie głównego wezla
                    System.out.println("heheszki");

                    // looping through All Contacts
                    for (int i = 0; i < 20; i++) { //wyciagamy z wezła info
                        System.out.println("weszło do fora");
                        JSONObject c = features.getJSONObject(i);
                        System.out.println("halo?");
                        String id = String.valueOf(i);
                        String type = c.getString(TAG_TYPE);
                        System.out.println(type);
                        JSONObject prop = c.getJSONObject(TAG_PROP);
                        System.out.println(prop);
                        String place = prop.getString(TAG_PLACE);
                        System.out.println(place);
                        String mag = prop.getString(TAG_MAG);

                        String time = prop.getString(TAG_TIME);
                        String tsunami = prop.getString(TAG_TSUNAMI);

                        // Geometry node is JSON Object
                        JSONObject phone = c.getJSONObject(TAG_GEOMETRY);
                        System.out.println(phone);
                        //opiasywanie wezla ktory ma dzieci.
                        String coor = phone.getString(TAG_COOR);

                        // tmp hashmap for single contact
                        HashMap<String, String> contact = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        contact.put(TAG_ID, id);
                        System.out.println(id);
                        contact.put(TAG_PLACE, place);
                        contact.put(TAG_MAG, mag);
                        contact.put(TAG_TIME, time);
                          contact.put(TAG_TSUNAMI, tsunami);
                          contact.put(TAG_COOR, coor);

                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, contactList,
                    R.layout.list_item, new String[]{TAG_PLACE, TAG_TIME,
                    TAG_MAG}, new int[]{R.id.place,
                    R.id.time, R.id.mag});
               //     TextView messageText = (TextView) findViewById(R.id.place);
                  //  if(Integer.parseInt(TAG_MAG) > 2) {
                   //     messageText.setTextColor(RED);
                  //  }
            setListAdapter(adapter);

         //   TextView messageText = (TextView) findViewById(R.id.place_label);

          /*  String b = TAG_MAG;
            System.out.println(b);
            int a= Integer.parseInt(b);
            System.out.println(a);
            if( a > 2) {
                messageText.setTextColor(RED);
            }*/
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        View menuItemView = findViewById(R.id.action_search); // SAME ID AS MENU ID
        PopupMenu popupMenu = new PopupMenu(this, menuItemView);
        popupMenu.inflate(R.menu.menu_items);



        popupMenu.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch(item.getItemId()){
                            case R.id.action_show:
                                Toast.makeText(MainActivity.this, "zaznaczone", Toast.LENGTH_SHORT).show();
                              //  sortowanie();
                            default:
                                break;
                        }

                        return false;
                    }
                    }

        );

        popupMenu.show();
        return true;
    }

    public void sortowanie(){
        Collections.sort(contactList, new Comparator<HashMap<String, String>>() {

            @Override
            public int compare(HashMap<String, String> o1,
                               HashMap<String, String> o2) {
                return Double.compare(Double.parseDouble(o1.get(TAG_TSUNAMI)), Double.parseDouble(o2.get(TAG_TSUNAMI))); // error
            }
        });
    }



}
