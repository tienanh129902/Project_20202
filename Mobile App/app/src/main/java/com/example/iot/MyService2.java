package com.example.iot;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class MyService2 extends Service {
        private LocationManager locationManager;
        private LocationListener locationListener = new MyService2.myLocationListener();
        static final Double EARTH_RADIUS = 6371.00;

        private boolean gps_enabled = false;
        private boolean network_enabled = false;

        private Handler handler = new Handler();
        Thread t;

        @Override
        public void onCreate() {
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public void onStart(Intent intent, int startid) {
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {

            Toast.makeText(getBaseContext(), "Service Started", Toast.LENGTH_SHORT).show();

            final Runnable r = new Runnable() {
                public void run() {
                    Log.v("Debug", "Hello");
                    location();
                    handler.postDelayed(this, 5000);
                }
            };
            handler.postDelayed(r, 5000);
            return START_STICKY;
        }


        private void location() {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            try {
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
                ex.getMessage();
            }
            try {
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
                ex.getMessage();
            }
            Log.v("Debug", "in on create..2");
            if (gps_enabled) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Log.v("Debug", "Enable..");
            }
            if (network_enabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                Log.v("Debug", "Disable..");
            }
            Log.v("Debug", "in on create..3");
        }

        private class myLocationListener implements LocationListener {

            double lat_old = 0.0;
            double lon_old = 0.0;
            double lat_new;
            double lon_new;
            double time = 10;
            double speed = 0.0;


            @Override
            public void onLocationChanged(Location location) {
                Log.v("Debug", "in onLocation changed..");
                if (location != null) {
                    locationManager.removeUpdates(locationListener);
                    //String Speed = "Device Speed: " +location.getSpeed();
                    lat_new = location.getLongitude();
                    lon_new = location.getLatitude();
                    String longitude = "Kinh độ: " + location.getLongitude();
                    String latitude = "Vĩ độ: " + location.getLatitude();
                    double distance = CalculationByDistance(lat_new, lon_new, lat_old, lon_old);
                    speed = distance / time;
                    Toast.makeText(getApplicationContext(), longitude + "\n" + latitude + "\nKhoảng cách là: "
                            + distance + "\nVận tốc là: " + speed, Toast.LENGTH_SHORT).show();
                    lat_old = lat_new;
                    lon_old = lon_new;
                }
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

        private double CalculationByDistance(double lat1, double lon1, double lat2, double lon2) {
            double Radius = EARTH_RADIUS;
            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                            Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.asin(Math.sqrt(a));
            return Radius * c;
        }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}