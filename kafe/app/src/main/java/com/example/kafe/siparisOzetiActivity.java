package com.example.kafe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class siparisOzetiActivity extends AppCompatActivity {

    private TextView musteriAdiText, toplamFiyatText;
    private LinearLayout urunOzetiLayout;
    private Button anaSayfaButon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siparis_ozeti);

        // XML'deki bileşenleri tanımla
        musteriAdiText = findViewById(R.id.musteriAdiText);
        toplamFiyatText = findViewById(R.id.toplamFiyatText);
        urunOzetiLayout = findViewById(R.id.urunOzetiLayout);
        anaSayfaButon = findViewById(R.id.anaSayfaButon);

        // Intent'ten gelen verileri al
        Intent intent = getIntent();
        String musteriAdi = intent.getStringExtra("MUSTERI_ADI");
        musteriAdiText.setText("Müşteri Adı: " + (musteriAdi != null ? musteriAdi : "Misafir"));
        int toplamFiyat = intent.getIntExtra("TOPLAM_FIYAT", 0);
        ArrayList<String> urunListesi = intent.getStringArrayListExtra("URUN_LISTESI");

        // Ürün özetini doldur
        if (urunListesi != null && !urunListesi.isEmpty()) {
            for (String urun : urunListesi) {
                String[] urunBilgi = urun.split("\\|");
                String urunAdi = urunBilgi[0];
                int urunFiyati = Integer.parseInt(urunBilgi[1]);
                int adet = Integer.parseInt(urunBilgi[2]);

                TextView urunTextView = new TextView(this);
                urunTextView.setText(urunAdi + " x " + adet + " - " + (urunFiyati * adet) + "₺");
                urunTextView.setTextSize(18);
                urunTextView.setTextColor(getResources().getColor(android.R.color.black));
                urunTextView.setPadding(8, 8, 8, 8);

                urunOzetiLayout.addView(urunTextView);
            }
        } else {
            TextView noUrunTextView = new TextView(this);
            noUrunTextView.setText("Sipariş bulunamadı.");
            noUrunTextView.setTextSize(18);
            noUrunTextView.setTextColor(getResources().getColor(android.R.color.black));
            noUrunTextView.setPadding(8, 8, 8, 8);

            urunOzetiLayout.addView(noUrunTextView);
        }

        // Toplam fiyatı ayarla
        toplamFiyatText.setText("Toplam Fiyat: " + toplamFiyat + "₺");

        // Ana Sayfa düğmesine tıklama işlemi
        anaSayfaButon.setOnClickListener(v -> {
            Intent anaSayfaIntent = new Intent(siparisOzetiActivity.this, MainActivity.class);
            anaSayfaIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(anaSayfaIntent);
            finish();
        });
    }
}




