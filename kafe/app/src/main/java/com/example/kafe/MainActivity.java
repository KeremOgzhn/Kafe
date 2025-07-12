package com.example.kafe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Başlık ve GridLayout tanımları
        TextView titleText = findViewById(R.id.baslikText);
        GridLayout gridLayout = findViewById(R.id.gridLayout);

        // Butonlar için satır ve sütun sayısını ayarlıyoruz
        gridLayout.setRowCount(5); // 5 Satır
        gridLayout.setColumnCount(3); // 3 Sütun

        // 15 buton oluşturuyoruz
        for (int i = 1; i <= 15; i++) {
            final int masaNumarasi = i;

            // Dinamik buton oluşturma
            Button button = new Button(this);
            button.setText("Masa " + masaNumarasi);
            button.setBackgroundColor(getResources().getColor(R.color.brown)); // Kahverengi
            button.setTextColor(getResources().getColor(android.R.color.white)); // Beyaz yazı
            button.setTextSize(18); // Yazı boyutu daha büyük
            button.setPadding(16, 16, 16, 16);

            // Tıklama olayını ayarlıyoruz
            button.setOnClickListener(v -> {
                Toast.makeText(MainActivity.this, "Masa " + masaNumarasi + " seçildi", Toast.LENGTH_SHORT).show();
                openPinActivity(masaNumarasi);
            });

            // Butonun GridLayout içinde yerleşim parametreleri
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(8, 8, 8, 8); // Kenar boşlukları
            params.width = 0; // Dinamik genişlik
            params.height = 0; // Dinamik yükseklik
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Sütun ağırlığı
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Satır ağırlığı
            button.setLayoutParams(params);

            // Butonu GridLayout'a ekliyoruz
            gridLayout.addView(button);
        }
    }

    // Masa seçimi sonrası PIN ekranını açan metod
    private void openPinActivity(int masaNumara) {
        Intent intent = new Intent(MainActivity.this, PinActivity.class);
        intent.putExtra("MASA_NUMARA", masaNumara);
        startActivity(intent);
    }
}