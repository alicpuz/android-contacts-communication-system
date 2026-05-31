package com.example.contactsprovider;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class CryptographyManager {

    public static void generateKey () {
        try {

            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            if (!keyStore.containsAlias("ContactsKey")) {

                KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                        "ContactsKey",
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
                )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setKeySize(256)
                        .build();

                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                keyGenerator.init(keyGenParameterSpec);
                keyGenerator.generateKey();
                Log.d("MainActivity", "Key successfully generated.");
            } else {
                Log.d("MainActivity", "Key already exists. Skipping generation.");
            }

        } catch (Exception e) {
            Log.e("MainActivity", "Generate key error: " + e.getMessage());
        }

    }

    public static String encryptContactsData (String contactsData) {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            SecretKey contactsSecretKey = (SecretKey) keyStore.getKey("ContactsKey", null);

            if (contactsSecretKey == null) {
                Log.e("MainActivity", "SecretKey is null. Unable to encrypt.");
                return null;
            }

            Cipher contactsCipher = Cipher.getInstance("AES/GCM/NoPadding");
            contactsCipher.init(Cipher.ENCRYPT_MODE, contactsSecretKey);

            byte[] iv = contactsCipher.getIV();
            byte[] cipherContacts = contactsCipher.doFinal(contactsData.getBytes(StandardCharsets.UTF_8));
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length+ cipherContacts.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherContacts);

            return Base64.encodeToString(byteBuffer.array(), Base64.DEFAULT);

        } catch (Exception e) {
            Log.e("MainActivity", "Contacts encrypting error: " + e.getMessage());
            return null;
        }
    }
}
