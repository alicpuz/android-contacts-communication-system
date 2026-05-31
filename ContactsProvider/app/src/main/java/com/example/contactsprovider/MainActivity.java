package com.example.contactsprovider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CryptographyManager.generateKey();
        EdgeToEdge.enable(this);

        Intent intent = getIntent();
        if (intent != null && "android.intent.action.CONTACTS".equals(intent.getAction())) {
            Toast.makeText(this, "Odebrano żądanie kontaktów :)", Toast.LENGTH_SHORT).show();

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
            } else {
                startContactsService();
            }
        }
    }
    private void startContactsService() {
        Intent serviceIntent = new Intent(this, ContactsService.class);
        startService(serviceIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Jeśli pozwolenie zostało przyznane, uruchamiamy usługę
                startContactsService();
            } else {
                sendPermissionDeniedToContactsApplication();
                Log.d("MaainActivity", "Brak uprawnien do kontaktów");
            }
        }
    }

    private void sendPermissionDeniedToContactsApplication() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.RECEIVE_CONTACTS");
        intent.putExtra("error", "ACCESS_DENIED");
        intent.setComponent(new ComponentName(
                "com.example.contactsapplication",
                "com.example.contactsapplication.ContactsReceiverActivity"
        ));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.d("MainActivity", "Nie znaleziono aplikacji do odbioru informacji o błędzie");
        }
    }
}

/*public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CryptographyManager.generateKey();
        EdgeToEdge.enable(this);


        Intent intent = getIntent();
        if (intent != null && "android.intent.action.CONTACTS".equals(intent.getAction())) {
            Log.d("MaainActivity", "Odebrano żądanie kontaktów");

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
            } else {
                // Zamiast bezpośrednio wywoływać metodę accessContacts() przenosimy to do serwisu
                requestContactsFromService();
            }
        }
    }

    private void requestContactsFromService() {
        Intent serviceIntent = new Intent(this, ContactsService.class);
        serviceIntent.setAction(ContactsService.ACTION_ACCESS_CONTACTS);
        startService(serviceIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Jeśli pozwolenie zostało przyznane, uruchamiamy usługę
                requestContactsFromService();
            } else {
                Log.d("MaainActivity", "Brak uprawnien do kontaktów");
            }
        }
    }
}*/







/* public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_READ_CONTACTS = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CryptographyManager.generateKey();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Intent intent = getIntent();
        if (intent != null && "android.intent.action.CONTACTS".equals(intent.getAction())) {
            Toast.makeText(this, "Odebrano żądanie kontaktów :)", Toast.LENGTH_SHORT).show();

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
            } else {
                accessContacts();
            }
        }

    }

    private void accessContacts() {
        ContentResolver contentResolver = getContentResolver();
        Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };

        Cursor cursor = contentResolver.query(
                contactsUri,
                projection,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        );

        if (cursor != null && cursor.getCount() > 0) {
            StringBuilder contactsBuilder = new StringBuilder();
            while (cursor.moveToNext()) {
                String contactID = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                contactsBuilder.append("ID: ").append(contactID).append(", NAME: ").append(displayName).append("\n");

                Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String[] phoneProjection = new String[] {
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                };

                Cursor phoneCursor = contentResolver.query(
                        phoneUri,
                        phoneProjection,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{contactID},
                        null
                );

                if (phoneCursor != null) {
                    while (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(
                                phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        );
                        contactsBuilder.append(" Phone: ").append(phoneNumber).append("\n");
                    }
                    phoneCursor.close();
                }
            }
            Toast.makeText(this, "Kontakty sprawdzone", Toast.LENGTH_SHORT).show();
            cursor.close();
            String encryptedContacts = CryptographyManager.encryptContactsData(contactsBuilder.toString());
            sendContactsToContactsApplication(encryptedContacts);
        } else {
            Toast.makeText(this, "Brak kontaktow", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                accessContacts();
            } else {
                Toast.makeText(this, "Brak uprawnien do kontaktow", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendContactsToContactsApplication (String contactsData) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.RECEIVE_CONTACTS");
        intent.putExtra("contacts_data", contactsData);
        intent.setComponent( new ComponentName (
                "com.example.contactsapplication",
                "com.example.contactsapplication.ContactsReceiverActivity"
        ));
        try {
            startActivity(intent);
            Toast.makeText(this, "Akcja udana", Toast.LENGTH_SHORT).show();
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Nie znaleziono aplikacji do odbioru dancyh", Toast.LENGTH_SHORT).show();
        }

    }
}*/