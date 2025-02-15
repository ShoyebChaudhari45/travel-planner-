package com.example.travel_panner_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HotelBookingActivity extends AppCompatActivity {

    private String destinationCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_booking);

        TextView cityNameText = findViewById(R.id.cityName);
        destinationCity = getIntent().getStringExtra("destCity");

        if (destinationCity == null || destinationCity.trim().isEmpty()) {
            destinationCity = "Unknown Destination";
        }

        Log.d("HotelBookingActivity", "Destination City: " + destinationCity);
        cityNameText.setText("Hotels in " + destinationCity);

        // Get ImageView and TextView references
        ImageView imgBookingCom = findViewById(R.id.imgBookingCom);
        ImageView imgAgoda = findViewById(R.id.imgAgoda);
        ImageView imgGoogleHotels = findViewById(R.id.imgGoogleHotels);

        TextView txtBookingCom = findViewById(R.id.txtBookingCom);
        TextView txtAgoda = findViewById(R.id.txtAgoda);
        TextView txtGoogleHotels = findViewById(R.id.txtGoogleHotels);

        // Set click listeners on images & texts to open respective booking sites
        View.OnClickListener bookingComClickListener = v ->
                openHotelWebsite("https://www.booking.com/searchresults.html?ss=" + Uri.encode(destinationCity));

        View.OnClickListener agodaClickListener = v ->
                openHotelWebsite("https://www.agoda.com/search?city=" + Uri.encode(destinationCity));

        View.OnClickListener googleHotelsClickListener = v ->
                openHotelWebsite("https://www.google.com/travel/hotels?q=" + Uri.encode(destinationCity));

        // Assign listeners to images and text
        imgBookingCom.setOnClickListener(bookingComClickListener);
        txtBookingCom.setOnClickListener(bookingComClickListener);

        imgAgoda.setOnClickListener(agodaClickListener);
        txtAgoda.setOnClickListener(agodaClickListener);

        imgGoogleHotels.setOnClickListener(googleHotelsClickListener);
        txtGoogleHotels.setOnClickListener(googleHotelsClickListener);
    }

    // Function to open a URL in a web browser
    private void openHotelWebsite(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
