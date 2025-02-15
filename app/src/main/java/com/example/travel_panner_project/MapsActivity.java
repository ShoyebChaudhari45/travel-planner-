package com.example.travel_panner_project;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String startLat, startLon, endLat, endLon;

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
    }
}
