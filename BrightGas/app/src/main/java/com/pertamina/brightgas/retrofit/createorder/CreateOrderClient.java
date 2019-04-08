package com.pertamina.brightgas.retrofit.createorder;

import android.content.Context;
import android.util.Log;

import com.pertamina.brightgas.AppLocal;
import com.pertamina.brightgas.retrofit.ApiClient;
import com.pertamina.brightgas.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOrderClient {

    private static final String TAG = "api_create_order";

    public static final String ID_55_KG = "1";
    public static final String ID_12_KG = "2";
    public static final String ID_220_GR = "3";

    public static final String TYPE_TABUNG_ISI = "1";
    public static final String TYPE_REFILL_ISI_ULANG = "2";
    public static final String TYPE_TRADE_IN_ELPIJI_3_KG = "3";
    public static final String TYPE_TRADE_IN_EASE_GAS_9_KG = "4";
    public static final String TYPE_TRADE_IN_JOYCOOK = "5";
    public static final String TYPE_TRADE_IN_ELPIJI_6_KG = "6";

    private CreateOrderInterface mInterface;
    private Context mContext;

    public CreateOrderClient(Context context, CreateOrderInterface anInteface) {
        mContext = context;
        mInterface = anInteface;
    }

    public void createOrder(CreateOrderRequest request) {
        ApiInterface client = ApiClient.getClient().create(ApiInterface.class);
        Call<CreateOrderResponse> call = client.createOrder(AppLocal.getToken(mContext), request);
        call.enqueue(new Callback<CreateOrderResponse>() {
            @Override
            public void onResponse(Call<CreateOrderResponse> call, Response<CreateOrderResponse> response) {
                Log.d(TAG, "RETROFIT RESPONSE OK");
                Log.d(TAG, "HTTP RAW: " + response.raw());
                Log.d(TAG, "HTTP CODE: " + response.code());
                Log.d(TAG, "HTTP HEADERS: " + response.headers());
                if (response.isSuccessful()) {
                    CreateOrderResponse createOrderResponse = response.body();
                    mInterface.retrofitCreateOrder(createOrderResponse);
                } else {
                    Log.d(TAG, "HTTP ERROR BODY: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<CreateOrderResponse> call, Throwable t) {
                Log.d(TAG, "RETROFIT RESPONSE ERROR");
            }
        });
    }

    public static String defineId(String id, String type) {

        Log.d(TAG, "id:" + id);
        Log.d(TAG, "type:" + type);

        if (id.equals(ID_12_KG) && type.equals(TYPE_TABUNG_ISI)) {
            Log.d(TAG, "id: 12 Kg");
            Log.d(TAG, "type: Tabung Isi");
            return "1ef3052b867b63b38810ce389e80614b";

        } else if (id.equals(ID_12_KG) && type.equals(TYPE_REFILL_ISI_ULANG)) {
            Log.d(TAG, "id: 12 Kg");
            Log.d(TAG, "type: Refill Isi Ulang");
            return "1b2430e5f41276776ce75843d40ffaf0";

        } else if (id.equals(ID_12_KG) && type.equals(TYPE_TRADE_IN_ELPIJI_3_KG)) {
            Log.d(TAG, "id: 12 Kg");
            Log.d(TAG, "type: Trade In elpiji 3 kg");
            return "bc493a32892b17f23fff1f183b5e092d";

        } else if (id.equals(ID_12_KG) && type.equals(TYPE_TRADE_IN_EASE_GAS_9_KG)) {
            Log.d(TAG, "id: 12 Kg");
            Log.d(TAG, "type: Trade In Eases Gas 9 Kg");
            return "bd5cbd31fac1d2378e40ba2ed0fbfa99";

        } else if (id.equals(ID_12_KG) && type.equals(TYPE_TRADE_IN_JOYCOOK)) {
            Log.d(TAG, "id: 12 Kg");
            Log.d(TAG, "type: Trade In Joycook");
            return "e8f0cdf957f16e105d0f48bf3da10cf5";

        } else if (id.equals(ID_12_KG) && type.equals(TYPE_TRADE_IN_ELPIJI_6_KG)) {
            Log.d(TAG, "id: 12 Kg");
            Log.d(TAG, "type: Trade In Elpiji 6 Kg");
            return "26dd4d5601c3be0bb247222d784053b1";

        } else if (id.equals(ID_55_KG) && type.equals(TYPE_TABUNG_ISI)) {
            Log.d(TAG, "id: 55 Kg");
            Log.d(TAG, "type: Tabung Isi");
            return "359b12417403bd0a07bdaff2c04bc37c";

        } else if (id.equals(ID_55_KG) && type.equals(TYPE_REFILL_ISI_ULANG)) {
            Log.d(TAG, "id: 55 Kg");
            Log.d(TAG, "type: Refill Isi Ulang");
            return "bf9f30297af4eee705ace892de8f480a";

        } else if (id.equals(ID_55_KG) && type.equals(TYPE_TRADE_IN_ELPIJI_3_KG)) {
            Log.d(TAG, "id: 55 Kg");
            Log.d(TAG, "type: Trade In Elpiji 3 Kg");
            return "d391931a057517aa4488242ef8b0fd7e";

        } else if (id.equals(ID_55_KG) && type.equals(TYPE_TRADE_IN_EASE_GAS_9_KG)) {
            Log.d(TAG, "id: 55 Kg");
            Log.d(TAG, "type: Trade In Ease Gas 9 Kg");
            return "fbce070ca6f4014e4960fd0c7bc3a388";

        } else if (id.equals(ID_55_KG) && type.equals(TYPE_TRADE_IN_JOYCOOK)) {
            Log.d(TAG, "id: 55 Kg");
            Log.d(TAG, "type: Trade In Joycook");
            return "ed985f1dcb5e2857f4916918cbfd4a44";

        } else if (id.equals(ID_55_KG) && type.equals(TYPE_TRADE_IN_ELPIJI_6_KG)) {
            Log.d(TAG, "id: 55 Kg");
            Log.d(TAG, "type: Trade In Elpiji 6 Kg");
            return "68f3e6726246176b18dad7f5a2fbb2bb";

        } else if (id.equals(ID_220_GR) && type.equals(TYPE_TABUNG_ISI)) {
            Log.d(TAG, "id: 220 Gr");
            Log.d(TAG, "type: Tabung Isi");
            return "e91db6f10a0b995b2157d9dd826fe18d";

        } else {
            return "wrong name";
        }
    }

    public static ProductIdentifier getProductIdentifier(String productId) {

        Log.d(TAG, "productId:" + productId);

        if (productId.equals("1ef3052b867b63b38810ce389e80614b")) {
            Log.d(TAG, "id: 12 Kg");
            Log.d(TAG, "type: Tabung Isi");
            return new ProductIdentifier(ID_12_KG, TYPE_TABUNG_ISI);

        } else if (productId.equals("1b2430e5f41276776ce75843d40ffaf0")) {
            Log.d(TAG, "id: 12 Kg");
            Log.d(TAG, "type: Refill Isi Ulang");
            return new ProductIdentifier(ID_12_KG, TYPE_REFILL_ISI_ULANG);

        } else if (productId.equals("bc493a32892b17f23fff1f183b5e092d")) {
            Log.d(TAG, "id: 12 Kg");
            Log.d(TAG, "type: Trade In elpiji 3 kg");
            return new ProductIdentifier(ID_12_KG, TYPE_TRADE_IN_ELPIJI_3_KG);

        } else if (productId.equals("bd5cbd31fac1d2378e40ba2ed0fbfa99")) {
            Log.d(TAG, "id: 12 Kg");
            Log.d(TAG, "type: Trade In Eases Gas 9 Kg");
            return new ProductIdentifier(ID_12_KG, TYPE_TRADE_IN_EASE_GAS_9_KG);

        } else if (productId.equals("e8f0cdf957f16e105d0f48bf3da10cf5")) {
            Log.d(TAG, "id: 12 Kg");
            Log.d(TAG, "type: Trade In Joycook");
            return new ProductIdentifier(ID_12_KG, TYPE_TRADE_IN_JOYCOOK);

        } else if (productId.equals("26dd4d5601c3be0bb247222d784053b1")) {
            Log.d(TAG, "id: 12 Kg");
            Log.d(TAG, "type: Trade In Elpiji 6 Kg");
            return new ProductIdentifier(ID_12_KG, TYPE_TRADE_IN_ELPIJI_6_KG);

        } else if (productId.equals("359b12417403bd0a07bdaff2c04bc37c")) {
            Log.d(TAG, "id: 55 Kg");
            Log.d(TAG, "type: Tabung Isi");
            return new ProductIdentifier(ID_55_KG, TYPE_TABUNG_ISI);

        } else if (productId.equals("bf9f30297af4eee705ace892de8f480a")) {
            Log.d(TAG, "id: 55 Kg");
            Log.d(TAG, "type: Refill Isi Ulang");
            return new ProductIdentifier(ID_55_KG, TYPE_REFILL_ISI_ULANG);

        } else if (productId.equals("d391931a057517aa4488242ef8b0fd7e")) {
            Log.d(TAG, "id: 55 Kg");
            Log.d(TAG, "type: Trade In Elpiji 3 Kg");
            return new ProductIdentifier(ID_55_KG, TYPE_TRADE_IN_ELPIJI_3_KG);

        } else if (productId.equals("fbce070ca6f4014e4960fd0c7bc3a388")) {
            Log.d(TAG, "id: 55 Kg");
            Log.d(TAG, "type: Trade In Ease Gas 9 Kg");
            return new ProductIdentifier(ID_55_KG, TYPE_TRADE_IN_EASE_GAS_9_KG);

        } else if (productId.equals("ed985f1dcb5e2857f4916918cbfd4a44")) {
            Log.d(TAG, "id: 55 Kg");
            Log.d(TAG, "type: Trade In Joycook");
            return new ProductIdentifier(ID_55_KG, TYPE_TRADE_IN_JOYCOOK);

        } else if (productId.equals("68f3e6726246176b18dad7f5a2fbb2bb")) {
            Log.d(TAG, "id: 55 Kg");
            Log.d(TAG, "type: Trade In Elpiji 6 Kg");
            return new ProductIdentifier(ID_55_KG, TYPE_TRADE_IN_ELPIJI_6_KG);

        } else if (productId.equals("e91db6f10a0b995b2157d9dd826fe18d")) {
            Log.d(TAG, "id: 220 Gr");
            Log.d(TAG, "type: Tabung Isi");
            return new ProductIdentifier(ID_220_GR, TYPE_TABUNG_ISI);

        } else {
            return null;
        }
    }

    public static class ProductIdentifier {

        private String mId;
        private String mType;

        public ProductIdentifier(String id, String type) {
            this.mId = id;
            this.mType = type;
        }

        public String getId() {
            return mId;
        }

        public String getType() {
            return mType;
        }
    }
}
