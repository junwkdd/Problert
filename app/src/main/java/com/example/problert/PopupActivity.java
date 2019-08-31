package com.example.problert;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.Text;
public class PopupActivity extends Activity {

    private ProgressBar spinner;
    TextView titleText;
    ImageView locationImage;
    TextView locationText;
    TextView descriptionText;
    public int allgood = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(this.getClass().getName(), "Popupactivity 진입====================================");
        super.onCreate(savedInstanceState);

        locationImage = (ImageView) findViewById(R.id.loc_img);
        new LoadImage().execute("http://websrver/img/IMG_1.jpg");

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        new LoadImage().execute("http://websrver/img/IMG_1.jpg");
        photoTitle = (TextView) findViewById(R.id.photoTitle);

        load_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadImage().execute("http://webserver/img/IMG_2.jpg");
                //사진 제목을 불러옴
                new GetData().execute("http://webserver/img/title2.txt");
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadImage().execute("http://webserver/img/IMG_1.jpg");
                new GetData().execute("http://webserver/img/title.txt");
            }
        });

        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        //UI 객체생성
        titleText = (TextView)findViewById(R.id.titleText);
        locationText = (TextView)findViewById(R.id.locationText);
        descriptionText = (TextView)findViewById(R.id.snippetText);
//        locationImage = (ImageView) findViewById(R.id.loc_img);


        //데이터 가져오기
        Intent intent = getIntent();

        String title = intent.getStringExtra("title");
        String location = intent.getStringExtra("location");
        String description = intent.getStringExtra("description");
//        byte[] loc_img = getIntent().getByteArrayExtra("loc_img");

        titleText.setText(title);
        locationText.setText(location);
        descriptionText.setText(description);
//        locationImage.setImageBitmap(BitmapFactory.decodeByteArray(loc_img, 0, loc_img.length));
        goodcheck();
    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        finish();
        overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
    }
    //////////////////////////스위치
    public void heartbutton(View v){
        Button bt1;
        Log.d(this.getClass().getName(), "heardbutton 실행=================");
        bt1 = (Button) findViewById(R.id.empty);
        bt1.setVisibility(View.INVISIBLE);
        allgood++;
        goodscreen();
    }
    public void unheartbutton(View v){
        Log.d(this.getClass().getName(), "unheardbutton 실행=================");
        Button bt1;
        bt1 = (Button) findViewById(R.id.empty);
        bt1.setVisibility(View.VISIBLE);
        allgood--;
        goodscreen();
    }
    public void heartbutton(){
        Button bt1;
        Log.d(this.getClass().getName(), "heartbutton 실행=================");
        bt1 = (Button) findViewById(R.id.empty);
        bt1.setVisibility(View.INVISIBLE);
        allgood++;
        goodscreen();
    }
    public void unheartbutton(){
        Log.d(this.getClass().getName(), "unheartbutton1 실행=================");
        Button bt1;
        bt1 = (Button) findViewById(R.id.empty);
        bt1.setVisibility(View.VISIBLE);
        allgood--;
        goodscreen();
    }
    public void goodcheck(){ //좋아요x 0 좋아여 1
        Log.d(this.getClass().getName(), "good check 진입=================");
        int good = 0;
        if(good == 1) heartbutton();
        goodscreen();
    }
    public void goodscreen(){
        TextView goodtext = (TextView)findViewById(R.id.goodall);
        goodtext.setText(String.valueOf(allgood));
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spinner.setVisibility(View.VISIBLE);
        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            if(image != null) {
                img.setImageBitmap(image);
                spinner.setVisibility(View.GONE);
            } else {
                spinner.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "이미지가 존재하지 않거나 네트워크 오류 발생", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //AsyncTask for text file
    private class GetData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            String result = "";
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                int code = urlConnection.getResponseCode();

                if(code==200){
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    if (in != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                        String line = "";

                        while ((line = bufferedReader.readLine()) != null)
                            result += line;
                    }
                    in.close();
                }

                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            finally {
                urlConnection.disconnect();
            }
            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            photoTitle.setText(result);
            photoTitle.setTextSize(23);
            super.onPostExecute(result);
        }
    }
}
