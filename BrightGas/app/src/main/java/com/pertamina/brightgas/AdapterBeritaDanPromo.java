package com.pertamina.brightgas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.pertamina.brightgas.model.BeritaDanPromo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterBeritaDanPromo extends ArrayAdapter<BeritaDanPromo> {

    private static final String TAG = "adapter_berita_promo";

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private final GestureDetector mDetector = new GestureDetector(new SwipeGestureDetector());

    private Animation mAnimationSlideInRight;
    private Animation mAnimationSlideOutLeft;
    private Animation mAnimationSlideInLeft;
    private Animation mAnimationSlideOutRight;

    private boolean mIsFling = true;

    private ViewFlipper mBanner;

    private boolean mIsStarted = false;

    private ArrayList<BeritaDanPromo> mDatas;

    private Context mContext;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 42) {
                mBanner.showNext();
                msg = obtainMessage(42);
                sendMessageDelayed(msg, 5000);
            } else if (msg.what == 41) {
                runFlipper();
            } else {
                //
            }
        }
    };

    private int[] dummyBannerUrl = new int[]{
            R.drawable.ic_banner_slide_promo_1,
            R.drawable.ic_banner_slide_promo_2
    };

    private String[] dummyVideoUrl = new String[]{
            "https://lh6.googleusercontent.com/TY2WGUy6xH3x9sEZVqNV3wa7YT2k8QJ6Ih3OIb59LgXCzOLRCap3cNJgoLzOCqMuMZJ5lNMAdtOCgfM=w1276-h659",
            "https://lh3.googleusercontent.com/1pbIh2NOi1x4oS6wcmFm-uPzEQZ5Ghc1L2NkLysrw_0wW21lhke3LPSlRZGAVdUyX7qZPentLAllpLI=w1276-h659"
    };

    public AdapterBeritaDanPromo(Context context, ArrayList<BeritaDanPromo> datas) {
        super(context, R.layout.item_berita_dan_promo, datas);
        this.mDatas = datas;
        this.mContext = context;
    }

    private void runFlipper() {
        if (!mIsStarted) {
            Message msg = mHandler.obtainMessage(42);
            mHandler.sendMessageDelayed(msg, 3000);
            mIsStarted = true;
        }
    }

    private void stopFlipper() {
        mHandler.removeMessages(42);
        Message msg = mHandler.obtainMessage(41);
        mHandler.sendMessageDelayed(msg, 2000);
        mIsStarted = false;
    }

    private void exitFlipper() {
        mHandler.removeMessages(42);
        mHandler.removeMessages(41);
        Message msg = mHandler.obtainMessage(40);
        mHandler.sendMessageDelayed(msg, 2000);
        mIsStarted = false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        switch (position) {
            case 0:
                if (mAnimationSlideInLeft == null) {
                    mAnimationSlideInRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
                    mAnimationSlideOutLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
                    mAnimationSlideInLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
                    mAnimationSlideOutRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right);
                }
                view = LayoutInflater.from(mContext).inflate(R.layout.item_berita_dan_promo_banner, parent, false);
                mBanner = (ViewFlipper) view;
                mBanner.setInAnimation(mAnimationSlideInRight);
                mBanner.setOutAnimation(mAnimationSlideOutLeft);
                mBanner.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(final View view, final MotionEvent event) {
                        mDetector.onTouchEvent(event);
                        return true;
                    }
                });
                runFlipper();
                for (int i = 0; i < dummyBannerUrl.length; i++) {
                    ImageView imageView = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.item_beranda_banner, mBanner, false);
                    mBanner.addView(imageView);
                    imageView.setImageResource(dummyBannerUrl[i]);
                }
                break;

            case 1:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_berita_dan_promo_video, parent, false);
                View video1 = view.findViewById(R.id.video1);
                video1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                View video2 = view.findViewById(R.id.video2);
                video2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;

            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_berita_dan_promo, parent, false);
                BeritaDanPromo data = getItem(position);
                TextView date = (TextView) view.findViewById(R.id.date);
                date.setText(data.date);
                TextView title = (TextView) view.findViewById(R.id.my_title);
                title.setText(data.title);
                TextView content = (TextView) view.findViewById(R.id.content);
                content.setText(data.content);
                ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
                Picasso.with(mContext).load(data.imageUrl).centerCrop().fit().placeholder(R.drawable.ic_brightgas_logo).into(imageView);
                break;
        }
        return view;
    }

    public void removeLast() {
        if (mDatas != null && mDatas.size() > 0) {
            mDatas.remove(mDatas.size() - 1);
            notifyDataSetChanged();
        }
    }

    @Override
    public BeritaDanPromo getItem(int position) {
        int calculatedPosition = position - 2;
        return mDatas.get(calculatedPosition);
    }

    @Override
    public void clear() {
        mDatas.clear();
    }

    @Override
    public void add(BeritaDanPromo object) {
        mDatas.add(object);
    }

    @Override
    public int getCount() {
        return mDatas.size() + 2;
    }

    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "ON SINGLE TAP UP " + mIsFling);
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            mIsFling = false;
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "ON FLING");
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    stopFlipper();
                    mBanner.setInAnimation(mAnimationSlideInRight);
                    mBanner.setOutAnimation(mAnimationSlideOutLeft);
                    mBanner.showNext();
                    mIsFling = true;
                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    stopFlipper();
                    mBanner.setInAnimation(mAnimationSlideInLeft);
                    mBanner.setOutAnimation(mAnimationSlideOutRight);
                    mBanner.showPrevious();
                    mIsFling = true;
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }

}