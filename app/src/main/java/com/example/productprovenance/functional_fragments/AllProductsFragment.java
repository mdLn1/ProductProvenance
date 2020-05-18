package com.example.productprovenance.functional_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productprovenance.MainActivity;
import com.example.productprovenance.adapters.ProductsRecyclerAdapter;
import com.example.productprovenance.R;
import com.example.productprovenance.RecyclerTouchListener;

public class AllProductsFragment  extends Fragment {

    private MainActivity parentActivity;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private String[] productNames;
    private String[] productIds;

    public static AllProductsFragment newInstance(String[] productNames, String[] productIds) {
        AllProductsFragment myFragment = new AllProductsFragment();

        Bundle args = new Bundle();
        args.putStringArray("productNames", productNames);
        args.putStringArray("productIds", productIds);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_products_fragment, container, false);
        parentActivity = (MainActivity) getActivity();

        if (getArguments() != null) {
            productIds = getArguments().getStringArray("productIds");
            productNames = getArguments().getStringArray("productNames");
        }
        recyclerView = view.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(parentActivity.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ProductsRecyclerAdapter(productNames, productIds);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(parentActivity.getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                parentActivity.fetchProductProvenance(productIds[position]);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recyclerView.setAdapter(mAdapter);

        return view;
    }
}
