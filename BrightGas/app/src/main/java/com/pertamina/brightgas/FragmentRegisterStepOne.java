package com.pertamina.brightgas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.datetimepicker.date.DatePickerDialog;
import com.google.android.gms.maps.model.LatLng;
import com.pertamina.brightgas.firebase.models.Customer;
import com.pertamina.brightgas.model.IdName;

import java.util.ArrayList;
import java.util.Calendar;

public class FragmentRegisterStepOne extends Fragment
        implements InterfaceMapPicker, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "register_one";

    private LatLng mLatLng;
    private String mAddress;

    private EditText mBirthdate;
    private EditText mNamaLengkap;
    private EditText mAlamatLengkap;

    private Calendar mCalendar;

    private AdapterIdName mAdapter;

    private Spinner mGender;

    @Override
    public void onResume() {
        super.onResume();
        mAlamatLengkap.setText(mAddress);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register_step1, container, false);

        mCalendar = Calendar.getInstance();
        mNamaLengkap = (EditText) view.findViewById(R.id.nama_lengkap);
        mAlamatLengkap = (EditText) view.findViewById(R.id.alamat_lengkap);

        View selanjutnya = view.findViewById(R.id.selanjutnya);
        selanjutnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((BaseActivity)getActivity()).isValidEditText(mNamaLengkap)) {

                    if (((BaseActivity)getActivity()).isValidEditText(mBirthdate)) {

                        if (mLatLng != null) {

                            if (((BaseActivity)getActivity()).isValidEditText(mAlamatLengkap)) {

                                        FragmentRegisterStepTwo fragmentRegisterStepTwo = new FragmentRegisterStepTwo();
                                        fragmentRegisterStepTwo.setData(
                                                ((BaseActivity)getActivity()).getTextFromEditText(mNamaLengkap),
                                                mAdapter.getItem(mGender.getSelectedItemPosition()).name,
                                                ((BaseActivity)getActivity()).getTextFromEditText(mBirthdate),
                                                ((BaseActivity)getActivity()).getTextFromEditText(mAlamatLengkap),
                                                mLatLng);

                                        ((BaseActivity)getActivity()).changeFragment(R.id.fragment_step, fragmentRegisterStepTwo, false, true);
                            } else {
                                ((BaseActivity)getActivity()).showDialog("", "Silahkan isi alamat lengkap terlebih dahulu", "", "OK");
                            }
                        } else {
                            ((BaseActivity)getActivity()).showDialog("", "Silahkan isi periksa alamat terlebih dahulu", "", "OK");
                        }
                    } else {
                        ((BaseActivity)getActivity()).showDialog("", "Silahkan isi tanggal lahir terlebih dahulu", "", "OK");
                    }
                } else {
                    ((BaseActivity)getActivity()).showDialog("", "Silahkan isi nama lengkap terlebih dahulu", "", "OK");
                }
            }
        });

        View periksaAlamat = view.findViewById(R.id.periksa_alamat);
        periksaAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapPicker();
            }
        });

        if (mLatLng != null) {
            ImageView addressCheck = (ImageView) view.findViewById(R.id.address_check);
            addressCheck.setBackgroundResource(R.drawable.ic_rectangle_primary);
        }

        mBirthdate = (EditText) view.findViewById(R.id.birthdate);
        mBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        ArrayList<IdName> datas = new ArrayList<>();
        datas.add(new IdName("1", Customer.MALE));
        datas.add(new IdName("2", Customer.FEMALE));

        mAdapter = new AdapterIdName(getContext(), datas);

        mGender = (Spinner) view.findViewById(R.id.gender);
        mGender.setAdapter(mAdapter);
        mGender.setSelection(0);
        mAdapter.notifyDataSetChanged();

        return view;
    }

    private void showDatePicker() {
        Log.d(TAG, "SHOW DATE PICKER");
        DatePickerDialog.newInstance(this,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)).show(getActivity().getFragmentManager(), "datePicker");
    }

    private void openMapPicker() {
        FragmentMapPicker fragment = new FragmentMapPicker();
        fragment.setInterfaceMapPicker(this);
        ((BaseActivity)getActivity()).changeFragment(R.id.fragment_step, fragment, false, true);
    }

    @Override
    public void setLatLng(LatLng latLng) {
        mLatLng = latLng;
    }

    @Override
    public void setAddress(String address) {
        mAddress = address;
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        mBirthdate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
    }
}
