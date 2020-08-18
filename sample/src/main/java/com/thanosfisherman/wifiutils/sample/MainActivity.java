package com.thanosfisherman.wifiutils.sample;

import android.Manifest;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionSuccessListener;
import com.thanosfisherman.wifiutils.wifiRemove.RemoveErrorCode;
import com.thanosfisherman.wifiutils.wifiRemove.RemoveSuccessListener;
import com.thanosfisherman.wifiutils.wifiScan.ScanResultsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String SSID = "Ot Dieu";
    private static final String PASSWORD = "abcd@8952";
    private ConnectionSuccessListener successListener = new ConnectionSuccessListener() {
        @Override
        public void success() {
            Toast.makeText(MainActivity.this, "SUCCESS!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void failed(@NonNull ConnectionErrorCode errorCode) {
            Toast.makeText(MainActivity.this, "EPIC FAIL!" + errorCode.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 555);
        WifiUtils.enableLog(true);
        //TODO: CHECK IF LOCATION SERVICES ARE ON

        final TextView textViewSSID = findViewById(R.id.textview_ssid);
        textViewSSID.setText(SSID);

        final Button buttonEnable = findViewById(R.id.button_enable);
        buttonEnable.setOnClickListener(v -> enableWifi());

        final Button buttonDisable = findViewById(R.id.button_disable);
        buttonDisable.setOnClickListener(v -> disableWifi());

        final Button buttonConnect = findViewById(R.id.button_connect);
        buttonConnect.setOnClickListener(v -> connectWithWpa());

        final Button buttonDisconnect = findViewById(R.id.button_disconnect);
        buttonDisconnect.setOnClickListener(v -> disconnect(v.getContext()));

        final Button buttonCheck = findViewById(R.id.button_check);
        buttonCheck.setOnClickListener(v -> checkWifi());

        final Button buttonRemove = findViewById(R.id.button_remove);
        buttonRemove.setOnClickListener(v -> remove(v.getContext()));

        final Button buttonScan = findViewById(R.id.button_scan);
        buttonScan.setOnClickListener(v -> scanWifi());

        WifiUtils.forwardLog((priority, tag, message) -> {
            String customTag = tag + "_" + MainActivity.class.getSimpleName();
            Log.i(customTag, message);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void connectWithWps() {
        WifiUtils.withContext(getApplicationContext()).connectWithWps("d8:74:95:e6:f5:f8", "51362485").onConnectionWpsResult(this::checkResult).start();
    }

    private void connectWithWpa() {
        WifiUtils.withContext(getApplicationContext())
                .connectWith(SSID, PASSWORD)
                .setTimeout(40000)
                .onConnectionResult(successListener)
                .start();
    }

    private void disconnect(final Context context) {
        WifiUtils.withContext(context)
                .disconnect(new DisconnectionSuccessListener() {
                    @Override
                    public void success() {
                        Toast.makeText(MainActivity.this, "Disconnect success!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failed(@NonNull DisconnectionErrorCode errorCode) {
                        Toast.makeText(MainActivity.this, "Failed to disconnect: " + errorCode.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void remove(final Context context) {
        WifiUtils.withContext(context)
                .remove(SSID, new RemoveSuccessListener() {
                    @Override
                    public void success() {
                        Toast.makeText(MainActivity.this, "Remove success!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failed(@NonNull RemoveErrorCode errorCode) {
                        Toast.makeText(MainActivity.this, "Failed to remove: " + errorCode.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void enableWifi() {
        WifiUtils.withContext(getApplicationContext()).enableWifi(this::checkResult);
        //or without the callback
        //WifiUtils.withContext(getApplicationContext()).enableWifi();
    }

    private void disableWifi() {
        WifiUtils.withContext(getApplicationContext()).disableWifi();
    }

    private void checkWifi() {
        boolean result = WifiUtils.withContext(getApplicationContext()).isWifiConnected(SSID);
        Toast.makeText(MainActivity.this, "Wifi Connect State: " + result, Toast.LENGTH_SHORT).show();
    }

    private void checkResult(boolean isSuccess) {
        if (isSuccess)
            Toast.makeText(MainActivity.this, "SUCCESS!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MainActivity.this, "EPIC FAIL!", Toast.LENGTH_SHORT).show();
    }

    private void scanWifi() {
        Log.d("scanWifi", "Start scanning...");
        WifiUtils.withContext(getApplicationContext()).scanWifi(this::getScanResults).start();
    }

    private void getScanResults(@NonNull final List<ScanResult> results) {
        if (results.isEmpty()) {
            Log.i("scanWifi", "SCAN RESULTS IT'S EMPTY");
            return;
        }
        
        //Log.i("scanWifi", "" + results);

        for (ScanResult result : results) {
            Log.i("scanWifi", "=== " + result.SSID + " ===");
            Log.i("scanWifi", "BSSID       : " + result.BSSID);
            Log.i("scanWifi", "capabilities: " + result.capabilities);
            Log.i("scanWifi", "level       : " + WifiManager.calculateSignalLevel(result.level, 5));
            Log.i("scanWifi", "frequency   : " + result.frequency);
        }
    }
}
