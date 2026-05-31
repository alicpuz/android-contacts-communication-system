package com.example.contactsapplication;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.List;

public class ContactsReceiverActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ContactsAppPrefs";
    private static final String KEY_PERMISSIONS_GRANTED = "permissions_granted";

    private ListView lvContacts;
    private TextView tvHeader;
    private LinearLayout errorLayout;
    private TextView tvError;
    private Button btnClose, btnGrantPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contacts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.lvContacts), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lvContacts = findViewById(R.id.lvContacts);
        tvHeader = findViewById(R.id.tvHeader);
        errorLayout = findViewById(R.id.errorLayout);
        tvError = findViewById(R.id.tvError);
        btnClose = findViewById(R.id.btnClose);
        btnGrantPermission = findViewById(R.id.btnGrantPermission);

        errorLayout.setVisibility(View.GONE);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean permissionsGranted = preferences.getBoolean(KEY_PERMISSIONS_GRANTED, false);

        Intent intent = getIntent();
        if (intent != null){

            if ((!SignatureUtils.isSignatureValid(this, "com.example.contactsprovider"))) {
                showInvalidSignatureView();
                return;
            }
            
            if ("ACCESS_DENIED".equals(intent.getStringExtra("error"))) {
                savePermissionsState(false);
                showPermissionDeniedView();
            } else if(intent.hasExtra("contacts_data")) {
                String encryptedContactsData = intent.getStringExtra("contacts_data");

                if (encryptedContactsData != null) {
                    Toast.makeText(this, "Otrzymano kontakty", Toast.LENGTH_SHORT).show();
                    String contactsData = CryptographyManager.decryptContactsData(encryptedContactsData);
                    showContactsDataView(contactsData);
                    savePermissionsState(true);
                }
            }
        }
        btnClose.setOnClickListener(v -> finish());
        btnGrantPermission.setOnClickListener(v -> {
            Intent requestPermissionIntent = new Intent();
            requestPermissionIntent.setAction("android.intent.action.CONTACTS");
            requestPermissionIntent.setComponent(new ComponentName(
                    "com.example.contactsprovider",
                    "com.example.contactsprovider.MainActivity"
            ));
            try {
                startActivity(requestPermissionIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Nie można otworzyć aplikacji ContactsProvider", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showInvalidSignatureView() {
        lvContacts.setVisibility(View.GONE);
        tvHeader.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
        tvError.setText("Nieprawidłowy podpis aplikacji!");
    }

    private void savePermissionsState(boolean b) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_PERMISSIONS_GRANTED, b);
        editor.apply();
    }

    private void showContactsDataView(String contactsData) {

        errorLayout.setVisibility(View.GONE);
        tvHeader.setVisibility(View.VISIBLE);
        lvContacts.setVisibility(View.VISIBLE);

        List<String> contactsList = Arrays.asList(contactsData.split("\n"));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                contactsList
        );
        lvContacts.setAdapter(adapter);
        tvHeader.setText("Liczba kontaktów: " + contactsList.size()/2);
    }

    private void showPermissionDeniedView() {
        Toast.makeText(this, "Nie przyznano uprawnień do kontaktów", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}
