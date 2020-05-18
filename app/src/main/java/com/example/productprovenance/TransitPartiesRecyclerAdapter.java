package com.example.productprovenance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TransitPartiesRecyclerAdapter extends RecyclerView.Adapter<TransitPartiesRecyclerAdapter.TransitPartyViewHolder> {
    private List<TransitParty> transitParties;

    public class TransitPartyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTransitPartyDateReceived, textViewTransitParty;
        public TransitPartyViewHolder(View v) {
            super(v);
            textViewTransitPartyDateReceived = (TextView) v.findViewById(R.id.textViewTransitPartyDateReceived);
            textViewTransitParty = (TextView) v.findViewById(R.id.textViewTransitParty);
        }
    }

    public TransitPartiesRecyclerAdapter(List<TransitParty> transitParties) {
        this.transitParties = transitParties;
    }

    @Override
    public TransitPartiesRecyclerAdapter.TransitPartyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                        int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transit_party_layout, parent, false);

        TransitPartiesRecyclerAdapter.TransitPartyViewHolder vh = new TransitPartiesRecyclerAdapter.TransitPartyViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(TransitPartiesRecyclerAdapter.TransitPartyViewHolder holder, int position) {
        TransitParty transitParty = transitParties.get(position);
            holder.textViewTransitPartyDateReceived.setText(transitParty.dateTransferred);
            holder.textViewTransitParty.setText(transitParty.companyName);
    }

    @Override
    public int getItemCount() {
        return transitParties.size();
    }
}
