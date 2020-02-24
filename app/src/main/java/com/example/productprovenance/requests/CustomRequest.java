package com.example.productprovenance.requests;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class CustomRequest extends Request<JSONObject> {
    private Response.Listener<JSONObject> listener;
    private Map<String, String> params;
    private Context context;
    private String requestUrl;

    public CustomRequest(Context context, String requestUrl, int method, Map<String, String> params,
                         Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener) {
        super(method, requestUrl, errorListener);
        try {
            this.listener = reponseListener;
            this.params = params;
            this.context = context;
            this.requestUrl = requestUrl;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected Map<String, String> getParams() {

        if (params == null)
            return new HashMap<>();

        return params;
    }

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
