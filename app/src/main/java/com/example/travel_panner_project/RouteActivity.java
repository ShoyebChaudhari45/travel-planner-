package com.example.travel_panner_project;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView routeInfo;
    private String startLat, startLon, endLat, endLon, sourceCity, destinationCity;
    private RequestQueue requestQueue;
    private GoogleMap mMap;
    private MapView smallMapView;
    private FloatingActionButton fullScreenButton;
    private LinearLayout transportOptionsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full-screen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_route);

        // Initialize UI components
        routeInfo = findViewById(R.id.routeInfo);
        smallMapView = findViewById(R.id.smallMapView);
        fullScreenButton = findViewById(R.id.fullScreenButton);
        transportOptionsContainer = findViewById(R.id.transportOptionsContainer);
        Button btnViewNearbyPlaces = findViewById(R.id.btnViewNearbyPlaces);
        Button btnHotelBooking = findViewById(R.id.btnHotelBooking);
        requestQueue = Volley.newRequestQueue(this);

        // Get data from Intent
        startLat = getIntent().getStringExtra("sourceLat");
        startLon = getIntent().getStringExtra("sourceLon");
        endLat = getIntent().getStringExtra("destLat");
        endLon = getIntent().getStringExtra("destLon");
        sourceCity = getIntent().getStringExtra("sourceCity");
        destinationCity = getIntent().getStringExtra("destCity");

        // Button actions
        btnHotelBooking.setOnClickListener(v -> openActivity(HotelBookingActivity.class, destinationCity));
        btnViewNearbyPlaces.setOnClickListener(v -> openActivity(NearbyPlacesActivity.class, endLat, endLon));

        // Fetch route
        if (startLat != null && startLon != null && endLat != null && endLon != null) {
            fetchRoute();
        } else {
            routeInfo.setText("Invalid location data received");
        }

        // Initialize the small map
        smallMapView.onCreate(savedInstanceState);
        smallMapView.getMapAsync(this);

        // Open full-screen map
        View.OnClickListener openFullScreen = v -> openFullScreenMap();
        smallMapView.setOnClickListener(openFullScreen);
        fullScreenButton.setOnClickListener(openFullScreen);

        // Load transport options
        loadTransportOptions();
    }

    private void openActivity(Class<?> activityClass, String... extras) {
        Intent intent = new Intent(RouteActivity.this, activityClass);
        if (extras.length > 0) intent.putExtra("destCity", extras[0]);
        if (extras.length > 1) {
            intent.putExtra("destLat", extras[0]);
            intent.putExtra("destLon", extras[1]);
        }
        startActivity(intent);
    }

    private void openFullScreenMap() {
        Intent intent = new Intent(RouteActivity.this, MapsActivity.class);
        intent.putExtra("sourceLat", startLat);
        intent.putExtra("sourceLon", startLon);
        intent.putExtra("destLat", endLat);
        intent.putExtra("destLon", endLon);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng source = new LatLng(Double.parseDouble(startLat), Double.parseDouble(startLon));
        LatLng destination = new LatLng(Double.parseDouble(endLat), Double.parseDouble(endLon));

        mMap.addMarker(new MarkerOptions().position(source).title("Source"));
        mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source, 10));

        fetchRouteOnMap();
    }

    private void fetchRouteOnMap() {
        String url = "https://router.project-osrm.org/route/v1/driving/" +
                startLon + "," + startLat + ";" + endLon + "," + endLat + "?overview=full&geometries=geojson";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray routes = response.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONArray coordinates = routes.getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates");
                            List<LatLng> routePoints = new ArrayList<>();
                            for (int i = 0; i < coordinates.length(); i++) {
                                JSONArray point = coordinates.getJSONArray(i);
                                routePoints.add(new LatLng(point.getDouble(1), point.getDouble(0)));
                            }
                            mMap.addPolyline(new PolylineOptions().addAll(routePoints).width(5).color(Color.BLUE));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace);

        requestQueue.add(request);
    }

    private void fetchRoute() {
        String url = "https://router.project-osrm.org/route/v1/driving/" +
                startLon + "," + startLat + ";" + endLon + "," + endLat + "?overview=full&geometries=geojson";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray routes = response.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            double distance = route.getDouble("distance") / 1000;
                            double duration = route.getDouble("duration") / 60;
                            routeInfo.setText("Distance: " + String.format("%.2f", distance) + " km\n"
                                    + "Duration: " + String.format("%.2f", duration) + " mins");
                        } else {
                            routeInfo.setText("No route found.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        routeInfo.setText("Error parsing route");
                    }
                },
                error -> routeInfo.setText("Error fetching route"));

        requestQueue.add(request);
    }

    private void loadTransportOptions() {
        addTransportOption(R.drawable.ic_bus, "Bus", "https://www.redbus.in/");
        addTransportOption(R.drawable.ic_train, "Train", "https://www.irctc.co.in/");
        addTransportOption(R.drawable.ic_flight, "Flight", "https://www.makemytrip.com/flights/");
    }

    private void addTransportOption(int iconRes, String label, String baseUrl) {
        View optionView = getLayoutInflater().inflate(R.layout.transport_option_item, transportOptionsContainer, false);
        ((ImageView) optionView.findViewById(R.id.transportIcon)).setImageResource(iconRes);
        ((TextView) optionView.findViewById(R.id.transportName)).setText(label);

        optionView.setOnClickListener(v -> {
            String searchUrl = baseUrl + "?from=" + sourceCity + "&to=" + destinationCity;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl)));
        });

        transportOptionsContainer.addView(optionView);
    }
}
