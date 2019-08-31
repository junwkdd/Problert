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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.widget.Toast;
import android.media.ExifInterface;
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
import android.widget.TextView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    final String TAG = getClass().getSimpleName();
    ImageView imageView;
    final static int TAKE_PICTURE = 1;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    String imageid;

    private final int GET_GALLERY_IMAGE = 200;
    private ImageView imageview;

    Context context;
    private RetrofitService retrofitService;

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
                        "com.roopre.cameratutorial.fileprovider",
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create()
                ).build();

        retrofitService = retrofit.create(RetrofitService.class);
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

        TextView locationtext = (TextView) findViewById(R.id.locationText);
        locationtext.setText(intent.getStringExtra("location"));

        imageview = (ImageView)findViewById(R.id.camera_view);
        imageview.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });


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
                input.put("imageid", imageid);

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
    }

    //권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    //카메라로 촬영한 영상을 가져오는 부분
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = new Intent();
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK && intent.hasExtra("data")){
                    Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
                    if(bitmap != null){
                        imageView.setImageBitmap(bitmap);
                    }
                }
                break;
        }
        if(requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            imageview.setImageURI(selectedImageUri);
            try {
                // 비트맵 이미지로 가져온다
                String imagePath = selectedImageUri.getPath();
                Bitmap image = BitmapFactory.decodeFile(imagePath);

                // 이미지를 상황에 맞게 회전시킨다
                ExifInterface exif = new ExifInterface(imagePath);
                int exifOrientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int exifDegree = exifOrientationToDegress(exifOrientation);
                image = rotate(image, exifDegree);

                // 변환된 이미지 사용
                imageView.setImageBitmap(image);
            } catch (Exception e) {
                Toast.makeText(this, "오류발생: " + e.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }

        File file = new File(getRealPathFromUri(context, selectedImageUri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        retrofitService.uploadImage(body).enqueue(new Callback<ImageId>() {
            @Override
            public void onResponse(Call<ImageId> call, Response<ImageId> response) {
                if (response.isSuccessful()) {
                    Log.d("서지우의 고생은", "여기서 결실을 맺는다.");
                    imageid = response.body().getImage();
                }
            }
            @Override
            public void onFailure(Call<ImageId> call, Throwable t) {
                Log.d("서지우의 고생", t.getMessage());
                Log.d("서지우의 고생", String.valueOf(t.getCause()));
                Log.d("서지우의 고생은", "이제 시작이다.");
            }
        });
    }

    private int exifOrientationToDegress(int exifOrientation) {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        } return 0;
    }

    public Bitmap rotate(Bitmap bitmap, int degress){
        if(degress != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degress, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;

                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }
}
