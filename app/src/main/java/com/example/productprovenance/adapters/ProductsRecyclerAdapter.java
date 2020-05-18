package com.example.productprovenance.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.productprovenance.classes.Product;
import com.example.productprovenance.R;

import java.util.ArrayList;
import java.util.List;

public class ProductsRecyclerAdapter extends RecyclerView.Adapter<ProductsRecyclerAdapter.ProductViewHolder> {
    private List<Product> products;

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        public TextView productNameTextView, productIdTextView;
        public ProductViewHolder(View v) {
            super(v);
            productIdTextView = (TextView) v.findViewById(R.id.productIdTextView);
            productNameTextView = (TextView) v.findViewById(R.id.productNameTextView);
        }
    }

    public ProductsRecyclerAdapter(List<Product> products) {
        this.products = products;
    }

    public ProductsRecyclerAdapter(String[] productNames, String[] productIds) {
        List<Product> productList = new ArrayList<>();
        for(int i = 0; i < productIds.length; i++){
            productList.add(new Product(productIds[i], productNames[i]));
        }
        this.products = productList;
    }

    @Override
    public ProductsRecyclerAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent,
                                                                        int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_display_layout, parent, false);

        ProductViewHolder vh = new ProductViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productIdTextView.setText(product.getProductId());
        holder.productNameTextView.setText(product.getProductName());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
