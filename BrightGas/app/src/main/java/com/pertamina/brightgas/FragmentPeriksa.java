package com.pertamina.brightgas;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.google.firebase.database.DataSnapshot;
import com.pertamina.brightgas.firebase.FirebaseLoadable;
import com.pertamina.brightgas.firebase.FirebaseStoreOrder;
import com.pertamina.brightgas.firebase.models.Package;
import com.pertamina.brightgas.model.Alamat;
import com.pertamina.brightgas.model.Order;
import com.pertamina.brightgas.retrofit.createorder.CreateOrderClient;
import com.pertamina.brightgas.retrofit.createorder.CreateOrderInterface;
import com.pertamina.brightgas.retrofit.createorder.CreateOrderRequest;
import com.pertamina.brightgas.retrofit.createorder.CreateOrderResponse;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class FragmentPeriksa extends Fragment
        implements DatePickerDialog.OnDateSetListener, RequestLoaderInterface, FirebaseLoadable, CreateOrderInterface {

    private static final String TAG = "fragment_periksa";

    private static final String TOTAL_BELANJA_NOL = "0";

    private String mDateValue;

    private Alamat mAlamat;

    private TextView mTotal;

    private TextView mWaktuPengiriman;

    private TextView mTanggalPengiriman;

    private TextView mHargatotalbarang;

    private TextView mOngkoskirim;

    private TextView mAlamatUtamaText;

    private Calendar mCalendar;

    private ArrayList<Order> mDatas;

    private ViewGroup mOrderContainer;

    private boolean mIsSameDay = false;

    private int mCurrentHour;

    private String mTotalOngkir;

    private String mTotalHarga;

    private String mTotalPrice;

    public void init(ArrayList<Order> datas, Alamat alamat) {
        this.mDatas = datas;
        this.mAlamat = alamat;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Konfirmasi Pesanan");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_periksa, container, false);

        mCalendar = Calendar.getInstance();

        mTotal = (TextView) rootView.findViewById(R.id.total);
        mHargatotalbarang = (TextView) rootView.findViewById(R.id.harga_total_barang);
        mOngkoskirim = (TextView) rootView.findViewById(R.id.ongkos_kirim);
        mWaktuPengiriman = (TextView) rootView.findViewById(R.id.waktu_pengiriman);
        mTanggalPengiriman = (TextView) rootView.findViewById(R.id.tanggal_pengiriman);

        mWaktuPengiriman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((BaseActivity) getActivity()).isValidTextView(mTanggalPengiriman)) {
                    showTimePicker();
                } else {
                    ((BaseActivity) getActivity()).showDialog("", "Silahkan pilih hari dan tanggal terlebih dahulu", "", "OK");
                }
            }
        });

        mTanggalPengiriman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        mOrderContainer = (ViewGroup) rootView.findViewById(R.id.order_container);
        for (int i = 0; i < mDatas.size(); i++) {
            addOrder(mDatas.get(i));
        }

        recalculateTotal();

        View buttonSelesai = rootView.findViewById(R.id.selesai);
        buttonSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((TOTAL_BELANJA_NOL).equals(mTotal.getText().toString())) {
                    ((BaseActivity) getActivity()).showDialog(
                            "Kesalahan", "Silahkan pilih pesananan terlebih dahulu", "", "Ok");
                    return;
                }

                Log.d(TAG, "mTanggalPengiriman = " + mTanggalPengiriman.getText().toString());
                Log.d(TAG, "mWaktuPengiriman = " + mWaktuPengiriman.getText().toString());

                if (((BaseActivity) getActivity()).isValidTextView(mTanggalPengiriman) && ((BaseActivity) getActivity()).isValidTextView(mWaktuPengiriman)) {

                    ((BaseActivity) getActivity()).showDialog("Konfirmasi", "Anda akan menyelesaikan transaksi. Lanjutkan?", "YA", "TIDAK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    doTransaction();
                                    dialog.dismiss();
                                }
                            },
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }
                    );

                } else {
                    if (!((BaseActivity) getActivity()).isValidTextView(mTanggalPengiriman)
                            && !((BaseActivity) getActivity()).isValidTextView(mWaktuPengiriman)) {
                        ((BaseActivity) getActivity()).showDialog(
                                "Kesalahan", "Silahkan pilih tanggal, dan waktu terlebih dahulu", "", "Ok");

                    } else if (((BaseActivity) getActivity()).isValidTextView(mTanggalPengiriman)
                            && !((BaseActivity) getActivity()).isValidTextView(mWaktuPengiriman)) {
                        ((BaseActivity) getActivity()).showDialog(
                                "Kesalahan", "Silahkan pilih waktu terlebih dahuru", "", "Ok");

                    } else if (!((BaseActivity) getActivity()).isValidTextView(mTanggalPengiriman)
                            && !((BaseActivity) getActivity()).isValidTextView(mWaktuPengiriman)) {
                        ((BaseActivity) getActivity()).showDialog(
                                "Kesalahan", "Silahkan pilih tanggal, dan waktu pengiriman terlebih dahulu", "", "Ok");
                    }
                }

            }
        });

        mAlamatUtamaText = (TextView) rootView.findViewById(R.id.alamat_utama_text);
        mAlamatUtamaText.setText(mAlamat.address);

        return rootView;
    }

    private void doTransaction() {

        ((BaseActivity) getActivity()).showLoading(true, "Pesan");

        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.LEGACY) {
            String item = "";
            try {
                JSONArray jsonArray = new JSONArray();
                for (Order data : mDatas) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("item_id", data.id);
                    jsonObject.put("type", data.type);
                    jsonObject.put("trade_in", "0");
                    jsonObject.put("tqy", data.quantity);
                    jsonObject.put("price", data.price);
                    jsonArray.put(jsonObject);
                }
                item = jsonArray.toString();
            } catch (Exception ex) {
                Log.d(TAG, ex.toString());
            }
            new RequestLoader(this).loadRequest(1, new BasicNameValuePair[]{
                    new BasicNameValuePair("customer_id", User.id),
                    new BasicNameValuePair("delivery_date", mDateValue),
                    new BasicNameValuePair("delivery_time", mWaktuPengiriman.getText().toString()),
                    new BasicNameValuePair("address_id", mAlamat.id),
                    new BasicNameValuePair("code_promo", ""),
                    new BasicNameValuePair("item", item)
            }, "transaction", "save", false);
        }

        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.FIREBASE) {
            ArrayList<Package.Item> items = new ArrayList<>();
            for (Order data : mDatas) {
                items.add(new Package.Item(
                        data.id,
                        data.type,
                        "0",
                        data.quantity + "",
                        data.price + "",
                        String.valueOf(data.extraPrice),
                        String.valueOf(data.orderPrice)
                ));
            }
            new FirebaseStoreOrder(getActivity(), this).storeOrder(new Package(
                    User.id,
                    mDateValue,
                    mWaktuPengiriman.getText().toString(),
                    mAlamat.id,
                    "",
                    items
            ));
        }

        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.SWAGGER) {
            ArrayList<CreateOrderRequest.Carts> cartses = new ArrayList<>();
            for (Order data : mDatas) {
                cartses.add(new CreateOrderRequest.Carts(
                        CreateOrderClient.defineId(data.id, data.type),
                        data.quantity + ""
                ));
            }
            new CreateOrderClient(getContext(), this).createOrder(new CreateOrderRequest(
                    mAlamat.id,
                    "0",
                    GlobalActivity.getCurrentDate(),
                    mTotalHarga,
                    mTotalOngkir,
                    mTotalPrice,
                    mDateValue,
                    mWaktuPengiriman.getText().toString(),
                    cartses
            ));
        }
    }

    private void addOrder(final Order data) {

        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_periksa, mOrderContainer, false);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.image_view);
        TextView name = (TextView) rootView.findViewById(R.id.name);
        final TextView quantity = (TextView) rootView.findViewById(R.id.quantity);
        TextView price = (TextView) rootView.findViewById(R.id.price);
        TextView orderPrice = (TextView) rootView.findViewById(R.id.order_price);
        View plusAction = rootView.findViewById(R.id.plus_action);
        View minusAction = rootView.findViewById(R.id.minus_action);
        TextView unitNumber = (TextView) rootView.findViewById(R.id.unit_number);

        if (data.id.equals("3")) {
            unitNumber.setText("Harga Per Lusin");
        } else {
            unitNumber.setText("Harga Satuan");
        }

        imageView.setImageResource(data.drawableResId);
        name.setText(data.name);
        quantity.setText(data.quantity + "");
        price.setText(data.getPrice());
        orderPrice.setText(data.getOrderPrice());


        plusAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.plus();
                quantity.setText(data.quantity + "");
                recalculateTotal();
            }
        });

        minusAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.minus();
                quantity.setText(data.quantity + "");
                recalculateTotal();
            }
        });

        mOrderContainer.addView(rootView);
    }

    private void recalculateTotal() {

        Log.d(TAG, "recalculateTotal()");

        long totalHarga = 0;
        long totalOngkir = 0;
        long totalPrice = 0;
        long qty = 0;

        Log.d(TAG, "mDatas.size = " + mDatas.size());

        for (int i = 0; i < mDatas.size(); i++) {
            totalHarga += mDatas.get(i).getTotal();
            totalOngkir += mDatas.get(i).getOrderPriceLong();
            qty = mDatas.get(i).quantity;
            if (qty > 1) {
                qty--;
                totalOngkir += qty * mDatas.get(i).extraPrice;
                Log.d(TAG, "mDatas.get(i).extraPrice = " + mDatas.get(i).extraPrice);
            }
        }

        totalPrice = totalHarga + totalOngkir;

        Log.d(TAG, "totalOngkir = " + totalOngkir);
        Log.d(TAG, "totalHarga = " + totalHarga);
        Log.d(TAG, "totalPrice = " + totalPrice);

        mTotalOngkir = String.valueOf(totalOngkir);
        mTotalHarga = String.valueOf(totalHarga);
        mTotalPrice = String.valueOf(totalPrice);

        mTotal.setText(GlobalActivity.getCalculatedPrice(totalPrice));
        mHargatotalbarang.setText(GlobalActivity.getCalculatedPrice(totalHarga));
        mOngkoskirim.setText(GlobalActivity.getCalculatedPrice(totalOngkir));
    }

    private void showDatePicker() {
        DatePickerDialog.newInstance(
                this,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
        ).show(getActivity().getFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, final int year, final int monthOfYear, final int dayOfMonth) {

        mIsSameDay = false;

        if ((year < mCalendar.get(Calendar.YEAR))
                || (mCalendar.get(Calendar.YEAR) == year && monthOfYear < mCalendar.get(Calendar.MONTH))
                || (mCalendar.get(Calendar.YEAR) == year && mCalendar.get(Calendar.MONTH) == monthOfYear && dayOfMonth < mCalendar.get(Calendar.DAY_OF_MONTH))) {
            ((BaseActivity) getActivity()).showDialog("Kesalahan", "Anda tidak bisa memilih hari sebelumnya", "", "OK");

        } else {
            if (mCalendar.get(Calendar.YEAR) == year
                    && mCalendar.get(Calendar.MONTH) == monthOfYear
                    && mCalendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth) {

                mIsSameDay = true;
                mCurrentHour = mCalendar.get(Calendar.HOUR_OF_DAY);

                if (mCurrentHour > 14) {

                    GregorianCalendar gregorianCalendar = new GregorianCalendar(year, monthOfYear, dayOfMonth + 1);

                    mDateValue = gregorianCalendar.get(GregorianCalendar.YEAR) + "-"
                            + gregorianCalendar.get(GregorianCalendar.MONTH) + "-" + gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH);

                    final String dayValue = getDayValue(year, monthOfYear, dayOfMonth + 1);

                    ((BaseActivity) getActivity()).showDialog("Info", "Pesanan anda akan dikirimkan pada hari selanjutnya, " + dayValue + " " + mDateValue, "SETUJU", "BATAL",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mIsSameDay = false;
                                    mTanggalPengiriman.setText(dayValue + ", " + mDateValue);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                } else {
                    mDateValue = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    mTanggalPengiriman.setText(getDayValue(year, monthOfYear, dayOfMonth) + ", " + mDateValue);
                }

            } else {
                mDateValue = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                mTanggalPengiriman.setText(getDayValue(year, monthOfYear, dayOfMonth) + ", " + mDateValue);
            }
        }
    }

    private String getDayValue(int year, int month, int day) {

        GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month, day - 1);
        int dayOfWeek = gregorianCalendar.get(GregorianCalendar.DAY_OF_WEEK);
        String dayValue = "";

        switch (dayOfWeek) {
            case 1:
                dayValue = "Senin";
                break;
            case 2:
                dayValue = "Selasa";
                break;
            case 3:
                dayValue = "Rabu";
                break;
            case 4:
                dayValue = "Kamis";
                break;
            case 5:
                dayValue = "Jumat";
                break;
            case 6:
                dayValue = "Sabtu";
                break;
            case 7:
                dayValue = "Minggu";
                break;
        }
        return dayValue;
    }

    private void showTimePicker() {

        View customView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_time_picker, null, false);
        final int color1 = ContextCompat.getColor(getContext(), R.color.clock1);
        final int color2 = ContextCompat.getColor(getContext(), R.color.timepickerselected);
        final int color3 = ContextCompat.getColor(getContext(), R.color.colorAccent);

        final View clock1 = customView.findViewById(R.id.clock_one);
        final View clock2 = customView.findViewById(R.id.clock_two);
        final View clock3 = customView.findViewById(R.id.clock_three);

        if (mIsSameDay) {
            if (mCurrentHour > 11) {
                clock1.setVisibility(View.GONE);
            }
        }

        final View time811 = customView.findViewById(R.id.time811);
        final View time122 = customView.findViewById(R.id.time122);
        final View time58 = customView.findViewById(R.id.time58);
        final View time25 = customView.findViewById(R.id.time25);

        final View timeContainer = customView.findViewById(R.id.time_container);
        final TextView selectedTime = (TextView) customView.findViewById(R.id.selected_time);
        final TextView selectedDay = (TextView) customView.findViewById(R.id.selected_day);

        time811.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(mIsSameDay && mCurrentHour > 11)) {
                    selectedTime.setText("08:00 - 11:00");
                    selectedDay.setText("Pagi");
                    selectedTime.setTextColor(color1);
                    selectedDay.setTextColor(color1);
                }
            }
        });

        time122.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTime.setText("11:00 - 14:00");
                selectedDay.setText("Siang");
                selectedTime.setTextColor(color2);
                selectedDay.setTextColor(color2);
            }
        });

        time25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTime.setText("14:00 - 17:00");
                selectedDay.setText("Siang");
                selectedTime.setTextColor(color3);
                selectedDay.setTextColor(color3);
            }
        });

        timeContainer.post(new Runnable() {
            @Override
            public void run() {
                float scale = (float) timeContainer.getWidth() / (float) (time811.getWidth() + time122.getWidth());
                ViewGroup.LayoutParams params = time811.getLayoutParams();
                params.width = (int) (scale * time811.getWidth());
                params.height = (int) (scale * time811.getHeight());
                time811.setLayoutParams(params);

                params = time122.getLayoutParams();
                params.width = (int) (scale * time122.getWidth());
                params.height = (int) (scale * time122.getHeight());
                time122.setLayoutParams(params);

                params = time58.getLayoutParams();
                params.width = (int) (scale * time58.getWidth());
                params.height = (int) (scale * time58.getHeight());
                time58.setLayoutParams(params);

                params = time25.getLayoutParams();
                params.width = (int) (scale * time25.getWidth());
                params.height = (int) (scale * time25.getHeight());
                time25.setLayoutParams(params);
            }
        });

        final AlertDialog dialog = ((BaseActivity) getActivity()).showDialog("Pilih Waktu Pengiriman", customView, "OK", "CANCEL", null, null);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeVal = "";
                if (selectedTime.getText() != null && selectedTime.getText().toString() != null) {
                    timeVal = selectedTime.getText().toString();
                }
                mWaktuPengiriman.setText(timeVal);
                dialog.dismiss();
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {
        ((BaseActivity) getActivity()).showLoading(false);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                FragmentRecapTransaksi fragment = new FragmentRecapTransaksi();
                fragment.setData(jsonArray, mAlamat);
                ((BaseActivity) getActivity()).changeFragment(fragment, true, true);
            } else {
                ((BaseActivity) getActivity()).showDialog("", jsonObject.getString("message"), "", "Ok");
            }
        } catch (Exception ex) {
            ((BaseActivity) getActivity()).showDialog("Error", ex.toString(), "", "Ok");
        }
    }

    @Override
    public void setFirebaseData(DataSnapshot dataSnapshot) {
        ((BaseActivity) getActivity()).showLoading(false);
        FragmentRecapTransaksi fragment = new FragmentRecapTransaksi();
        fragment.setData(dataSnapshot, mAlamat);
        ((BaseActivity) getActivity()).changeFragment(fragment, true, true);
    }

    @Override
    public void retrofitCreateOrder(CreateOrderResponse response) {
        ((BaseActivity) getActivity()).showLoading(false);
        FragmentRecapTransaksi fragment = new FragmentRecapTransaksi();
        fragment.setData(response, mAlamat);
        ((BaseActivity) getActivity()).changeFragment(fragment, true, true);
    }
}
