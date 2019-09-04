package com.b7anka.hollywoodtracker.Helpers;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import com.b7anka.hollywoodtracker.R;
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
import androidx.core.app.ActivityCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;

public class FingerPrintManager
{
    public interface FingerPrintListener
    {
        void onAuthenticationSuccess();
        void onAuthenticationFailed();
        void onAuthenticationCancelled();
        void onAuthenticationHelp(int helpMsgId, CharSequence helpString);
        void onAuthenticationError(int errMsgId, CharSequence errString);
        void onPermissionNotGranted(String type);
    }

    private KeyStore keyStore;
    private KeyguardManager keyguardManager;
    FingerprintManagerCompat fingerprintManager;
    private Cipher cipher;
    private Activity activity;
    private FingerPrintListener fingerPrintListener;

    public FingerPrintManager(Activity activity)
    {
        this.activity = activity;
        this.fingerprintManager = FingerprintManagerCompat.from(this.activity);
        this.keyguardManager = (KeyguardManager)this.activity.getSystemService(Context.KEYGUARD_SERVICE);
    }

    public FingerPrintManager(Activity activity, FingerPrintListener fingerPrintListener)
    {
        this(activity);
        this.fingerPrintListener = fingerPrintListener;
    }

    public boolean cipherInit()
    {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES+"/"+KeyProperties.BLOCK_MODE_CBC+"/"+KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return false;
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey)keyStore.getKey(Constants.KEY_NAME,null);
            cipher.init(Cipher.ENCRYPT_MODE,key);
            return true;
        } catch (CertificateException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
            return false;
        } catch (KeyStoreException e) {
            e.printStackTrace();
            return false;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void generateKey()
    {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        KeyGenerator keyGenerator;

        try {
            keyStore.load(null);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                keyGenerator.init(new KeyGenParameterSpec.Builder(Constants.KEY_NAME,KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC).setUserAuthenticationRequired(true).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build());
                keyGenerator.generateKey();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    public boolean isHardwareSupported() {
        return fingerprintManager.isHardwareDetected();
    }

    public boolean isFingerprintAvailable() {
        return fingerprintManager.hasEnrolledFingerprints();
    }

    public boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.USE_FINGERPRINT) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public boolean isPermissionGrantedForBiometrics()
    {
        return ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.USE_BIOMETRIC) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public boolean isKeyguardSecure()
    {
        if(!keyguardManager.isKeyguardSecure())
        {
            return false;
        }
        return true;
    }

    public void initializeFingerPrintAuthentication()
    {
        if(fingerPrintListener != null)
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            {
                if(isPermissionGrantedForBiometrics())
                {
                    BiometricPrompt prompt = new BiometricPrompt.Builder(this.activity)
                            .setTitle(this.activity.getString(R.string.login_button))
                            .setSubtitle(Constants.EMPTY)
                            .setDescription(this.activity.getString(R.string.fingerprint_information))
                            .setNegativeButton(this.activity.getString(android.R.string.cancel), this.activity.getMainExecutor(), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    fingerPrintListener.onAuthenticationCancelled();
                                }
                            })
                            .build();

                    prompt.authenticate(new BiometricPrompt.CryptoObject(cipher), new android.os.CancellationSignal(), this.activity.getMainExecutor(),new BiometricCallBack());
                }
                else
                {
                    fingerPrintListener.onPermissionNotGranted(Constants.BIOMETRIC);
                }
            }
            else
            {
                if(isPermissionGranted())
                {
                    generateKey();
                    if(cipherInit())
                    {
                        fingerprintManager.authenticate(new FingerprintManagerCompat.CryptoObject(cipher),0, new CancellationSignal(), new FingerPrintHandler(), null);
                    }
                }
                else
                {
                    fingerPrintListener.onPermissionNotGranted(Constants.FINGER_PRINT);
                }
            }
        }
    }

    private class FingerPrintHandler extends FingerprintManagerCompat.AuthenticationCallback
    {
        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result)
        {
            super.onAuthenticationSucceeded(result);
            fingerPrintListener.onAuthenticationSuccess();
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString)
        {
            super.onAuthenticationHelp(helpMsgId, helpString);
            fingerPrintListener.onAuthenticationHelp(helpMsgId, helpString);
        }

        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString)
        {
            super.onAuthenticationError(errMsgId, errString);
            fingerPrintListener.onAuthenticationError(errMsgId, errString);
        }

        @Override
        public void onAuthenticationFailed()
        {
            super.onAuthenticationFailed();
            fingerPrintListener.onAuthenticationFailed();
        }
    }

    @TargetApi(Build.VERSION_CODES.P)
    private class BiometricCallBack extends BiometricPrompt.AuthenticationCallback
    {
        @Override
        public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result)
        {
            super.onAuthenticationSucceeded(result);
            fingerPrintListener.onAuthenticationSuccess();
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString)
        {
            super.onAuthenticationHelp(helpCode, helpString);
            fingerPrintListener.onAuthenticationHelp(helpCode, helpString);
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString)
        {
            fingerPrintListener.onAuthenticationError(errorCode, errString);
        }

        @Override
        public void onAuthenticationFailed()
        {
            super.onAuthenticationFailed();
            fingerPrintListener.onAuthenticationFailed();
        }
    }
}
