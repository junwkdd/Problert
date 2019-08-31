package com.example.problert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class PopupActivity extends Activity {
    TextView titleText;
    TextView locationText;
    TextView descriptionText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        //UI 객체생성
        titleText = (TextView)findViewById(R.id.titleText);
        locationText = (TextView)findViewById(R.id.locationText);
        descriptionText = (TextView)findViewById(R.id.snippetText);

        //데이터 가져오기
        Intent intent = getIntent();

        String title = intent.getStringExtra("title");
        String location = intent.getStringExtra("location");
        String description = intent.getStringExtra("description");
        titleText.setText(title);
        locationText.setText(location);
        descriptionText.setText(description);
    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        finish();
        overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
    }

    public void heardbutton(View v){

    }


    public void unheardbutton(View v){

    }
}
