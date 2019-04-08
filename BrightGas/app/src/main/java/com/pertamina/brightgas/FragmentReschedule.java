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
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class FragmentReschedule extends Fragment implements RequestLoaderInterface, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "frag_reschedule";

    private String mDateValue;

    private TextView mWaktuPengiriman;

    private TextView mTanggalPengiriman;

    private Calendar mCalendar;

    private String mId;

    private boolean mIsSameDay = false;

    private int mCurrentHour;

    public void setData(String id) {
        this.mId = id;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Jadwal Ulang");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_reschedule, container, false);

        mCalendar = Calendar.getInstance();
        mWaktuPengiriman = (TextView) rootView.findViewById(R.id.waktu_pengiriman);
        mTanggalPengiriman = (TextView) rootView.findViewById(R.id.tanggal_pengiriman);
        mWaktuPengiriman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((BaseActivity)getActivity()).isValidTextView(mTanggalPengiriman))
                    showTimePicker();
                else
                    ((BaseActivity)getActivity()).showDialog("", "Silahkan pilih hari dan tanggal terlebih dahulu", "", "OK");
            }
        });
        mTanggalPengiriman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        View selesai = rootView.findViewById(R.id.selesai);
        selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((BaseActivity)getActivity()).isValidTextView(mTanggalPengiriman) && ((BaseActivity)getActivity()).isValidTextView(mWaktuPengiriman)) {
                    ((BaseActivity)getActivity()).showDialog("Konfirmasi", "Anda akan yakin ingin merubah waktu pengiriman ?", "YA", "TIDAK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    doReschedule();
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
                    if (!((BaseActivity)getActivity()).isValidTextView(mTanggalPengiriman) && !((BaseActivity)getActivity()).isValidTextView(mWaktuPengiriman)) {
                        ((BaseActivity)getActivity()).showDialog("Kesalahan", "Silahkan pilih tanggal, dan waktu terlebih dahuru", "", "Ok");
                    } else if (((BaseActivity)getActivity()).isValidTextView(mTanggalPengiriman) && !((BaseActivity)getActivity()).isValidTextView(mWaktuPengiriman)) {
                        ((BaseActivity)getActivity()).showDialog("Kesalahan", "Silahkan pilih waktu terlebih dahuru", "", "Ok");
                    } else if (!((BaseActivity)getActivity()).isValidTextView(mTanggalPengiriman) && !((BaseActivity)getActivity()).isValidTextView(mWaktuPengiriman)) {
                        ((BaseActivity)getActivity()).showDialog("Kesalahan", "Silahkan pilih tanggal, dan waktu pengiriman terlebih dahuru", "", "Ok");
                    }
                }

            }
        });

        return rootView;
    }

    private void doReschedule() {
        Log.d(TAG, "DO RESCHEDULE");
        ((BaseActivity)getActivity()).showLoading(true);
        new RequestLoader(this).loadRequest(0, new BasicNameValuePair[]{
                new BasicNameValuePair("id", mId),
                new BasicNameValuePair("delivery_date", mDateValue),
                new BasicNameValuePair("delivery_time", mWaktuPengiriman.getText().toString())
        }, "transaction", "reschedule", true, true);
    }

    private void showDatePicker() {
        DatePickerDialog.newInstance(this, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show(((BaseActivity)getActivity()).getFragmentManager(), "datePicker");
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
        final AlertDialog dialog = ((BaseActivity)getActivity()).showDialog("Pilih Waktu Pengiriman", customView, "OK", "CANCEL", null, null);
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
        ((BaseActivity)getActivity()).showLoading(false);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                getActivity().onBackPressed();
            } else {
                ((BaseActivity)getActivity()).showDialog("", jsonObject.getString("message"), "", "Ok");
            }
        } catch (Exception ex) {
            ((BaseActivity)getActivity()).showDialog("Error", ex.toString(), "", "Ok");
        }
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, final int year, final int monthOfYear, final int dayOfMonth) {
        mIsSameDay = false;
        if ((year < mCalendar.get(Calendar.YEAR)) || (mCalendar.get(Calendar.YEAR) == year && monthOfYear < mCalendar.get(Calendar.MONTH)) || (mCalendar.get(Calendar.YEAR) == year && mCalendar.get(Calendar.MONTH) == monthOfYear && dayOfMonth < mCalendar.get(Calendar.DAY_OF_MONTH))) {
            ((BaseActivity)getActivity()).showDialog("Kesalahan", "Anda tidak bisa memilih hari sebelumnya", "", "OK");
        } else {
            if (mCalendar.get(Calendar.YEAR) == year && mCalendar.get(Calendar.MONTH) == monthOfYear && mCalendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth) {
                mIsSameDay = true;
                mCurrentHour = mCalendar.get(Calendar.HOUR_OF_DAY);
                if (mCurrentHour > 14) {
                    GregorianCalendar gregorianCalendar = new GregorianCalendar(year, monthOfYear, dayOfMonth + 1);
                    mDateValue = gregorianCalendar.get(GregorianCalendar.YEAR) + "-" + gregorianCalendar.get(GregorianCalendar.MONTH) + "-" + gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH);
                    final String dayValue = getDayValue(year, monthOfYear, dayOfMonth + 1);
                    ((BaseActivity)getActivity()).showDialog("Info", "Pesanan anda akan dikirimkan pada hari selanjutnya, " + dayValue + " " + mDateValue, "SETUJU", "BATAL",
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
}
