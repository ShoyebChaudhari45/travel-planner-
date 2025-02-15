package com.example.travel_panner_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransportAdapter extends RecyclerView.Adapter<TransportAdapter.ViewHolder> {
    private List<TransportOption> transportList;

    public TransportAdapter(List<TransportOption> transportList) {
        this.transportList = transportList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transport, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransportOption transport = transportList.get(position);
        holder.type.setText(transport.getType());
        holder.provider.setText(transport.getProvider());
        holder.fare.setText("Fare: " + transport.getFare());
        holder.duration.setText("Duration: " + transport.getDuration());
    }

    @Override
    public int getItemCount() {
        return transportList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView type, provider, fare, duration;

        public ViewHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.transportType);
            provider = itemView.findViewById(R.id.transportProvider);
            fare = itemView.findViewById(R.id.transportFare);
            duration = itemView.findViewById(R.id.transportDuration);
        }
    }
}
