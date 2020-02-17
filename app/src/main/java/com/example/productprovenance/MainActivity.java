package com.example.productprovenance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationHost{

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

}
