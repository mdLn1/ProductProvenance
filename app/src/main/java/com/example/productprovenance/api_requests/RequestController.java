package com.example.productprovenance.api_requests;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;

public class RequestController {

    private static RequestController controller = new RequestController();
    private RequestQueueSingleton requestQueueSingleton;

    public static RequestController getInstance()
    {
        return controller;
    }

    public void makeRequest(Context context, String url, int method, int requestCode, HashMap<String, String> stringParams, final ResultListener resultListener)
    {
        try
        {

                requestQueueSingleton = RequestQueueSingleton.getInstance(context);

                CustomRequest networkRequest = new
                        CustomRequest(context, url, method, stringParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        resultListener.onResult(method, requestCode,true, jsonObject, null);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                resultListener.onResult(method, requestCode, false, null, error);
                                error.printStackTrace();
                            }
                        });

                networkRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueueSingleton.getRequestQueue().add(networkRequest);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
