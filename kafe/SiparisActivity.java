public class SiparisActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "DatabasePrefs";
    private static final String IS_DB_INITIALIZED = "isDbInitialized";
    
    EditText cardNumberEdit, expiryDateEdit, cvvEdit;
    Button payButton;
    SQLiteDatabase database;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siparis);
        
        // SharedPreferences başlatma
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        
        // UI elemanlarını tanımlama
        cardNumberEdit = findViewById(R.id.cardNumberEditText);
        expiryDateEdit = findViewById(R.id.expiryDateEditText);
        cvvEdit = findViewById(R.id.cvvEditText);
        payButton = findViewById(R.id.payButton);
        
        // Veritabanı ilk kurulum kontrolü
        if (!sharedPreferences.getBoolean(IS_DB_INITIALIZED, false)) {
            initializeDatabase();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(IS_DB_INITIALIZED, true);
            editor.apply();
        }
        
        // Kart numarası için 16 hane sınırı
        cardNumberEdit.setFilters(new InputFilter[] {
            new InputFilter.LengthFilter(16)
        });
        
        // Kart numarası formatı için TextWatcher
        cardNumberEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Boşlukları kaldır
                String input = s.toString().replaceAll("\\s+", "");
                
                // 4'er gruplar halinde boşluk ekle
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < input.length(); i++) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(" ");
                    }
                    formatted.append(input.charAt(i));
                }
                
                if (!s.toString().equals(formatted.toString())) {
                    cardNumberEdit.setText(formatted.toString());
                    cardNumberEdit.setSelection(formatted.length());
                }
            }
        });
        
        // Ay/Yıl için format ve sınır
        expiryDateEdit.setFilters(new InputFilter[] {
            new InputFilter.LengthFilter(5)
        });
        
        expiryDateEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                
                if (input.length() == 2 && !input.contains("/")) {
                    // Ay kontrolü (01-12)
                    int month = Integer.parseInt(input);
                    if (month > 12) {
                        expiryDateEdit.setText("12");
                        expiryDateEdit.setSelection(2);
                    }
                    s.append("/");
                }
            }
        });
        
        // CVV için 3 hane sınırı
        cvvEdit.setFilters(new InputFilter[] {
            new InputFilter.LengthFilter(3)
        });
        
        // Ödeme butonu işlemi
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndProcessPayment();
            }
        });
    }
    
    private void initializeDatabase() {
        try {
            database = this.openOrCreateDatabase("Siparis", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS urunler (id INTEGER PRIMARY KEY, isim VARCHAR, fiyat VARCHAR)");
            
            // Örnek ürünleri ekle (sadece ilk kurulumda)
            database.execSQL("INSERT INTO urunler (isim, fiyat) VALUES ('Ürün 1', '100 TL')");
            database.execSQL("INSERT INTO urunler (isim, fiyat) VALUES ('Ürün 2', '200 TL')");
            database.execSQL("INSERT INTO urunler (isim, fiyat) VALUES ('Ürün 3', '300 TL')");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void validateAndProcessPayment() {
        String cardNumber = cardNumberEdit.getText().toString().replaceAll("\\s+", "");
        String expiryDate = expiryDateEdit.getText().toString();
        String cvv = cvvEdit.getText().toString();
        
        // Validasyon kontrolleri
        if (cardNumber.length() != 16) {
            cardNumberEdit.setError("Kart numarası 16 haneli olmalıdır");
            return;
        }
        
        if (expiryDate.length() != 5) {
            expiryDateEdit.setError("Geçerli bir son kullanma tarihi giriniz (AA/YY)");
            return;
        }
        
        if (cvv.length() != 3) {
            cvvEdit.setError("CVV 3 haneli olmalıdır");
            return;
        }
        
        // Tüm validasyonlar başarılı ise ödeme işlemine devam et
        Toast.makeText(this, "Ödeme işlemi başarılı!", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
        }
    }
} 