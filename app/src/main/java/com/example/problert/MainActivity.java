package com.example.problert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final String TAG = getClass().getSimpleName();
    ImageView imageView;
    Button cameraBtn;
    final static int TAKE_PICTURE = 1;

    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;

    Context context;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.problert",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getBaseContext();

        imageView = findViewById(R.id.camera_view);
        cameraBtn = findViewById(R.id.camera_button);

        cameraBtn.setOnClickListener(this);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        final EditText title = (EditText)findViewById(R.id.title);
        final EditText description = (EditText)findViewById(R.id.description);
        Button submit_button = (Button) findViewById(R.id.submit_button);
        Button back_btn = (Button) findViewById(R.id.back_btn);
        Intent intent = getIntent();

        final double lat = intent.getDoubleExtra("lat", 0.00);
        final double lng = intent.getDoubleExtra("lng", 0.00);
        Double.toString(lat);
        Double.toString(lng);

        Log.d("lat:", lat+"");
        Log.d("lng:", lng+"");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
//                startActivity(intent);
                onBackPressed();
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, Object> input = new HashMap<>();
                input.put("title", title.getText());
                input.put("description", description.getText());
                input.put("lat", lat);
                input.put("lng", lng);

                retrofitService.postData(input).enqueue(new Callback<Data>() {
                    @Override
                    public void onResponse(@NonNull Call<Data> call, @NonNull Response<Data> response) {
                        Log.d("response", response.code()+"");
                        if (response.isSuccessful()) {
                            Data body = response.body();
                            if (body != null) {
//                                Log.d("data.getTitle()", body.getTitle()+"");
//                                Log.d("data.getDescription()", body.getDescription()+"");
//                                Log.d("data.lat", body.getLat()+"");
//                                Log.d("data.lng", body.getLng()+"");
                                Log.e("postData end", "======================================");
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Data> call, @NonNull Throwable t) {
                        Log.e("postData failed", "======================================");
                    }
                });
            }
        });
//        checkPermission();
    }
//
//    private void checkPermission() {
//        if(Build.VERSION.SDK_INT>=23) { //안드로이드6.0이상 권한 체크
//            TedPermission.with(context)
//                    .setPermissionListener(permissionListener)
//                    .setRationaleMessage("이미지를 다루기 위해서는 접근 권한이 필요합니다")
//                    .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다..\n [설정] > [권한] 에서 사용으로 활성화 해 주세요")
//                    .setPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
//                    .check();
//    } else {
//
//        }

    //권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.camera_button:
                //카메라 앱을 연다.
                dispatchTakePictureIntent();
                break;
        }
    }

    //카메라로 촬영한 영상을 가져오는 부분
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try{
            switch(requestCode) {
                case REQUEST_TAKE_PHOTO:
                    if (resultCode == RESULT_OK){
                        File file = new File(mCurrentPhotoPath);
                        Bitmap bitmap = MediaStore.Images.Media
                                .getBitmap(getContentResolver(), Uri.fromFile(file));
                        if(bitmap != null){
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                    break;
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
}
