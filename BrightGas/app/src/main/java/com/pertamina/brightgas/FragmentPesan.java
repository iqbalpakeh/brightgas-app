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

import com.google.firebase.database.DataSnapshot;
import com.pertamina.brightgas.firebase.FirebaseLoadable;
import com.pertamina.brightgas.firebase.FirebaseProductQuery;
import com.pertamina.brightgas.firebase.models.ProductPrices;
import com.pertamina.brightgas.model.Alamat;
import com.pertamina.brightgas.model.Order;
import com.pertamina.brightgas.retrofit.listproduct.ListProductClient;
import com.pertamina.brightgas.retrofit.listproduct.ListProductInterface;
import com.pertamina.brightgas.retrofit.listproduct.ListProductResponse;
import com.pertamina.brightgas.util.CheckboxHelper;
import com.pertamina.brightgas.util.ItemOrderConfiguration;
import com.pertamina.brightgas.util.ItemOrderDropdownHelper;
import com.pertamina.brightgas.util.ItemOrderHelper;

import java.util.ArrayList;
import java.util.List;

public class FragmentPesan extends Fragment
        implements InterfacePesanItemDropdown, FirebaseLoadable, ListProductInterface {

    private static final String TAG = "frag_pesan";

    private static final String PRODUCT_TYPE_55 = "5,5 KG";
    private static final String PRODUCT_TYPE_12 = "12 KG";
    private static final String PRODUCT_TYPE_220 = "220 GRAM";

    private final int POS_TABUNG_ISI = 0;
    private final int POS_REFILL = 1;
    private final int POS_TRADE_IN_55kg = 2;
    private final int POS_TRADE_IN_12kg = 3;

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


    private String[] mTitles = new String[]{
            "TABUNG & ISI",
            "REFILL / ISI ULANG",
            "TRADE IN"
    };

    private long[] mPriceType55;

    private long[] mPriceType12;

    private long[] mPriceType220;

    private final int POS_TYPE_55 = 0;
    private final int POS_TYPE_12 = 1;
    private final int POS_TYPE_220 = 2;

    private long[] mOrderDeliveryFee;

    private long mExtraPrice;

    private TextView[] mMenusTextView = new TextView[3];

    private View mChosenMenuIndicator;

    private ImageView mImageTabung;
    private ImageView mImageText;

    private int mColorOn;
    private int mColorOff;

    private int mSelectedMenu = 0;

    private ItemOrderHelper mTabungDanIsiOrderHelper;
    private ItemOrderHelper mRefillIsiUlangOrderHelper;
    private ItemOrderDropdownHelper mTradeInOrderDropdownHelper;

    private CheckboxHelper[] mCheckboxHelpersTradeIn55kg = new CheckboxHelper[4];
    private CheckboxHelper[] mCheckboxHelpersTradeIn12kg = new CheckboxHelper[4];

    private View mTradeInPopupContainer;

    private ItemOrderConfiguration[] mConfigurationsType55 = new ItemOrderConfiguration[4];
    private ItemOrderConfiguration[] mConfigurationsType12 = new ItemOrderConfiguration[4];
    private ItemOrderConfiguration[] mConfigurationsType220 = new ItemOrderConfiguration[4];
    private ItemOrderConfiguration[] mConfigurationsSelected;

    private ArrayList<Order> mListOrder = new ArrayList<>();

    private Alamat mAlamat;

    private ProductPrices mProductPrices;

    public void setData(Alamat alamat) {
        this.mAlamat = alamat;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Pesan");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pesan, container, false);

        prepareView(view);
        prepareOrderConfiguration();
        prepareMenu();

        ((BaseActivity) getActivity()).showLoading(true, "Cek harga");

        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.FIREBASE) {
            new FirebaseProductQuery(getContext(), this).query();
        }

        if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.SWAGGER) {
            new ListProductClient(getContext(), this).listProduct();
        }

        View orderButton = view.findViewById(R.id.order_button);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePurchase();
            }
        });

        return view;
    }

    @Override
    public void retrofitListProduct(ListProductResponse response) {
        ((BaseActivity) getActivity()).showLoading(false, "Cek harga");
        mProductPrices = new ProductPrices();

        List<ListProductResponse.Data> products = response.getData();
        for (ListProductResponse.Data product : products) {

            if (product.getId().equals("1ef3052b867b63b38810ce389e80614b")) {
                Log.d(TAG, "BRIGHT GAS 12 KG TABUNG & ISI");
                mProductPrices.product_12_kg_tabung_isi = Long.parseLong(product.getPrice());
                mProductPrices.product_12_kg_delivery = Long.parseLong(product.getDeliveryFee());
                mProductPrices.price_per_extra = 5000; //todo: request API to provide this data

            } else if (product.getId().equals("1b2430e5f41276776ce75843d40ffaf0")) {
                Log.d(TAG, "BRIGHT GAS 12 KG REFILL / ISI ULANG");
                mProductPrices.product_12_kg_refill = Long.parseLong(product.getPrice());
                mProductPrices.product_12_kg_delivery = Long.parseLong(product.getDeliveryFee());
                mProductPrices.price_per_extra = 5000; //todo: request API to provide this data

            } else if (product.getId().equals("bc493a32892b17f23fff1f183b5e092d")) {
                Log.d(TAG, "BRIGHT GAS 12 KG TRADE IN 1 TABUNG ELPIJI 3 KG");
                mProductPrices.product_12_kg_trade_in_elpiji_3_kg = Long.parseLong(product.getPrice());
                mProductPrices.product_12_kg_delivery = Long.parseLong(product.getDeliveryFee());
                mProductPrices.price_per_extra = 5000; //todo: request API to provide this data

            } else if (product.getId().equals("bd5cbd31fac1d2378e40ba2ed0fbfa99")) {
                Log.d(TAG, "BRIGHT GAS 12 KG TRADE IN 1 TABUNG EASE GAS 9 KG");
                mProductPrices.product_12_kg_trade_in_ease_gas_9_kg = Long.parseLong(product.getPrice());
                mProductPrices.product_12_kg_delivery = Long.parseLong(product.getDeliveryFee());
                mProductPrices.price_per_extra = 5000; //todo: request API to provide this data

            } else if (product.getId().equals("e8f0cdf957f16e105d0f48bf3da10cf5")) {
                Log.d(TAG, "BRIGHT GAS 12 KG TRADE IN 1 TABUNG JOYCOOK");
                mProductPrices.product_12_kg_trade_in_joycook = Long.parseLong(product.getPrice());
                mProductPrices.product_12_kg_delivery = Long.parseLong(product.getDeliveryFee());
                mProductPrices.price_per_extra = 5000; //todo: request API to provide this data

            } else if (product.getId().equals("26dd4d5601c3be0bb247222d784053b1")) {
                Log.d(TAG, "BRIGHT GAS 12 KG TRADE IN 2 TABUNG ELPIJI 6 KG");
                mProductPrices.product_12_kg_trade_in_elpiji_6_kg = Long.parseLong(product.getPrice());
                mProductPrices.product_12_kg_delivery = Long.parseLong(product.getDeliveryFee());
                mProductPrices.price_per_extra = 5000; //todo: request API to provide this data

            } else if (product.getId().equals("359b12417403bd0a07bdaff2c04bc37c")) {
                Log.d(TAG, "BRIGHT GAS 5,5 KG TABUNG & ISI");
                mProductPrices.product_5_kg_tabung_isi = Long.parseLong(product.getPrice());
                mProductPrices.product_5_kg_delivery = Long.parseLong(product.getDeliveryFee());
                mProductPrices.price_per_extra = 5000; //todo: request API to provide this data

            } else if (product.getId().equals("bf9f30297af4eee705ace892de8f480a")) {
                Log.d(TAG, "BRIGHT GAS 5,5 KG REFILL / ISI ULANG");
                mProductPrices.product_5_kg_refill = Long.parseLong(product.getPrice());
                mProductPrices.product_5_kg_delivery = Long.parseLong(product.getDeliveryFee());
                mProductPrices.price_per_extra = 5000; //todo: request API to provide this data

            } else if (product.getId().equals("d391931a057517aa4488242ef8b0fd7e")) {
                Log.d(TAG, "BRIGHT GAS 5,5 KG TRADE IN 1 TABUNG ELPIJI 3 KG");
                mProductPrices.product_5_kg_trade_in_elpiji_3_kg = Long.parseLong(product.getPrice());
                mProductPrices.product_5_kg_delivery = Long.parseLong(product.getDeliveryFee());
                mProductPrices.price_per_extra = 5000; //todo: request API to provide this data

            } else if (product.getId().equals("fbce070ca6f4014e4960fd0c7bc3a388")) {
                Log.d(TAG, "BRIGHT GAS 5,5 KG TRADE IN 1 TABUNG EASE GAS 9 KG");
                mProductPrices.product_5_kg_trade_in_ease_gas_9_kg = Long.parseLong(product.getPrice());
                mProductPrices.product_5_kg_delivery = Long.parseLong(product.getDeliveryFee());
                mProductPrices.price_per_extra = 5000; //todo: request API to provide this data

            } else if (product.getId().equals("ed985f1dcb5e2857f4916918cbfd4a44")) {
                Log.d(TAG, "BRIGHT GAS 5,5 KG TRADE IN 1 TABUNG JOYCOOK");
                mProductPrices.product_5_kg_trade_in_joycook = Long.parseLong(product.getPrice());
                mProductPrices.product_5_kg_delivery = Long.parseLong(product.getDeliveryFee());
                mProductPrices.price_per_extra = 5000; //todo: request API to provide this data

            } else if (product.getId().equals("68f3e6726246176b18dad7f5a2fbb2bb")) {
                Log.d(TAG, "BRIGHT GAS 5,5 KG TRADE IN 2 TABUNG ELPIJI 6 KG");
                mProductPrices.product_5_kg_trade_in_elpiji_6_kg = Long.parseLong(product.getPrice());
                mProductPrices.product_5_kg_delivery = Long.parseLong(product.getDeliveryFee());
                mProductPrices.price_per_extra = 5000; //todo: request API to provide this data

            } else if (product.getId().equals("e91db6f10a0b995b2157d9dd826fe18d")) {
                Log.d(TAG, "BRIGHT GAS 220 GRAM TABUNG & ISI");
                mProductPrices.product_220_gr_tabung_isi = Long.parseLong(product.getPrice());
                mProductPrices.product_220_gr_delivery = Long.parseLong(product.getDeliveryFee());
                mProductPrices.price_per_extra = 5000; //todo: request API to provide this data
            }
        }
        prepareProductPrice();
        prepareTradeInContainer();
    }

    @Override
    public void setFirebaseData(DataSnapshot dataSnapshot) {
        ((BaseActivity) getActivity()).showLoading(false, "Cek harga");
        mProductPrices = dataSnapshot.getValue(ProductPrices.class);
        Log.d(TAG, dataSnapshot.toString());
        prepareProductPrice();
        prepareTradeInContainer();
    }

    private void prepareTradeInContainer() {
        prepareTradeInContainer12();
        prepareTradeInContainer55();
    }

    private void prepareView(View view) {

        mColorOn = ContextCompat.getColor(getActivity(), R.color.colorPrimary);
        mColorOff = ContextCompat.getColor(getActivity(), R.color.berandamenutextcolor);

        mImageTabung = (ImageView) view.findViewById(R.id.image_tabung);
        mImageText = (ImageView) view.findViewById(R.id.image_text);

        mMenusTextView[0] = (TextView) view.findViewById(R.id.menu_one);
        mMenusTextView[1] = (TextView) view.findViewById(R.id.menu_two);
        mMenusTextView[2] = (TextView) view.findViewById(R.id.menu_three);

        mTabungDanIsiOrderHelper = new ItemOrderHelper(view.findViewById(R.id.tabung_dan_isi), mTitles[POS_TABUNG_ISI]);
        mRefillIsiUlangOrderHelper = new ItemOrderHelper(view.findViewById(R.id.refill_isi_ulang), mTitles[POS_REFILL]);
        mTradeInOrderDropdownHelper = new ItemOrderDropdownHelper(getActivity(), view.findViewById(R.id.trade_in), mTitles[POS_TRADE_IN_55kg], this);

        mChosenMenuIndicator = view.findViewById(R.id.menu_line);

        mTradeInPopupContainer = view.findViewById(R.id.trade_in_popup_container);
    }

    private void prepareOrderConfiguration() {
        for (int i = 0; i < mConfigurationsType55.length; i++) {
            mConfigurationsType55[i] = new ItemOrderConfiguration();
            mConfigurationsType12[i] = new ItemOrderConfiguration();
            mConfigurationsType220[i] = new ItemOrderConfiguration();
        }
        mConfigurationsSelected = mConfigurationsType55;
    }

    private void prepareProductPrice() {

        mPriceType55 = new long[]{
                mProductPrices.product_5_kg_tabung_isi,
                mProductPrices.product_5_kg_refill,
                0
        };

        mPriceType12 = new long[]{
                mProductPrices.product_12_kg_tabung_isi,
                mProductPrices.product_12_kg_refill,
                0
        };

        mPriceType220 = new long[]{
                mProductPrices.product_220_gr_tabung_isi,
                0, // no refill price
                0
        };

        mOrderDeliveryFee = new long[]{
                mProductPrices.product_5_kg_delivery,
                mProductPrices.product_12_kg_delivery,
                mProductPrices.product_220_gr_delivery
        };

        mExtraPrice = mProductPrices.price_per_extra;
        Log.d(TAG, "mExtraPrice = " + mExtraPrice);

        mTabungDanIsiOrderHelper.setSubTitle(
                PRODUCT_TYPE_55,
                showPrice(mPriceType55[POS_TABUNG_ISI]),
                mConfigurationsSelected[POS_TABUNG_ISI]
        );

        mRefillIsiUlangOrderHelper.setSubTitle(
                PRODUCT_TYPE_55,
                showPrice(mPriceType55[POS_REFILL]),
                mConfigurationsSelected[POS_REFILL]
        );

        mTradeInOrderDropdownHelper.setSubTitle(
                PRODUCT_TYPE_55,
                mConfigurationsSelected[POS_TRADE_IN_55kg]
        );
    }

    private void prepareMenu() {
        mMenusTextView[0].post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = mChosenMenuIndicator.getLayoutParams();
                params.width = mMenusTextView[0].getWidth();
                mChosenMenuIndicator.setLayoutParams(params);
            }
        });

        for (int i = 0; i < mMenusTextView.length; i++) {
            final int menusIndex = i;
            mMenusTextView[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prepareSelectedMenu(menusIndex);
                }
            });
        }
    }

    private void prepareTradeInContainer55() {

        if (mCheckboxHelpersTradeIn55kg[0] != null) {
            mCheckboxHelpersTradeIn55kg[0].setCheckBoxPointer(
                    0,
                    mTradeInPopupContainer.findViewById(R.id.trade_in_elpiji_3kg),
                    "DENGAN 1 TABUNG ELPIJI 3 Kg",
                    mProductPrices.product_5_kg_trade_in_elpiji_3_kg,
                    this
            );
        } else {
            mCheckboxHelpersTradeIn55kg[0] = new CheckboxHelper(
                    0,
                    mTradeInPopupContainer.findViewById(R.id.trade_in_elpiji_3kg),
                    "DENGAN 1 TABUNG ELPIJI 3 Kg",
                    mProductPrices.product_5_kg_trade_in_elpiji_3_kg,
                    this
            );
        }

        if (mCheckboxHelpersTradeIn55kg[1] != null) {
            mCheckboxHelpersTradeIn55kg[1].setCheckBoxPointer(
                    1,
                    mTradeInPopupContainer.findViewById(R.id.trade_in_ease_gas_9kg),
                    "DENGAN 1 TABUNG EASE GAS 9 Kg",
                    mProductPrices.product_5_kg_trade_in_ease_gas_9_kg,
                    this
            );
        } else {
            mCheckboxHelpersTradeIn55kg[1] = new CheckboxHelper(
                    1,
                    mTradeInPopupContainer.findViewById(R.id.trade_in_ease_gas_9kg),
                    "DENGAN 1 TABUNG EASE GAS 9 Kg",
                    mProductPrices.product_5_kg_trade_in_ease_gas_9_kg,
                    this
            );
        }

        if (mCheckboxHelpersTradeIn55kg[2] != null) {
            mCheckboxHelpersTradeIn55kg[2].setCheckBoxPointer(
                    2,
                    mTradeInPopupContainer.findViewById(R.id.trade_in_joy_cook),
                    "DENGAN 1 TABUNG JOYCOOK",
                    mProductPrices.product_5_kg_trade_in_joycook,
                    this
            );
        } else {
            mCheckboxHelpersTradeIn55kg[2] = new CheckboxHelper(
                    2,
                    mTradeInPopupContainer.findViewById(R.id.trade_in_joy_cook),
                    "DENGAN 1 TABUNG JOYCOOK",
                    mProductPrices.product_5_kg_trade_in_joycook,
                    this
            );
        }

        if (mCheckboxHelpersTradeIn55kg[3] != null) {
            mCheckboxHelpersTradeIn55kg[3].setCheckBoxPointer(
                    3,
                    mTradeInPopupContainer.findViewById(R.id.trade_in_elpiji_6kg),
                    "DENGAN 2 TABUNG ELPIJI 6 Kg",
                    mProductPrices.product_5_kg_trade_in_elpiji_6_kg,
                    this
            );
        } else {
            mCheckboxHelpersTradeIn55kg[3] = new CheckboxHelper(
                    3,
                    mTradeInPopupContainer.findViewById(R.id.trade_in_elpiji_6kg),
                    "DENGAN 2 TABUNG ELPIJI 6 Kg",
                    mProductPrices.product_5_kg_trade_in_elpiji_6_kg,
                    this
            );
        }
    }

    private void prepareTradeInContainer12() {

        if (mCheckboxHelpersTradeIn12kg[0] != null) {
            mCheckboxHelpersTradeIn12kg[0].setCheckBoxPointer(
                    0,
                    mTradeInPopupContainer.findViewById(R.id.trade_in_elpiji_3kg),
                    "DENGAN 1 TABUNG ELPIJI 3 Kg",
                    mProductPrices.product_12_kg_trade_in_elpiji_3_kg,
                    this
            );
        } else {
            mCheckboxHelpersTradeIn12kg[0] = new CheckboxHelper(
                    0,
                    mTradeInPopupContainer.findViewById(R.id.trade_in_elpiji_3kg),
                    "DENGAN 1 TABUNG ELPIJI 3 Kg",
                    mProductPrices.product_12_kg_trade_in_elpiji_3_kg,
                    this
            );
        }

        if (mCheckboxHelpersTradeIn12kg[1] != null) {
            mCheckboxHelpersTradeIn12kg[1].setCheckBoxPointer(
                    1,
                    mTradeInPopupContainer.findViewById(R.id.trade_in_ease_gas_9kg),
                    "DENGAN 1 TABUNG EASE GAS 9 Kg",
                    mProductPrices.product_12_kg_trade_in_ease_gas_9_kg,
                    this
            );
        } else {
            mCheckboxHelpersTradeIn12kg[1] = new CheckboxHelper(
                    1,
                    mTradeInPopupContainer.findViewById(R.id.trade_in_ease_gas_9kg),
                    "DENGAN 1 TABUNG EASE GAS 9 Kg",
                    mProductPrices.product_12_kg_trade_in_ease_gas_9_kg,
                    this
            );
        }

        if (mCheckboxHelpersTradeIn12kg[2] != null) {
            mCheckboxHelpersTradeIn12kg[2].setCheckBoxPointer(
                    2,
                    mTradeInPopupContainer.findViewById(R.id.trade_in_joy_cook),
                    "DENGAN 1 TABUNG JOYCOOK",
                    mProductPrices.product_12_kg_trade_in_joycook,
                    this
            );
        } else {
            mCheckboxHelpersTradeIn12kg[2] = new CheckboxHelper(
                    2,
                    mTradeInPopupContainer.findViewById(R.id.trade_in_joy_cook),
                    "DENGAN 1 TABUNG JOYCOOK",
                    mProductPrices.product_12_kg_trade_in_joycook,
                    this
            );
        }

        if (mCheckboxHelpersTradeIn12kg[3] != null) {
            mCheckboxHelpersTradeIn12kg[3].setCheckBoxPointer(
                    3,
                    mTradeInPopupContainer.findViewById(R.id.trade_in_elpiji_6kg),
                    "DENGAN 2 TABUNG ELPIJI 6 Kg",
                    mProductPrices.product_12_kg_trade_in_elpiji_6_kg,
                    this
            );
        } else {
            mCheckboxHelpersTradeIn12kg[3] = new CheckboxHelper(
                    3,
                    mTradeInPopupContainer.findViewById(R.id.trade_in_elpiji_6kg),
                    "DENGAN 2 TABUNG ELPIJI 6 Kg",
                    mProductPrices.product_12_kg_trade_in_elpiji_6_kg,
                    this
            );
        }
    }

    private String showPrice(long price) {
        return "Harga Rp " + GlobalActivity.getCalculatedPrice(price) + " / tabung";
    }

    private boolean isTradeInValid() {
        for (int i = 0; i < 4; i++) {
            if (mCheckboxHelpersTradeIn12kg[i].isNotEmpty() || mCheckboxHelpersTradeIn55kg[i].isNotEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void makePurchase() {

        saveConfiguration();

        if (mTabungDanIsiOrderHelper.isValid() || mRefillIsiUlangOrderHelper.isValid() || isTradeInValid()) {

            ViewGroup customView = (ViewGroup) LayoutInflater
                    .from(getActivity()).inflate(R.layout.dialog_container, null, false);

            mListOrder.clear();

            generateConfirmationDialog(
                    "1",
                    customView,
                    mConfigurationsType55,
                    PRODUCT_TYPE_55,
                    mPriceType55,
                    mOrderDeliveryFee[0],
                    mImageUrls[0]
            );

            generateConfirmationDialog(
                    "2",
                    customView,
                    mConfigurationsType12,
                    PRODUCT_TYPE_12,
                    mPriceType12,
                    mOrderDeliveryFee[1],
                    mImageUrls[1]
            );

            generateConfirmationDialog(
                    "3",
                    customView,
                    mConfigurationsType220,
                    PRODUCT_TYPE_220,
                    mPriceType220,
                    mOrderDeliveryFee[2],
                    mImageUrls[2]
            );

            generateTradeInConfirmationDialog(customView);

            final AlertDialog alertDialog = ((BaseActivity) getActivity()).showDialog(
                    "Anda akan memesan",
                    customView,
                    "SELESAI",
                    "LANJUT BELANJA",
                    null,
                    null
            );

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentPeriksa fragment = new FragmentPeriksa();
                    fragment.init(mListOrder, mAlamat);
                    ((BaseActivity) getActivity()).changeFragment(fragment, false, true);
                    alertDialog.dismiss();
                }
            });

            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

        } else {
            ((BaseActivity) getActivity()).showDialog(
                    "Pesan",
                    "Salah satu opsi atau lebih harus terisi untuk melanjutkan pesanan",
                    "Ok",
                    "",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, null);
        }
    }

    private void generateConfirmationDialog(
            String orderId,
            ViewGroup parentView,
            ItemOrderConfiguration[] configurations,
            String type,
            long[] priceList,
            long orderPrice,
            int drawableResId) {

        Log.d(TAG, "@generateConfirmationDialog()");

        for (int i = 0; i < configurations.length; i++) {

            Log.d(TAG, "configurations.length = " + configurations.length);

            if (configurations[i].isValid()) {

                Log.d(TAG, "i = " + i);

                String name = "BRIGHT GAS " + type + " " + mTitles[i];
                String title = configurations[i].qty + " " + name;

                View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_konfirmasi, null, false);
                parentView.addView(itemView);

                TextView textView = (TextView) itemView.findViewById(R.id.confirmation_details);
                textView.setText(title);

                mListOrder.add(new Order(
                        orderId,
                        (i + 1) + "", //1:Tabung&Isi, 2:Refill/IsiUlang, 3:TradeInElpiji3Kg, 4:TradeInEaseGas9Kg, 5:TradeInJoycook, 6:TradeInElpiji6Kg
                        name,
                        configurations[i].qty,
                        priceList[i],
                        orderPrice,
                        drawableResId,
                        mExtraPrice)
                );
            }
        }
    }

    private void generateTradeInConfirmationDialog(ViewGroup parentView) {

        Log.d(TAG, "@generateTradeInConfirmationDialog()");

        for (int i = 0; i < mCheckboxHelpersTradeIn55kg.length; i++) {
            if (mCheckboxHelpersTradeIn55kg[i].isNotEmpty()) {

                String name = "BRIGHT GAS " + PRODUCT_TYPE_55 + " " + mTitles[2] + " " + mCheckboxHelpersTradeIn12kg[i].title;
                String title = mCheckboxHelpersTradeIn55kg[i].qty + " " + name;

                View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_konfirmasi, null, false);
                parentView.addView(itemView);

                TextView textView = (TextView) itemView.findViewById(R.id.confirmation_details);
                textView.setText(title);

                Log.d(TAG, "type = " + (i + 3));

                mListOrder.add(new Order(
                        "1",
                        (i + 3) + "", //1:Tabung&Isi, 2:Refill/IsiUlang, 3:TradeInElpiji3Kg, 4:TradeInEaseGas9Kg, 5:TradeInJoycook, 6:TradeInElpiji6Kg
                        name,
                        mCheckboxHelpersTradeIn55kg[i].qty,
                        mCheckboxHelpersTradeIn55kg[i].price,
                        mProductPrices.product_12_kg_delivery,
                        mImageUrls[1],
                        mExtraPrice)
                );

            }
        }

        for (int i = 0; i < mCheckboxHelpersTradeIn12kg.length; i++) {
            if (mCheckboxHelpersTradeIn12kg[i].isNotEmpty()) {

                String name = "BRIGHT GAS " + PRODUCT_TYPE_12 + " " + mTitles[2] + " " + mCheckboxHelpersTradeIn12kg[i].title;
                String title = mCheckboxHelpersTradeIn12kg[i].qty + " " + name;

                View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_konfirmasi, null, false);
                parentView.addView(itemView);

                TextView textView = (TextView) itemView.findViewById(R.id.confirmation_details);
                textView.setText(title);

                Log.d(TAG, "type = " + (i + 3));

                mListOrder.add(new Order(
                        "2",
                        (i + 3) + "", //1:Tabung&Isi, 2:Refill/IsiUlang, 3:TradeInElpiji3Kg, 4:TradeInEaseGas9Kg, 5:TradeInJoycook, 6:TradeInElpiji6Kg
                        name,
                        mCheckboxHelpersTradeIn12kg[i].qty,
                        mCheckboxHelpersTradeIn12kg[i].price,
                        mProductPrices.product_5_kg_delivery,
                        mImageUrls[0],
                        mExtraPrice)
                );

            }
        }

    }

    private void prepareSelectedMenu(int index) {

        if (mSelectedMenu != index) {
            mMenusTextView[mSelectedMenu].setTextColor(mColorOff);
            mChosenMenuIndicator.startAnimation(((BaseActivity) getActivity()).createTranslateAnimation(
                    mSelectedMenu * mChosenMenuIndicator.getWidth(),
                    index * mChosenMenuIndicator.getWidth(),
                    0,
                    0,
                    500)
            );
            mSelectedMenu = index;
            mMenusTextView[mSelectedMenu].setTextColor(mColorOn);
            String productType = "";
            long[] priceList = mPriceType55;

            saveConfiguration();

            switch (mSelectedMenu) {
                case POS_TYPE_55:
                    productType = PRODUCT_TYPE_55;
                    priceList = mPriceType55;
                    mConfigurationsSelected = mConfigurationsType55;
                    break;
                case POS_TYPE_12:
                    productType = PRODUCT_TYPE_12;
                    priceList = mPriceType12;
                    mConfigurationsSelected = mConfigurationsType12;
                    break;
                case POS_TYPE_220:
                    productType = PRODUCT_TYPE_220;
                    priceList = mPriceType220;
                    mConfigurationsSelected = mConfigurationsType220;
                    break;
            }

            mImageTabung.setImageResource(mImageUrls[mSelectedMenu]);
            mImageText.setImageResource(mTextUrls[mSelectedMenu]);
            mTabungDanIsiOrderHelper.setSubTitle(productType, showPrice(priceList[POS_TABUNG_ISI]), mConfigurationsSelected[POS_TABUNG_ISI]);

            if (mSelectedMenu == POS_TYPE_220) {
                mTabungDanIsiOrderHelper.setAdditionalVisible(View.VISIBLE);
                mRefillIsiUlangOrderHelper.setVisibility(View.GONE);
                mTradeInOrderDropdownHelper.setVisibility(View.GONE);
            } else if (mSelectedMenu == POS_TYPE_55) {
                mTabungDanIsiOrderHelper.setAdditionalVisible(View.GONE);
                mRefillIsiUlangOrderHelper.setVisibility(View.VISIBLE);
                mTradeInOrderDropdownHelper.setVisibility(View.VISIBLE);
                mRefillIsiUlangOrderHelper.setSubTitle(productType, showPrice(priceList[POS_REFILL]), mConfigurationsSelected[POS_REFILL]);
                mTradeInOrderDropdownHelper.setSubTitle(productType, mConfigurationsSelected[POS_TRADE_IN_55kg]);
                prepareTradeInContainer55();
            } else if (mSelectedMenu == POS_TYPE_12) {
                mTabungDanIsiOrderHelper.setAdditionalVisible(View.GONE);
                mRefillIsiUlangOrderHelper.setVisibility(View.VISIBLE);
                mTradeInOrderDropdownHelper.setVisibility(View.VISIBLE);
                mRefillIsiUlangOrderHelper.setSubTitle(productType, showPrice(priceList[POS_REFILL]), mConfigurationsSelected[POS_REFILL]);
                mTradeInOrderDropdownHelper.setSubTitle(productType, mConfigurationsSelected[POS_TRADE_IN_12kg]);
                prepareTradeInContainer12();
            }
        }
    }

    private void saveConfiguration() {
        mTabungDanIsiOrderHelper.saveConfiguration();
        mRefillIsiUlangOrderHelper.saveConfiguration();
    }

    @Override
    public void showDropdown(int visibility) {
        mTradeInPopupContainer.setVisibility(visibility);
    }

    @Override
    public void setChecked(CheckboxHelper helper) {
        mTradeInOrderDropdownHelper.setCheck(helper);
    }

}
