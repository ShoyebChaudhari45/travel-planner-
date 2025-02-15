package com.example.travel_panner_project;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TransportActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TransportAdapter adapter;
    private List<TransportOption> transportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        transportList = new ArrayList<>();
        adapter = new TransportAdapter(transportList);
        recyclerView.setAdapter(adapter);

        fetchTransportData();
    }

    private void fetchTransportData() {
        transportList.add(new TransportOption("Bus", "RedBus", "₹500", "6 hrs"));
        transportList.add(new TransportOption("Train", "Indian Railways", "₹350", "5 hrs"));
        transportList.add(new TransportOption("Flight", "IndiGo", "₹2500", "1 hr"));
        adapter.notifyDataSetChanged();
    }
}
