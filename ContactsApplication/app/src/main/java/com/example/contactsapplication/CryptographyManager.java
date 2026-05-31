package com.example.contactsapplication;

import android.util.Base64;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class CryptographyManager {

    public static String decryptContactsData (String encryptedContacts) {

        if (encryptedContacts == null || encryptedContacts.isEmpty()) {
            Log.e("MainActivity", "Encrypted contacts are empty or null");
            return "";
        }


        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            SecretKey contactsSecretKey = (SecretKey) keyStore.getKey("ContactsKey", null);

            if (contactsSecretKey == null) {
                Log.e("MainActivity", "Key not found in KeyStore");
                return null;
            }

            byte[] encryptedData = Base64.decode(encryptedContacts, Base64.DEFAULT);
            ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedData);
            byte[] iv  = new byte[12];
            byteBuffer.get(iv);

            byte[] cipherContacts = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherContacts);
            Cipher contactsCipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv);
            contactsCipher.init(Cipher.DECRYPT_MODE, contactsSecretKey, gcmParameterSpec);

            byte[] contactsData = contactsCipher.doFinal(cipherContacts);

            return new String(contactsData, StandardCharsets.UTF_8);

        } catch (Exception e) {
            Log.e("MainActivity", "Contacts decrypting error: " + e.getMessage());
            return null;
        }
    }

}
