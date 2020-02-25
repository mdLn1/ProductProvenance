package com.example.productprovenance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity implements NavigationHost, View.OnClickListener {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private static final String MainFragmentTAG = "MainFragment";
    private static final String LoginFragmentTag = "LoginFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager=getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .add(R.id.mainContainer, new MainFragment(), MainFragmentTAG)
                    .addToBackStack(MainFragmentTAG)
                    .commit();
        }
    }
    public void onTabSelected(int position) {
        // Pop off everything up to and including the current tab
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(MainFragmentTAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // Add the new tab fragment
        fragmentManager.beginTransaction()
                .replace(R.id.container, new LoginFragment())
                .addToBackStack(MainFragmentTAG)
                .commit();
    }

    /**
     * Add a fragment on top of the current tab
     */

    public void addFragmentOnTop(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        if(fragment instanceof MainFragment) {
                Fragment fragment1 = fragmentManager.findFragmentByTag(MainFragmentTAG);
                if(fragment1 != null) {
                    fragmentManager.popBackStack(MainFragmentTAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
        }
            fragmentTransaction =
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.mainContainer, fragment);


            fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }

    @Override
    public void navigatePrevious() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if(fragmentManager.getBackStackEntryCount() > 1){
            fragmentManager.popBackStack();
        }else{
            fragmentManager.popBackStack();
            super.onBackPressed();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == Constants.QR_SCAN_ACTION) {
                String qrData = data.getStringExtra(Constants.GET_SCANNED_QR_DATA);
                ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
                Bundle fragmentData = new Bundle();
                fragmentData.putString(Constants.GET_SCANNED_QR_DATA, qrData);
                productDetailsFragment.setArguments(fragmentData);//F
                navigateTo(productDetailsFragment, true);
            }
        } else if(resultCode == RESULT_CANCELED) {
            if (requestCode == Constants.QR_SCAN_ACTION) {
                Toast.makeText(this, "You need to scan a QR code to find the product details", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menuItemProducts:
                Toast.makeText(this, "My Products", Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonLogout:
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Confirm scan")
                        .setMessage("Scanned Successfully")
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "Main Clicking", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNeutralButton("Scan Again", null)
                        .show();
                break;
            case R.id.menuItemCommercial:
                startActivity(new Intent(this, CommercialActivity.class));
                break;
            case R.id.buttonQRCodeScan:
                // TODO: remember to edit this replace SimpleTestActivity with QRScan
                startActivityForResult(new Intent(this, SimpleTestActivity.class), Constants.QR_SCAN_ACTION);
                break;
            default:
                Toast.makeText(this, "Main Activity", Toast.LENGTH_SHORT).show();
        }
    }
}
