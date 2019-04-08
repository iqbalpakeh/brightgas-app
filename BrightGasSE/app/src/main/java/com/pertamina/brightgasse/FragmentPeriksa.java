package com.pertamina.brightgasse;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.pertamina.brightgasse.model.Order;

import java.util.ArrayList;
import java.util.Calendar;

public class FragmentPeriksa extends Fragment implements DatePickerDialog.OnDateSetListener {

    private View alamatContainer, alamatPengiriman, alamatArrow, tambahAlamat;
    private TextView total, waktuPengiriman, tanggalPengiriman, hargatotalbarang, ongkoskirim, alamatPengirimanText, alamatUtamaText;
    private Calendar c;
    private boolean isAlamatContainer = false;
    private CheckBox alamatUtama;
    private ArrayList<Order> datas;
    private ViewGroup orderContainer;

    public FragmentPeriksa() {

    }

    public void init(ArrayList<Order> datas) {
        this.datas = datas;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).setTitle("Konfirmasi Pesanan");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_periksa, container, false);
        tambahAlamat = rootView.findViewById(R.id.tambahalamat);
        tambahAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) getActivity()).changeFragment(new FragmentTambahAlamat(), false, true);
            }
        });
        alamatContainer = rootView.findViewById(R.id.alamatcontainer);
        alamatPengiriman = rootView.findViewById(R.id.alamatpengiriman);
        alamatArrow = rootView.findViewById(R.id.alamatarrow);
        alamatPengiriman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAlamatContainer();
            }
        });
        c = Calendar.getInstance();
        total = (TextView) rootView.findViewById(R.id.total);
        hargatotalbarang = (TextView) rootView.findViewById(R.id.harga_total_barang);
        ongkoskirim = (TextView) rootView.findViewById(R.id.ongkos_kirim);
        waktuPengiriman = (TextView) rootView.findViewById(R.id.waktupengiriman);
        tanggalPengiriman = (TextView) rootView.findViewById(R.id.tanggalpengiriman);
        waktuPengiriman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
        tanggalPengiriman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        orderContainer = (ViewGroup) rootView.findViewById(R.id.ordercontainer);
        for (int i = 0; i < datas.size(); i++) {
            addOrder(datas.get(i));
        }

        recalculateTotal();

        View selesai = rootView.findViewById(R.id.selesai);
        selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) getActivity()).showDialog("Konfirmasi", "Anda akan menyelesakan transaksi. Lanjutkan?", "YA", "TIDAK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FragmentInformasiTransaksi.counter = 0;
                                FragmentInformasiTransaksi fragment = new FragmentInformasiTransaksi();
                                fragment.init(datas);
                                ((BaseActivity) getActivity()).changeFragment(fragment);
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
            }
        });

        alamatPengirimanText = (TextView) rootView.findViewById(R.id.alamatpengirimantext);
        alamatUtamaText = (TextView) rootView.findViewById(R.id.alamatutamatext);

        alamatUtama = (CheckBox) rootView.findViewById(R.id.alamatutama);
        alamatUtama.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    alamatPengirimanText.setText(alamatUtamaText.getText().toString());
                } else {
                    alamatPengirimanText.setText("Alamat Pengiriman");
                }
            }
        });

        return rootView;
    }

    private void addOrder(final Order data) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_periksa, orderContainer, false);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageview);
        imageView.setImageResource(data.drawableResId);

        TextView name = (TextView) rootView.findViewById(R.id.name);
        name.setText(data.name);
        final TextView quantity = (TextView) rootView.findViewById(R.id.quantity);
        quantity.setText(data.quantity + "");
        TextView price = (TextView) rootView.findViewById(R.id.price);
        price.setText(data.getPrice());
        TextView orderPrice = (TextView) rootView.findViewById(R.id.orderprice);
        orderPrice.setText(data.getOrderPrice());

        View plusAction = rootView.findViewById(R.id.plusaction);
        plusAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.plus();
                quantity.setText(data.quantity + "");
                recalculateTotal();
            }
        });
        View minusAction = rootView.findViewById(R.id.minusaction);
        minusAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.minus();
                quantity.setText(data.quantity + "");
                recalculateTotal();
            }
        });
        orderContainer.addView(rootView);
    }

    public String getCalculatedPrice(long input) {
        String millionString = "";
        String thousandString = "";
        String unitString = "";

        long million = input / 1000000;
        if (million > 0) {
            millionString = million + "";
        }
        long thousand = (input % 1000000) / 1000;
        if (thousand > 0) {
            if (million > 0 && thousand < 100) {
                thousandString = "0" + thousand;
            } else {
                thousandString = thousand + "";
            }

        } else {
            if (million != 0) {
                thousandString = "000";
            }
        }
        long unit = (input % 1000);
        if (unit > 0) {
            if (thousand > 0 || million > 0) {
                if (unit < 100) {
                    unitString = "0" + unit;
                } else {
                    unitString = unit + "";
                }
            } else {
                unitString = unit + "";
            }
        } else {
            if (thousand > 0 || million > 0) {
                unitString = "000";
            }
        }
        if (millionString.length() > 0) {
            return millionString + "." + thousandString + "." + unitString;
        } else if (thousandString.length() > 0) {
            return thousandString + "." + unitString;
        } else if (unitString.length() > 0) {
            return unitString;
        } else {
            return "0";
        }
    }

    public void recalculateTotal() {
        long totalHarga = 0;
        long totalOngkir = 0;
        long totalPrice = 0;
        for (int i = 0; i < datas.size(); i++) {
            totalHarga += datas.get(i).getTotal();
            totalOngkir += datas.get(i).orderPrice;
        }
        totalPrice = totalHarga + totalOngkir;
        total.setText(getCalculatedPrice(totalPrice));
        hargatotalbarang.setText(getCalculatedPrice(totalHarga));
        ongkoskirim.setText(getCalculatedPrice(totalOngkir));
    }

    private void toggleAlamatContainer() {
        isAlamatContainer = !isAlamatContainer;
        if (isAlamatContainer) {
            alamatContainer.setVisibility(View.VISIBLE);
            alamatArrow.startAnimation(((BaseActivity) getActivity()).createRotateAnimation(0, 180, 0.5f, 0.5f, 300, 0, null));
        } else {
            alamatContainer.setVisibility(View.GONE);
            alamatArrow.startAnimation(((BaseActivity) getActivity()).createRotateAnimation(180, 0, 0.5f, 0.5f, 300, 0, null));
        }
    }

    private void showDatePicker() {
        DatePickerDialog.newInstance(this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show(((BaseActivity) getActivity()).getFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        tanggalPengiriman.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
    }

    private void showTimePicker() {
        View customView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_time_picker, null, false);
        final View time811 = customView.findViewById(R.id.time811);
        final View time122 = customView.findViewById(R.id.time122);
        final View time58 = customView.findViewById(R.id.time58);
        final View time25 = customView.findViewById(R.id.time25);
        final View timeContainer = customView.findViewById(R.id.timecontainer);
        final TextView selectedTime = (TextView) customView.findViewById(R.id.selectedtime);
        time811.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTime.setText("8 - 11");
            }
        });
        time122.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTime.setText("11 - 14");
            }
        });
        time25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTime.setText("14 - 17");
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
                waktuPengiriman.setText(timeVal);
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

}
