package com.pertamina.brightgas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.pertamina.brightgas.model.Message;
import com.pertamina.brightgas.model.PesanMasuk;

import java.util.ArrayList;

public class FragmentMessage extends Fragment {

    private static final String TAG = "frag_message";

    private PesanMasuk mData;
    private AdapterMessage mAdapter;
    private EditText mMessage;

    public void init(PesanMasuk data) {
        this.mData = data;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Pesan");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        final ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        ArrayList<Message> datas = new ArrayList<>();
        datas.add(new Message("13:56 WIB", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua", Message.TYPE_RIGHT));
        datas.add(new Message("13:56 WIB", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua", Message.TYPE_LEFT));
        mAdapter = new AdapterMessage(getContext(), datas, mData.name, "mikaila dasilva");
        listView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        mMessage = (EditText) rootView.findViewById(R.id.message);
        View send = rootView.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((BaseActivity)getActivity()).isValidEditText(mMessage)) {
                    mAdapter.add(new Message("NOW", ((BaseActivity)getActivity()).getTextFromEditText(mMessage), Message.TYPE_RIGHT));
                    ((BaseActivity)getActivity()).hideKeyboard();
                    mMessage.setText("");
                    listView.smoothScrollToPosition(mAdapter.getCount() - 1);
                }
            }
        });
        return rootView;
    }
}
