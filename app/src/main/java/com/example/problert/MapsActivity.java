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
    private double lat;
    private double lng;
    String imgID;

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
//                    lat = location.getLatitude()-0.0032711;
//                    lng = location.getLongitude()-0.0690149;

                    lat = location.getLatitude();
                    lng = location.getLongitude();

                    Double.toString(lat);
                    Double.toString(lng);

                    retrofitService.getData(lat+"", lng+"").enqueue(new Callback<List<Data>>() {
                        @Override
                        public void onResponse(@NonNull Call<List<Data>> call, @NonNull Response<List<Data>> response) {
                            if (response.isSuccessful()) {
                                List<Data> datas = response.body();
                                if (datas != null) {
                                    for (int i = 0; i < datas.size(); i++) {
                                        Log.e("data" + i, datas.get(i).getCoordinate().getCoordinates()[0] + "");
                                        mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(datas.get(i).getCoordinate().getCoordinates()[1], datas.get(i).getCoordinate().getCoordinates()[0]))
                                                .title(datas.get(i).getTitle())
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bluepin))
                                                .snippet(datas.get(i).getDescription()+"`"+datas.get(i).getImageid())

                                        );
                                        Log.d("ㄱㄱ", datas.get(i).getDescription()+"`"+datas.get(i).getImageid());
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
                            .position(new LatLng(lat,lng))
                            .title("내 위치")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.redpin))
                            .snippet("현재 당신의 위치입니다!"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lng)));
                    // 카메라 줌
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f)); //.zoomTo(17.0f));
                }
            }
        });
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
        //36.5749321, 128.5038
        addmarking();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lng)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
    }

    @Override
    public boolean onMarkerClick(Marker marker){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(marker.getPosition().latitude-0.0007, marker.getPosition().longitude)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
//        Toast.makeText(this, marker.getTitle()+"\n"+marker.getSnippet(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, PopupActivity.class);
        String[] etc = marker.getSnippet().split("`");
        Log.d("etc", etc.length+"");
        if(etc.length > 1)
            Log.d("etc1", etc[1]+"");
        try {
            intent.putExtra("title", marker.getTitle());
            intent.putExtra("location", findAddress(marker.getPosition().latitude, marker.getPosition().longitude));
            intent.putExtra("description", etc[0]);
            if(etc.length > 1)
                intent.putExtra("imgID", etc[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        startActivityForResult(intent, 1);
        overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
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

    public void writebutton(View view) throws IOException {
        Intent intentw = new Intent(this, MainActivity.class);
        intentw.putExtra("lat", lat);
        intentw.putExtra("lng", lng);
        intentw.putExtra("location", findAddress(lat, lng));
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