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

import java.util.ArrayList;

public class FragmentListAgen extends Fragment
        implements RequestLoaderInterface, FirebaseQueryAgent.FirebaseQueryAgentLoadable {

    private static final String TAG = "frag_list_agent";

    private AdapterAgen mAdapter;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Agen");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_agen, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.list_view);

        mAdapter = new AdapterAgen(getContext(), new ArrayList<Agen>());
        listView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Agen agen = mAdapter.getItem(position);
                Log.d(TAG, "uid: " + agen.id);

                FragmentInformasiAgen fragment = new FragmentInformasiAgen();
                fragment.setData(agen);
                ((BaseActivity) getActivity()).changeFragment(fragment, false, true);
            }
        });

        new FirebaseQueryAgent(getContext(), this).queryAllAgents();

        return rootView;
    }

    @Override
    public void onAllAgentsReady(DataSnapshot dataSnapshot) {

        Log.d(TAG, "agents: " + dataSnapshot.toString());

        for (DataSnapshot children : dataSnapshot.getChildren()) {
            Agent agent = children.getValue(Agent.class);
            mAdapter.add(new Agen(
                    children.getKey(),
                    agent.getName(),
                    agent.getTelp(),
                    Float.parseFloat(agent.getRating()))
            );
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAssignAgentReady() {
        // not implemented
    }

    @Override
    public void setData(int index, String result, View[] impactedViews) {
        ((BaseActivity) getActivity()).showLoading(false);
    }
}
