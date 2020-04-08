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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements NavigationHost, View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private static final String MainFragmentTAG = "MainFragment";
    private static final String LoginFragmentTAG = "LoginFragment";
    private static final String TransferProductFragmentTAG = "TransferProductFragment";
    private static final String CreateProductFragmentTAG = "CreateProductFragmentTAG";
    private static final String ViewQRWriteNFCProductFragmentTAG = "ViewQRWriteNFCProductFragmentTAG";
    private SharedPreferences sharedPref;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private QRNFCProductFragment qrnfcProductFragment;
    private AlertDialog nfcDialog;
    private NFCWriter nfcWriter;
    private View view;
    private NdefMessage message = null;
    Tag currentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = getSharedPreferences(Constants.userDataStore, Context.MODE_PRIVATE);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fragmentManager = getSupportFragmentManager();
        nfcWriter = new NFCWriter(this);

        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .add(R.id.mainContainer, new MainFragment(), MainFragmentTAG)
                    .addToBackStack(MainFragmentTAG)
                    .commit();
        }
        checkInternetPermission();
        checkLocationPermission();

        view = findViewById(R.id.mainContainer);
        if(checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED && !Utils.hasInternetAccess(getApplicationContext())){
            Snackbar snackbar = Snackbar
                    .make(view,
                            "An internet connection is required so you can connect to our services", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("Acknowledged internet", "dismissed snackbar");
                        }
                    });;
            snackbar.show();
        }
    }

    public void checkLocationPermission() {
        getLocation();
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            Toast.makeText(getApplicationContext(), "permissions should be reviewed for android 10 and above", Toast.LENGTH_LONG).show();
        } else {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                while (currentLocation == null) {
                    getLocation();
                }
                new MaterialAlertDialogBuilder(getApplicationContext())
                        .setTitle("Successfully received location")
                        .setMessage("Latitude: " + currentLocation.getLatitude() + " Longitude: " + currentLocation.getLongitude())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("okish", "no biggie");
                            }
                        })
                        .show();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.mainContainer),
                                    "Location permission is needed so we can check your authorization", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_LOCATION_PERMISSION);
                }
            }
        }

    }

    public void checkInternetPermission() {
        if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar snackbar = Snackbar
                        .make(view,
                                "Internet access is needed for the app to perform", Snackbar.LENGTH_LONG);
                snackbar.show();
                requestPermissions(new String[]{Manifest.permission.INTERNET}, Constants.REQUEST_INTERNET_PERMISSION);
            }
        }
    }

    public void getLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = location;
                        } else {
                            currentLocation = null;
                        }
                    }
                });
    }


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
        }
    }

    public void addFragmentOnTop(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }


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

    //region Kishan Donga https://stackoverflow.com/questions/45373007/progressdialog-is-deprecated-what-is-the-alternate-one-to-use
    public void setProgressDialog(String message) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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

        nfcDialog = builder.create();
        nfcDialog.show();
        Window window = nfcDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(nfcDialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            nfcDialog.getWindow().setAttributes(layoutParams);
        }
    }

    public void dismissProgressDialog(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        nfcDialog.dismiss();
    }

    //endregion

    @Override
    public void navigatePrevious() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
        } else {
            fragmentManager.popBackStack();
            super.onBackPressed();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode > 1100 && requestCode < 1200) {
                String qrData = data.getStringExtra(Constants.GET_SCANNED_QR_DATA);
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
                    case Constants.QR_SCAN_TRANSFER_TO_ACCOUNT:
                        TransferProductFragment fragment =
                                (TransferProductFragment) getSupportFragmentManager().findFragmentByTag(TransferProductFragmentTAG);
                        if (fragment != null) {
                            fragment.updateTransferAccountText(qrData);
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
            } else if (requestCode == Constants.SKIP_LOGIN_ACTION) {
                String chosenAccount = data.getStringExtra(Constants.GET_SELECTED_ACCOUNT_DATA);
                if (Constants.accounts.containsKey(chosenAccount)) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(Constants.accountAddress, Constants.accounts.get(chosenAccount));
                    editor.putString(Constants.accountName, chosenAccount);
                    editor.commit();
                    MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragmentTAG);
                    if (fragment != null) {
                        fragment.checkLoginStatus();
                    }
                } else {
                    Toast.makeText(this, "Invalid Selection", Toast.LENGTH_LONG).show();

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
            if(result == Constants.NFC_ENABLED)
{
    //nfcMger.enableDispatch();
            Intent nfcIntent = new Intent(this, getClass());
            nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, 0);
            IntentFilter[] intentFiltersArray = new IntentFilter[] {};
            String[][] techList = new String[][] { { android.nfc.tech.Ndef.class.getName() }, { android.nfc.tech.NdefFormatable.class.getName() } };
            NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techList);
        }
        else if(result == Constants.NFC_NOT_SUPPORTED) {
            Snackbar.make(view, "NFC not supported", Snackbar.LENGTH_LONG).show();
        }
        else {
            Snackbar.make(view, "NFC Not enabled", Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcWriter.disableDispatch();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCreateUserMainMenu:
                navigateTo(new CreateUserFragment(), false);
                break;
            case R.id.buttonShowAccountQRMainMenu:
                startActivity(new Intent(this, QRCodeGeneratorActivity.class));
                break;
            case R.id.buttonQRCodeScanMainMenu:
            case R.id.buttonQRCodeScan:
                // TODO: remember to edit this replace SimpleTestActivity with QRScan
                startActivityForResult(new Intent(this, QRScanActivity.class), Constants.QR_SCAN_ACTION);
                break;
            case R.id.writeNfcTagButton:
                String productName1 = qrnfcProductFragment.getProductName();
                String productId1= qrnfcProductFragment.getProductId();
                message =  nfcWriter.createTextMessage(productName1);
                if (message != null) {
                    setProgressDialog("Tap phone on NFC tag");
                }
                break;
            case R.id.buttonAddProductMainMenu:
                fragmentManager
                        .beginTransaction()
                        .add(R.id.mainContainer, new CreateProductFragment(), CreateProductFragmentTAG)
                        .addToBackStack(CreateProductFragmentTAG)
                        .commit();
                break;
            case R.id.buttonCreateProduct:
                // after creating product with API request
                String productName = "get product name";
                String productId = "get product id";

                qrnfcProductFragment = QRNFCProductFragment.newInstance(productName, productId);
                fragmentManager
                        .beginTransaction()
                        .add(R.id.mainContainer, qrnfcProductFragment, ViewQRWriteNFCProductFragmentTAG)
                        .addToBackStack(ViewQRWriteNFCProductFragmentTAG)
                        .commit();
                break;
            case R.id.skipButton:
                startActivityForResult(new Intent(this, AccountsActivity.class), Constants.SKIP_LOGIN_ACTION);
                break;
            case R.id.menuItemProducts:
                Toast.makeText(this, "My Products", Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonTransferProductMainMenu:
                if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    checkLocationPermission();
                }
                else {
                    getLocation();
                    fragmentManager
                            .beginTransaction()
                            .add(R.id.mainContainer, new TransferProductFragment(), TransferProductFragmentTAG)
                            .addToBackStack(TransferProductFragmentTAG)
                            .commit();
                }
                break;
            case R.id.buttonQRCodeScanProduct:
                startActivityForResult(new Intent(this, QRScanActivity.class), Constants.QR_SCAN_TRANSFER_PRODUCT);
                break;
            case R.id.buttonQRCodeScanTransferAccount:
                startActivityForResult(new Intent(this, QRScanActivity.class), Constants.QR_SCAN_TRANSFER_TO_ACCOUNT);
                break;
            case R.id.buttonQRCodeScanSellerAccount:
                startActivityForResult(new Intent(this, QRScanActivity.class), Constants.QR_SCAN_SET_PRODUCT_SELLER);
                break;
            case R.id.buttonLogout:
//                new MaterialAlertDialogBuilder(this)
//                        .setTitle("Confirm scan")
//                        .setMessage("Scanned Successfully")
//                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(getApplicationContext(), "Main Clicking", Toast.LENGTH_SHORT).show();
//                            }
//                        })
//                        .setNeutralButton("Scan Again", null)
//                        .show();
                navigateTo(new MainFragment(), true);
                break;
            case R.id.menuItemCommercial:
                startActivity(new Intent(this, CommercialActivity.class));
                break;
            default:
                Toast.makeText(this, "Main Activity", Toast.LENGTH_SHORT).show();
        }
    }
}
