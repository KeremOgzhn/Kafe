package com.example.kafe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class siparisActivity extends AppCompatActivity {

    private SQLiteDatabase database;
    private LinearLayout urunListeLayout;
    private TextView toplamFiyatText, hosgeldinizText, siparisEkraniText;
    private int toplamFiyat = 0;
    private int toplamUrunSayisi = 0; // Ürün sayısını tutacak değişken
    private String musteriAdi;

    // Ürün listesi ve adetleri tutacak veri yapısı
    private HashMap<String, Integer> urunAdetMap = new HashMap<>();
    private HashMap<String, Integer> urunFiyatMap = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siparis);

        // Ekrandaki bileşenleri tanımlayalım
        urunListeLayout = findViewById(R.id.urunListeLayout);
        toplamFiyatText = findViewById(R.id.toplamFiyatText);
        hosgeldinizText = findViewById(R.id.hosgeldinizText);
        siparisEkraniText = findViewById(R.id.siparisEkraniText);

        // PinActivity'den gelen müşteri adını alalım
        musteriAdi = getIntent().getStringExtra("MUSTERI_ADI");

        // Müşteri adını ekranda göster
        if (musteriAdi != null) {
            hosgeldinizText.setText("Hoşgeldiniz, " + musteriAdi);
        } else {
            hosgeldinizText.setText("Hoşgeldiniz, Misafir");
        }

        // Sipariş ekranı başlığını göster
        siparisEkraniText.setText("Sipariş Ekranı");

        // Veritabanını açıyoruz
        database = openOrCreateDatabase("KafeSiparisDb", MODE_PRIVATE, null);
        createTables();
        insertSampleProducts();
        displayCategories();
    }

    private void createTables() {
        // İçecekler Tablosu
        database.execSQL("CREATE TABLE IF NOT EXISTS Icecekler (id INTEGER PRIMARY KEY AUTOINCREMENT, urunAdi TEXT, urunFiyati INT, urunFotografi TEXT)");

        // Tatlılar Tablosu
        database.execSQL("CREATE TABLE IF NOT EXISTS Tatlilar (id INTEGER PRIMARY KEY AUTOINCREMENT, urunAdi TEXT, urunFiyati INT, urunFotografi TEXT)");

        // Pasta Çeşitleri Tablosu
        database.execSQL("CREATE TABLE IF NOT EXISTS PastaCesitleri (id INTEGER PRIMARY KEY AUTOINCREMENT, urunAdi TEXT, urunFiyati INT, urunFotografi TEXT)");

        // Kahve Çeşitleri Tablosu
        database.execSQL("CREATE TABLE IF NOT EXISTS KahveCesitleri (id INTEGER PRIMARY KEY AUTOINCREMENT, urunAdi TEXT, urunFiyati INT, urunFotografi TEXT)");
    }

    private void insertSampleProducts() {
        // İçecekler Tablosuna Ürünler
        database.execSQL("INSERT INTO Icecekler (urunAdi, urunFiyati, urunFotografi) VALUES "+
                "('Latte', 30, 'latte.jpg'),"+
                "('Espresso', 25, 'espresso.jpg'),"+
                "('Cappuccino', 35, 'cappuccino.jpg'),"+
                "('Americano', 28, 'americano.jpg'),"+
                "('Mocha', 32, 'mocha.jpg')");

        // Tatlılar Tablosuna Ürünler
        database.execSQL("INSERT INTO Tatlilar (urunAdi, urunFiyati, urunFotografi) VALUES "+
                "('Cheesecake', 40, 'cheesecake.jpg'),"+
                "('Brownie', 35, 'brownie.jpg'),"+
                "('Profiterol', 38, 'profiterol.jpg'),"+
                "('Tiramisu', 42, 'tiramisu.jpg'),"+
                "('Sufle', 45, 'sufle.jpg')");

        // Pasta Çeşitleri Tablosuna Ürünler
        database.execSQL("INSERT INTO PastaCesitleri (urunAdi, urunFiyati, urunFotografi) VALUES "+
                "('Meyveli Pasta', 48, 'meyveli_pasta.jpg'),"+
                "('Havuçlu Kek', 36, 'havuc_kek.jpg'),"+
                "('Karamelli Pasta', 55, 'karamel_pasta.jpg'),"+
                "('Fıstıklı Pasta', 52, 'fistikli_pasta.jpg')");

        // Kahve Çeşitleri Tablosuna Ürünler
        database.execSQL("INSERT INTO KahveCesitleri (urunAdi, urunFiyati, urunFotografi) VALUES "+
                "('Türk Kahvesi', 18, 'turk_kahvesi.jpg'),"+
                "('Filtre Kahve', 26, 'filtre_kahve.jpg'),"+
                "('Macchiato', 32, 'macchiato.jpg'),"+
                "('Irish Coffee', 38, 'irish_coffee.jpg'),"+
                "('Flat White', 30, 'flat_white.jpg')");
    }

    // Kategorileri ve ürünlerini ekrana gösterir.
    private void displayCategories() {
        String[] kategoriler = {"Icecekler", "Tatlilar", "PastaCesitleri", "KahveCesitleri"};

        for (String kategori : kategoriler) {
            // Kategori başlığını oluştur ve ekle.
            TextView kategoriBaslik = new TextView(this);
            kategoriBaslik.setText(kategori);
            kategoriBaslik.setTextSize(20);
            kategoriBaslik.setTypeface(null, Typeface.BOLD);
            urunListeLayout.addView(kategoriBaslik);

            // Ürünleri göster.
            displayCategoryProducts(kategori);
        }
    }

    @SuppressLint("Range")
    private void displayCategoryProducts(String kategoriAdi) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + kategoriAdi, null);

        while (cursor.moveToNext()) {
            String urunAdi = cursor.getString(cursor.getColumnIndex("urunAdi"));
            int urunFiyati = cursor.getInt(cursor.getColumnIndex("urunFiyati"));
            String urunFotografi = cursor.getString(cursor.getColumnIndex("urunFotografi"));

            LinearLayout urunLayout = new LinearLayout(this);
            urunLayout.setOrientation(LinearLayout.HORIZONTAL);
            urunLayout.setPadding(8, 8, 8, 8);

            TextView urunAdText = new TextView(this);
            urunAdText.setText(urunAdi + " - " + urunFiyati + "₺");
            urunAdText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
            urunAdText.setTextSize(16);

            ImageView urunResmi = new ImageView(this);
            urunResmi.setLayoutParams(new LinearLayout.LayoutParams(200, 200));

            String urunFotografiAdi = urunFotografi.split("\\.")[0];
            int imageId = getResources().getIdentifier(urunFotografiAdi, "drawable", getPackageName());

            if (imageId != 0) {
                urunResmi.setImageResource(imageId);
            }

            Button ekleButton = new Button(this);
            ekleButton.setText("+");
            ekleButton.setTextColor(getResources().getColor(android.R.color.white));
            ekleButton.setBackgroundColor(Color.parseColor("#8B4513"));
            ekleButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            ekleButton.setOnClickListener(v -> {
                // Eğer ürün daha önce sepete eklenmişse adet artırılır
                if (urunAdetMap.containsKey(urunAdi)) {
                    urunAdetMap.put(urunAdi, urunAdetMap.get(urunAdi) + 1);
                } else {
                    urunAdetMap.put(urunAdi, 1);
                    urunFiyatMap.put(urunAdi, urunFiyati);
                }

                toplamFiyat += urunFiyati;
                toplamUrunSayisi++;
                toplamFiyatText.setText("Toplam Fiyat: " + toplamFiyat + "₺");
            });

            Button silButton = new Button(this);
            silButton.setText("-");
            silButton.setTextColor(getResources().getColor(android.R.color.white));
            silButton.setBackgroundColor(Color.parseColor("#8B4513"));
            silButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            silButton.setOnClickListener(v -> {
                if (urunAdetMap.containsKey(urunAdi) && urunAdetMap.get(urunAdi) > 0) {
                    urunAdetMap.put(urunAdi, urunAdetMap.get(urunAdi) - 1);
                    toplamFiyat -= urunFiyati;
                    toplamUrunSayisi--;

                    if (urunAdetMap.get(urunAdi) == 0) {
                        urunAdetMap.remove(urunAdi);
                        urunFiyatMap.remove(urunAdi);
                    }
                }

                toplamFiyat = Math.max(toplamFiyat, 0);
                toplamFiyatText.setText("Toplam Fiyat: " + toplamFiyat + "₺");
            });

            urunLayout.addView(urunResmi);
            urunLayout.addView(urunAdText);
            urunLayout.addView(ekleButton);
            urunLayout.addView(silButton);

            urunListeLayout.addView(urunLayout);
        }

        cursor.close();
    }

    public void showPaymentDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        builder.setTitle("Ödeme Ekranı");

        LinearLayout paymentLayout = new LinearLayout(this);
        paymentLayout.setOrientation(LinearLayout.VERTICAL);
        paymentLayout.setPadding(16, 16, 16, 16);

        EditText kartSahibiAdi = new EditText(this);
        kartSahibiAdi.setHint("Kart Sahibi Adı");
        paymentLayout.addView(kartSahibiAdi);

        EditText kartNumarasi = new EditText(this);
        kartNumarasi.setHint("Kart Numarası");
        kartNumarasi.setInputType(InputType.TYPE_CLASS_NUMBER);
        paymentLayout.addView(kartNumarasi);

        EditText ayYil = new EditText(this);
        ayYil.setHint("Ay/Yıl");
        paymentLayout.addView(ayYil);

        EditText cvv = new EditText(this);
        cvv.setHint("CVV");
        cvv.setInputType(InputType.TYPE_CLASS_NUMBER);
        paymentLayout.addView(cvv);

        builder.setView(paymentLayout);

        builder.setPositiveButton("Siparişi Onayla", (dialog, which) -> {
            Toast.makeText(this, "Ödeme başarıyla gerçekleşmiştir. Afiyet olsun!", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(siparisActivity.this, siparisOzetiActivity.class);
            intent.putExtra("MUSTERI_ADI", musteriAdi);
            intent.putExtra("TOPLAM_FIYAT", toplamFiyat);

            // Ürün listesi oluşturma
            ArrayList<String> urunListesi = new ArrayList<>();
            for (String urunAdi : urunAdetMap.keySet()) {
                int adet = urunAdetMap.get(urunAdi);
                int urunFiyati = urunFiyatMap.get(urunAdi);
                urunListesi.add(urunAdi + "|" + urunFiyati + "|" + adet);
            }
            intent.putStringArrayListExtra("URUN_LISTESI", urunListesi);
            intent.putExtra("URUN_ADET_MAP", urunAdetMap);
            startActivity(intent);
        });

        builder.setNegativeButton("İptal", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Yazı rengini değiştirecek kodları buraya ekledik
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#8B4513"));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#8B4513"));
    }
}






