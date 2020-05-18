package com.example.productprovenance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


// https://stackoverflow.com/questions/26174527/android-mapview-in-fragment

public class ProvenanceMapFragment  extends Fragment implements OnMapReadyCallback{
    SupportMapFragment mMapView;
    GoogleMap mGoogleMap;
    private String productDetails;
    private String transitParties;
    private MainActivity parentActivity;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<TransitParty> parties;

    public static ProvenanceMapFragment newInstance(JSONObject productDetails, JSONArray transitParties) {
        ProvenanceMapFragment myFragment = new ProvenanceMapFragment();

        Bundle args = new Bundle();
        args.putString("productDetails", productDetails.toString());
        args.putString("transitParties", transitParties.toString());
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.provenance_map_fragment, container, false);
        parentActivity = (MainActivity) getActivity();
        TextView textViewOriginProductName = v.findViewById(R.id.textViewOriginProductName);
        TextView textViewOriginManufacturerName = v.findViewById(R.id.textViewOriginManufacturerName);
        TextView textViewOriginProductWebsite = v.findViewById(R.id.textViewOriginProductWebsite);
        recyclerView = v.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(parentActivity.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        if(getArguments() != null){
            JSONObject productIds = null;
            try {
                JSONObject product = new JSONObject(getArguments().getString("productDetails"));
                String productName = product.getString("productName");
                String manufacturerName = product.getString("manufacturerName");
                String linkToMerch = product.getString("linkToMerch");
                textViewOriginProductWebsite.setText(linkToMerch);
                textViewOriginProductName.setText(productName);
                textViewOriginManufacturerName.setText(manufacturerName);
                JSONArray partiesJSON = new JSONArray(getArguments().getString("transitParties"));
                parties = new ArrayList<>();
                for (int i = 0; i < partiesJSON.length(); i++) {
                    parties.add(Utils.deserializeTransitParty(partiesJSON.getString(i)));
                }
                mAdapter = new TransitPartiesRecyclerAdapter(parties);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mMapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_provenance);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync((OnMapReadyCallback) this); //this is important

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        float[] colors = new float[]{
                BitmapDescriptorFactory.HUE_AZURE,
                BitmapDescriptorFactory.HUE_GREEN,
                BitmapDescriptorFactory.HUE_VIOLET,
                BitmapDescriptorFactory.HUE_RED
        };
        for(int i = 0; i < parties.size(); i++) {
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(parties.get(i).latitudeLocation),
                            Double.parseDouble(parties.get(i).longitudeLocation))).title(parties.get(i).companyName)
            .icon(BitmapDescriptorFactory.defaultMarker(colors[i % 4])));
        }
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(parties.get(0).latitudeLocation),
                Double.parseDouble(parties.get(0).longitudeLocation)), 2));
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
