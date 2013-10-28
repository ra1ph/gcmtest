package com.ra1ph.macexample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public abstract class ApiRequest extends AsyncTask<Void, Integer, RequestResult> {
    private Context context;
    private int requestType;
    private OnRequestResultListener listener;
    protected String BASE_URL = "http://app.wifix.ru/android";
    private HttpRequestBase httpRequest;

    public ApiRequest(Context context, int requestType, OnRequestResultListener listener) {
        super();
        this.context = context;
        this.requestType = requestType;
        this.listener = listener;


    }

    @Override
    protected void onProgressUpdate(Integer... values) {
//        MALogger.d("Progress: " + values[0]);
        listener.onProgressUpdate(values[0]);
    }

    @Override
    protected void onCancelled() {
        //MALogger.d("API request cancelled");
        super.onCancelled();
    }

    public void cancelRequest() {
        if (httpRequest != null) {
            httpRequest.abort();
            //MALogger.d("Request aborted");
        }
        cancel(true);
    }

    @Override
    protected void onPostExecute(RequestResult result) {
        switch (result.getCode()) {
            case HttpStatus.SC_OK:
            case HttpStatus.SC_NOT_MODIFIED:
            case 208:
                listener.onSuccess(result);
                return;
//            case HttpStatus.SC_FORBIDDEN:
//            case HttpStatus.SC_UNAUTHORIZED:
//                Toast.makeText(context, "РћС€РёР±РєР° Р°РІС‚РѕСЂРёР·Р°С†РёРё", Toast.LENGTH_SHORT).show();
//                break;
//            case RequestResult.NO_COORDINATES:
//                Toast.makeText(context, "РћС€РёР±РєР° РѕРїСЂРµРґРµР»РµРЅРёСЏ РјРµСЃС‚РѕРїРѕР»РѕР¶РµРЅРёСЏ", Toast.LENGTH_SHORT).show();
//                break;
//            case HttpStatus.SC_CONFLICT:
//                Toast.makeText(context, R.string.task_reserve_error, Toast.LENGTH_SHORT).show();
//                break;
//            case HttpStatus.SC_NOT_FOUND:
//                Toast.makeText(context, R.string.task_not_found, Toast.LENGTH_SHORT).show();
//                break;
//            case RequestResult.NO_INTERNET:
//                Toast.makeText(context, "РћС‚СЃСѓС‚СЃС‚РІСѓРµС‚ РїРѕРґРєР»СЋС‡РµРЅРёРµ Рє СЃРµС‚Рё", Toast.LENGTH_SHORT).show();
//                break;
//            default:
//                if (result.getCode() == RequestResult.UNKNOWN && result.getResponse() != null) {
//                    Toast.makeText(context, "РћС€РёР±РєР°: " + result.getResponse(), Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(context, "РћС€РёР±РєР°: " + result.getCode(), Toast.LENGTH_SHORT).show();
//                }
//                break;
        }
        listener.onFail(result);
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    protected abstract String getUrlParams();

    public int getRequestType() {
        return requestType;
    }

    protected HttpRequestBase getHttpRequest() {
        try {
            HttpPost request = new HttpPost(BASE_URL + getUrlParams());
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("json", getRequestParams()));
            request.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
            return request;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected abstract String getRequestParams();

    protected RequestResult doRequest() throws IOException {
        String error;
        try {
            httpRequest = getHttpRequest();
            Log.d("api","REQUEST: " + httpRequest.getURI());

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
            HttpConnectionParams.setSoTimeout(httpParameters, 15000);

            HttpClient httpClient = new DefaultHttpClient(httpParameters);

            HttpResponse response = httpClient.execute(httpRequest);
            String responseString = EntityUtils.toString(response.getEntity());
            return new RequestResult(requestType, response.getStatusLine().getStatusCode(),
                    responseString);
        } catch (ClientProtocolException e) {
            //MALogger.logException(e);
            error = getContext().getString(R.string.connection_error);
        } catch (SocketTimeoutException e) {
            //MALogger.logException(e);
            error = getContext().getString(R.string.connection_error);
        } catch (IOException e) {
            //MALogger.logException(e);
            error = getContext().getString(R.string.connection_error);
        } catch (IllegalStateException e) {
            //MALogger.logException(e);
            error = getContext().getString(R.string.connection_error);
        } catch (IllegalArgumentException e) {
            //MALogger.logException(e);
            error = getContext().getString(R.string.connection_error);
        }
        return new RequestResult(requestType, RequestResult.UNKNOWN, String.format("{response: \"%s\"}", error));
    }

    @Override
    protected RequestResult doInBackground(Void... params) {
        if (!isConnected()){
            return new RequestResult(requestType, RequestResult.NO_INTERNET,
                    String.format("{response: \"%s\"}", getContext().getString(R.string.no_connection)));
        }

        String error;
        try {
            return doRequest();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            error = getContext().getString(R.string.malformed_url);
        } catch (IOException e) {
            e.printStackTrace();
            error = getContext().getString(R.string.connection_error);
        }
        return new RequestResult(requestType, RequestResult.UNKNOWN, String.format("{response: \"%s\"}", error));
    }

    protected Context getContext() {
        return context;
    }

    public double getDiagonal(){
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        double x = Math.pow((float)dm.widthPixels/dm.xdpi,2);
        double y = Math.pow((float)dm.heightPixels/dm.ydpi,2);
        double screenInches = Math.sqrt(x+y);
        return screenInches;
    }

    public double getDPI(){
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        return dm.densityDpi;
    }
}