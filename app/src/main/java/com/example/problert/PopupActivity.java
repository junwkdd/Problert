package com.example.problert;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    Handler handler = new Handler();  // 외부쓰레드 에서 메인 UI화면을 그릴때 사용
    TextView titleText;
    ImageView locationImage;
    TextView locationText;
    TextView descriptionText;
    public int allgood = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(this.getClass().getName(), "Popupactivity 진입====================================");
        super.onCreate(savedInstanceState);

        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);      
        setContentView(R.layout.activity_popup);

        //UI 객체생성
        titleText = (TextView) findViewById(R.id.titleText);
        locationText = (TextView) findViewById(R.id.locationText);
        descriptionText = (TextView) findViewById(R.id.snippetText);

        //데이터 가져오기
        Intent intent = getIntent();

        String title = intent.getStringExtra("title");
        String location = intent.getStringExtra("location");
        String description = intent.getStringExtra("description");
        final String imgID = getIntent().getStringExtra("imgID");

//        if (imgID != null) {
//            Thread t = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    // TODO Auto-generated method stub
//                    try {
//                        final ImageView imgView = (ImageView) findViewById(R.id.loc_img);
//                        URL url = new URL("http://donote.co:3000/images/" + imgID);
//                        Log.d("url", url + "");
//                        InputStream is = url.openStream();
//                        final Bitmap bm = BitmapFactory.decodeStream(is);
//                        Log.d("bm", bm + "");
//                        handler.post(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                imgView.setImageBitmap(bm);
//                            }
//                        });
//                        imgView.setImageBitmap(bm);
//                    } catch (Exception e) {
//
//                    }
//
//                }
//            });
//            t.start();
//        }

        titleText.setText(title);
        locationText.setText(location);
        descriptionText.setText(description);
        goodcheck();
    }

    //확인 버튼 클릭
    public void mOnClose(View v) {
        finish();
        overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
    }

    //////////////////////////스위치
    public void heartbutton(View v) {
        Button bt1;
        Log.d(this.getClass().getName(), "heardbutton 실행=================");
        bt1 = (Button) findViewById(R.id.empty);
        bt1.setVisibility(View.INVISIBLE);
        allgood++;
        goodscreen();
    }

    public void unheartbutton(View v) {
        Log.d(this.getClass().getName(), "unheardbutton 실행=================");
        Button bt1;
        bt1 = (Button) findViewById(R.id.empty);
        bt1.setVisibility(View.VISIBLE);
        allgood--;
        goodscreen();
    }

    public void heartbutton() {
        Button bt1;
        Log.d(this.getClass().getName(), "heartbutton 실행=================");
        bt1 = (Button) findViewById(R.id.empty);
        bt1.setVisibility(View.INVISIBLE);
        allgood++;
        goodscreen();
    }

    public void unheartbutton() {
        Log.d(this.getClass().getName(), "unheartbutton1 실행=================");
        Button bt1;
        bt1 = (Button) findViewById(R.id.empty);
        bt1.setVisibility(View.VISIBLE);
        allgood--;
        goodscreen();
    }

    public void goodcheck() { //좋아요x 0 좋아여 1
        Log.d(this.getClass().getName(), "good check 진입=================");
        int good = 0;
        if (good == 1) heartbutton();
        goodscreen();
    }

    public void goodscreen() {
        TextView goodtext = (TextView) findViewById(R.id.goodall);
        goodtext.setText(String.valueOf(allgood));
    }
}