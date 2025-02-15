package com.example.travel_panner_project;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class TransportDetailsActivity extends AppCompatActivity {

    private TextView trainDetails;
    private String source, destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_details);

        trainDetails = findViewById(R.id.trainDetails);

        if (getIntent().hasExtra("source") && getIntent().hasExtra("destination")) {
            source = getIntent().getStringExtra("source");
            destination = getIntent().getStringExtra("destination");
            fetchAvailableTrains();
        } else {
            trainDetails.setText("Invalid Source or Destination");
        }
    }

    private void fetchAvailableTrains() {
        AsyncHttpClient client = new DefaultAsyncHttpClient();

        String url = "https://irctc1.p.rapidapi.com/api/v1/getTrainScheduleV2?trainNo=12936";

        client.prepare("GET", url)
                .setHeader("x-rapidapi-key", "b47dddbf4amshb91ca5a3b98edeep1c37a8jsn864f059597ad")
                .setHeader("x-rapidapi-host", "irctc1.p.rapidapi.com")
                .execute()
                .toCompletableFuture()
                .thenApply(Response::getResponseBody)
                .thenAccept(responseBody -> {
                    runOnUiThread(() -> parseAndDisplayTrains(responseBody));
                })
                .exceptionally(e -> {
                    runOnUiThread(() -> trainDetails.setText("Error fetching train details"));
                    e.printStackTrace();
                    return null;
                });

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void parseAndDisplayTrains(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray trainsArray = jsonResponse.getJSONArray("trains");

            StringBuilder trainInfo = new StringBuilder();
            for (int i = 0; i < trainsArray.length(); i++) {
                JSONObject train = trainsArray.getJSONObject(i);
                String trainName = train.getString("trainName");
                String trainNo = train.getString("trainNo");
                String departureTime = train.getString("departureTime");

                trainInfo.append("Train Name: ").append(trainName)
                        .append("\nTrain No: ").append(trainNo)
                        .append("\nDeparture: ").append(departureTime)
                        .append("\n-------------------\n");
            }

            trainDetails.setText(trainInfo.toString());

        } catch (Exception e) {
            trainDetails.setText("Error parsing train details");
            e.printStackTrace();
        }
    }
}
