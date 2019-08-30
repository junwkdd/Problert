package com.example.problert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }
}
