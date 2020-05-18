package com.example.productprovenance.api_requests;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface ResultListener {
    void onResult(int requestType, int requestCode, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError);
}
