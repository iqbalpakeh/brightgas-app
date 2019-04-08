package com.pertamina.brightgasagen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.pertamina.brightgasagen.model.Message;
import com.pertamina.brightgasagen.model.PesanMasuk;

import java.util.ArrayList;

public class FragmentMessage extends Fragment {

    PesanMasuk data;
    AdapterMessage adapter;
    EditText message;

    public FragmentMessage(){

    }

    public void init(PesanMasuk data){
        this.data = data;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).setTitle("Pesan");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message,container,false);
        final ListView listView = (ListView)rootView.findViewById(R.id.listview);
        ArrayList<Message> datas = new ArrayList<>();
        datas.add(new Message("13:56 WIB","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua",Message.TYPE_RIGHT));
        datas.add(new Message("13:56 WIB","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua",Message.TYPE_LEFT));
        adapter = new AdapterMessage(getContext(),datas,data.name,"mikaila dasilva");
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        message = (EditText)rootView.findViewById(R.id.message);
        View send = rootView.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(((BaseActivity) getActivity()).isValidEditText(message)){
                    adapter.add(new Message("NOW",((BaseActivity) getActivity()).getTextFromEditText(message),Message.TYPE_RIGHT));
                    ((BaseActivity) getActivity()).hideKeyboard();
                    message.setText("");
                    listView.smoothScrollToPosition(adapter.getCount()-1);
                }
            }
        });
        return rootView;
    }
}
