package com.example.travel_panner_project;

import android.content.Intent;
import android.location.Geocoder;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private AutoCompleteTextView sourceInput, destinationInput;
    private Button btnFindRoutes, btnSwap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full-screen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_search);

        sourceInput = findViewById(R.id.sourceInput);
        destinationInput = findViewById(R.id.destinationInput);
        btnFindRoutes = findViewById(R.id.btnFindRoutes);
        btnSwap = findViewById(R.id.btnSwap);

        btnFindRoutes.setOnClickListener(v -> searchRoutes());
        btnSwap.setOnClickListener(v -> swapInputs());
    }

    private void swapInputs() {
        String tempSource = sourceInput.getText().toString();
        sourceInput.setText(destinationInput.getText().toString());
        destinationInput.setText(tempSource);
    }

    private void searchRoutes() {
        String source = sourceInput.getText().toString();
        String destination = destinationInput.getText().toString();

        if (source.isEmpty() || destination.isEmpty()) {
            Toast.makeText(this, "Please enter both locations", Toast.LENGTH_SHORT).show();
            return;
        }

        double[] sourceCoordinates = getCoordinates(source);
        double[] destCoordinates = getCoordinates(destination);

        if (sourceCoordinates == null || destCoordinates == null) {
            Toast.makeText(this, "Unable to fetch location data", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(SearchActivity.this, RouteActivity.class);
        intent.putExtra("sourceCity", source);
        intent.putExtra("destCity", destination);
        intent.putExtra("sourceLat", String.valueOf(sourceCoordinates[0]));
        intent.putExtra("sourceLon", String.valueOf(sourceCoordinates[1]));
        intent.putExtra("destLat", String.valueOf(destCoordinates[0]));
        intent.putExtra("destLon", String.valueOf(destCoordinates[1]));
        startActivity(intent);
    }

    private double[] getCoordinates(String location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return new double[]{address.getLatitude(), address.getLongitude()};
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
