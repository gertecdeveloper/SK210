package com.example.serial;

import android.annotation.SuppressLint;
import android.os.Build;
import android.content.Context;
import android.provider.Settings;

public class DeviceInfoUtil {

    @SuppressLint("HardwareIds")
    public static String getSerialNumber(Context context) {
        String serialNumber = null;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                serialNumber = Build.getSerial();
            }
        } catch (SecurityException e) {
            // getSerial() requer permissão READ_PHONE_STATE
            serialNumber = "Permissão necessária";
        }

        if (serialNumber.equals(Build.UNKNOWN)) {
            // O número de série não está disponível ou requer permissão
            serialNumber = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        return serialNumber;
    }
}
