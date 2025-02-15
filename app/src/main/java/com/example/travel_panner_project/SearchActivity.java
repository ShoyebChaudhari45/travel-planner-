package com.example.travel_panner_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URLEncoder;

public class SearchActivity extends AppCompatActivity {
    private EditText sourceField, destinationField;
    private Button swapButton, searchButton;
    private String sourceLat, sourceLon, destLat, destLon;
    private RequestQueue requestQueue;
    private static final String TAG = "SearchActivity"; // For logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        sourceField = findViewById(R.id.sourceField);
        destinationField = findViewById(R.id.destinationField);
        swapButton = findViewById(R.id.swapButton);
        searchButton = findViewById(R.id.searchButton);

        requestQueue = Volley.newRequestQueue(this);

        swapButton.setOnClickListener(v -> swapSourceAndDestination());
        searchButton.setOnClickListener(v -> fetchCoordinatesAndStartRouteActivity());
    }

    private void swapSourceAndDestination() {
        String temp = sourceField.getText().toString();
        sourceField.setText(destinationField.getText().toString());
        destinationField.setText(temp);
    }

    private void fetchCoordinatesAndStartRouteActivity() {
        String source = sourceField.getText().toString().trim();
        String destination = destinationField.getText().toString().trim();

        if (source.isEmpty() || destination.isEmpty()) {
            Toast.makeText(this, "Please enter both source and destination", Toast.LENGTH_SHORT).show();
            return;
        }

        fetchCoordinates(source, true);
        fetchCoordinates(destination, false);
    }

    private void fetchCoordinates(String placeName, boolean isSource) {
        try {
            String encodedPlace = URLEncoder.encode(placeName, "UTF-8");
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + encodedPlace;

            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                    response -> handleLocationResponse(response, isSource, placeName),
                    error -> {
                        Log.e(TAG, "Volley error fetching location: " + error.getMessage());
                        Toast.makeText(this, "Error fetching location for: " + placeName, Toast.LENGTH_SHORT).show();
                    });

            requestQueue.add(request);
        } catch (Exception e) {
            Log.e(TAG, "Error encoding URL", e);
            Toast.makeText(this, "Invalid location input", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleLocationResponse(JSONArray response, boolean isSource, String placeName) {
        try {
            if (response.length() > 0) {
                JSONObject location = response.getJSONObject(0); // Consider showing all options instead
                String lat = location.getString("lat");
                String lon = location.getString("lon");

                if (isSource) {
                    sourceLat = lat;
                    sourceLon = lon;
                } else {
                    destLat = lat;
                    destLon = lon;
                }

                if (sourceLat != null && destLat != null) {
                    startRouteActivity();
                }
            } else {
                Toast.makeText(this, "Location not found: " + placeName, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing location JSON", e);
            Toast.makeText(this, "Error processing location data", Toast.LENGTH_SHORT).show();
        }
    }

    private void startRouteActivity() {
        Intent intent = new Intent(this, RouteActivity.class);
        intent.putExtra("sourceLat", sourceLat);
        intent.putExtra("sourceLon", sourceLon);
        intent.putExtra("destLat", destLat);
        intent.putExtra("destLon", destLon);
        startActivity(intent);
    }
}
