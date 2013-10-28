package com.ra1ph.macexample;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;

public class RequestResult {
    public static final int NO_COORDINATES = 0;
    public static final int UNKNOWN = 999;
    public static final int NO_INTERNET = 998;

    // request types
    public static final int WIFI = 0;

    private int code;
    private String response;
    private int requestType;

    public RequestResult(int requestType, int code, String response) {
        this.requestType = requestType;
        this.code = code;
        this.response = response;
    }

    public static String formatResponse(Context context, String response) {
        /*if (response.equals("invalid_data")) return context.getString(R.string.error_login_data);
        if (response.equals("not_verified_email")) return context.getString(R.string.error_login_not_verified);   */
        return response;
    }

    public String getFormattedResponse() {
        try {
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.getString("response");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return "Wrong response format";
    }

    public int getCode() {return code;}
    public int getRequestType() {return requestType;}
    public String getResponse() {return response;}
}
