package com.com.ceti_sb.android.tests;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ceti_sb.android.LoginActivity;
import com.ceti_sb.android.NetworkVolley;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by nandu on 12/15/2015.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private LoginActivity myLoginActivity;
    private Context context;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = this.getInstrumentation().getTargetContext().getApplicationContext();
        myLoginActivity = getActivity();
    }

    public void testLogin() {

        String url = "http://ceti-production-spnenzsmun.elasticbeanstalk.com/api/users/sign_in";
        RequestQueue queue = NetworkVolley.getInstance(context)
                .getRequestQueue();
        HashMap<String, String> inner = new HashMap<String, String>();
        inner.put("email", "a@b.c");
        inner.put("password", "1234");
        HashMap<String, HashMap<String, String>> outer = new HashMap<String, HashMap<String, String>>();
        outer.put("user", inner);
        JSONObject obj = new JSONObject(outer);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,url,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        //Success
                        assertNull(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        //Error
                        assertNotNull(error);
                    }
                }){
                @Override
                public String getBodyContentType(){
                    return "application/json";
                }
            };
        queue.add(jsonRequest);
    }
}