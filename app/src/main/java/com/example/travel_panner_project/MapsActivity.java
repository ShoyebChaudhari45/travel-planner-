package com.example.travel_panner_project;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private String startLat, startLon, endLat, endLon;
    private Polyline polyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Get coordinates
        startLat = getIntent().getStringExtra("sourceLat");
        startLon = getIntent().getStringExtra("sourceLon");
        endLat = getIntent().getStringExtra("destLat");
        endLon = getIntent().getStringExtra("destLon");

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fullscreenMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Setup buttons
        Button btnNormal = findViewById(R.id.btnNormal);
        Button btnSatellite = findViewById(R.id.btnSatellite);
        Button btnTerrain = findViewById(R.id.btnTerrain);
        Button btnHybrid = findViewById(R.id.btnHybrid);

        btnNormal.setOnClickListener(v -> mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL));
        btnSatellite.setOnClickListener(v -> mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE));
        btnTerrain.setOnClickListener(v -> mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN));
        btnHybrid.setOnClickListener(v -> mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng source = new LatLng(Double.parseDouble(startLat), Double.parseDouble(startLon));
        LatLng destination = new LatLng(Double.parseDouble(endLat), Double.parseDouble(endLon));

        mMap.addMarker(new MarkerOptions().position(source).title("Source"));
        mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source, 10));

        // Fetch and draw route
        new FetchRouteTask().execute(getDirectionsUrl(source, destination));
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        return "https://router.project-osrm.org/route/v1/driving/" +
                origin.longitude + "," + origin.latitude + ";" +
                dest.longitude + "," + dest.latitude + "?overview=full&geometries=geojson";
    }

    private class FetchRouteTask extends AsyncTask<String, Void, List<LatLng>> {
        @Override
        protected List<LatLng> doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return parseRoute(response.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<LatLng> points) {
            if (points != null && !points.isEmpty()) {
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(points)
                        .width(10)
                        .color(Color.BLUE);
                polyline = mMap.addPolyline(polylineOptions);
            }
        }
    }

    private List<LatLng> parseRoute(String jsonResponse) {
        List<LatLng> routePoints = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray routes = jsonObject.getJSONArray("routes");
            if (routes.length() > 0) {
                JSONObject route = routes.getJSONObject(0);
                JSONObject geometry = route.getJSONObject("geometry");
                JSONArray coordinates = geometry.getJSONArray("coordinates");
                for (int i = 0; i < coordinates.length(); i++) {
                    JSONArray coord = coordinates.getJSONArray(i);
                    double lon = coord.getDouble(0);
                    double lat = coord.getDouble(1);
                    routePoints.add(new LatLng(lat, lon));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routePoints;
    }
}
