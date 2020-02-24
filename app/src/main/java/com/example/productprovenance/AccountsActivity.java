package com.example.productprovenance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AccountsActivity extends AppCompatActivity {

    private ListView accountsListView;
    private List accountsList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        accountsListView = (ListView)findViewById(R.id.accountsListView);
        accountsList.add("ADMIN");
        accountsList.add("MANUFACTURER1");
        accountsList.add("MANUFACTURER2");
        accountsList.add("SELLER1");
        accountsList.add("SELLER2");
        accountsList.add("DISTRIBUTOR1");
        accountsList.add("DISTRIBUTOR2");
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, accountsList);
        accountsListView.setAdapter(arrayAdapter);

        accountsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("chosenAccount",accountsList.get(position).toString());
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }
}
