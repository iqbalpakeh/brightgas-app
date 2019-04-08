package com.pertamina.brightgasse;

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
import com.pertamina.brightgasse.model.Dashboard;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterDashboard extends ArrayAdapter<Dashboard> {

    private static final String TAG = "adapter_dashboard";

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private ArrayList<Dashboard> mDatas;

    private Context mContext;

    private Animation mAnimationSlideInRight;
    private Animation mAnimationSlideOutLeft;
    private Animation mAnimationSlideInLeft;
    private Animation mAnimationSlideOutRight;

    private String[] chartMonth = new String[]{
            "JANUARI",
            "FEBRUARI",
            "MARET",
            "APRIL",
            "MEI",
            "JUNI",
            "JULI",
            "AGUSTUS",
            "SEPTEMBER",
            "OKTOBER",
            "NOVEMBER",
            "DESEMBER"
    };

    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());

    private boolean isFling = true;

    private ViewFlipper banner;

    private int currentYear;

    public AdapterDashboard(Context context, ArrayList<Dashboard> datas) {

        super(context, R.layout.item_dashboard_schedule, datas);

        mDatas = datas;

        mContext = context;

        currentYear = Calendar.getInstance().get(Calendar.YEAR);

        mAnimationSlideInRight = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
        mAnimationSlideOutLeft = AnimationUtils.loadAnimation(context, R.anim.slide_out_left);
        mAnimationSlideInLeft = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
        mAnimationSlideOutRight = AnimationUtils.loadAnimation(context, R.anim.slide_out_right);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView;

        switch (position) {
            case 0:
                rootView = LayoutInflater.from(mContext).inflate(R.layout.item_dashboard_chart, parent, false);
                banner = (ViewFlipper) rootView.findViewById(R.id.flipper);
                banner.setInAnimation(mAnimationSlideInRight);
                banner.setOutAnimation(mAnimationSlideOutLeft);
                banner.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(final View view, final MotionEvent event) {
                        detector.onTouchEvent(event);
                        return true;
                    }
                });

                for (String aChartMonth : chartMonth) {
                    TextView bannerText = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_dashboard_date, banner, false);
                    banner.addView(bannerText);
                    bannerText.setText(aChartMonth + " " + currentYear);
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

                LineChartView chartView = (LineChartView) rootView.findViewById(R.id.chartview);
                chartView.setYAxis(false).setStep(5);
                chartView.setYLabels(AxisController.LabelPosition.NONE);
                LineSet dataset = new LineSet();

                for (int i = 1; i < 10; i++) {
                    dataset.addPoint(i + "", i * 1000);
                }

                dataset.setColor(ContextCompat.getColor(mContext, R.color.colorAccent))
                        .setDotsColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                        .setThickness(4)
                        .setDotsStrokeColor(ContextCompat.getColor(mContext, R.color.colorAccent))
                        .setDotsStrokeThickness(2);
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
                rootView = LayoutInflater.from(mContext).inflate(R.layout.item_dashboard_schedule, parent, false);
                Dashboard data = getItem(position);
                TextView name = (TextView) rootView.findViewById(R.id.name);
                name.setText(data.name);
                TextView inv = (TextView) rootView.findViewById(R.id.inv);
                inv.setText(data.inv);
                TextView date = (TextView) rootView.findViewById(R.id.date);
                date.setText(data.date);
                TextView time = (TextView) rootView.findViewById(R.id.my_time);
                time.setText(data.time);
                break;

        }
        return rootView;
    }

    public void removeLast() {
        if (mDatas != null && mDatas.size() > 0) {
            mDatas.remove(mDatas.size() - 1);
            notifyDataSetChanged();
        }
    }

    @Override
    public Dashboard getItem(int position) {
        int calculatedPosition = position - 1;
        return mDatas.get(calculatedPosition);
    }

    @Override
    public void clear() {
        mDatas.clear();
    }

    @Override
    public void add(Dashboard object) {
        mDatas.add(object);
    }

    @Override
    public int getCount() {
        return mDatas.size() + 1;
    }

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "ON SINGLE TAP UP " + isFling);
            if (!isFling) {
                //FragmentDetailInformation fragmentDetailInformation = new FragmentDetailInformation();
                //fragmentDetailInformation.setData(data);
                //BaseActivity.self.changeFragment(fragmentDetailInformation,false);
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
            Log.d(TAG, "ON FLING");
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    banner.setInAnimation(mAnimationSlideInRight);
                    banner.setOutAnimation(mAnimationSlideOutLeft);
                    banner.showNext();
                    isFling = true;
                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    banner.setInAnimation(mAnimationSlideInLeft);
                    banner.setOutAnimation(mAnimationSlideOutRight);
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