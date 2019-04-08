package com.pertamina.brightgasagen;

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

import com.pertamina.brightgasagen.model.BeritaDanPromo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AdapterBeritaDanPromo extends ArrayAdapter<BeritaDanPromo> {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    ArrayList<BeritaDanPromo> datas;
    Context context;
    Animation animationSlideInRight;
    Animation animationSlideOutLeft;
    Animation animationSlideInLeft;
    Animation animationSlideOutRight;
    private boolean isStarted = false;
    private int[]dummyBannerUrl = new int[]{R.drawable.ic_banner_slide_promo_1,
            R.drawable.ic_banner_slide_promo_2};
    private String[]dummyVideoUrl = new String[]{"https://lh6.googleusercontent.com/TY2WGUy6xH3x9sEZVqNV3wa7YT2k8QJ6Ih3OIb59LgXCzOLRCap3cNJgoLzOCqMuMZJ5lNMAdtOCgfM=w1276-h659",
            "https://lh3.googleusercontent.com/1pbIh2NOi1x4oS6wcmFm-uPzEQZ5Ghc1L2NkLysrw_0wW21lhke3LPSlRZGAVdUyX7qZPentLAllpLI=w1276-h659"};
    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());
    boolean isFling = true;
    ViewFlipper banner;

    private void runFlipper() {
        if (isStarted == false) {
            Message msg = mHandler.obtainMessage(42);
            mHandler.sendMessageDelayed(msg, 3000);
            isStarted = true;
        }
    }

    private void stopFlipper() {
        mHandler.removeMessages(42);
        Message msg = mHandler.obtainMessage(41);
        mHandler.sendMessageDelayed(msg, 2000);
        isStarted = false;
    }

    private void exitFlipper(){
        mHandler.removeMessages(42);
        mHandler.removeMessages(41);
        Message msg = mHandler.obtainMessage(40);
        mHandler.sendMessageDelayed(msg, 2000);
        isStarted = false;
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 42) {
                banner.showNext();
                msg = obtainMessage(42);
                sendMessageDelayed(msg, 5000);
            }else if(msg.what == 41){
                runFlipper();
            }else{
                //
            }
        }
    };

    public AdapterBeritaDanPromo(Context context, ArrayList<BeritaDanPromo> datas) {
        super(context, R.layout.item_berita_dan_promo,datas);
        this.datas = datas;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = null;
        switch (position){
            case 0:
                if(animationSlideInLeft == null){
                    animationSlideInRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
                    animationSlideOutLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
                    animationSlideInLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
                    animationSlideOutRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right);
                }
                rootView = LayoutInflater.from(context).inflate(R.layout.item_berita_dan_promo_banner,parent,false);
                banner = (ViewFlipper)rootView;
                banner.setInAnimation(animationSlideInRight);
                banner.setOutAnimation(animationSlideOutLeft);
                banner.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(final View view, final MotionEvent event) {
                        detector.onTouchEvent(event);
                        return true;
                    }
                });
                runFlipper();
                for(int i=0; i<dummyBannerUrl.length; i++){
                    ImageView imageView = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.item_beranda_banner, banner,false);
                    banner.addView(imageView);
                    imageView.setImageResource(dummyBannerUrl[i]);
                }
                break;
            case 1:
                rootView = LayoutInflater.from(context).inflate(R.layout.item_berita_dan_promo_video,parent,false);
                View video1 = rootView.findViewById(R.id.video1);
                video1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                View video2 = rootView.findViewById(R.id.video2);
                video2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

//                ImageView imageView1 = (ImageView)rootView.findViewById(R.id.video1image);
//                ImageView imageView2 = (ImageView)rootView.findViewById(R.id.video2image);
//                Picasso.with(context).load(dummyVideoUrl[0]).centerCrop().fit().placeholder(R.drawable.ic_brightgas_logo).into(imageView1);
//                Picasso.with(context).load(dummyVideoUrl[1]).centerCrop().fit().placeholder(R.drawable.ic_brightgas_logo).into(imageView2);
                break;
            default:
                rootView = LayoutInflater.from(context).inflate(R.layout.item_berita_dan_promo,parent,false);
                BeritaDanPromo data = getItem(position);
                TextView date = (TextView) rootView.findViewById(R.id.date);
                date.setText(data.date);
                TextView title = (TextView) rootView.findViewById(R.id.mytitle);
                title.setText(data.title);
                TextView content = (TextView) rootView.findViewById(R.id.content);
                content.setText(data.content);
                ImageView imageView = (ImageView) rootView.findViewById(R.id.imageview);
                Picasso.with(context).load(data.imageUrl).centerCrop().fit().placeholder(R.drawable.ic_brightgas_logo).into(imageView);
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
    public BeritaDanPromo getItem(int position) {
        int calculatedPosition = position-2;
        return datas.get(calculatedPosition);
    }

    @Override
    public void clear() {
        datas.clear();
    }

    @Override
    public void add(BeritaDanPromo object) {
        datas.add(object);
    }

    @Override
    public int getCount() {
        return datas.size()+2;
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
                    stopFlipper();
                    banner.setInAnimation(animationSlideInRight);
                    banner.setOutAnimation(animationSlideOutLeft);
                    banner.showNext();
                    isFling = true;
                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    stopFlipper();
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