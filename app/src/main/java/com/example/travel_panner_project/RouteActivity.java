package com.example.travel_panner_project;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView routeInfo;
    private String startLat, startLon, endLat, endLon;
    private RequestQueue requestQueue;
    private GoogleMap mMap;
    private MapView smallMapView;
    private FloatingActionButton fullScreenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable full-screen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_route);

        routeInfo = findViewById(R.id.routeInfo);
        smallMapView = findViewById(R.id.smallMapView);
        fullScreenButton = findViewById(R.id.fullScreenButton);
        requestQueue = Volley.newRequestQueue(this);

        // Get coordinates from intent
        startLat = getIntent().getStringExtra("sourceLat");
        startLon = getIntent().getStringExtra("sourceLon");
        endLat = getIntent().getStringExtra("destLat");
        endLon = getIntent().getStringExtra("destLon");

        if (startLat != null && startLon != null && endLat != null && endLon != null) {
            fetchRoute();
        } else {
            routeInfo.setText("Invalid location data received");
        }

        // Initialize the small map
        smallMapView.onCreate(savedInstanceState);
        smallMapView.getMapAsync(this);

        // Open fullscreen map on click
        View.OnClickListener openFullScreen = v -> {
            Intent intent = new Intent(RouteActivity.this, MapsActivity.class);
            intent.putExtra("sourceLat", startLat);
            intent.putExtra("sourceLon", startLon);
            intent.putExtra("destLat", endLat);
            intent.putExtra("destLon", endLon);
            startActivity(intent);
        };

        smallMapView.setOnClickListener(openFullScreen);
        fullScreenButton.setOnClickListener(openFullScreen);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng source = new LatLng(Double.parseDouble(startLat), Double.parseDouble(startLon));
        LatLng destination = new LatLng(Double.parseDouble(endLat), Double.parseDouble(endLon));

        // Add markers
        mMap.addMarker(new MarkerOptions().position(source).title("Source"));
        mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));

        // Move camera to source
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source, 10));

        // Fetch and draw route
        fetchRouteOnMap();
    }

    private void fetchRoute() {
        String url = "https://router.project-osrm.org/route/v1/driving/" +
                startLon + "," + startLat + ";" + endLon + "," + endLat +
                "?overview=full&geometries=geojson";

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
                error -> {
                    error.printStackTrace();
                    routeInfo.setText("Error fetching route");
                });

        requestQueue.add(request);
    }

    private void fetchRouteOnMap() {
        String url = "https://router.project-osrm.org/route/v1/driving/" +
                startLon + "," + startLat + ";" + endLon + "," + endLat +
                "?overview=full&geometries=geojson";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray routes = response.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject geometry = route.getJSONObject("geometry");
                            JSONArray coordinates = geometry.getJSONArray("coordinates");

                            List<LatLng> routePoints = new ArrayList<>();
                            for (int i = 0; i < coordinates.length(); i++) {
                                JSONArray point = coordinates.getJSONArray(i);
                                double lon = point.getDouble(0);
                                double lat = point.getDouble(1);
                                routePoints.add(new LatLng(lat, lon));
                            }

                            // Draw polyline on map
                            mMap.addPolyline(new PolylineOptions()
                                    .addAll(routePoints)
                                    .width(5)
                                    .color(Color.BLUE));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace());

        requestQueue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        smallMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        smallMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        smallMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        smallMapView.onLowMemory();
    }
}
