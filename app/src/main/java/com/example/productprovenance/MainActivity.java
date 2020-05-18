package com.example.productprovenance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
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
import com.example.productprovenance.api_requests.RequestController;
import com.example.productprovenance.api_requests.RequestQueueSingleton;
import com.example.productprovenance.api_requests.ResultListener;
import com.example.productprovenance.functional_fragments.AllProductsFragment;
import com.example.productprovenance.functional_fragments.CreateProductFragment;
import com.example.productprovenance.functional_fragments.CreateUserFragment;
import com.example.productprovenance.functional_fragments.LoginFragment;
import com.example.productprovenance.functional_fragments.ProductDetailsFragment;
import com.example.productprovenance.functional_fragments.QRNFCProductFragment;
import com.example.productprovenance.functional_fragments.TransferProductFragment;
import com.example.productprovenance.navigation.NavigationHost;
import com.example.productprovenance.nfc_functions.NFCWriter;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MainActivity extends AppCompatActivity implements NavigationHost, View.OnClickListener, ResultListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private static final String MainFragmentTAG = "MainFragment";
    private static final String LoginFragmentTAG = "LoginFragment";
    private static final String TransferProductFragmentTAG = "TransferProductFragment";
    private static final String CreateProductFragmentTAG = "CreateProductFragmentTAG";
    private static final String ViewQRWriteNFCProductFragmentTAG = "ViewQRWriteNFCProductFragmentTAG";
    private static final String AllProductsFragmentTAG = "AllProductsFragmentTAG";
    private SharedPreferences sharedPref;
    private Location currentLocation;
    private QRNFCProductFragment qrnfcProductFragment;
    private AllProductsFragment allProductsFragment;
    private TransferProductFragment transferProductFragment;
    private AlertDialog alertDialog;
    private NFCWriter nfcWriter;
    private View view;
    private NdefMessage message = null;
    Tag currentTag;
    private HashMap<String, String> params;


    private RequestQueueSingleton mQueue;
    private LocationRequest locationRequest;

    // called when activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = getSharedPreferences(Constants.userDataStore, Context.MODE_PRIVATE);

        fragmentManager = getSupportFragmentManager();
        nfcWriter = new NFCWriter(this);
        makeLocationUpdates();
        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .add(R.id.mainContainer, new MainFragment(), MainFragmentTAG)
                    .addToBackStack(MainFragmentTAG)
                    .commit();
        }
        mQueue = RequestQueueSingleton.getInstance(this);

        view = findViewById(R.id.mainContainer);

    }

    // navigate to login fragment
    public void goToLoginFragment() {
        fragmentManager
                .beginTransaction()
                .add(R.id.mainContainer, new LoginFragment(), LoginFragmentTAG)
                .addToBackStack(LoginFragmentTAG)
                .commit();
    }

    // make location requests within 2-8 s intervals
    // https://guides.codepath.com/android/Retrieving-Location-with-LocationServices-API
    protected void makeLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(8000);
        locationRequest.setFastestInterval(2000);
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

    // check location permissions have been agreed
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

    // check camera permission have been agreed
    public void checkCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.mainContainer),
                            "Camera permission is needed so we can scan products", Snackbar.LENGTH_LONG);
            snackbar.show();
            requestPermissions(new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_CAMERA_PERMISSION);
        }

    }

    // check internet permissions have been agreed
    public void checkInternetPermission() {
        if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            Snackbar snackbar = Snackbar
                    .make(view,
                            "Internet access is needed for the app to perform", Snackbar.LENGTH_LONG);
            snackbar.show();
            requestPermissions(new String[]{Manifest.permission.INTERNET}, Constants.REQUEST_INTERNET_PERMISSION);

        }
    }

    // function called once a user selects to accept/decline the rights for the app to make use of a feature that require permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == Constants.REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.mainContainer),
                                "Location access granted", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        } else if (requestCode == Constants.REQUEST_INTERNET_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.mainContainer),
                                "Internet access granted", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        } else if (requestCode == Constants.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.mainContainer),
                                "Camera access granted", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }

    // function that enables navigation from fragment to fragment
    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        if (fragment instanceof MainFragment) {
            Fragment fragment1 = fragmentManager.findFragmentByTag(MainFragmentTAG);
            if (fragment1 != null) {
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

    // creating a dialog that shows loading animation
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

    // hide the dialog created above
    public void dismissProgressDialog() {
        alertDialog.dismiss();
    }

    //endregion

    // go to a previous fragment
    @Override
    public void navigatePrevious() {
        onBackPressed();
    }

    // when the back button is pressed on android devices
    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
        } else {
            fragmentManager.popBackStack();
            super.onBackPressed();
        }

    }

    // once a started activity has ended and it comes back to main activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode > 1100 && requestCode < 1200) {
                Bundle bundle = data.getExtras();
                String qrData = bundle.getString(Constants.GET_SCANNED_QR_DATA);
                switch (requestCode) {
                    case Constants.QR_SCAN_ACTION:
                        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
                        Bundle fragmentData = new Bundle();
                        fragmentData.putString(Constants.GET_SCANNED_QR_DATA, qrData);
                        productDetailsFragment.setArguments(fragmentData);//F
                        navigateTo(productDetailsFragment, true);
                        break;
                    case Constants.QR_SCAN_SET_PRODUCT_SELLER:
                        CreateProductFragment fragment2 =
                                (CreateProductFragment) getSupportFragmentManager().findFragmentByTag(CreateProductFragmentTAG);
                        if (fragment2 != null) {
                            fragment2.updateSellerAccountAddress(qrData);
                        }
                        break;
                    case Constants.QR_FIND_HOLDER_ACTION:
                        setProgressDialog("Loading...");
                        RequestController.getInstance().makeRequest(this, Constants.GET_CONTRACT_OWNER + qrData, Constants.GET_REQUEST, Constants.GET_CONTRACT_OWNER_RESPONSE, null, this);
                        break;
                    case Constants.QR_FIND_SELLER_ACTION:
                        setProgressDialog("Loading...");
                        RequestController.getInstance().makeRequest(this, Constants.GET_CONTRACT_PRODUCT_SELLER + qrData, Constants.GET_REQUEST, Constants.GET_CONTRACT_PRODUCT_SELLER_RESPONSE, null, this);
                        break;
                    case Constants.QR_SEARCH_PRODUCT_ACTION:
                        RequestController.getInstance().makeRequest(this, Constants.GET_PRODUCT_DETAILS + "/?product=" + qrData, Constants.GET_REQUEST, Constants.GET_PRODUCT_DETAILS_RESPONSE, null, this);
                        break;
                    case Constants.QR_SCAN_TRANSFER_TO_ACCOUNT:
                        transferProductFragment =
                                (TransferProductFragment) getSupportFragmentManager().findFragmentByTag(TransferProductFragmentTAG);
                        if (transferProductFragment != null) {
                            transferProductFragment.updateTransferAccountText(qrData);
                        }
                        break;
                    case Constants.QR_SCAN_TRANSFER_PRODUCT:
                        TransferProductFragment fragment1 =
                                (TransferProductFragment) getSupportFragmentManager().findFragmentByTag(TransferProductFragmentTAG);
                        if (fragment1 != null) {
                            fragment1.updateTransferProductText(qrData);
                        }
                        break;
                    default:
                        // 2
                        break;
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == Constants.QR_SCAN_ACTION) {
                Toast.makeText(this, "You need to scan a QR code to find the product details", Toast.LENGTH_LONG).show();
            }
        }

    }

    //region android nfc writer code start
    @Override
    protected void onResume() {
        super.onResume();

        int result = nfcWriter.checkNFCWorking();
        if (result == Constants.NFC_ENABLED) {
            //nfcMger.enableDispatch();
            Intent nfcIntent = new Intent(this, getClass());
            nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, 0);
            IntentFilter[] intentFiltersArray = new IntentFilter[]{};
            String[][] techList = new String[][]{{android.nfc.tech.Ndef.class.getName()}, {android.nfc.tech.NdefFormatable.class.getName()}};
            NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techList);
        } else if (result == Constants.NFC_NOT_SUPPORTED) {
            Snackbar.make(view, "NFC not supported", Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(view, "NFC Not enabled", Snackbar.LENGTH_LONG).show();
        }
        checkInternetPermission();
        if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED && !Utils.hasInternetAccess(getApplicationContext())) {
            Snackbar snackbar = Snackbar
                    .make(view,
                            "An internet connection is required so you can connect to our services", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", v -> Log.i("Acknowledged internet", "dismissed snackbar"));
            ;
            snackbar.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        nfcWriter.disableDispatch();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // It is the time to write the tag
        currentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (message != null) {
            nfcWriter.writeTag(currentTag, message);
            dismissProgressDialog();
            Snackbar.make(view, "Tag written", Snackbar.LENGTH_LONG).show();

        } else {
            // Handle intent

        }
    }
    //endregion

    // request product provenance details
    public void fetchProductProvenance(String productId) {
        setProgressDialog("Loading...");
        params = new HashMap<String, String>();
        params.put("productId", String.valueOf(productId));
        RequestController.getInstance().makeRequest(this, Constants.GET_PRODUCT_DETAILS, Constants.POST_REQUEST, Constants.GET_PRODUCT_DETAILS_RESPONSE, params, this);
    }

    // listening to events from buttons throughout the application
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin:
                if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    checkInternetPermission();
                } else {
                    setProgressDialog("Loading");
                    LoginFragment fragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(LoginFragmentTAG);
                    if (fragment != null) {
                        params = new HashMap<String, String>();
                        params.put("username", fragment.getUsernameValue());
                        params.put("password", fragment.getPasswordValue());
                        params.put("nodeAddress", fragment.getNodeAddressValue());
                        RequestController.getInstance().makeRequest(this, Constants.POST_LOGIN_USER, Constants.POST_REQUEST, Constants.POST_LOGIN_USER_RESPONSE, params, this);
                    }
                }
                break;
            case R.id.buttonCreateUserMainMenu:
                if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    checkInternetPermission();
                } else {
                    navigateTo(new CreateUserFragment(), false);
                }
                break;
            case R.id.buttonShowAccountQRMainMenu:
                startActivity(new Intent(this, QRCodeGeneratorActivity.class));
                break;
            case R.id.buttonQRCodeScanMainMenu:
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    checkCameraPermission();
                } else if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    checkInternetPermission();
                } else {
                    startActivityForResult(new Intent(this, QRScanActivity.class), Constants.QR_SEARCH_PRODUCT_ACTION);
                }
                break;
            case R.id.buttonQRCodeScan:
                if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    checkInternetPermission();
                } else {
                    startActivityForResult(new Intent(this, QRScanActivity.class), Constants.QR_SCAN_ACTION);
                }
                break;
            case R.id.writeNfcTagButton:
                String productNFC1 = qrnfcProductFragment.getProductNFC();
                message = nfcWriter.createTextMessage(productNFC1);
                if (message != null) {
                    setProgressDialog("Tap phone on NFC tag");
                }
                break;
            case R.id.buttonProductCurrentHolder:
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    checkCameraPermission();
                } else {
                    startActivityForResult(new Intent(this, QRScanActivity.class), Constants.QR_FIND_HOLDER_ACTION);
                }
                break;
            case R.id.buttonProductSeller:
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    checkCameraPermission();
                } else {
                    startActivityForResult(new Intent(this, QRScanActivity.class), Constants.QR_FIND_SELLER_ACTION);
                }
                break;
            case R.id.buttonTransferProduct:
                setProgressDialog("Loading");
                if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    checkInternetPermission();
                } else if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    checkLocationPermission();
                } else if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    checkCameraPermission();
                } else {
                    params = new HashMap<String, String>();
                TextInputEditText inputTextProductAddress = view.findViewById(R.id.inputTextProductAddress);
                TextInputEditText inputTextTransferAccountAddress = view.findViewById(R.id.inputTextTransferAccountAddress);
                    params.put("productAddress", Objects.requireNonNull(inputTextProductAddress.getText()).toString());
                    params.put("destinationAddress", Objects.requireNonNull(inputTextTransferAccountAddress.getText()).toString());
                    params.put("latitude", String.valueOf(currentLocation.getLatitude()));
                    params.put("longitude", String.valueOf(currentLocation.getLongitude()));
                    RequestController.getInstance().makeRequest(this, Constants.POST_TRANSFER_PRODUCT,
                            Constants.POST_REQUEST, Constants.POST_TRANSFER_PRODUCT_RESPONSE, params, this);
                }
                break;
            case R.id.buttonAddProductMainMenu:
                if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    checkInternetPermission();
                } else if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    checkLocationPermission();
                } else {
                    fragmentManager
                            .beginTransaction()
                            .add(R.id.mainContainer, new CreateProductFragment(), CreateProductFragmentTAG)
                            .addToBackStack(CreateProductFragmentTAG)
                            .commit();
                }
                break;
            case R.id.buttonCreateProduct:
                if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    checkInternetPermission();
                } else {
                    setProgressDialog("Loading");
                    params = new HashMap<String, String>();
                    TextInputEditText inputTextSellerAccountAddress = view.findViewById(R.id.inputTextSellerAccountAddress);
                    TextInputEditText inputTextProductName = view.findViewById(R.id.inputTextProductName);
                    TextInputEditText inputTextProductLinkToMerch = view.findViewById(R.id.inputTextProductLinkToMerch);
                    params.put("productName", Objects.requireNonNull(inputTextProductName.getText()).toString());
                    String valAccountInput = Objects.requireNonNull(inputTextSellerAccountAddress.getText()).toString();
                    if (valAccountInput.startsWith("0x"))
                        params.put("sellerAddress", Objects.requireNonNull(inputTextSellerAccountAddress.getText()).toString());
                    else
                        params.put("sellerName", Objects.requireNonNull(inputTextSellerAccountAddress.getText()).toString());
                    if (currentLocation != null) {
                        params.put("latitude", String.valueOf(currentLocation.getLatitude()));
                        params.put("longitude", String.valueOf(currentLocation.getLongitude()));
                    }
                    params.put("linkToMerch", Objects.requireNonNull(inputTextProductLinkToMerch.getText()).toString());
                    RequestController.getInstance().makeRequest(this, Constants.POST_CREATE_PRODUCT_CONTRACT,
                            Constants.POST_REQUEST, Constants.POST_CREATE_PRODUCT_CONTRACT_RESPONSE, params, this);
                }

                break;
            case R.id.skipButton:
                startActivityForResult(new Intent(this, AccountsActivity.class), Constants.SKIP_LOGIN_ACTION);
                break;
            case R.id.menuItemProducts:
                if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    checkInternetPermission();
                } else {
                    setProgressDialog("Loading");
                    RequestController.getInstance().makeRequest(this, Constants.GET_ALL_PRODUCTS,
                            Constants.GET_REQUEST, Constants.GET_ALL_PRODUCTS_RESPONSE, null, this);
                }
                break;
            case R.id.buttonTransferProductMainMenu:
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    checkLocationPermission();
                } else if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    checkCameraPermission();
                } else {
                    fragmentManager
                            .beginTransaction()
                            .add(R.id.mainContainer, new TransferProductFragment(), TransferProductFragmentTAG)
                            .addToBackStack(TransferProductFragmentTAG)
                            .commit();
                }
                break;
            case R.id.buttonQRCodeScanProduct:
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    checkCameraPermission();
                } else {
                    startActivityForResult(new Intent(this, QRScanActivity.class), Constants.QR_SCAN_TRANSFER_PRODUCT);
                }
                break;
            case R.id.buttonQRCodeScanTransferAccount:
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    checkCameraPermission();
                } else {
                    startActivityForResult(new Intent(this, QRScanActivity.class), Constants.QR_SCAN_TRANSFER_TO_ACCOUNT);
                }
                break;
            case R.id.buttonQRCodeScanSellerAccount:
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    checkCameraPermission();
                } else {
                    startActivityForResult(new Intent(this, QRScanActivity.class), Constants.QR_SCAN_SET_PRODUCT_SELLER);
                }
                break;
            case R.id.menuItemCommercial:
                startActivity(new Intent(this, CommercialActivity.class));
                break;
            default:
                Toast.makeText(this, "Main Activity", Toast.LENGTH_SHORT).show();
        }
    }

    // listening to results received from network requests
    @Override
    public void onResult(int requestType, int requestCode, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError) {
        if (alertDialog != null && alertDialog.isShowing()) {
            dismissProgressDialog();
        }
        if (isSuccess) {
            try {
                JSONArray arr = null;
                switch (requestCode) {
                    case Constants.GET_TEST_RESPONSE:
                        Log.d("data fetched ", jsonObject.getString("message"));
                        break;
                    case Constants.GET_ACCOUNTS_RESPONSE:
                        arr = jsonObject.getJSONArray("accounts");
                        for (int i = 0; i < arr.length(); i++) {
                            Log.d("Value account" + i, arr.getString(i));
                        }
                        break;
                    case Constants.GET_CONTRACT_OWNER_RESPONSE:
                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Success!")
                                .setMessage("Current holder: " + jsonObject.getString("owner"))
                                .show();
                        break;
                    case Constants.GET_CONTRACT_PRODUCT_SELLER_RESPONSE:
                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Success!")
                                .setMessage("Product seller: " + jsonObject.getString("seller"))
                                .show();
                        break;
                    case Constants.GET_ALL_PRODUCTS_RESPONSE:
                        arr = jsonObject.getJSONArray("products");
                        String[] productIds = new String[arr.length()];
                        String[] productNames = new String[arr.length()];
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject product = arr.getJSONObject(i);
                            productIds[i] = product.getString("productId");
                            productNames[i] = product.getString("productName");
                        }
                        allProductsFragment = AllProductsFragment.newInstance(productNames, productIds);
                        fragmentManager
                                .beginTransaction()
                                .add(R.id.mainContainer, allProductsFragment, AllProductsFragmentTAG)
                                .addToBackStack(AllProductsFragmentTAG)
                                .commit();
                        break;
                    case Constants.GET_PRODUCT_DETAILS_RESPONSE:
                        arr = jsonObject.getJSONArray("transits");
                        JSONObject product = jsonObject.getJSONObject("product");
                        ProvenanceMapFragment provenanceMapFragment = ProvenanceMapFragment.newInstance(product, arr);
                        navigateTo(provenanceMapFragment, true);
                        break;
                    case Constants.GET_PRODUCT_STATE_RESPONSE:
                        Log.d("Product State", jsonObject.getString("state"));
                        break;
                    case Constants.GET_COMPANY_BRANCHES_RESPONSE:
                        arr = jsonObject.getJSONArray("branches");
                        for (int i = 0; i < arr.length(); i++) {
                            Log.d("Value branch " + i, arr.getString(i));
                        }
                        break;
                    case Constants.POST_CREATE_PRODUCT_CONTRACT_RESPONSE:
                        String productContractAddress = jsonObject.getString("productContractAddress"),
                                manufacturerAddress = jsonObject.getString("manufacturerAddress"),
                                productId = jsonObject.getString("productId"),
                                productQR = jsonObject.getString("productQR"),
                                productNFC = jsonObject.getString("productNFC"),
                                manufacturerName = jsonObject.getString("manufacturerName"),
                                productName = jsonObject.getString("productName"),
                                linkToMerch = jsonObject.getString("linkToMerch"),
                                dateAdded = jsonObject.getString("dateAdded");

                        qrnfcProductFragment = QRNFCProductFragment.newInstance(productName, productId, productQR, productNFC);
                        fragmentManager
                                .beginTransaction()
                                .add(R.id.mainContainer, qrnfcProductFragment, ViewQRWriteNFCProductFragmentTAG)
                                .addToBackStack(ViewQRWriteNFCProductFragmentTAG)
                                .commit();
                        break;
                    case Constants.POST_CREATE_USER_RESPONSE:
                        Log.d("User Name", jsonObject.getString("companyName"));
                        break;
                    case Constants.POST_LOGIN_USER_RESPONSE:
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(Constants.userToken, jsonObject.getString("token"));
                        JSONObject user = jsonObject.getJSONObject("user");
                        editor.putString(Constants.accountAddress, user.getString("companyAddress"));
                        editor.putString(Constants.accountName, user.getString("companyName"));
                        editor.putInt(Constants.userRole, user.getInt("role"));
                        editor.putBoolean(Constants.userDisabled, user.getBoolean("disabled"));
                        editor.commit();
                        MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragmentTAG);
                        if (fragment != null) {
                            fragment.checkLoginStatus();
                        }
                        navigatePrevious();
                        break;
                    case Constants.POST_TRANSFER_PRODUCT_RESPONSE:
                        JSONObject transit = jsonObject.getJSONObject("transit");
                        String text = "Product transferred to " + transit.getString("companyName");
                        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
                        transferProductFragment = (TransferProductFragment) getSupportFragmentManager().findFragmentByTag(TransferProductFragmentTAG);
                        if (transferProductFragment != null) {
                            transferProductFragment.updateTransferProductText("");
                        }
                        break;
                    default:
                        Log.d("OUT_OF_CASES ", "not sure why it got here");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
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
                    case Constants.GET_TEST_RESPONSE:
                        Log.d("data fetched ", msg);
                        break;
                    case Constants.GET_ACCOUNTS_RESPONSE:
                    case Constants.GET_CONTRACT_OWNER_RESPONSE:
                    case Constants.GET_ALL_PRODUCTS_RESPONSE:
                    case Constants.GET_PRODUCT_DETAILS_RESPONSE:
                    case Constants.GET_PRODUCT_STATE_RESPONSE:
                    case Constants.GET_COMPANY_BRANCHES_RESPONSE:
                    case Constants.POST_CREATE_USER_RESPONSE:
                    case Constants.POST_CREATE_PRODUCT_CONTRACT_RESPONSE:
                    case Constants.GET_CONTRACT_PRODUCT_SELLER_RESPONSE:
                    case Constants.POST_TRANSFER_PRODUCT_RESPONSE:
                        if (msg.trim().equals("jwt expired")) {
                            msg = "Please login again";
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.remove(Constants.accountAddress);
                            editor.remove(Constants.accountName);
                            editor.remove(Constants.userRole);
                            editor.remove(Constants.userToken);
                            editor.remove(Constants.userDisabled);
                            editor.commit();
                            MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragmentTAG);
                            if (fragment != null) {
                                fragment.checkLoginStatus();
                            }
                        }
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
                    case Constants.POST_LOGIN_USER_RESPONSE:
                        LoginFragment fragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(LoginFragmentTAG);
                        if (fragment != null) {
                            fragment.showInvalidLogin(msg);
                        }
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
