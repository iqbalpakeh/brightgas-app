package com.pertamina.brightgasse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.pertamina.brightgasse.firebase.FirebaseQueryAgent;
import com.pertamina.brightgasse.firebase.models.Agent;
import com.pertamina.brightgasse.model.Agen;
import com.pertamina.brightgasse.model.SimpleOrder;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentPilihAgen extends Fragment
        implements RequestLoaderInterface, FirebaseQueryAgent.FirebaseQueryAgentLoadable {

    private static final String TAG = "frag_pilih_agen";

    private static final int DATA = 0;
    private static final int ACTION = 1;

    private String mOrderId;
    private SimpleOrder mOrder;

    private AdapterPilihAgen adapter;

    public void setData(String orderId) {
        this.mOrderId = orderId;
    }

    public void setData(SimpleOrder order) {
        this.mOrder = order;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Pilih Agen");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_pilih_agen, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.list_view);

        adapter = new AdapterPilihAgen(getContext(), new ArrayList<Agen>());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Agen data = adapter.getItem(position);
                assignAgent(data.id);
            }
        });

        queryListOfAgent();
        return rootView;
    }

    private void assignAgent(String agenId) {

        ((BaseActivity) getActivity()).showLoading(true);

        new RequestLoader(this).loadRequest(ACTION, new BasicNameValuePair[]{
                new BasicNameValuePair("id", mOrderId),
                new BasicNameValuePair("agen_id", agenId)
        }, "transaction", "send_to_agen", true);

        new FirebaseQueryAgent(getContext(), this).assignAgent(mOrder, agenId);

    }

    private void queryListOfAgent() {

        ((BaseActivity) getActivity()).showLoading(true);

        new RequestLoader(this).loadRequest(DATA, new BasicNameValuePair[]{
                new BasicNameValuePair("id", mOrderId)
        }, "agen", "get_agen", true, true);

        new FirebaseQueryAgent(getContext(), this).queryAllAgents();

    }

    @Override
    public void onAllAgentsReady(DataSnapshot dataSnapshot) {

        Log.d(TAG, "agent:" + dataSnapshot.toString());

        for (DataSnapshot children : dataSnapshot.getChildren()) {
            Agent agent = children.getValue(Agent.class);
            adapter.add(new Agen(
                    children.getKey(),
                    agent.name,
                    0,
                    "5") //todo: need to implement with GOOGLE API
            );
        }

        adapter.notifyDataSetChanged();

        ((BaseActivity) getActivity()).showLoading(false);
    }

    @Override
    public void onAssignAgentReady() {
        ((BaseActivity) getActivity()).showLoading(false);
        getActivity().onBackPressed();
    }

    private void parseData(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                adapter.clear();
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        adapter.add(new Agen(
                                jsonObject.getString("age_id"),
                                jsonObject.getString("age_name"),
                                jsonObject.getInt("age_rating"),
                                jsonObject.getString("distance"))
                        );
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                ((BaseActivity) getActivity()).showDialog("", jsonObject.getString("message"), "", "Ok");
            }
        } catch (Exception ex) {
            ((BaseActivity) getActivity()).showDialog("Error", ex.toString(), "", "Ok");
        }
    }

    private void parseAction(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("status") == 1) {
                getActivity().onBackPressed();
            } else {
                ((BaseActivity) getActivity()).showDialog("", jsonObject.getString("message"), "", "Ok");
            }
        } catch (Exception ex) {
            ((BaseActivity) getActivity()).showDialog("Error", ex.toString(), "", "Ok");
        }
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {
        ((BaseActivity) getActivity()).showLoading(false);
        switch (index) {
            case DATA:
                parseData(result);
                break;
            case ACTION:
                parseAction(result);
                break;
        }
    }
}
