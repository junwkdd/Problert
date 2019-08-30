package com.example.problert;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//lat 위도 lag 경도

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final int REQUEST_CODE_PERMISSIONS = 1000;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    private List<Address> address;
    private String ad;
    private double lat;
    private double lng;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(RetrofitService.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    final RetrofitService retrofitService = retrofit.create(RetrofitService.class);

    public Marker addmarking() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSIONS);
            return null;
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // 현재 위치
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    Double.toString(lat);
                    Double.toString(lng);
                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    try {
                        ad = findAddress(lat, lng);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    retrofitService.getData(lat + "", lng + "").enqueue(new Callback<Data>() {
                        @Override
                        public void onResponse(@NonNull Call<Data> call, @NonNull Response<Data> response) {
                            if (response.isSuccessful()) {
                                Data body = response.body();
                                if (body != null) {
                                    Log.d("data.lat", body.getLat() + "");
                                    Log.d("data.lng", body.getLng() + "");
                                    Log.e("getData end", "======================================");
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Data> call, @NonNull Throwable t) {
                            Log.e("getData failed", "======================================");
                        }
                    });

                    mMap.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("내 위치")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.redpin))
                            .snippet(ad));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                    // 카메라 줌
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                }
            }
        });
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions().position(new LatLng(35.1426, 126.8)));
        mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
        addmarking();
    }

    @Override
    public boolean onMarkerClick(Marker marker){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
        Toast.makeText(this, marker.getTitle()+"\n"+marker.getSnippet() + "\n"+marker.getPosition(), Toast.LENGTH_SHORT).show();
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한 체크 거부 됨", Toast.LENGTH_SHORT).show();
                }
                return;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onLastLocationButtonClicked(View view) throws IOException {
        mMap.clear();
        addmarking();
    }

    public void writebutton(View view) {
        Intent intentw = new Intent(this, MainActivity.class);
        intentw.putExtra("lat", lat);
        intentw.putExtra("lng", lng);
        startActivity(intentw);
    }

    private String findAddress(double a, double b) throws IOException {
        StringBuffer bf = new StringBuffer();
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        address = geocoder.getFromLocation(a, b, 1);
        Log.d("address", address+"");
        if (address != null && address.size() > 0) {
            // 주소
            String currentLocationAddress = address.get(0).getAddressLine(0).toString();
            // 전송할 주소 데이터 (위도/경도 포함 편집)
            bf.append(currentLocationAddress);
        }
        return bf.toString();
    }
}
