package com.example.contactsapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ContactsAppPrefs";
    private static final String KEY_PERMISSIONS_GRANTED = "permissions_granted";

    private TextView tvExplanation;
    private Button btnRequestPermission;

    private LinearLayout errorLayout;
    private TextView tvError;
    private Button btnClose, btnGrantPermission;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        tvExplanation = findViewById(R.id.tvExplanation);
        btnRequestPermission = findViewById(R.id.btnRequestPermission);

        errorLayout = findViewById(R.id.errorLayout);
        tvError = findViewById(R.id.tvError);
        btnClose = findViewById(R.id.btnClose);
        btnGrantPermission = findViewById(R.id.btnGrantPermission);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean permissionsGranted = preferences.getBoolean(KEY_PERMISSIONS_GRANTED, false);

        if (!permissionsGranted) {
            showPermissionDeniedView();
        } else {
            requestContactsData();
        }

        btnClose.setOnClickListener(v -> finish());
        btnGrantPermission.setOnClickListener(v -> {
            requestContactsData();
        });

        btnRequestPermission.setOnClickListener( v -> {
            Intent intent = new Intent("android.intent.action.CONTACTS");
            intent.addCategory(Intent.CATEGORY_DEFAULT);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                tvExplanation.setText("Nie znaleziono aplikacji do obsługi kontaktów.");
            }
        });
    }

    private void showPermissionDeniedView() {
        tvExplanation.setVisibility(View.GONE);
        btnRequestPermission.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
    }

    private void requestContactsData() {
        Intent intent = new Intent("android.intent.action.CONTACTS");
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}