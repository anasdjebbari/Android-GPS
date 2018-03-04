package com.android.anasdjebbari.assignment3;


public class GPSActivityDetails {
    double speed, langt, longt, distance;
    long time;

    void set_speed(Double s){
        speed = s;
    }
    Double getSpeed(){
        return speed;
    }
    void setLangt(Double l){
        langt = l;
    }
    void setLongt(Double l){
        longt = l;
    }
    void set_distance_traveled(Double d){
        distance = d;
    }
    Double get_distance(){
        return distance;
    }
    void setTime(long l){
        time = l;
    }
    long getTime(){
        return time;
    }
}


