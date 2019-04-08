package com.pertamina.brightgas;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class FragmentBeranda extends Fragment implements InterfaceOnRequestPermissionsResult {

    private static final String TAG = "fragment_beranda";

    private static final float INDICATOR_MIN_SCALE = 0.5f;
    private static final float INDICATOR_MAX_TRANSPARENCY = 0.8f;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());

    private Animation mAnimationSlideInRight;
    private Animation mAnimationSlideOutLeft;
    private Animation mAnimationSlideInLeft;
    private Animation mAnimationSlideOutRight;

    boolean mIsFling = true;

    private ViewFlipper mBanner;

    private static boolean mIsStarted = false;

    private int[] mDummyBannerUrl = new int[]{R.drawable.ic_banner_slide_home_1,
            R.drawable.ic_banner_slide_home_2,
            R.drawable.ic_banner_slide_home_3};

    private int[] mImageUrls = new int[]{
            R.drawable.ic_lpg_55_kg,
            R.drawable.ic_lpg_12_kg,
            R.drawable.ic_lpg_220_gr
    };

    private int[] mTextUrls = new int[]{
            R.drawable.ic_brightgas55,
            R.drawable.ic_brightgas12,
            R.drawable.ic_brightgas220
    };

    private TextView[] mMenus = new TextView[3];

    private View mMenuLine;

    private int mColorOn;

    private int mColorOff;

    private int mSelectedMenu = 0;

    private static boolean mIsToRight = true;

    private static int mCurrentBanner = 0;

    private ImageView mImageTabung;

    private ImageView mImageText;

    private static View[] mIndicators = new View[3];

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_beranda, container, false);

        mColorOn = ContextCompat.getColor(getActivity(), R.color.colorPrimary);
        mColorOff = ContextCompat.getColor(getActivity(), R.color.berandamenutextcolor);
        mImageTabung = (ImageView) rootView.findViewById(R.id.image_tabung);
        mImageText = (ImageView) rootView.findViewById(R.id.image_text);
        mMenuLine = rootView.findViewById(R.id.menu_line);
        mBanner = (ViewFlipper) rootView.findViewById(R.id.flipper);

        prepareIndicator(rootView);
        prepareMenu(rootView);
        prepareFlipper();
        prepareBanner();

        View orderButton = rootView.findViewById(R.id.order_button);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePurchase();
            }
        });
        return rootView;
    }

    private void prepareIndicator(View view) {
        mIndicators[0] = view.findViewById(R.id.indicator1);
        mIndicators[1] = view.findViewById(R.id.indicator2);
        mIndicators[2] = view.findViewById(R.id.indicator3);

        mIndicators[1].startAnimation(GlobalActivity.createAnimationSet(new Animation[]{
                GlobalActivity.createScaleAnimation(
                        INDICATOR_MIN_SCALE,
                        INDICATOR_MIN_SCALE,
                        INDICATOR_MIN_SCALE,
                        INDICATOR_MIN_SCALE,
                        0.5f,
                        0.5f,
                        0
                ),
                GlobalActivity.createAlphaAnimation(
                        INDICATOR_MIN_SCALE,
                        INDICATOR_MIN_SCALE,
                        300
                )
        }));

        mIndicators[2].startAnimation(GlobalActivity.createAnimationSet(new Animation[]{
                GlobalActivity.createScaleAnimation(
                        INDICATOR_MIN_SCALE,
                        INDICATOR_MIN_SCALE,
                        INDICATOR_MIN_SCALE,
                        INDICATOR_MIN_SCALE,
                        0.5f,
                        0.5f,
                        0
                ),
                GlobalActivity.createAlphaAnimation(
                        INDICATOR_MIN_SCALE,
                        INDICATOR_MIN_SCALE,
                        300
                )
        }));
    }

    private void prepareMenu(View view) {
        mMenus[0] = (TextView) view.findViewById(R.id.menu_one);
        mMenus[1] = (TextView) view.findViewById(R.id.menu_two);
        mMenus[2] = (TextView) view.findViewById(R.id.menu_three);

        mMenus[0].post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = mMenuLine.getLayoutParams();
                params.width = mMenus[0].getWidth();
                mMenuLine.setLayoutParams(params);
            }
        });

        for (int i = 0; i < mMenus.length; i++) {
            final int menusIndex = i;
            mMenus[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectedMenu(menusIndex);
                }
            });
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 42) {
                mBanner.showNext();
                nextIndicator();
                msg = obtainMessage(42);
                sendMessageDelayed(msg, 5000);
            } else if (msg.what == 41) {
                prepareFlipper();
            }
        }
    };

    private void prepareFlipper() {
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
    public void onPause() {
        exitFlipper();
        super.onPause();
    }

    @Override
    public void onStop() {
        exitFlipper();
        super.onStop();
    }

    @Override
    public void onResume() {
        prepareFlipper();
        super.onResume();
        ((BaseActivity) getActivity()).setInterfaceOnRequestPermissionsResult(this);
        getActivity().setTitle("Beranda");
    }

    static private void nextIndicator() {
        mIndicators[mCurrentBanner].startAnimation(GlobalActivity.createAnimationSet(new Animation[]{
                GlobalActivity.createScaleAnimation(1, INDICATOR_MIN_SCALE, 1, INDICATOR_MIN_SCALE, 0.5f, 0.5f, 0),
                GlobalActivity.createAlphaAnimation(INDICATOR_MAX_TRANSPARENCY, INDICATOR_MIN_SCALE, 300)
        }));

        if (mIsToRight) {
            mCurrentBanner++;
            if (mCurrentBanner >= mIndicators.length) {
                mCurrentBanner = 0;
            }
        } else {
            mCurrentBanner--;
            if (mCurrentBanner < 0) {
                mCurrentBanner = mIndicators.length - 1;
            }
        }

        mIndicators[mCurrentBanner].startAnimation(GlobalActivity.createAnimationSet(new Animation[]{
                GlobalActivity.createScaleAnimation(INDICATOR_MIN_SCALE, 1, INDICATOR_MIN_SCALE, 1, 0.5f, 0.5f, 0),
                GlobalActivity.createAlphaAnimation(INDICATOR_MIN_SCALE, INDICATOR_MAX_TRANSPARENCY, 300)
        }));
    }

    private void makePurchase() {
        // Check user condition, if not login,
        // ask user to login or register
        if (User.id != null) {
            ((BaseActivity) getActivity()).changeFragment(new FragmentPilihAlamat(), false, true);
        } else {
            ((BaseActivity) getActivity()).changeActivity(LoginActivity.class, true);
        }
    }

    private void prepareBanner() {
        mAnimationSlideInRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        mAnimationSlideOutLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
        mAnimationSlideInLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
        mAnimationSlideOutRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right);

        mBanner.setInAnimation(mAnimationSlideInRight);
        mBanner.setOutAnimation(mAnimationSlideOutLeft);
        mBanner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });

        for (int i = 0; i < mDummyBannerUrl.length; i++) {
            ImageView imageView = (ImageView) LayoutInflater.from(getContext()).inflate(
                    R.layout.item_beranda_banner,
                    mBanner,
                    false
            );
            mBanner.addView(imageView);
            imageView.setImageResource(mDummyBannerUrl[i]);
        }
    }

    private void setSelectedMenu(int index) {
        if (mSelectedMenu != index) {
            mMenus[mSelectedMenu].setTextColor(mColorOff);
            mMenuLine.startAnimation(((BaseActivity) getActivity()).createTranslateAnimation(
                    mSelectedMenu * mMenuLine.getWidth(),
                    index * mMenuLine.getWidth(),
                    0,
                    0,
                    500
            ));
            mSelectedMenu = index;
            mMenus[mSelectedMenu].setTextColor(mColorOn);
            mImageTabung.setImageResource(mImageUrls[mSelectedMenu]);
            mImageText.setImageResource(mTextUrls[mSelectedMenu]);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        makePurchase();
    }

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "ON SINGLE TAP UP " + mIsFling);
            if (!mIsFling) {

            }
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
                    mIsToRight = true;
                    mBanner.showNext();
                    nextIndicator();
                    mIsFling = true;
                    return true;

                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    stopFlipper();
                    mBanner.setInAnimation(mAnimationSlideInLeft);
                    mBanner.setOutAnimation(mAnimationSlideOutRight);
                    mBanner.showPrevious();
                    mIsToRight = false;
                    nextIndicator();
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
