package com.pertamina.brightgasagen;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pertamina.brightgasagen.gcm.MyGcmListenerService;
import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, RequestLoaderInterface, InterfaceMenu {

    private static final String TAG = "main_activity";

    public static final int MENU_COUNTER = 0;

    public static final int LOGOUT = 1;

    private NavigationView mNavigationView;

    private DrawerLayout mDrawer;

    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Data.self.getUser();
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mToggle.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
        mDrawer.setDrawerListener(mToggle);
        mToggle.syncState();
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    // show back button
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                } else {
                    //show hamburger
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    mToggle.syncState();
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDrawer.openDrawer(GravityCompat.START);
                        }
                    });
                }
            }
        });

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        parsingNavigationInfo();

        mNavigationView.inflateMenu(R.menu.main);
        setInterfaceMenu(this);
        setMenuTitle(R.id.nav_order_list, "Transaksi Baru", true);

        Intent intent = getIntent();
        if (intent != null) {
            Log.d(TAG, "INTENT NOT NULL");
            try {
                int type = Integer.parseInt(intent.getStringExtra("type"));
                Log.d(TAG, "TYPE FROM INTENT : " + type);
                if (type != -1) {
                    String raw = intent.getStringExtra("data");
                    if (type == MyGcmListenerService.HISTORY_DETAIL) {
                        try {
                            JSONObject jsonObject = new JSONObject(raw);
                            String id = jsonObject.getString("id");
                            FragmentHistoryDetail fragment = new FragmentHistoryDetail();
                            fragment.setData(id);
                            changeFragment(fragment, true, false);
                        } catch (Exception ex) {
                            Log.d(TAG, ex.toString());
                            changeFragment(new FragmentDashboard(), true, false);
                        }
                    } else if (type == MyGcmListenerService.RATNG) {
                        try {
                            JSONObject jsonObject = new JSONObject(raw);
                            String id = jsonObject.getString("id");
                            FragmentTransaksiSelesai fragment = new FragmentTransaksiSelesai();
                            fragment.setData(id);
                            changeFragment(fragment, true, false);
                        } catch (Exception ex) {
                            Log.d(TAG, ex.toString());
                            changeFragment(new FragmentDashboard(), true, false);
                        }
                    } else {
                        changeFragment(new FragmentDashboard(), true, false);
                    }
                } else {
                    changeFragment(new FragmentDashboard(), true, false);
                }
            } catch (Exception ex) {
                Log.d(TAG, ex.toString());
                changeFragment(new FragmentDashboard(), true, false);
            }
        } else {
            changeFragment(new FragmentDashboard(), true, false);
        }

    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void parsingNavigationInfo() {
        View headerLayout = mNavigationView.getHeaderView(0);

        if (User.id != null) {
            if (mNavigationView == null) {
                Log.d(TAG, "NAVIGATION VIEW IS NULL");
            } else {
                Log.d(TAG, "NAVIGATION VIEW IS NOT NULL");
            }

            TextView name = (TextView) headerLayout.findViewById(R.id.name);
            name.setText(User.name);
            TextView agen = (TextView) headerLayout.findViewById(R.id.agen);
            agen.setText(User.agen);

            ImageView image = (ImageView) headerLayout.findViewById(R.id.imageView);
            if (image != null) {
                if (this.isValidString(User.image_url)) {
                    Picasso.with(this).load(Network.baseUrlImage + User.image_url).placeholder(R.drawable.ic_photo_camera).fit().centerCrop().into(image);
                }
            } else {
                Log.d(TAG, "IMAGE IS NULL");
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        switch (id) {
//            case R.id.nav_profil:
//                changeFragment(new FragmentProfil());
//                break;
            case R.id.nav_dashboard:
                changeFragment(new FragmentDashboard());
                break;
//            case R.id.nav_beritapromo:
//                changeFragment(new FragmentBeritaDanPromo());
//                break;
            case R.id.nav_order_list:
                changeFragment(new FragmentOrderList());
                break;
            case R.id.nav_my_order:
                changeFragment(new FragmentMyOrder());
                break;
            case R.id.nav_manage_driver:
                changeFragment(new FragmentManageDriver());
                break;
//            case R.id.nav_transaksi:
//                if(FragmentInformasiTransaksi.counter<200){
//                    changeFragment(new FragmentInformasiTransaksi());
//                }else if(FragmentInformasiTransaksi.counter==200){
//                    changeFragment(new FragmentTransaksiSelesai());
//                }else{
//                    changeFragment(new FragmentRiwayatTransaksi());
//                }
//                break;
//            case R.id.nav_kotak_masuk:
//                changeFragment(new FragmentKotakMasuk());
//                break;
            case R.id.nav_kontakpertamina:
                changeFragment(new FragmentKontakPertamina());
                break;
            case R.id.nav_pusat_bantuan:
                changeFragment(new FragmentPusatBantuan());
                break;
            case R.id.nav_logout:
                this.showLoading(true, "Logout");
                new RequestLoader(this).loadRequest(LOGOUT, new BasicNameValuePair[]{
                        new BasicNameValuePair("token", User.token)
                }, "user", "logout", true);
                break;
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void parseMenuCounter(String result) {
        try {
            Menu menu = mNavigationView.getMenu();
            JSONObject jsonObject = new JSONObject(result);
        } catch (Exception ex) {
            Log.d("gugum", ex.toString());
        }
    }

    private void parseLogout(String result) {
        try {
            Data.self.clearTable(Data.TABLE_USER);
            User.clear();
            changeActivity(LoginActivity.class, true);
        } catch (Exception ex) {
            this.showDialog("Failed Logout", ex.toString());
        }
    }

    @Override
    public void setData(int index, String result, View[] impactedView) {
        this.showLoading(false);
        switch (index) {
            case MENU_COUNTER:
                parseMenuCounter(result);
                break;
            case LOGOUT:
                parseLogout(result);
                break;
        }
    }

    private void setMenuTitle(int idView, String defaultTitle, boolean isHaveNotification) {
        Menu menu = mNavigationView.getMenu();
        MenuItem menuItem = menu.findItem(idView);
        if (menuItem != null) {
            try {
                SpannableString s;
                if (!isHaveNotification) {
                    s = new SpannableString(defaultTitle);
                } else {
                    s = new SpannableString(defaultTitle + "   ");
                    Drawable d = getResources().getDrawable(R.drawable.ic_circle_notification);
                    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                    ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
                    int start = defaultTitle.length();
                    s.setSpan(span, start + 2, start + 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                menuItem.setTitle(s);
            } catch (Exception ex) {
                menuItem.setTitle(defaultTitle);
            }
        }
    }

    @Override
    public void setMenuTitle() {
        new RequestLoader(this).loadRequest(MENU_COUNTER, new BasicNameValuePair[]{
                new BasicNameValuePair("token", User.token)
        }, "menu", "getcount");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}
