package com.pertamina.brightgasagen;

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

import com.pertamina.brightgasagen.model.BeritaDanPromo;
import com.squareup.picasso.Picasso;

public class FragmentBerita extends Fragment {

    private BeritaDanPromo data;

    public FragmentBerita(){

    }

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
        inflater.inflate(R.menu.berita,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("gugum","ON OPTIONS ITEM SELECTED");
        switch (item.getItemId()){
            case R.id.share:
                shareIt();
                return true;
        }
        return false;
    }

    private void shareIt(){
        Log.d("gugum","SHARE IT");
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "AndroidSolved");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, data.title+"\n"+data.content);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void init(BeritaDanPromo data){
        this.data = data;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_berita,container,false);
        TextView date = (TextView) rootView.findViewById(R.id.date);
        date.setText(data.date);
        TextView title = (TextView) rootView.findViewById(R.id.mytitle);
        title.setText(data.title);
        TextView content = (TextView) rootView.findViewById(R.id.content);
        content.setText(data.content);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageview);
        Picasso.with(getContext()).load(data.imageUrl).centerCrop().fit().placeholder(R.drawable.ic_brightgas_logo).into(imageView);
        return rootView;
    }
}
