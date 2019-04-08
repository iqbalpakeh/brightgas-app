package com.pertamina.brightgas;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pertamina.brightgas.firebase.FirebaseQueryAddress;
import com.pertamina.brightgas.model.Alamat;
import com.pertamina.brightgas.retrofit.deleteaddress.DeleteAddressClient;
import com.pertamina.brightgas.retrofit.deleteaddress.DeleteAddressInterface;

import java.util.ArrayList;

public class AdapterAlamatProfil extends ArrayAdapter<Alamat>
        implements FirebaseQueryAddress.FirebaseAddressRemovable, DeleteAddressInterface {

    private static final String TAG = "adapter_alamat_profil";

    private ArrayList<Alamat> mDatas;
    private ArrayList<Alamat> mFilteredDatas;
    private Context mContext;

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<Alamat> tempList = new ArrayList<>();

            //constraint is the result from text you want to filter against.
            //objects is your data set you will filter from
            if (constraint != null && mDatas != null) {
                int length = mDatas.size();
                int i = 0;
                while (i < length) {
                    Alamat item = mDatas.get(i);
                    if (item.address.toLowerCase().contains(constraint)) {
                        tempList.add(item);
                    }
                    i++;
                }

                //following two lines is very important
                //as publish result can only take FilterResults objects
                filterResults.values = tempList;
                filterResults.count = tempList.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredDatas = (ArrayList<Alamat>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };

    public AdapterAlamatProfil(Context context, ArrayList<Alamat> datas) {
        super(context, R.layout.item_alamat_profil, datas);
        this.mDatas = datas;
        this.mFilteredDatas = this.mDatas;
        this.mContext = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        int resId = R.layout.item_alamat_profil;
        View rootView = LayoutInflater.from(mContext).inflate(resId, parent, false);
        TextView name = (TextView) rootView;
        name.setText(mFilteredDatas.get(position).address);
        return rootView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_alamat_profil, parent, false);
        Alamat data = getItem(position);

        TextView address = (TextView) rootView.findViewById(R.id.address);
        TextView title = (TextView) rootView.findViewById(R.id.title);
        ImageView hapusAlamatButton = (ImageView) rootView.findViewById(R.id.hapus_alamat);

        hapusAlamatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDatas.size() > 1) {
                    Log.d(TAG, "Hapus Alamat: " + getItem(position).id);
                    ((BaseActivity) mContext).showLoading(true);

                    if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.FIREBASE) {
                        new FirebaseQueryAddress(mContext, AdapterAlamatProfil.this)
                                .deleteAddress(getItem(position).id, position);
                    }

                    if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.SWAGGER) {
                        new DeleteAddressClient(mContext, AdapterAlamatProfil.this)
                                .deleteAddress(getItem(position).id, position);
                    }

                } else {
                    ((BaseActivity) mContext).showDialog("", "Minimal 1 alamat diperlukan", "", "Ok");
                }
            }
        });

        if (position == 0) {
            title.setText("Alamat Utama");
        } else {
            title.setText("Alamat Lain");
        }

        address.setText(data.address);

        return rootView;
    }

    @Override
    public void removeAddress(int position) {
        ((BaseActivity) mContext).showLoading(false);
        mDatas.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public void retrofitDeleteAddress(Boolean result, int position) {
        ((BaseActivity) mContext).showLoading(false);
        if (result == true) {
            mDatas.remove(position);
            notifyDataSetChanged();
        } else {
            ((BaseActivity) mContext).showDialog("", "Hapus alamat gagal", "", "Ok");
        }
    }

    public void removeLast() {
        if (mDatas != null && mDatas.size() > 0) {
            mDatas.remove(mDatas.size() - 1);
            notifyDataSetChanged();
        }
    }

    @Override
    public Alamat getItem(int position) {
        if (mFilteredDatas == null) {
            mFilteredDatas = mDatas;
        }
        return mFilteredDatas.get(position);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public void clear() {
        mDatas.clear();
        mFilteredDatas.clear();
    }

    @Override
    public void add(Alamat object) {
        mDatas.add(object);
        mFilteredDatas = mDatas;
    }

    @Override
    public int getCount() {
        if (mFilteredDatas == null) {
            if (mDatas == null)
                return 0;
            else {
                mFilteredDatas = mDatas;
            }
        }
        return mFilteredDatas.size();
    }

}
