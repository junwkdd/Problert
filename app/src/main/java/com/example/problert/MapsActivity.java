package com.example.problert;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//lat 위도 lag 경도

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int REQUEST_CODE_PERMISSIONS = 1000;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;

    private double lat;
    private double lng;
    double locations[][];
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(RetrofitService.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    final RetrofitService retrofitService = retrofit.create(RetrofitService.class);

    public Marker addmarking(){
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

                    retrofitService.getData(lat+"", lng+"").enqueue(new Callback<List<Data>>() {
                        @Override
                        public void onResponse(@NonNull Call<List<Data>> call, @NonNull Response<List<Data>> response) {
                            if (response.isSuccessful()) {
                                List<Data> datas = response.body();
                                if (datas != null) {
                                    for (int i = 0; i < datas.size(); i++) {
                                        Log.e("data" + i, datas.get(i).getCoordinate().getCoordinates()[0] + "");
                                        // 위치 정보가 들있을 것 같긴 한데 사실 잘 모르겠어
                                        // locations[i] = datas.get(i).getCoordinate().getCoordinates();
                                        // 어떠한 배열이나 변수에 넣으렴
                                        // datas.get(i).getTitle();
                                    }
                                    Log.e("getData2 end", "======================================");
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<List<Data>> call, @NonNull Throwable t) {
                            Log.e("getData failed", "======================================");
                            Log.e("getData failed", t.getMessage());
                        }
                    });

                    mMap.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("내 위치")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.redpin))
                            .snippet("여의도 한강 치맥 합시다."));
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
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment   = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        addmarking();
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
    public void onLastLocationButtonClicked(View view) {
        addmarking();
    }

//    public  void address(){
//        Context mConText = null;
//        Geocoder mGeocoder = new Geocoder(mConText);
//
//    }

    public void writebutton(View view){
        Intent intentw = new Intent(this, MainActivity.class);
        intentw.putExtra("lat", lat);
        intentw.putExtra("lng", lng);
        startActivity(intentw);
    }
}
