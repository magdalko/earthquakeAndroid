package com.example.magdalena.json1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


public class SingleContactActivity  extends Activity
{
    // JSON node keys
    private static final String TAG_PLACE = "place";
    private static final String TAG_TIME = "email";
    private static final String TAG_MAG = "mobile";
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_contact);

        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        String place = in.getStringExtra(TAG_PLACE);
        System.out.println("pl" + place);
        long tim = in.getLongExtra("time", 0);
        System.out.println("ti" + tim);
        double mag = in.getDoubleExtra("mag", 0);
        System.out.println("ma" + mag);

        String time = Long.toString(tim);
        String magn = Double.toString(mag);


        // Displaying all values on the screen
        TextView lblName = (TextView) findViewById(R.id.place_label);
        TextView lblEmail = (TextView) findViewById(R.id.time_label);
        TextView lblMobile = (TextView) findViewById(R.id.mag_label);

        lblName.setText(place);
        lblEmail.setText(time);
        lblMobile.setText(magn);

    }

}