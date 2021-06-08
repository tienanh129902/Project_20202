package com.example.iot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.security.Provider;
import java.util.List;
import java.util.Locale;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Toado extends AppCompatActivity {
    //Khai báo biến

    Button btnlocation;
    TextView textView1, textView2, textView3, textView4, textView5;
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toado);
        Toolbar toolbar2 = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Trỏ vào biến

        btnlocation = findViewById(R.id.btn_location);
        textView1 = findViewById(R.id.text_view1);
        textView2 = findViewById(R.id.text_view2);
        textView3 = findViewById(R.id.text_view3);
        textView4 = findViewById(R.id.text_view4);
        textView5 = findViewById(R.id.text_view5);

        //Khai báo biến fusedlocationProviderclient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        btnlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Kiểm tra quyền truy cập
                if (ActivityCompat.checkSelfPermission(Toado.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //Khi quyền cho phép truy cập
                    getLocation();
                    getDistanceandSpeed();

                } else {
                    //Khi quyền cho phép từ chối
                    ActivityCompat.requestPermissions(Toado.this
                            , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });
    }

    private void getDistanceandSpeed() {
        Intent intent = new Intent(Toado.this,MyService2.class);
        this.startService(intent);
    }

    private void getLocation() {
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
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    //Khai báo biến location
                    Location location = task.getResult();
                    if (location != null) {
                        try {
                            //Khai báo biến geocoder
                            Geocoder geocoder = new Geocoder(Toado.this,
                                    Locale.getDefault());
                            //Khai báo địa chỉ
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1
                            );
                            //Set latitude ở TextView
                            textView1.setText(Html.fromHtml(
                                    "<font color='#6200EE'><b>Vĩ độ :</b><br></font>"
                                            + addresses.get(0).getLatitude()
                            ));
                            //Set longitude ở TextView
                            textView2.setText(Html.fromHtml(
                                    "<font color='#6200EE'><b>Kinh độ :</b><br></font>"
                                            + addresses.get(0).getLongitude()
                            ));
                            //Set tên nước
                            textView3.setText(Html.fromHtml(
                                    "<font color='#6200EE'><b>Đất nước :</b><br></font>"
                                            + addresses.get(0).getCountryName()
                            ));
                            //Set tên vùng
                            textView4.setText(Html.fromHtml(
                                    "<font color='#6200EE'><b>Vùng :</b><br></font>"
                                            + addresses.get(0).getLocality()
                            ));
                            //Set địa chỉ
                            textView5.setText(Html.fromHtml(
                                    "<font color='#6200EE'><b>Địa chỉ :</b><br></font>"
                                            + addresses.get(0).getAddressLine(0)
                            ));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
}

