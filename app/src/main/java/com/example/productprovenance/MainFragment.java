package com.example.productprovenance;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment implements View.OnClickListener{
    private MainActivity parentActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        // Set up the tool bar
        setUpToolbar(view);
        parentActivity = (MainActivity)getActivity();
        // Set cut corner background for API 23+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.nested_grid)
                    .setBackgroundResource(R.drawable.grid_shape_background);
        }

        Button buttonQRScan = view.findViewById(R.id.buttonQRCodeScan),
                buttonLogout = view.findViewById(R.id.buttonLogout),
                menuItemLogin = view.findViewById(R.id.menuItemLogin),
                menuItemLogout = view.findViewById(R.id.menuItemLogout),
                menuItemCommercial = view.findViewById(R.id.menuItemCommercial),
                menuItemProducts = view.findViewById(R.id.menuItemProducts),
                buttonNext  = view.findViewById(R.id.buttonNext);


        buttonQRScan.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
        menuItemLogin.setOnClickListener(this);
        menuItemLogout.setOnClickListener(this);
        menuItemCommercial.setOnClickListener(this);
        menuItemProducts.setOnClickListener(this);
        buttonNext.setOnClickListener(this);

        return view;
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        toolbar.setNavigationOnClickListener(new NavigationIconOnClickListener(
                getContext(),
                view.findViewById(R.id.nested_grid),
                new AccelerateDecelerateInterpolator(),
                ContextCompat.getDrawable(getActivity(), R.drawable.menu_icon),
                ContextCompat.getDrawable(getActivity(), R.drawable.close_icon)));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menuItemLogin:
                ((NavigationHost) getActivity()).navigateTo(new LoginFragment(), true);
                break;
            case R.id.menuItemLogout:
                Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();
                break;
            default:
                parentActivity.onClick(v);
        }
    }


}
