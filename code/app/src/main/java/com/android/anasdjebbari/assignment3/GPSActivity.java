package com.android.anasdjebbari.assignment3;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class GPSActivity extends Activity {
    Button backbtn;
    TextView distance_view,time_view,speed_view;
    ArrayList<Integer> collected_info;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        backbtn = (Button)findViewById(R.id.back);
        speed_view = (TextView)findViewById(R.id.user_page_speed);
        distance_view = (TextView)findViewById(R.id.user_page_distance);
        time_view = (TextView)findViewById(R.id.user_page_time);
        DrawView drawView;
        drawView = (DrawView) findViewById(R.id.secondlay);

        Bundle data = getIntent().getExtras();
        int speed = data.getInt("avg_speed");
        String dist = data.getString("total_distance");
        String time = data.getString("total_time");
        collected_info = data.getIntegerArrayList("graph_array");

        if(collected_info != null) {
            Log.d("UserActivity", "testing" + collected_info.size());
        }

        speed_view.setText("Average user speed : " + speed);
        distance_view.setText("Distance : " + dist);
        time_view.setText("Total time is: " + time + " seconds");

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (collected_info.isEmpty() || collected_info == null){
            Log.e("123", "Avoiding null pointer, the routes are null!!!");
        }

        else {
            ArrayList<Float> temp = new ArrayList<>();

            for (int i = 0; i < collected_info.size(); i++) {
                float x = (float) collected_info.get(i);
                temp.add(x);
            }
            drawView.set_temp(temp);
        }

    }
}


