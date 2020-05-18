package com.example.productprovenance.api_requests;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.productprovenance.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomRequest extends Request<JSONObject> {
    private Response.Listener<JSONObject> listener;
    private Map<String, String> params;
    private Context context;
    private String requestUrl;

    public CustomRequest(Context context, String requestUrl, int method, Map<String, String> params,
                         Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super(method, requestUrl, errorListener);
        try {
            this.listener = responseListener;
            this.params = params;
            this.context = context;
            this.requestUrl = requestUrl;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String>  headers = new HashMap<String, String>();
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.userDataStore, Context.MODE_PRIVATE);
        if(sharedPref.contains(Constants.userToken))
            headers.put("x-auth-token", Objects.requireNonNull(sharedPref.getString(Constants.userToken, "unknown")));
        return headers;
    }

    @Override
    protected Map<String, String> getParams() {
        return params;
    };

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));

            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            je.printStackTrace();
            return Response.error(new ParseError(je));
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.error(new ParseError(ex));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        try {
            listener.onResponse(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Response.ErrorListener getErrorListener() {

        return super.getErrorListener();
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        volleyError.printStackTrace();
        return super.parseNetworkError(volleyError);
    }

    @Override
    public void deliverError(VolleyError volleyError) {

        try {
            volleyError.printStackTrace();

            super.deliverError(volleyError);
        } catch (Exception ex) {
            ex.printStackTrace();
            super.deliverError(volleyError);
        }
    }
}
