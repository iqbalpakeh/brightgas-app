package com.pertamina.brightgas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.pertamina.brightgas.firebase.FirebaseLoadable;
import com.pertamina.brightgas.firebase.FirebaseQueryOrder;
import com.pertamina.brightgas.firebase.models.Package;
import com.pertamina.brightgas.model.RiwayatTransaksi;
import com.pertamina.brightgas.retrofit.listorder.ListOrderClient;
import com.pertamina.brightgas.retrofit.listorder.ListOrderInterface;
import com.pertamina.brightgas.retrofit.listorder.ListOrderResponse;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentRiwayatTransaksi extends Fragment
        implements RequestLoaderInterface, FirebaseLoadable, ListOrderInterface {

    private static final String TAG = "riwayat_transaksi";

    private TextView[] mMenus = new TextView[2];

    private View mMenuLine;

    private int mColorOn;
    private int mColorOff;

    private static final int MENU_ON_GOING = 0;
    private static final int MENU_HISTORY = 1;
    private int mSelectedMenu = MENU_ON_GOING;

    private ArrayList<RiwayatTransaksi> mOnGoingDatas = new ArrayList<>();
    private ArrayList<RiwayatTransaksi> mHistoryDatas = new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Riwayat Transaksi");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedmenu", mSelectedMenu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_riwayat_transaksi, container, false);
        mColorOn = ContextCompat.getColor(getActivity(), R.color.colorPrimary);
        mColorOff = ContextCompat.getColor(getActivity(), R.color.berandamenutextcolor);
        mMenuLine = rootView.findViewById(R.id.menu_line);
        mMenus[MENU_ON_GOING] = (TextView) rootView.findViewById(R.id.menu_on_going);
        mMenus[MENU_HISTORY] = (TextView) rootView.findViewById(R.id.menu_history);
        prepareMenu();
        requestTransactionHistory();
        return rootView;
    }

    private void prepareMenu() {
        mMenus[MENU_ON_GOING].post(new Runnable() {
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

    private void requestTransactionHistory() {
        Log.d(TAG, "CUSTOMER ID : " + User.id);
        ((BaseActivity) getActivity()).showLoading(true);
        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.LEGACY) {
            new RequestLoader(this).loadRequest(0, new BasicNameValuePair[]{
                    new BasicNameValuePair("customer_id", User.id)
            }, "transaction", "get_trx_customer", false);
        }
        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.FIREBASE) {
            new FirebaseQueryOrder(getActivity(), this).query();
        }
        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.SWAGGER) {
            new ListOrderClient(getContext(), this).listOrder();
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
            setContent(true);
        }
    }

    private void setContent(boolean isAnimate) {
        switch (mSelectedMenu) {
            case MENU_ON_GOING:
                if (mOnGoingDatas.size() == 0) {
                    ((BaseActivity) getActivity()).changeFragment(
                            R.id.transasicontentfragment,
                            new FragmentNoTransaction(),
                            true,
                            isAnimate);
                } else {
                    FragmentListTransaksi fragmentListTransaksi = new FragmentListTransaksi();
                    fragmentListTransaksi.setData(mOnGoingDatas);
                    ((BaseActivity) getActivity()).changeFragment(
                            R.id.transasicontentfragment,
                            fragmentListTransaksi,
                            true,
                            isAnimate);
                }
                break;
            case MENU_HISTORY:
                if (mHistoryDatas.size() == 0) {
                    ((BaseActivity) getActivity()).changeFragment(
                            R.id.transasicontentfragment,
                            new FragmentNoTransaction(),
                            true,
                            isAnimate);
                } else {
                    FragmentListTransaksi fragmentListTransaksi = new FragmentListTransaksi();
                    fragmentListTransaksi.setData(mHistoryDatas);
                    ((BaseActivity) getActivity()).changeFragment(
                            R.id.transasicontentfragment,
                            fragmentListTransaksi,
                            true,
                            isAnimate);
                }
                break;
        }
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {
        try {
            mOnGoingDatas.clear();
            mHistoryDatas.clear();
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (jsonArray.length() > 0) {
                    int statusId;
                    RiwayatTransaksi data;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        statusId = jsonObject.getInt("tran_status_id");
                        data = new RiwayatTransaksi(jsonObject.getString("tran_id"),
                                jsonObject.getString("tran_date"),
                                jsonObject.getString("tran_time_start"),
                                jsonObject.getString("tran_time_end"),
                                jsonObject.getString("tran_no_invoice"),
                                jsonObject.getInt("tran_status_id"),
                                jsonObject.getString("tran_agen_id"),
                                jsonObject.getString("tran_driver_id"));
                        if (statusId == 3) {
                            mHistoryDatas.add(data);
                        } else {
                            mOnGoingDatas.add(data);
                        }
                    }
                }
            }
            setContent(false);
        } catch (Exception ex) {
            Log.d(TAG, "FragmentRiwayatTransaksi " + ex.toString());
        }
    }

    @Override
    public void retrofitListOrder(ListOrderResponse response) {
        ((BaseActivity) getActivity()).showLoading(false);
        RiwayatTransaksi riwayatTransaksi;
        mOnGoingDatas.clear();
        mHistoryDatas.clear();
        List<ListOrderResponse.Data> datas = response.getDatas();
        for (ListOrderResponse.Data data : datas) {
            riwayatTransaksi = new RiwayatTransaksi(
                    data.getId(),
                    data.getArriveDate().split("T")[0],
                    "???", //data.getWaktuPengiriman().split(" - ")[0], // todo: issue #T493
                    "???", //data.getWaktuPengiriman().split(" - ")[1], // todo: issue #T493
                    "???/???/???",
                    ListOrderClient.definedStatus(data.getStatus()),
                    data.getUserAgentID(),
                    data.getDriverID()
            );
            if (ListOrderClient.definedStatus(data.getStatus()) == 3) {
                mHistoryDatas.add(riwayatTransaksi);
            } else {
                mOnGoingDatas.add(riwayatTransaksi);
            }
        }
        setContent(false);
    }

    @Override
    public void setFirebaseData(DataSnapshot dataSnapshot) {
        ((BaseActivity) getActivity()).showLoading(false);
        int statusId;
        RiwayatTransaksi data;
        mOnGoingDatas.clear();
        mHistoryDatas.clear();
        for (DataSnapshot children : dataSnapshot.getChildren()) {
            Log.d(TAG, children.toString());
            Package aPackage = children.getValue(Package.class);
            statusId = Integer.parseInt(aPackage.statusId);
            data = new RiwayatTransaksi(
                    children.getKey(),
                    aPackage.deliveryDate,
                    aPackage.startTime(),
                    aPackage.endTime(),
                    aPackage.invoice,
                    statusId,
                    aPackage.agentId,
                    aPackage.driverId
            );
            if (statusId == 3) {
                mHistoryDatas.add(data);
            } else {
                mOnGoingDatas.add(data);
            }
        }
        setContent(false);
    }
}
