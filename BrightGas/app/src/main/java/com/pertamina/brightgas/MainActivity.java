package com.pertamina.brightgas;

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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pertamina.brightgas.firebase.FirebaseLoadable;
import com.pertamina.brightgas.firebase.FirebaseLogout;
import com.pertamina.brightgas.firebase.FirebaseRegistration;
import com.pertamina.brightgas.gcm.MyGcmListenerService;
import com.pertamina.brightgas.model.RiwayatTransaksi;
import com.pertamina.brightgas.retrofit.logout.LogoutClient;
import com.pertamina.brightgas.retrofit.logout.LogoutInterface;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        RequestLoaderInterface, InterfaceMenu, FirebaseLoadable, LogoutInterface {

    private static final String TAG = "main_activity";

    private static final int MENU_COUNTER = 0;

    private static final int LOGOUT = 1;

    private NavigationView mNavigationView;

    private DrawerLayout mDrawer;

    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Data.self.getUser();
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }

        setContentView(R.layout.activity_main);

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
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                } else {
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
        setMenuTitle(R.id.nav_beranda, "Beranda", true);

        Intent intent = getIntent();
        if (intent != null) {

            Log.d(TAG, "INTENT NOT NULL");

            try {

                int type = Integer.parseInt(intent.getStringExtra("type"));
                Log.d(TAG, "TYPE FROM INTENT : " + type);

                if (type != -1) {

                    String raw = intent.getStringExtra("data");

                    if (type == MyGcmListenerService.TRANSACTION_DETAIL) {

                        RiwayatTransaksi data = null;

                        try {
                            JSONObject jsonObject = new JSONObject(raw);
                            data = new RiwayatTransaksi(jsonObject.getString("tran_id"),
                                    jsonObject.getString("tran_date"),
                                    jsonObject.getString("tran_time_start"),
                                    jsonObject.getString("tran_time_end"),
                                    jsonObject.getString("tran_no_invoice"),
                                    jsonObject.getInt("tran_status_id"),
                                    jsonObject.getString("tran_agen_id"),
                                    jsonObject.getString("tran_driver_id"));
                        } catch (Exception ex) {
                            Log.d(TAG, ex.toString());
                        }

                        if (data == null) {
                            changeFragment(new FragmentBeranda(), true, false);
                        } else {

                            switch (data.statusId) {

                                case RiwayatTransaksi.STATUS_DITERIMA:
                                    FragmentRincianRiwayatTransaksi fragment = new FragmentRincianRiwayatTransaksi();
                                    fragment.setData(data);
                                    this.changeFragment(fragment, true, false);
                                    break;

                                default:
                                    FragmentInformasiTransaksi fragment1 = new FragmentInformasiTransaksi();
                                    fragment1.setData(data);
                                    this.changeFragment(fragment1, true, false);
                                    break;
                            }
                        }

                    } else if (type == MyGcmListenerService.RATING) {

                        try {

                            JSONObject jsonObject = new JSONObject(raw);
                            String id = jsonObject.getString("tran_id");
                            FragmentTransaksiSelesai fragment = new FragmentTransaksiSelesai();
                            fragment.setData(id);
                            changeFragment(fragment, true, false);

                        } catch (Exception ex) {

                            Log.d(TAG, ex.toString());
                            changeFragment(new FragmentBeranda(), true, false);

                        }
                    } else if (type == MyGcmListenerService.TRANSACTION_TRACKER) {

                        try {

                            JSONObject jsonObject = new JSONObject(raw);
                            String driverName = jsonObject.getString("dri_name");
                            LatLng driverLocation = new LatLng(jsonObject.getDouble("dri_lat"), jsonObject.getDouble("dri_lon"));
                            LatLng destinationLocation = new LatLng(jsonObject.getDouble("cus_lat"), jsonObject.getDouble("cus_lon"));
                            FragmentLacak fragment = new FragmentLacak();
                            fragment.setData(driverName, driverLocation, destinationLocation);
                            changeFragment(fragment, true, false);

                        } catch (Exception ex) {
                            Log.d(TAG, ex.toString());
                            changeFragment(new FragmentBeranda(), true, false);
                        }
                    } else {
                        changeFragment(new FragmentBeranda(), true, false);
                    }
                } else {

                    changeFragment(new FragmentBeranda(), true, false);

                }
            } catch (Exception ex) {
                Log.d(TAG, ex.toString());
                changeFragment(new FragmentBeranda(), true, false);
            }

        } else {
            changeFragment(new FragmentBeranda(), true, false);
        }
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void parsingNavigationInfo() {

        StorageReference storageReference;
        View headerLayout = mNavigationView.getHeaderView(0);

        CircleImageView picture = (CircleImageView) headerLayout.findViewById(R.id.user_picture);
        if (User.id != null && !User.picture.equals(FirebaseRegistration.NO_URL)) {
            if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.FIREBASE) {
                storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(User.picture);
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(storageReference)
                        .into(picture);
            }
            if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.SWAGGER) {
                //todo: add swagger code
            }
        }

        View profil = headerLayout.findViewById(R.id.profil);
        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.id != null) {
                    changeFragment(new FragmentProfil());
                    mDrawer.closeDrawers();
                }
            }
        });

        View message = headerLayout.findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.id != null) {
                    changeFragment(new FragmentKotakMasuk());
                    mDrawer.closeDrawers();
                }
            }
        });

        if (User.id != null) {
            if (mNavigationView == null) {
                Log.d(TAG, "NAVIGATION VIEW IS NULL");
            } else {
                Log.d(TAG, "NAVIGATION VIEW IS NOT NULL");
            }

            TextView name = (TextView) headerLayout.findViewById(R.id.name);
            if (this.isValidString(User.name)) {
                name.setText(User.name);
            } else {
                name.setText("");
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        switch (id) {
            case R.id.nav_beranda:
                clearNavMenuMarking();
                setMenuTitle(R.id.nav_beranda, "Beranda", true);
                changeFragment(new FragmentBeranda());
                break;

            case R.id.nav_beritapromo:
                if (User.id != null) {
                    clearNavMenuMarking();
                    setMenuTitle(R.id.nav_beritapromo, "Berita & Promo", true);
                    changeFragment(new FragmentBeritaDanPromo());
                } else {
                    changeActivity(LoginActivity.class, true);
                }
                break;

            case R.id.nav_pengaturan:
                if (User.id != null) {
                    clearNavMenuMarking();
                    setMenuTitle(R.id.nav_pengaturan, "Pengaturan", true);
                    changeFragment(new FragmentPengaturan());
                } else {
                    changeActivity(LoginActivity.class, true);
                }
                break;

            case R.id.nav_transaksi:
                if (User.id != null) {
                    clearNavMenuMarking();
                    setMenuTitle(R.id.nav_transaksi, "Transaksi", true);
                    changeFragment(new FragmentRiwayatTransaksi());
                } else {
                    changeActivity(LoginActivity.class, true);
                }
                break;

            case R.id.nav_kontakpertamina:
                if (User.id != null) {
                    clearNavMenuMarking();
                    setMenuTitle(R.id.nav_kontakpertamina, "Hubungi Kami", true);
                    changeFragment(new FragmentKontakPertamina());
                } else {
                    changeActivity(LoginActivity.class, true);
                }
                break;

            case R.id.nav_pusat_bantuan:
                if (User.id != null) {
                    clearNavMenuMarking();
                    setMenuTitle(R.id.nav_pusat_bantuan, "Pusat Bantuan", true);
                    changeFragment(new FragmentPusatBantuan());
                } else {
                    changeActivity(LoginActivity.class, true);
                }
                break;

            case R.id.nav_logout:
                if (User.id != null) {

                    clearNavMenuMarking();
                    setMenuTitle(R.id.nav_logout, "Keluar", true);
                    this.showLoading(true, "Logout");

                    if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.LEGACY) {
                        new RequestLoader(this).loadRequest(LOGOUT, new BasicNameValuePair[]{
                                new BasicNameValuePair("token", User.id)
                        }, "user", "logout", true);
                    }

                    if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.FIREBASE) {
                        new FirebaseLogout(this, this).logout();
                    }

                    if (GlobalActivity.getBackEndServiceProvider() == GlobalActivity.SWAGGER) {
                        new LogoutClient(getApplicationContext(), this).logout();
                    }

                } else {
                    changeActivity(LoginActivity.class, true);
                }
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
            Log.d(TAG, ex.toString());
        }
    }

    private void clearNavMenuMarking() {
        setMenuTitle(R.id.nav_beranda, "Beranda", false);
        setMenuTitle(R.id.nav_beritapromo, "Berita & Promo", false);
        setMenuTitle(R.id.nav_pengaturan, "Pengaturan", false);
        setMenuTitle(R.id.nav_transaksi, "Transaksi", false);
        setMenuTitle(R.id.nav_kontakpertamina, "Hubungi Kami", false);
        setMenuTitle(R.id.nav_pusat_bantuan, "Pusat Bantuan", false);
        setMenuTitle(R.id.nav_logout, "Keluar", false);
    }

    private void parseLogout() {
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
                parseLogout();
                break;
        }
    }

    @Override
    public void retrofitLogOut(boolean result) {
        this.showLoading(false);
        parseLogout();
    }


    @Override
    public void setFirebaseData(DataSnapshot dataSnapshot) {
        this.showLoading(false);
        parseLogout();
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
                new BasicNameValuePair("token", User.id)
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
