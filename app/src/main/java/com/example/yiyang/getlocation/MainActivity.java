package com.example.yiyang.getlocation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final long MIN_TIME = 5000;
    private static final float MIN_DIST = 5;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1234;
    private TextView txtLocInfo;
    private Button btnSetupGps;
    private LocationManager locMgr;
    private Location mLocation;
    private Button btnGetAddr;
    private TextView txtAddrInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txtLocInfo = (TextView) findViewById(R.id.txtLocInfo);
        btnSetupGps = (Button) findViewById(R.id.btnSetup);

        locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);

        btnGetAddr = (Button) findViewById(R.id.btnGetAddr);
        txtAddrInfo = (TextView) findViewById(R.id.txtAddrInfo);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                Toast.makeText(this, "Locate position need extra permission!", Toast.LENGTH_LONG).show();
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return;
        }

        String bestPovider = locMgr.getBestProvider(new Criteria(), true);
        if (bestPovider != null) {
            txtLocInfo.setText("取得定位中...");

            locMgr.requestLocationUpdates(bestPovider, MIN_TIME, MIN_DIST, this);
        } else
            txtLocInfo.setText("請檢查設定");
    }

    @Override
    protected void onPause() {
        super.onPause();
        locMgr.removeUpdates(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void setup(View view) {
        Intent it = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(it);
    }

    public void getAddr(View view) {
        txtAddrInfo.setText("查詢中...");

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> listAddr = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
            if (listAddr == null || listAddr.size() == 0) {
                txtAddrInfo.setText("無法取得地址資料");
            } else {
                Address address = listAddr.get(0);
                String str = "查詢結果:\n";
                for (int i=0;i<=address.getMaxAddressLineIndex(); i++) {
                    str += address.getAddressLine(i) + "\n";
                }
                txtAddrInfo.setText(str);
            }


        } catch (IOException e) {
            e.printStackTrace();
            txtAddrInfo.setText("讀取地址發生錯誤\n" + e.toString());
        }
    }



    @Override
    public void onLocationChanged(Location location) {
        txtLocInfo.setText(
                "提供者 " + location.getProvider()
                        + String.format("\n經度 %.5f\n緯度 %.5f\n高度 %.2f公尺"
                        , location.getLatitude(), location.getLongitude(), location.getAltitude())
        );
        mLocation = location;

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
