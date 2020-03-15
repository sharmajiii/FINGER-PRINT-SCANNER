package com.sharmaji.fingerprint;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {
    private KeyStore keyStore;
    private static  final String key_name="vipul";
    private Cipher cipher;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KeyguardManager keyguardManager=(KeyguardManager)getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager=(FingerprintManager)getSystemService(FINGERPRINT_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        if(!fingerprintManager.isHardwareDetected()){
            Toast.makeText(this,"Authenticatio not available",Toast.LENGTH_SHORT);
        }
        else {
            if(!fingerprintManager.hasEnrolledFingerprints()){
                Toast.makeText(this,"Resister atleast one finger print",Toast.LENGTH_SHORT);

            }
            else {
                if(!keyguardManager.isKeyguardSecure())
                    Toast.makeText(this,"Lock screen security not enable",Toast.LENGTH_SHORT);
                else
                    genkey();
                if(cipherinit()){
                    FingerprintManager.CryptoObject cryptoObject=new FingerprintManager.CryptoObject(cipher);
                    FingerprintHandler helper=new FingerprintHandler(this);
                    helper.startAuthentication(fingerprintManager,cryptoObject);

                }

            }
        }
    }

    private boolean cipherinit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
            try {
                keyStore.load(null);
                SecretKey key = (SecretKey) keyStore.getKey(key_name, null);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return true;
            } catch (CertificateException ex) {
                ex.printStackTrace();
                return false;
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            } catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();
                return false;
            } catch (UnrecoverableKeyException ex) {
                ex.printStackTrace();
                return false;
            } catch (KeyStoreException ex) {
                ex.printStackTrace();
                return false;

            } catch (InvalidKeyException ex) {
                ex.printStackTrace();
                return false;

            }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void genkey() {
        try {
            keyStore=KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator=KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            keyStore.load(null);

            keyGenerator.init(new KeyGenParameterSpec.Builder(key_name,KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build()
            );
            keyGenerator.generateKey();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (InvalidAlgorithmParameterException e){
            e.printStackTrace();
        }
    }}

