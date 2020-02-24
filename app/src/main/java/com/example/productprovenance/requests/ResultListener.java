package com.example.productprovenance.requests;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface ResultListener {
    void onResult(int requestCode, boolean isSuccess, JSONObject jsonObject, VolleyError volleyError);
}
