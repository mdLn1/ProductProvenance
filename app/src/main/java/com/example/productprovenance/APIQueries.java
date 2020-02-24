package com.example.productprovenance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.productprovenance.requests.RequestController;
import com.example.productprovenance.requests.RequestQueueSingleton;
import com.example.productprovenance.requests.ResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class APIQueries extends AppCompatActivity implements View.OnClickListener, ResultListener {

    private TextView mTextViewResult;
        private RequestQueueSingleton mQueue;
    private int reqType = Request.Method.GET;
    private JSONObject reqBody = null;

    private String link;

// POST request example
//    JSONObject jsonObject = new JSONObject();
//        try {
//        jsonObject.put("name", "JournalDev.com");
//        jsonObject.put("job", "To teach you the best");
//    } catch (JSONException e) {
//        e.printStackTrace();
//    }
//
//
//    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(uri, jsonObject, new Response.Listener() {
//        @Override
//        public void onResponse(JSONObject response) {
//            VolleyLog.wtf(response.toString(), "utf-8");
//            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
//
//        }
//    }, errorListener) {
//
//        @Override
//        public int getMethod() {
//            return Method.POST;
//        }
//
//        @Override
//        public Priority getPriority() {
//            return Priority.NORMAL;
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apiqueries);

        mTextViewResult = findViewById(R.id.resultTextView);
        Button helloButton = findViewById(R.id.helloButton);
        Button accountsButton = findViewById(R.id.accountsButton);
        Button pickAccountButton = findViewById(R.id.pickAccountButton);

        mQueue = RequestQueueSingleton.getInstance(this);
        helloButton.setOnClickListener(this);
        accountsButton.setOnClickListener(this);
        pickAccountButton.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == Constants.PICK_ACCOUNT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String chosenAccount = intent.getStringExtra("chosenAccount");
                mTextViewResult.setText("Account selected:" + Constants.accounts.get(chosenAccount));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(APIQueries.this, "You must chose an account to continue", Toast.LENGTH_LONG);
            }
        }
    }


    private void jsonParse() {

        String url = Constants.ROUTE_DEFAULT + link;


        JsonObjectRequest request = new JsonObjectRequest(reqType, url, reqBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mTextViewResult.setText("Response: ");
                            String result = "";
                            switch (link) {
                                case "accounts":
                                    JSONArray accounts = response.getJSONArray("accounts");

                                    for (int i = 0; i < accounts.length(); i++) {
                                        result += "\n" + accounts.getString(i);
                                    }
                                    break;
                                default:
                                    result = "\n" + response.getString("message");
                            }
                            mTextViewResult.append(result);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(APIQueries.this, "Check if API is running", Toast.LENGTH_SHORT).show();
            }
        });

        mQueue.addToRequestQueue(request);
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.accountsButton:
                link = "accounts";
                reqType = Constants.GET_REQUEST;
                reqBody = null;
//                jsonParse();
                break;
            case R.id.pickAccountButton:
                Intent pickAccountIntent = new Intent(this, AccountsActivity.class);
                startActivityForResult(pickAccountIntent, Constants.PICK_ACCOUNT_REQUEST);
                break;
            default:
                link = "hello";
                reqBody = null;
                reqType = Constants.GET_REQUEST;
                RequestController.getInstance().makeRequest(this, Constants.ROUTE_DEFAULT + link, Constants.GET_REQUEST, null, this);
//                jsonParse();
        }

    }

    @Override
    public void onResult(int requestCode, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError) {
        if (requestCode == Constants.POST_REQUEST) {
            if (isSuccess) {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == Constants.GET_REQUEST) {
            if (isSuccess) {
                try {
                    mTextViewResult.append(jsonObject.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }

    }

}
