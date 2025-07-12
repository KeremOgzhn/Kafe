package com.example.kafe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PinActivity extends AppCompatActivity {

    SQLiteDatabase database;
    TextView tabloDetayTextView;
    EditText pinGiris, musteriAdiGiris;
    Button onaylaButton;
    int masaNumarasi;
    String masaAdi;
    String masaPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        tabloDetayTextView = findViewById(R.id.tabloDetayTextView);
        pinGiris = findViewById(R.id.pinGiris);
        musteriAdiGiris = findViewById(R.id.musteriAdiGiris);
        onaylaButton = findViewById(R.id.onaylaButton);

        // Intent'ten masa numarasını al
        masaNumarasi = getIntent().getIntExtra("MASA_NUMARA", -1);

        // SQLite Veritabanı aç/kontrol
        try {
            database = this.openOrCreateDatabase("KafePin", MODE_PRIVATE, null);
            // Tablo oluşturma
            database.execSQL("CREATE TABLE IF NOT EXISTS tblMasaBilgileri (id INTEGER PRIMARY KEY, masaAdi NVARCHAR, masaPin NVARCHAR)");
            // Örnek veri ekleme
            insertSampleData();

            // Seçilen masanın bilgilerini getir
            getTableDetails();


        } catch (Exception e) {
            e.printStackTrace();
            tabloDetayTextView.setText("HATA: " + e.getMessage());
        }

        // Onay butonu tıklama olayı
        onaylaButton.setOnClickListener(v -> verifyPinAndProceed());
    }

    // Tabloya örnek veri ekleme
    private void insertSampleData() {
        database.execSQL("INSERT OR IGNORE INTO tblMasaBilgileri (id, masaAdi, masaPin) VALUES (1, 'Masa 1', '1234')");
        database.execSQL("INSERT OR IGNORE INTO tblMasaBilgileri (id, masaAdi, masaPin) VALUES (2, 'Masa 2', '5678')");
        database.execSQL("INSERT OR IGNORE INTO tblMasaBilgileri (id, masaAdi, masaPin) VALUES (3, 'Masa 3', '4321')");
        database.execSQL("INSERT OR IGNORE INTO tblMasaBilgileri (id, masaAdi, masaPin) VALUES (4, 'Masa 4', '8765')");
        database.execSQL("INSERT OR IGNORE INTO tblMasaBilgileri (id, masaAdi, masaPin) VALUES (5, 'Masa 5', '3456')");
        database.execSQL("INSERT OR IGNORE INTO tblMasaBilgileri (id, masaAdi, masaPin) VALUES (6, 'Masa 6', '6543')");
        database.execSQL("INSERT OR IGNORE INTO tblMasaBilgileri (id, masaAdi, masaPin) VALUES (7, 'Masa 7', '7890')");
        database.execSQL("INSERT OR IGNORE INTO tblMasaBilgileri (id, masaAdi, masaPin) VALUES (8, 'Masa 8', '0987')");
        database.execSQL("INSERT OR IGNORE INTO tblMasaBilgileri (id, masaAdi, masaPin) VALUES (9, 'Masa 9', '1122')");
        database.execSQL("INSERT OR IGNORE INTO tblMasaBilgileri (id, masaAdi, masaPin) VALUES (10, 'Masa 10', '3344')");
        database.execSQL("INSERT OR IGNORE INTO tblMasaBilgileri (id, masaAdi, masaPin) VALUES (11, 'Masa 11', '5566')");
        database.execSQL("INSERT OR IGNORE INTO tblMasaBilgileri (id, masaAdi, masaPin) VALUES (12, 'Masa 12', '7788')");
        database.execSQL("INSERT OR IGNORE INTO tblMasaBilgileri (id, masaAdi, masaPin) VALUES (13, 'Masa 13', '9900')");
        database.execSQL("INSERT OR IGNORE INTO tblMasaBilgileri (id, masaAdi, masaPin) VALUES (14, 'Masa 14', '1212')");
        database.execSQL("INSERT OR IGNORE INTO tblMasaBilgileri (id, masaAdi, masaPin) VALUES (15, 'Masa 15', '3434')");
    }


    // Masa bilgilerini veritabanından getir
    @SuppressLint("Range")
    private void getTableDetails() {
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM tblMasaBilgileri WHERE id = ?", new String[]{String.valueOf(masaNumarasi)});

            if (cursor.moveToFirst()) {
                masaAdi = cursor.getString(cursor.getColumnIndex("masaAdi"));
                masaPin = cursor.getString(cursor.getColumnIndex("masaPin"));
                tabloDetayTextView.setText("Seçilen Masa: " + masaAdi);
            } else {
                tabloDetayTextView.setText("Masa bilgisi bulunamadı!");
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            tabloDetayTextView.setText("HATA: " + e.getMessage());
        }
    }

    // PIN doğrulama işlemi
    private void verifyPinAndProceed() {
        String enteredPin = pinGiris.getText().toString();
        String customerName = musteriAdiGiris.getText().toString();

        if (enteredPin.isEmpty() || customerName.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enteredPin.equals(masaPin)) {
            Toast.makeText(this, "PIN doğru! Sipariş ekranına yönlendiriliyorsunuz.", Toast.LENGTH_SHORT).show();
            openOrderActivity(customerName);
        } else {
            Toast.makeText(this, "Hatalı PIN! Masa PIN kodunuzu kontrol edin.", Toast.LENGTH_SHORT).show();
            pinGiris.setText(""); // PIN girişini sıfırla
        }
    }

    // Sipariş ekranına geçiş
    private void openOrderActivity(String musteriAdi) {
        Intent intent = new Intent(PinActivity.this, siparisActivity.class);
        intent.putExtra("MASA_NUMARA", masaNumarasi);
        intent.putExtra("MUSTERI_ADI", musteriAdi);
        startActivity(intent);
        finish();
    }
}