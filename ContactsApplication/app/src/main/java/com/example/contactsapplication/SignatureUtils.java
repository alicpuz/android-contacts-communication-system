package com.example.contactsapplication;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignatureUtils {

    public static boolean isSignatureValid(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            Signature[] signatures = packageInfo.signatures;

            PackageInfo appPackageInfo = pm.getPackageInfo("com.example.contactsapplication", PackageManager.GET_SIGNATURES);
            Signature[] appSignatures = appPackageInfo.signatures;

            for (Signature signature : signatures) {
                String signatureSHA256 = SignatureUtils.getSha256(signature.toByteArray());

                if (signatureSHA256 != null) {
                    for (Signature appSignature : appSignatures) {
                        String appSignatureSHA256 = SignatureUtils.getSha256(appSignature.toByteArray());

                        if (signatureSHA256.equals(appSignatureSHA256)) {
                            return true;
                        }
                    }
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String getSha256(byte[] signatureBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(signatureBytes);
            return Base64.encodeToString(hash, Base64.NO_WRAP).trim();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
