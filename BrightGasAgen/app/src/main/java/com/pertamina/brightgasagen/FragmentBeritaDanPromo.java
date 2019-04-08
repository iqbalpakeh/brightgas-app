package com.pertamina.brightgasagen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pertamina.brightgasagen.model.BeritaDanPromo;

import java.util.ArrayList;

public class FragmentBeritaDanPromo extends Fragment {

    public FragmentBeritaDanPromo() {

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Berita & Promo");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ListView rootView = (ListView) inflater.inflate(R.layout.fragment_berita_dan_promo, container, false);
        ArrayList<BeritaDanPromo> datas = new ArrayList<>();
        datas.add(new BeritaDanPromo("10 Nov 2016", "Pemerintah Dukung Pertamina Impor BBM dari Malaysia", "Pemerintah mendukung PT Pertamina (Persero) menggandeng Petronas untuk memasok Bahan Bakar Minyak (BBM) di perbatasan. Langkah itu guna menerapkan program BBM satu harga yang mulai diterapkan 1 Januari 2017.\n" +
                "\n" +
                "Pertamina berencana mengimpor BBM dari Malaysia dengan menjalin kerja sama dengan Petronas. Impor BBM tersebut untuk memasok BBM di perbatasan Indonesia-Malaysia, yang wilayahnya lebih mudah diakses dari Malaysia.\n" +
                "\n" +
                "Direktur Jenderal Minyak dan Gas Bumi Kementerian Energi Sumber Daya Mineral (ESDM) I Gusti Nyoman Wiratmaja mengatakan, jika mengimpor BBM dari Malaysia lebih murah, sehingga dapat mengurangi biaya transportasi. \n" +
                "\n" +
                "\u200E\"Impor oke-oke saja. Ini mengurangi biaya transportasi,\" kata Wiratmaja, di Jakarta, Kamis (10/11/2016).", "http://cdn1-a.production.images.static6.com/ZONIgTZjku6vI3qMpQmYSMWmSWA=/640x355/smart/filters:quality(75):strip_icc():format(webp)/liputan6-media-production/medias/1185642/original/041642900_1459239851-20160329-Harga-BBM-Jakarta-Angga-Yuniar3.jpg"));
        datas.add(new BeritaDanPromo("09 Nov 2016", "Sukseskan BBM Satu Harga, Begini Langkah Pertamina", "Program Bahan Bakar Minyak (BBM) Satu Harga di seluruh wilayah Indonesia, menjadi prioritas pemerintah untuk diterapkan pada 2017. Lalu apa yang dilakukan PT Pertamina (Persero) untuk mewujudkannya?\n" +
                "\n" +
                "Wakil Direktur Utama Pertamina Ahmad Bambang mengatakan, untuk menerapkan BBM Satu Harga\u200E, Pertamina akan membangun lembaga penjualan resmi Agen Penyalur Minyak Solar (APMS) di wilayah terpencil. Nantinya, Pertamina akan memasok langsung BBM tersebut.\n" +
                "\n" +
                "\u200E\"Memang begini solusinya, sudah saya sampaikan. Pertama APMS harus kita suplai. APMS itu harganya biaya ke APMS, Pertamina yang tanggung. jadi APMS sama dengan SPBU,\" kata dia di Jakarta, Rabu (9/11/2016).", "http://cdn1-a.production.images.static6.com/ObRDRISIhrg-i3RIUfZEbhHVDS0=/640x355/smart/filters:quality(75):strip_icc():format(webp)/liputan6-media-production/medias/933975/big/082806300_1437570899-GEEK3922.jpg"));
        datas.add(new BeritaDanPromo("09 Nov 2016", "Pertamina Gandeng Petronas Pasok BBM, Bagaimana Harganya?", "PT Pertamina (Persero) bekerjasama dengan Petronas untuk memasok  Bahan Bakar\u200E Minyak (BBM) di perbatasan Indonesia dengan Malaysia. Kerja sama ini guna menerapkan  program BBM satu harga. Lalu bagaimana dengan harga BBM yang dipasok\u200E Petronas?\n" +
                "\n" +
                "Wakil Direktur Utama Pertamina Ahmad Bambang mengatakan, meski BBM diimpor dari Malaysia, harga BBM tetap sama seperti yang ditetapkan Pemerintah Indonesia. \"Tetap harga yang dijual sesuai yang diatur Pemerintah,\" kata Bambang, Rabu (9/11/2016).\n" +
                "\n" +
                "Bambang mengatakan, impor BBM dari Malaysia dilakukan agar lebih efisien. Hal itu karena ada beberapa  wilayah perbatasan yang belum memiliki akses, sehingga untuk memasok BBM harus menggunakan pesawat. Dengan demikian biaya pengiriman BBM menjadi mahal.", "http://cdn1-a.production.images.static6.com/YGkJudT63KOf_IOAUIbggoAqOuA=/640x355/smart/filters:quality(75):strip_icc():format(webp)/liputan6-media-production/medias/1171884/original/027743900_1458037827-20160315-Hari-ini-BBM-turun-Rp-200-Angga-2.jpg"));
        datas.add(new BeritaDanPromo("01 Nov 2016", "Dampak Efisiensi, Laba Pertamina Kuartal III 2016 Naik", "PT Pertamina (Persero) mencatat laba sampai kuartal III 2016, sebesar \u200EUS$ 2,83 miliar, angka tersebut naik hampir 100 persen dibanding periode yang sama pada tahun lalu. Hal ini disebabkan keberhasilan Pertamina melakukan efisiensi.\n" +
                "\n" +
                "Direktur Utama Pertamina Dwi Soetjipto mengatakan, laba bersih perseroan pada kuartal III 2016 sebesar US$ 2,83 miliar, naik US$ US$ 1,41 miliar, dari periode yang sama tahun lalu sebesar US$ 1,42 miliar.", "http://cdn1-a.production.images.static6.com/XL49GoloozLGf0WUiyRsfDUF2Qo=/640x355/smart/filters:quality(75):strip_icc():format(webp)/liputan6-media-production/medias/708678/big/minyak-pertamina-140717-andri.jpg"));
        final AdapterBeritaDanPromo adapterBeritaDanPromo = new AdapterBeritaDanPromo(getContext(), datas);
        rootView.setAdapter(adapterBeritaDanPromo);
        adapterBeritaDanPromo.notifyDataSetChanged();
        rootView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 1) {
                    BeritaDanPromo data = adapterBeritaDanPromo.getItem(position);
                    FragmentBerita fragment = new FragmentBerita();
                    fragment.init(data);
                    ((BaseActivity) getActivity()).changeFragment(fragment, false, true);
                }
            }
        });
        return rootView;
    }
}
