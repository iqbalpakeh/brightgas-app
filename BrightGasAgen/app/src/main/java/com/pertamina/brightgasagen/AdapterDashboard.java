package com.pertamina.brightgasagen;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.easing.BounceEase;
import com.pertamina.brightgasagen.model.Dashboard;

import java.util.ArrayList;
import java.util.Calendar;


public class AdapterDashboard extends ArrayAdapter<Dashboard> {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    ArrayList<Dashboard> datas;
    Context context;
    Animation animationSlideInRight;
    Animation animationSlideOutLeft;
    Animation animationSlideInLeft;
    Animation animationSlideOutRight;
    private boolean isStarted = false;
    private String[] chartMonth = new String[]{"JANUARI", "FEBRUARI","MARET","APRIL","MEI","JUNI","JULI","AGUSTUS","SEPTEMBER","OKTOBER","NOVEMBER","DESEMBER"};
    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());
    boolean isFling = true;
    ViewFlipper banner;
    int currentYear;

    public AdapterDashboard(Context context, ArrayList<Dashboard> datas) {
        super(context, R.layout.item_dashboard_schedule,datas);
        this.datas = datas;
        this.context = context;
        currentYear = Calendar.getInstance().get(Calendar.YEAR);
        animationSlideInRight = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
        animationSlideOutLeft = AnimationUtils.loadAnimation(context, R.anim.slide_out_left);
        animationSlideInLeft = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
        animationSlideOutRight = AnimationUtils.loadAnimation(context, R.anim.slide_out_right);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = null;
        switch (position){
            case 0:
                rootView = LayoutInflater.from(context).inflate(R.layout.item_dashboard_chart,parent,false);
                banner = (ViewFlipper)rootView.findViewById(R.id.flipper);
                banner.setInAnimation(animationSlideInRight);
                banner.setOutAnimation(animationSlideOutLeft);
                banner.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(final View view, final MotionEvent event) {
                        detector.onTouchEvent(event);
                        return true;
                    }
                });

                for(int i=0; i<chartMonth.length; i++){
                    TextView bannerText = (TextView) LayoutInflater.from(context).inflate(R.layout.item_dashboard_date,banner,false);
                    banner.addView(bannerText);
                    bannerText.setText(chartMonth[i]+" "+currentYear);
                }

                View arrowLeft = rootView.findViewById(R.id.arrowleft);
                arrowLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        banner.showPrevious();
                    }
                });

                View arrowRight = rootView.findViewById(R.id.arrowright);
                arrowRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        banner.showNext();
                    }
                });

                LineChartView chartView = (LineChartView)rootView.findViewById(R.id.chartview);
                chartView.setYAxis(false).setStep(5);
                chartView.setYLabels(AxisController.LabelPosition.NONE);
                LineSet dataset = new LineSet();
                for(int i=1; i<10; i++){
                    dataset.addPoint(i+"", i*1000);
                }
                dataset.setColor(ContextCompat.getColor(context,R.color.colorAccent))
                        .setDotsColor(ContextCompat.getColor(context,R.color.colorPrimary))
                        .setThickness(4)
                        .setDotsStrokeColor(ContextCompat.getColor(context,R.color.colorAccent))
                        .setDotsStrokeThickness(2)
                ;
                chartView.addData(dataset);
                chartView.notifyDataUpdate();
                com.db.chart.view.animation.Animation anim = new com.db.chart.view.animation.Animation()
                        .setEasing(new BounceEase());
                chartView.show(anim);

                final View tabungExample = rootView.findViewById(R.id.tabungexample);
                final View lusinExample = rootView.findViewById(R.id.lusinexample);
                tabungExample.post(new Runnable() {
                    @Override
                    public void run() {
                        int defaultWidth = tabungExample.getWidth();
                        ViewGroup.LayoutParams params = lusinExample.getLayoutParams();
                        params.width = defaultWidth;
                        lusinExample.setLayoutParams(params);
                    }
                });

                break;
            default:
                rootView = LayoutInflater.from(context).inflate(R.layout.item_dashboard_schedule,parent,false);
                Dashboard data = getItem(position);
                TextView name = (TextView)rootView.findViewById(R.id.name);
                name.setText(data.name);
                TextView inv = (TextView)rootView.findViewById(R.id.inv);
                inv.setText(data.inv);
                TextView date = (TextView)rootView.findViewById(R.id.date);
                date.setText(data.date);
                TextView time = (TextView)rootView.findViewById(R.id.mytime);
                time.setText(data.time);
                break;

        }
        return rootView;
    }

    public void removeLast(){
        if(datas!=null && datas.size()>0){
            datas.remove(datas.size()-1);
            notifyDataSetChanged();
        }
    }

    @Override
    public Dashboard getItem(int position) {
        int calculatedPosition = position-1;
        return datas.get(calculatedPosition);
    }

    @Override
    public void clear() {
        datas.clear();
    }

    @Override
    public void add(Dashboard object) {
        datas.add(object);
    }

    @Override
    public int getCount() {
        return datas.size()+1;
    }

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d("gugum","ON SINGLE TAP UP "+isFling);
            if(!isFling){
//                FragmentDetailInformation fragmentDetailInformation = new FragmentDetailInformation();
//                fragmentDetailInformation.setData(data);
//                BaseActivity.self.changeFragment(fragmentDetailInformation,false);
            }
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            isFling = false;
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("gugum","ON FLING");
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    banner.setInAnimation(animationSlideInRight);
                    banner.setOutAnimation(animationSlideOutLeft);
                    banner.showNext();
                    isFling = true;
                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    banner.setInAnimation(animationSlideInLeft);
                    banner.setOutAnimation(animationSlideOutRight);
                    banner.showPrevious();
                    isFling = true;
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }

}