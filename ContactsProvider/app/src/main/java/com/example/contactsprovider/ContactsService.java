package com.example.contactsprovider;

import android.Manifest;
import android.app.IntentService;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;


import androidx.core.content.ContextCompat;

public class ContactsService extends Service {

    private static final int REQUEST_CODE_READ_CONTACTS = 100;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("ContactsService", "Serwis nie ma uprawnien");
        } else {
            accessContacts();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
            Log.d("ContactsService", "Kontakty sprawdzone");
            cursor.close();
            String encryptedContacts = CryptographyManager.encryptContactsData(contactsBuilder.toString());
            sendContactsToContactsApplication(encryptedContacts);
        } else {
            Log.d("ContactsService", "Brak kontaktów");;
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
            Log.d("ContactsService", "Akcja udana");
        } catch (ActivityNotFoundException e) {
            Log.d("ContactsService", "Nie znaleziono aplikacji do odbioru dancyh");
        }

    }
}

/*public class ContactsService extends IntentService {

    public static final String ACTION_ACCESS_CONTACTS = "android.intent.action.ACCESS_CONTACTS";

    public ContactsService() {
        super("ContactsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && ACTION_ACCESS_CONTACTS.equals(intent.getAction())) {
            accessContacts();
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
                String[] phoneProjection = new String[]{
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

            // Szyfruj dane kontaktów przed wysłaniem
            String encryptedContacts = CryptographyManager.encryptContactsData(contactsBuilder.toString());
            sendContactsToContactsApplication(encryptedContacts);

            cursor.close();
        } else {
            Log.d("Contacts Service", "Brak kontaktów");
        }
    }

    private void sendContactsToContactsApplication(String contactsData) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("android.intent.action.RECEIVE_CONTACTS");
        broadcastIntent.putExtra("contacts_data", contactsData);
        broadcastIntent.setComponent(new ComponentName(
                "com.example.contactsapplication",
                "com.example.contactsapplication.ContactsReceiverActivity"
        ));

        try {
            startActivity(broadcastIntent);
        } catch (ActivityNotFoundException e) {
            Log.e("Contacts Service", "Nie znaleziono aplikacji do odbioru danych.");
        }
    }
}*/

