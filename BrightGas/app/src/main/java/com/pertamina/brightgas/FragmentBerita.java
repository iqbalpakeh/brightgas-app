package com.pertamina.brightgas;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pertamina.brightgas.model.BeritaDanPromo;
import com.squareup.picasso.Picasso;

public class FragmentBerita extends Fragment {

    private static final String TAG = "fragment_berita";

    private BeritaDanPromo mDatas;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Berita");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.berita, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "ON OPTIONS ITEM SELECTED");
        switch (item.getItemId()) {
            case R.id.share:
                shareIt();
                return true;
        }
        return false;
    }

    private void shareIt() {
        Log.d(TAG, "SHARE IT");
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "AndroidSolved");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mDatas.title + "\n" + mDatas.content);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void init(BeritaDanPromo data) {
        this.mDatas = data;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_berita, container, false);
        TextView date = (TextView) rootView.findViewById(R.id.date);
        date.setText(mDatas.date);
        TextView title = (TextView) rootView.findViewById(R.id.my_title);
        title.setText(mDatas.title);
        TextView content = (TextView) rootView.findViewById(R.id.content);
        content.setText(mDatas.content);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.image_view);
        Picasso.with(getContext()).load(mDatas.imageUrl).centerCrop().fit().placeholder(R.drawable.ic_brightgas_logo).into(imageView);
        return rootView;
    }
}
