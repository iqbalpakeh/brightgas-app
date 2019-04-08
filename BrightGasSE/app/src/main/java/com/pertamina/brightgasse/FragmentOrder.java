package com.pertamina.brightgasse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.pertamina.brightgasse.firebase.FirebaseLoadable;
import com.pertamina.brightgasse.firebase.FirebaseQueryOrder;
import com.pertamina.brightgasse.firebase.models.Package;
import com.pertamina.brightgasse.model.SimpleOrder;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentOrder extends Fragment implements RequestLoaderInterface, FirebaseLoadable {

    private static final String TAG = "frag_my_order";

    private final int MENU_PENDING = 0;

    private final int MENU_ON_PROCESS = 1;

    private BottomMenu mWaktuPesan;

    private BottomMenu mWaktuAntar;

    private BottomMenu mSelectedMenu;

    private TextView[] mMenus = new TextView[2];

    private View mMenuLine;

    private View mRootView;

    private int mColorOn;

    private int mColorOff;

    private int mSelectedTopMenu = 0;

    private ArrayList<SimpleOrder> mOrderListsOnGoing = new ArrayList<>();

    private ArrayList<SimpleOrder> mOrderListsPendingOrder = new ArrayList<>();

    private FragmentOrderListContent mFragment;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Transaksi");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mColorOn = ContextCompat.getColor(getActivity(), R.color.chat_right_name);
        mColorOff = ContextCompat.getColor(getActivity(), R.color.beranda_menu_text_color);

        mRootView = inflater.inflate(R.layout.fragment_order, container, false);
        mWaktuPesan = new BottomMenu(mRootView.findViewById(R.id.waktu_pesan), R.drawable.ic_clock, "Waktu Pesan");
        mWaktuAntar = new BottomMenu(mRootView.findViewById(R.id.waktu_antar), R.drawable.ic_clock, "Waktu Antar");

        mMenus[MENU_PENDING] = (TextView) mRootView.findViewById(R.id.menu_pending);
        mMenus[MENU_ON_PROCESS] = (TextView) mRootView.findViewById(R.id.menu_on_process);

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
        mMenuLine = mRootView.findViewById(R.id.menu_line);

        mFragment = new FragmentOrderListContent();
        mFragment.setData(mOrderListsPendingOrder);
        ((BaseActivity) getActivity()).changeFragment(R.id.fragment_content, mFragment, true, false);

        ((BaseActivity) getActivity()).showLoading(true);

        new RequestLoader(this).loadRequest(0, new BasicNameValuePair[]{}, "transaction", "get_trx_se");

        new FirebaseQueryOrder(getActivity(), this).queryOrders();

        return mRootView;
    }

    private void setSelectedMenu(int index) {

        if (mSelectedTopMenu != index) {

            mMenus[mSelectedTopMenu].setTextColor(mColorOff);
            mMenuLine.startAnimation(((BaseActivity) getActivity()).createTranslateAnimation(
                    mSelectedTopMenu * mMenuLine.getWidth(),
                    index * mMenuLine.getWidth(),
                    0,
                    0,
                    500));
            mSelectedTopMenu = index;
            mMenus[mSelectedTopMenu].setTextColor(mColorOn);

            switch (index) {
                case MENU_PENDING:
                    mFragment = new FragmentOrderListContent();
                    mFragment.setData(mOrderListsPendingOrder);
                    ((BaseActivity) getActivity()).changeFragment(R.id.fragment_content, mFragment, true, true);
                    break;

                case MENU_ON_PROCESS:
                    mFragment = new FragmentOrderListContent();
                    mFragment.setData(mOrderListsOnGoing);
                    ((BaseActivity) getActivity()).changeFragment(R.id.fragment_content, mFragment, true, true);
                    break;
            }
        }
    }

    @Override
    public void setFirebaseData(ArrayList<DataSnapshot> dataSnapshots) {

        ((BaseActivity) getActivity()).showLoading(false);

        DataSnapshot dataSnapshot = dataSnapshots.get(0);

        if (dataSnapshot.exists()) {

            mOrderListsPendingOrder.clear();
            mOrderListsOnGoing.clear();

            for (DataSnapshot uids : dataSnapshot.getChildren()) {

                for (DataSnapshot ordersPerUid : uids.getChildren()) {

                    Log.d(TAG, ordersPerUid.toString());

                    Package aPackage = ordersPerUid.getValue(Package.class);

                    if (aPackage.statusId.equals(Package.STATUS_PENDING + "")) {
                        mOrderListsPendingOrder.add(new SimpleOrder(
                                ordersPerUid.getKey(),
                                aPackage.invoice,
                                "TODO_countdown",
                                aPackage.deliveryDate,
                                aPackage.startTime() + " - " + aPackage.endTime(),
                                "TODO_distance",
                                "TODO_item",
                                aPackage.grandTotal,
                                Integer.parseInt(aPackage.statusId),
                                aPackage.agentId,
                                aPackage.driverId,
                                aPackage.uid));
                        
                    } else if (aPackage.statusId.equals(Package.STATUS_DIPROSES + "")) {
                        mOrderListsOnGoing.add(new SimpleOrder(
                                ordersPerUid.getKey(),
                                aPackage.invoice,
                                "TODO_countdown",
                                aPackage.deliveryDate,
                                aPackage.startTime() + " - " + aPackage.endTime(),
                                "TODO_distance",
                                "TODO_item",
                                aPackage.grandTotal,
                                Integer.parseInt(aPackage.statusId),
                                aPackage.agentId,
                                aPackage.driverId,
                                aPackage.uid));
                    }
                }
            }
        }

        if (mFragment != null) {
            switch (mSelectedTopMenu) {
                case 0:
                    mFragment.replaceDatas(mOrderListsPendingOrder);
                    break;
                case 1:
                    mFragment.replaceDatas(mOrderListsOnGoing);
                    break;
            }
        }
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {

        try {

            JSONObject jsonObject = new JSONObject(result);

            if (jsonObject.getInt("status") == 1) {

                mOrderListsPendingOrder.clear();
                mOrderListsOnGoing.clear();
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i).getJSONObject("header");
                        int status = jsonObject.getInt("tran_status_id");
                        if (status == 0) {
                            mOrderListsPendingOrder.add(new SimpleOrder(
                                    jsonObject.getString("tran_id"),
                                    jsonObject.getString("tran_no_invoice"),
                                    jsonObject.getString("diff"),
                                    jsonObject.getString("tran_date"),
                                    jsonObject.getString("tran_time_start") + " - " + jsonObject.getString("tran_time_end"),
                                    jsonObject.getString("distance"),
                                    jsonObject.getString("tran_total_item"),
                                    jsonObject.getString("tran_grand_total"),
                                    status,
                                    jsonObject.getString("tran_agen_id"),
                                    jsonObject.getString("tran_driver_id"),
                                    ""));

                        } else {
                            mOrderListsOnGoing.add(new SimpleOrder(
                                    jsonObject.getString("tran_id"),
                                    jsonObject.getString("tran_no_invoice"),
                                    jsonObject.getString("diff"),
                                    jsonObject.getString("tran_date"),
                                    jsonObject.getString("tran_time_start") + " - " + jsonObject.getString("tran_time_end"),
                                    jsonObject.getString("distance"),
                                    jsonObject.getString("tran_total_item"),
                                    jsonObject.getString("tran_grand_total"),
                                    status,
                                    jsonObject.getString("tran_agen_id"),
                                    jsonObject.getString("tran_driver_id"),
                                    ""));
                        }
                    }
                }

                if (mFragment != null) {
                    switch (mSelectedTopMenu) {
                        case 0:
                            mFragment.replaceDatas(mOrderListsPendingOrder);
                            break;
                        case 1:
                            mFragment.replaceDatas(mOrderListsOnGoing);
                            break;
                    }
                }

            } else {
                ((BaseActivity) getActivity()).showDialog("", jsonObject.getString("message"), "", "Ok");
            }
        } catch (Exception ex) {
            ((BaseActivity) getActivity()).showDialog("Error", ex.toString(), "", "Ok");
        }
    }

    class BottomMenu {

        ImageView mImageView;
        TextView mTextView;
        boolean mIsSelected;

        BottomMenu(View rootView, int imageId, String text) {
            mImageView = (ImageView) rootView.findViewById(R.id.imageview);
            mImageView.setImageResource(imageId);
            mTextView = (TextView) rootView.findViewById(R.id.textview);
            mTextView.setText(text);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelected(true);
                }
            });
        }

        void setSelected(boolean val) {
            if (mIsSelected != val) {
                mIsSelected = val;
                int color;
                if (mIsSelected) {
                    if (mSelectedMenu != null) {
                        mSelectedMenu.setSelected(false);
                    }
                    mSelectedMenu = this;
                    color = ContextCompat.getColor(getContext(), R.color.chat_right_name);
                } else {
                    color = ContextCompat.getColor(getContext(), R.color.berhasilgrey);
                }
                mImageView.setColorFilter(color);
                mTextView.setTextColor(color);
            }
        }

    }

}
