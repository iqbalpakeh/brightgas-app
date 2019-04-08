package com.pertamina.brightgas.retrofit.listorder;

import android.content.Context;
import android.util.Log;

import com.pertamina.brightgas.AppLocal;
import com.pertamina.brightgas.retrofit.ApiClient;
import com.pertamina.brightgas.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListOrderClient {

    private static final String TAG = "api_list_product";

    //Status orders disesuaikan dengan mockup menjadi seperti berikut:
    //
    //    pending (customer masih milih barang)
    //    sent (customer memesan barang)
    //    taken (diambil oleh agent yang ambil orderan)
    //    on progress (memilih driver yang akan mengantar)
    //    delivered (sedang diantar oleh driver)
    //    paid (sampai ditujuan dan sudah dibayar)
    //    finish (divalidasi oleh agen)
    //    cancel (pesanan dibatalkan oleh customer atau agen karena diluar kesepakatan)

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_SENT = "sent";
    public static final String STATUS_TAKEN = "taken";
    public static final String STATUS_ON_PROGRESS = "on progress";
    public static final String STATUS_DELIVERED = "delivered";
    public static final String STATUS_PAID = "paid";
    public static final String STATUS_FINISH = "finish";
    public static final String STATUS_CANCEL = "cancel";

    private ListOrderInterface mInterface;
    private Context mContext;

    public ListOrderClient(Context context, ListOrderInterface anInterface) {
        this.mContext = context;
        this.mInterface = anInterface;
    }

    public void listOrder() {
        ApiInterface client = ApiClient.getClient().create(ApiInterface.class);
        Call<ListOrderResponse> call = client.listOrder(AppLocal.getToken(mContext));
        call.enqueue(new Callback<ListOrderResponse>() {
            @Override
            public void onResponse(Call<ListOrderResponse> call, Response<ListOrderResponse> response) {
                Log.d(TAG, "RETROFIT RESPONSE OK");
                Log.d(TAG, "HTTP RAW: " + response.raw());
                Log.d(TAG, "HTTP CODE: " + response.code());
                Log.d(TAG, "HTTP HEADERS: " + response.headers());
                if (response.isSuccessful()) {
                    ListOrderResponse listOrderResponse = response.body();
                    mInterface.retrofitListOrder(listOrderResponse);
                } else {
                    Log.d(TAG, "HTTP ERROR BODY: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ListOrderResponse> call, Throwable t) {
                Log.d(TAG, "RETROFIT RESPONSE ERROR");
            }
        });
    }

    public static int definedStatus(String status) {

        // Following the status at RiwayatTransaksi.java:
        //        public static final int STATUS_PENDING = 0;
        //        public static final int STATUS_DIPROSES = 1;
        //        public static final int STATUS_DIKIRIM = 2;
        //        public static final int STATUS_DITERIMA = 3;

        Log.d(TAG, "status: " + status);

        if (status.equals(STATUS_PENDING)) {
            Log.d(TAG, "return " + 0);
            return 0;
        } else if (status.equals(STATUS_SENT)) {
            Log.d(TAG, "return " + 0);
            return 0;
        } else if (status.equals(STATUS_TAKEN)) {
            Log.d(TAG, "return " + 1);
            return 1;
        } else if (status.equals(STATUS_ON_PROGRESS)) {
            Log.d(TAG, "return " + 1);
            return 1;
        } else if (status.equals(STATUS_DELIVERED)) {
            Log.d(TAG, "return " + 2);
            return 2;
        } else if (status.equals(STATUS_PAID)) {
            Log.d(TAG, "return " + 3);
            return 3;
        } else if (status.equals(STATUS_FINISH)) {
            Log.d(TAG, "return " + 4);
            return 4; // new introduced state
        } else if (status.equals(STATUS_CANCEL)) {
            Log.d(TAG, "return " + 5);
            return 5; // new introduced state
        } else {
            Log.d(TAG, "return " + -1);
            return -1;
        }
    }

}
