package com.android.anasdjebbari.assignment3;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

class DrawView extends View {
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    LinkedHashMap dt = new LinkedHashMap<Integer, Float>();
    ArrayList<Float> graphValues = new ArrayList<>();
    Integer base = 0;

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {}

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for(int i=0;i<graphValues.size();i++){
            float x = graphValues.get(i);
            insert(x);
        }
//        testing
        insert(7.0f);
        insert(7.0f);
        insert(5.0f);
        insert(8.0f);
        insert(7.0f);
        insert(5.0f);
        insert(8.0f);
        insert(7.0f);

        drawupChart(canvas, dt);
        dt.clear();
    }

    public void insert(float time){
        int temp = dt.size();
        Integer km = base + (temp * 86400);
        dt.put(km, time);
    }

    public void set_temp(ArrayList list){
        graphValues.addAll(list);
    }

    public void drawupChart(Canvas canvas, LinkedHashMap dt) {
        //Typeface definitions
        Typeface robotoNormal = Typeface.create("Roboto",Typeface.NORMAL);
        Typeface robotoBold = Typeface.create("Roboto",Typeface.BOLD);

        // Clear background
        //paint.setColor(Color.parseColor("#3498DB"));
        //canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        int screenWidthHeight = getWidth();

        float scale = 1.0f;

        float graphWidthHeight = screenWidthHeight - 70;
        float sqr = (screenWidthHeight - graphWidthHeight)/2;
        float left = sqr;
        float top = sqr;


        /*
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.parseColor("#000000"));
        canvas.drawLine(30*scale, 60*scale, screenWidth-30*scale, 60*scale, paint);

        paint.setTypeface(robotoBold);
        paint.setColor(Color.parseColor("#0699fa"));
        paint.setTextSize(35*scale);
        canvas.drawText("Anas the wanker :", 30*scale, 120*scale, paint);
        canvas.drawLine(30*scale, 150*scale, screenWidth-30*scale, 150*scale, paint);

=       paint.setColor(Color.parseColor("#0699fa"));
        paint.setTextSize(35*scale);
        canvas.drawText("Average Speed: ", 30*scale, 240*scale, paint);

        paint.setColor(Color.parseColor("#0699fa"));
        paint.setTypeface(robotoNormal);
        paint.setTextSize(35*scale);
        canvas.drawText("Total Time: ", 30*scale, (int)320*scale, paint);

        paint.setTypeface(robotoNormal);
        paint.setTextSize(35*scale);
        canvas.drawText("Total Distance Travelled: ", 30*scale, (int)400*scale, paint);
        */

        // Margin and inner dimensions for the entire graph
        float margin = (8 * scale);
        float innerWidth = (graphWidthHeight - 2*margin);
        float innerHeight = graphWidthHeight - 8*margin;

        // Auto detect xmin, xmax, ymin, ymax
        float xmin = 0;
        float xmax = 0;
        float ymin = 0;
        float ymax = 0;
        boolean s = false;

        Iterator<Integer> keySetIterator = dt.keySet().iterator();
        while(keySetIterator.hasNext()){
            Integer time = keySetIterator.next();
            System.out.println(time);
            float value = (Float) dt.get(time);
            System.out.println(value);
            if (!s) {
                xmin = time;
                xmax = time;
                ymin = value;
                ymax = value;
                s = true;
            }

            if (value>ymax) ymax = value;
            if (value<ymin) ymin = value;
            if (time>xmax) xmax = time;
            if (time<xmin) xmin = time;
        }

        float r = (ymax - ymin);
        ymax = (ymax - (r / 2f)) + (r/1.5f);

        ymin = 0;
        int temp = 10000;
        float barWidth = 3600*20;
        xmin -= (barWidth /2);
        xmax += (barWidth /2);

        float barWidthpx = (barWidth / (xmax - xmin)) * innerWidth;

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(35*scale);

        keySetIterator = dt.keySet().iterator();
        int km = 1;

        while(keySetIterator.hasNext()){

            Integer time = keySetIterator.next();
            float value = (Float) dt.get(time);

            float px = (((time - xmin) / (xmax - xmin))) * innerWidth;
            float py = ((value - ymin) / (ymax - ymin)) * innerHeight;
            //System.out.println(px+" "+py);

            float barLeft = left + margin + px - barWidthpx/2;
            float barBottom = top + margin + innerHeight;

            float barTop = barBottom - py;
            float barRight = barLeft + barWidthpx;

            paint.setColor(Color.parseColor("#43464B"));
            canvas.drawRect(barLeft,barTop,barRight,barBottom,paint);

            if (py>38*scale) {
                int offset = (int)(55*scale);
                // System.out.println("test"+value);
                convert(value);
                paint.setTypeface(robotoNormal);

                paint.setColor(Color.parseColor("#ffffff"));
                paint.setTextSize(40*scale);
                canvas.drawText(String.format("%.0f",value), left+margin+px, barTop + offset, paint);
                paint.setTextSize(30*scale);
                canvas.drawText(String.format("sec"), left+margin+px, barTop + offset+40, paint);
                paint.setTextSize(32*scale);
                canvas.drawText(String.format(String.valueOf(km) + "KM"), left+margin+px, barBottom + offset, paint);
            }
            km++;
        }
    }

    @Override
    protected void onMeasure(int w, int h) {
        super.onMeasure(w, h);
        int width = MeasureSpec.getSize(w);
        int height = MeasureSpec.getSize(h);
        int size = width > height ? height : width;
        setMeasuredDimension(size, size);
    }

    public void convert (float value){
        String s = String.format(String.valueOf(value));
//        System.out.println("testing"+s);
    }
}
