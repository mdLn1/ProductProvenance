package com.example.productprovenance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.productprovenance.adapters.CommercialStateAdapter;
import com.example.productprovenance.api_requests.RequestController;
import com.example.productprovenance.api_requests.RequestQueueSingleton;
import com.example.productprovenance.api_requests.ResultListener;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class CommercialActivity extends AppCompatActivity implements View.OnClickListener, ResultListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private String[] titles = new String[]{"Sell", "Resell", "Return"};
    private ViewPager2 viewPager;
    private HashMap<String, String> params;
    private RequestQueueSingleton mQueue;
    private LocationRequest locationRequest;
    private CommercialStateAdapter commercialActivityAdapter;
    private Location currentLocation;
    private AlertDialog alertDialog;

    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commercial);
        viewPager = findViewById(R.id.viewPagerCommercial);
        TabLayout tabLayout = findViewById(R.id.tabLayCommercial);
        commercialActivityAdapter = new CommercialStateAdapter(this);
        viewPager.setAdapter(commercialActivityAdapter);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles[position]);
            }
        }).attach();
        makeLocationUpdates();
        view = findViewById(R.id.mainContainer);
        mQueue = RequestQueueSingleton.getInstance(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkInternetPermission();
        checkLocationPermission();
        if (checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED && !Utils.hasInternetAccess(getApplicationContext())) {
            Snackbar snackbar = Snackbar
                    .make(view,
                            "An internet connection is required so you can connect to our services", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", v -> Log.i("Acknowledged internet", "dismissed snackbar"));
            ;
            snackbar.show();
        }
    }

    // https://guides.codepath.com/android/Retrieving-Location-with-LocationServices-API
    protected void makeLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        currentLocation = locationResult.getLastLocation();
                    }
                },
                Looper.myLooper());
    }

    public void checkLocationPermission() {
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            Toast.makeText(getApplicationContext(), "permissions should be reviewed for android 10 and above", Toast.LENGTH_LONG).show();
        } else if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.mainContainer),
                            "Location permission is needed so we can check your authorization", Snackbar.LENGTH_LONG);
            snackbar.show();
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_LOCATION_PERMISSION);
        }

    }

    public void checkInternetPermission() {
        if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.mainContainer),
                                "Internet access is needed for the app to perform", Snackbar.LENGTH_LONG);
                snackbar.show();
                requestPermissions(new String[]{Manifest.permission.INTERNET}, Constants.REQUEST_INTERNET_PERMISSION);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.NFC_SCAN_ACTION) {
                String nfcData = data.getStringExtra(Constants.GET_SCANNED_NFC_DATA);
                commercialActivityAdapter.getCurrentFragment(viewPager.getCurrentItem()).onNFCScan(nfcData);
            } else if (requestCode == Constants.QR_SCAN_ACTION) {
                String qrData = data.getStringExtra(Constants.GET_SCANNED_QR_DATA);
                commercialActivityAdapter.getCurrentFragment(viewPager.getCurrentItem()).onQRScan(qrData);
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == Constants.NFC_SCAN_ACTION) {
                Toast.makeText(this, "You need to scan an nfc tag to proceed further", Toast.LENGTH_LONG).show();
            } else if (requestCode == Constants.QR_SCAN_ACTION) {
                Toast.makeText(this, "You need to scan a QR code to proceed further", Toast.LENGTH_LONG).show();
            }
        }

    }

    //region Kishan Donga https://stackoverflow.com/questions/45373007/progressdialog-is-deprecated-what-is-the-alternate-one-to-use
    public void setProgressDialog(String message) {
        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText(message);
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(ll);

        alertDialog = builder.create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            alertDialog.getWindow().setAttributes(layoutParams);
        }
    }

    public void dismissProgressDialog() {
        alertDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonQRCodeScan:
                startActivityForResult(new Intent(this, QRScanActivity.class), Constants.QR_SCAN_ACTION);
                break;
            case R.id.buttonNFCTagScan:
                startActivityForResult(new Intent(this, NFCScanActivity.class), Constants.NFC_SCAN_ACTION);
                break;
            case R.id.buttonSaleCheckout:
                setProgressDialog("Loading...");
                params = new HashMap<String, String>();
                String[] el = commercialActivityAdapter.getCurrentFragment(viewPager.getCurrentItem()).getInputs();
                params.put("buyer", commercialActivityAdapter.getCurrentFragment(viewPager.getCurrentItem()).getInputs()[0]);
                String sellNFC = commercialActivityAdapter.getCurrentFragment(viewPager.getCurrentItem()).getNfcCode();
                String sellQR = commercialActivityAdapter.getCurrentFragment(viewPager.getCurrentItem()).getQRCode();
                params.put("productNFC", sellNFC);
                params.put("productQR", sellQR);
                RequestController.getInstance().makeRequest(this, Constants.POST_SELL_PRODUCT, Constants.POST_REQUEST, Constants.POST_SELL_PRODUCT_RESPONSE, params, this);
                break;
            case R.id.buttonReturnProduct:
                setProgressDialog("Loading...");
                params = new HashMap<String, String>();
                String[] el1 = commercialActivityAdapter.getCurrentFragment(viewPager.getCurrentItem()).getInputs();
                String returnNFC = commercialActivityAdapter.getCurrentFragment(viewPager.getCurrentItem()).getNfcCode();
                String returnQR = commercialActivityAdapter.getCurrentFragment(viewPager.getCurrentItem()).getQRCode();
                params.put("buyer", commercialActivityAdapter.getCurrentFragment(viewPager.getCurrentItem()).getInputs()[0]);
                params.put("productNFC", returnNFC);
                params.put("productQR", returnQR);
                RequestController.getInstance().makeRequest(this, Constants.POST_RETURN_PRODUCT, Constants.POST_REQUEST, Constants.POST_RETURN_PRODUCT_RESPONSE, params, this);
                break;
            case R.id.buttonResellProduct:
                setProgressDialog("Loading...");
                params = new HashMap<String, String>();
                String[] el2 = commercialActivityAdapter.getCurrentFragment(viewPager.getCurrentItem()).getInputs();
                String resellNFC = commercialActivityAdapter.getCurrentFragment(viewPager.getCurrentItem()).getNfcCode();
                String resellQR = commercialActivityAdapter.getCurrentFragment(viewPager.getCurrentItem()).getQRCode();
                params.put("buyer", commercialActivityAdapter.getCurrentFragment(viewPager.getCurrentItem()).getInputs()[0]);
                params.put("newBuyer", commercialActivityAdapter.getCurrentFragment(viewPager.getCurrentItem()).getInputs()[1]);
                params.put("productNFC", resellNFC);
                params.put("productQR", resellQR);
                RequestController.getInstance().makeRequest(this, Constants.POST_RESELL_PRODUCT, Constants.POST_REQUEST, Constants.POST_RESELL_PRODUCT_RESPONSE, params, this);

                break;
            default:
                Toast.makeText(this, "blafsasf", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResult(int requestType, int requestCode, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError) {
        if (alertDialog != null && alertDialog.isShowing()) {
            dismissProgressDialog();
        }
        if (isSuccess) {
            try {
                switch (requestCode) {
                    case Constants.POST_RESELL_PRODUCT_RESPONSE:
                    case Constants.POST_SELL_PRODUCT_RESPONSE:
                    case Constants.POST_RETURN_PRODUCT_RESPONSE:
                        String message = jsonObject.getString("message");
                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Success!")
                                .setMessage(message)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d("okish", "no biggie");
                                    }
                                })
                                .show();
                        commercialActivityAdapter.getCurrentFragment(viewPager.getCurrentItem()).clearInputs();
                        break;
                    default:
                        Log.d("OUT_OF_SUCCESS+CASES ", "not sure why it got here");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                byte[] arr = volleyError.networkResponse.data;
                String hs = new String(arr);
                JSONObject json = new JSONObject(hs);
                JSONArray array = json.getJSONArray("errors");
                String msg = "";
                for (int i = 0; i < array.length(); i++) {
                    msg += array.getString(i) + "\n";
                }
                switch (requestCode) {
                    case Constants.POST_RESELL_PRODUCT_RESPONSE:
                    case Constants.POST_SELL_PRODUCT_RESPONSE:
                    case Constants.POST_RETURN_PRODUCT_RESPONSE:
                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Errors")
                                .setMessage(msg)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d("okish", "no biggie");
                                    }
                                })
                                .show();
                        break;
                    default:
                        Log.d("OUT_OF_CASES ", "not sure why it got here");
                }
            } catch (JSONException e) {
                Toast.makeText(this, "Invalid response errors", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
