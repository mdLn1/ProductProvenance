package com.example.productprovenance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationHost, View.OnClickListener {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private static final String MainFragmentTAG = "MainFragment";
    private static final String LoginFragmentTAG = "LoginFragment";
    private static final String TransferProductFragmentTAG = "TransferProductFragment";
    private static final String CreateProductFragmentTAG = "CreateProductFragmentTAG";
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = getSharedPreferences(Constants.userDataStore, Context.MODE_PRIVATE);

        fragmentManager = getSupportFragmentManager();

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
                            fragment2.updateSellerAccountAddres(qrData);
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
                    Toast.makeText(this, "Invalid Selection", Toast.LENGTH_LONG);

                }

            }
        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == Constants.QR_SCAN_ACTION) {
                Toast.makeText(this, "You need to scan a QR code to find the product details", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCreateUserMainMenu:
                navigateTo(new CreateUserFragment(), false);
                break;
            case R.id.buttonShowAccountQRMainMenu:
                startActivity(new Intent(this, QRCodeGeneratorActivity.class));
                break;
            case R.id.buttonAddProductMainMenu:
                fragmentManager
                        .beginTransaction()
                        .add(R.id.mainContainer, new CreateProductFragment(), CreateProductFragmentTAG)
                        .addToBackStack(CreateProductFragmentTAG)
                        .commit();
                break;
            case R.id.buttonAddProduct:
                break;
            case R.id.skipButton:
                startActivityForResult(new Intent(this, AccountsActivity.class), Constants.SKIP_LOGIN_ACTION);
                break;
            case R.id.menuItemProducts:
                Toast.makeText(this, "My Products", Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonTransferProductMainMenu:
                fragmentManager
                        .beginTransaction()
                        .add(R.id.mainContainer, new TransferProductFragment(), TransferProductFragmentTAG)
                        .addToBackStack(TransferProductFragmentTAG)
                        .commit();
                break;
            case R.id.buttonQRCodeScanProduct:
                startActivityForResult(new Intent(this, SimpleTestActivity.class), Constants.QR_SCAN_TRANSFER_PRODUCT);
                break;
            case R.id.buttonQRCodeScanTransferAccount:
                startActivityForResult(new Intent(this, SimpleTestActivity.class), Constants.QR_SCAN_TRANSFER_TO_ACCOUNT);
                break;
            case R.id.buttonQRCodeScanSellerAccount:
                startActivityForResult(new Intent(this, SimpleTestActivity.class), Constants.QR_SCAN_SET_PRODUCT_SELLER);
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
                navigateTo(new CreateUserFragment(), true);
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
