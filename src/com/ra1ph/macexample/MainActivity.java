package com.ra1ph.macexample;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.ParseException;

public class MainActivity extends Activity implements OnRequestResultListener {
    private static final String PREFS = "prefs";
    private static final String IS_SENDED = "is_sended";
    /**
     * Called when the activity is first created.
     */
    Button send;
    private WiFiRequest request;
    SharedPreferences prefs;
    private String appId = "tD8QzW1SZ7OhSGQHsDBZGDoNNqMzlK6Mf81pUayw";
    private String clientKey = "g5nH8uzAd1lW5yN7oI1ixMGzb3OLsbfopc8cphlc";
    private String os,device,macAddr,uuid;
    private TextView text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Parse.initialize(this, appId, clientKey);

        text = (TextView) findViewById(R.id.text);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        send = (Button) findViewById(R.id.send_button);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean test = true;
                if (test) {
                    if (true) {
                        WifiManager wifiMan = (WifiManager) MainActivity.this.getSystemService(
                                Context.WIFI_SERVICE);
                        WifiInfo wifiInf = wifiMan.getConnectionInfo();
                        macAddr = wifiInf.getMacAddress();

                        uuid = Settings.Secure.getString(MainActivity.this.getContentResolver(),Settings.Secure.ANDROID_ID);
                        os = "Android " + android.os.Build.VERSION.RELEASE;

                        device = Build.MANUFACTURER + " " + Build.MODEL;

                        request = new WiFiRequest(MainActivity.this, MainActivity.this, macAddr
                                , uuid, os, device);
                        request.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
            }
        });
    }


    @Override
    public void onSuccess(RequestResult result) {
        if (result.getRequestType() == RequestResult.WIFI) {
            //Toast.makeText(this, result.getResponse(), Toast.LENGTH_SHORT).show();
            ((TextView)findViewById(R.id.reply)).setText(result.getResponse());
            StringBuilder builder = new StringBuilder();
            builder.append("Android ID: ");
            builder.append(uuid);
            builder.append("\n");
            builder.append("Device: ");
            builder.append(device);
            builder.append("\n");
            builder.append("MAC: ");
            builder.append(macAddr);
            builder.append("\n");
            builder.append("OS: ");
            builder.append(os);
            builder.append("\n");
            text.setText(builder.toString());
            prefs.edit().putBoolean(IS_SENDED, true).commit();
        }
    }

    @Override
    public void onFail(RequestResult result) {
        Toast.makeText(this, "Wrong server question", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProgressUpdate(Integer progress) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
