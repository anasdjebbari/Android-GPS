package com.android.anasdjebbari.assignment3;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private ArrayList<GPSActivityDetails> tracker;
    public boolean running = false;
    private LocationManager lm;
    private Location loc = null;
    private Double ttlDist = 0.0;

    private TextView spd, avgSpeed, dis;
    private Button strtBtn, stpBtn, rstrtBtn;
    private Chronometer cm;
    private RelativeLayout lay_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getPermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tracker = new ArrayList<GPSActivityDetails>();
//        stpBtn.setEnabled(false);
//        rstrtBtn.setEnabled(false);


        spd = (TextView) findViewById(R.id.speed_view);
        avgSpeed = (TextView) findViewById(R.id.avg_speed_view);
        dis = (TextView) findViewById(R.id.distance);
        lay_out = (RelativeLayout) findViewById(R.id.activity_main);

//        speed.setVisibility(View.GONE);
//        avgSpeed.setVisibility(View.GONE);
//        dis.setVisibility(View.GONE);

        cm = (Chronometer) findViewById(R.id.cmtime);

        strtBtn = (Button) findViewById(R.id.btn_start);
        stpBtn = (Button) findViewById(R.id.btn_finish);
        rstrtBtn = (Button) findViewById(R.id.reset_btn);

        stpBtn.setClickable(false);
        rstrtBtn.setClickable(false);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        final LocationListener ll = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {

                    GPSActivityDetails user = new GPSActivityDetails();
                    user.setLongt(location.getLongitude());
                    user.setLangt(location.getLatitude());

                    // Speed calculation
                    Double activitySpeed = (double) location.getSpeed();
                    user.set_speed(activitySpeed * 3.6);
                    Double s = activitySpeed * 3.6;
                    double temp_speed = tracker.isEmpty() ? s : getAvgSpeed();

                    // Distance calculation
                    if (loc == null) {
                        loc = location;
                    }

                    double tLocation = loc.distanceTo(location);
                    ttlDist = ttlDist + tLocation;
                    user.set_distance_traveled(ttlDist);
                    String displayed_distance = (ttlDist < 1000) ?
                            (int) Math.round(ttlDist) + " meters" :
                            String.format("%.2f", ttlDist / 1000) + " km";



                    long duration_milisec = SystemClock.elapsedRealtime() - cm.getBase();
                    long duration_sec = duration_milisec / 1000;
                    user.setTime(duration_sec);

                    spd.setText("Speed " + (int) Math.round(s) + " km/hour");
                    avgSpeed.setText("Avg Speed " + (int) Math.round(temp_speed) + " km/hour");
                    dis.setText("Distance : " + displayed_distance);

                    loc = location;
                    tracker.add(user);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };


        strtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cm.setBase(SystemClock.elapsedRealtime());
                cm.start();
//                strtBtn.setVisibility(View.GONE);
                startTracking(ll);
//                speed.setVisibility(View.VISIBLE);
//                avgSpeed.setVisibility(View.VISIBLE);
//                dis.setVisibility(View.VISIBLE);
                
                strtBtn.setClickable(false);
                rstrtBtn.setClickable(true);
                stpBtn.setClickable(true);

                running = true;
                lay_out.setBackgroundColor(Color.parseColor("#7084a3"));
                cm.setBackgroundColor(Color.parseColor("#7084a3"));
                cm.setTextColor(Color.parseColor("#F4F4F4"));
            }
        });
        rstrtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishTracking(ll);
                reset();

                stpBtn.setClickable(false);
                rstrtBtn.setClickable(false);
                strtBtn.setClickable(true);

//                strtBtn.setVisibility(View.VISIBLE);
//                avgSpeed.setVisibility(View.GONE);
//                dis.setVisibility(View.GONE);

//                speed.setVisibility(View.GONE);
                lay_out.setBackgroundColor(Color.parseColor("#ffffff"));

                cm.setBackgroundColor(Color.parseColor("#ffffff"));
                cm.setTextColor(Color.parseColor("#000000"));
            }
        });
        stpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent showDetails = new Intent(MainActivity.this, GPSActivity.class);
                finishTracking(ll);
                Bundle data = new Bundle();
                cm.setBackgroundColor(Color.parseColor("#ffffff"));
                cm.setTextColor(Color.parseColor("#000000"));
                lay_out.setBackgroundColor(Color.parseColor("#ffffff"));

                stpBtn.setClickable(false);
                rstrtBtn.setClickable(false);
                strtBtn.setClickable(true);

                if (!tracker.isEmpty()) {
                    data.putInt("avg_speed", (int) Math.round(getAvgSpeed()));

                    Double dist = tracker.get(tracker.size() - 1).get_distance();
                    String distance= "88";
                    distance = (dist < 1000) ?
                            (int) Math.round(dist) + " meters" :
                            String.format("%.2f", dist / 1000) + " km";

                    data.putString("total_distance", distance);

                    long totalTime = tracker.get(tracker.size() - 1).getTime();
                    String userTime = "" + totalTime;
                    data.putString("total_time", userTime);

                    ArrayList<Integer> graphData = getGraphInfo();
                    data.putIntegerArrayList("graph_array", graphData);
                }

                reset();
                showDetails.putExtras(data);
                startActivity(showDetails);
            }
        });
    }

    public void reset() {
        running = false;
        spd.setText("Speed ");
        avgSpeed.setText("Avg Speed ");
        cm.stop();
        cm.setText("00:00");
        tracker.removeAll(tracker);
        ttlDist = 0.0;
        loc = null;
        dis.setText("Distance ");
    }

    public double getAvgSpeed() {
        double result = 0;
        for (int i = 0; i < tracker.size(); i++) {
            result = result + tracker.get(i).getSpeed();
        }
        return result / tracker.size();
    }

    public void startTracking(LocationListener temp) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String PERMISSIONS_REQUIRED[] = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
            ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, 1);
        } else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, temp);
        }

    }

    public void finishTracking(LocationListener temp) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.removeUpdates(temp);
    }
    public ArrayList<Integer> getGraphInfo(){
        ArrayList<Integer> times = new ArrayList<>();
        double distanceCheck = 1000;
        for(int i=0;i<tracker.size();i++){
            if(tracker.get(i).get_distance()>=distanceCheck){
                distanceCheck = distanceCheck + 1000;
                times.add((int) Math.round(tracker.get(i).getTime()));
            }
        }
        return times;
    }

    public void onResume() {
        super.onResume();
    }

    public void getPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String PERMISSIONS_REQUIRED[] = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
            ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, 1);
        }
    }

}
