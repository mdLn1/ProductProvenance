package com.example.productprovenance;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private Button buttonQRScanMainMenu ,
            buttonLogout ,
            menuItemLogin ,
            menuItemLogout ,
            menuItemCommercial ,
            menuItemProducts ,
            buttonNext,
            buttonCreateUserMainMenu,
            buttonAddProductMainMenu,
            buttonTransferProductMainMenu,
            buttonShowAccountQRMainMenu;
    private SharedPreferences sharedPref;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        view = inflater.inflate(R.layout.main_fragment, container, false);
        sharedPref = this.getActivity().getSharedPreferences(Constants.userDataStore, Context.MODE_PRIVATE);

        // Set up the tool bar
        setUpToolbar(view);
        parentActivity = (MainActivity)getActivity();
        // Set cut corner background for API 23+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.nested_grid)
                    .setBackgroundResource(R.drawable.grid_shape_background);
        }

        buttonQRScanMainMenu = view.findViewById(R.id.buttonQRCodeScanMainMenu);
                buttonLogout = view.findViewById(R.id.buttonLogout);
                menuItemLogin = view.findViewById(R.id.menuItemLogin);
                menuItemLogout = view.findViewById(R.id.menuItemLogout);
                menuItemCommercial = view.findViewById(R.id.menuItemCommercial);
                menuItemProducts = view.findViewById(R.id.menuItemProducts);
                buttonNext  = view.findViewById(R.id.buttonNext);
        buttonAddProductMainMenu = view.findViewById(R.id.buttonAddProductMainMenu);
        buttonShowAccountQRMainMenu = view.findViewById(R.id.buttonShowAccountQRMainMenu);
        buttonCreateUserMainMenu = view.findViewById(R.id.buttonCreateUserMainMenu);
        buttonTransferProductMainMenu = view.findViewById(R.id.buttonTransferProductMainMenu);

        checkLoginStatus();

        buttonQRScanMainMenu.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
        menuItemLogin.setOnClickListener(this);
        menuItemLogout.setOnClickListener(this);
        menuItemCommercial.setOnClickListener(this);
        menuItemProducts.setOnClickListener(this);
        buttonAddProductMainMenu.setOnClickListener(this);
        buttonCreateUserMainMenu.setOnClickListener(this);
        buttonShowAccountQRMainMenu.setOnClickListener(this);
        buttonTransferProductMainMenu.setOnClickListener(this);

        return view;
    }

    protected void checkLoginStatus() {
        if(sharedPref.contains(Constants.accountAddress)) {
            menuItemLogin.setVisibility(View.GONE);
            menuItemLogout.setVisibility(View.VISIBLE);
            menuItemCommercial.setVisibility(View.GONE);
            String account = sharedPref.getString(Constants.accountName, "unknown");
            if(account.trim().equals("ADMIN")) {
                buttonShowAccountQRMainMenu.setVisibility(View.GONE);
                buttonAddProductMainMenu.setVisibility(View.GONE);
                buttonCreateUserMainMenu.setVisibility(View.VISIBLE);
                buttonTransferProductMainMenu.setVisibility(View.GONE);
            } else {
                buttonCreateUserMainMenu.setVisibility(View.GONE);
                buttonShowAccountQRMainMenu.setVisibility(View.VISIBLE);
                buttonTransferProductMainMenu.setVisibility(View.VISIBLE);
                if(account.trim().startsWith("MANUFACTURER")) {
                    buttonAddProductMainMenu.setVisibility(View.VISIBLE);
                } else if(account.trim().startsWith("SELLER")) {
                    menuItemCommercial.setVisibility(View.VISIBLE);
                }
            }
        } else {
            Toolbar toolbar = view.findViewById(R.id.app_bar);
            toolbar.setTitle(R.string.app_name);
            menuItemLogin.setVisibility(View.VISIBLE);
            menuItemLogout.setVisibility(View.GONE);
            buttonShowAccountQRMainMenu.setVisibility(View.GONE);
            buttonAddProductMainMenu.setVisibility(View.GONE);
            buttonCreateUserMainMenu.setVisibility(View.GONE);
            buttonTransferProductMainMenu.setVisibility(View.GONE);
        }
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
        if(sharedPref.contains(Constants.accountName))
        {
            toolbar.setTitle(sharedPref.getString(Constants.accountName, "Product Provenance"));
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
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove(Constants.accountAddress);
                editor.remove(Constants.accountName);
                editor.commit();
                checkLoginStatus();
                break;
            default:
                parentActivity.onClick(v);
        }
    }


}
