package com.example.scan_teste_cliente.util;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.topwise.cloudpos.aidl.AidlDeviceService;
import com.topwise.cloudpos.aidl.buzzer.AidlBuzzer;
import com.topwise.cloudpos.aidl.camera.AidlCameraScanCode;
import com.topwise.cloudpos.aidl.cpucard.AidlCPUCard;
import com.topwise.cloudpos.aidl.decoder.AidlDecoderManager;
import com.topwise.cloudpos.aidl.emv.level2.AidlAmex;
import com.topwise.cloudpos.aidl.emv.level2.AidlEmvL2;
import com.topwise.cloudpos.aidl.emv.level2.AidlEntry;
import com.topwise.cloudpos.aidl.emv.level2.AidlPaypass;
import com.topwise.cloudpos.aidl.emv.level2.AidlPaywave;
import com.topwise.cloudpos.aidl.emv.level2.AidlPure;
import com.topwise.cloudpos.aidl.emv.level2.AidlQpboc;
import com.topwise.cloudpos.aidl.fingerprint.AidlFingerprint;
import com.topwise.cloudpos.aidl.iccard.AidlICCard;
import com.topwise.cloudpos.aidl.led.AidlLed;
import com.topwise.cloudpos.aidl.magcard.AidlMagCard;
import com.topwise.cloudpos.aidl.pedestal.AidlPedestal;
import com.topwise.cloudpos.aidl.pinpad.AidlPinpad;
import com.topwise.cloudpos.aidl.pm.AidlPM;
import com.topwise.cloudpos.aidl.printer.AidlPrinter;
import com.topwise.cloudpos.aidl.psam.AidlPsam;
import com.topwise.cloudpos.aidl.rfcard.AidlRFCard;
import com.topwise.cloudpos.aidl.serialport.AidlSerialport;
import com.topwise.cloudpos.aidl.shellmonitor.AidlShellMonitor;
import com.topwise.cloudpos.aidl.system.AidlSystem;
import com.topwise.cloudpos.aidl.tm.AidlTM;

import java.lang.reflect.Method;

/**
 * @author caixh
 */
public class DeviceServiceManager {
    private static final String TAG = "PriceScan/DeviceServiceManager";

    private static final String DEVICE_SERVICE_PACKAGE_NAME = "com.android.topwise.topusdkservice";
    private static final String DEVICE_SERVICE_CLASS_NAME = "com.android.topwise.topusdkservice.service.DeviceService";
    private static final String ACTION_DEVICE_SERVICE = "topwise_cloudpos_device_service";

    private static DeviceServiceManager instance;
    private Context mContext;
    private AidlDeviceService mDeviceService;
     boolean isBind = false;

    @SuppressLint("LongLogTag")
    public static DeviceServiceManager getInstance() {
        Log.d(TAG,"getInstance()");
        if (null == instance) {
            synchronized (DeviceServiceManager.class) {
                instance = new DeviceServiceManager();
            }
        }
        return instance;
    }

    public boolean isBind() {
        return isBind;
    }

    @SuppressLint("LongLogTag")
    public boolean bindDeviceService(Context context) {
        Log.i(TAG,"bindDeviceService");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return true;
        }
        this.mContext = context;
        Intent intent = new Intent();
        intent.setAction(ACTION_DEVICE_SERVICE);
        intent.setClassName(DEVICE_SERVICE_PACKAGE_NAME, DEVICE_SERVICE_CLASS_NAME);

        try {
            boolean bindResult = mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            Log.i(TAG,"bindResult = " + bindResult);
            return bindResult;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @SuppressLint("LongLogTag")
    public void unBindDeviceService() {
        Log.i(TAG,"unBindDeviceService");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return;
        }
        try {
            mContext.unbindService(mConnection);
        } catch (Exception e) {
            Log.i(TAG,"unbind DeviceService service failed : " + e);
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {

        @SuppressLint("LongLogTag")
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mDeviceService = AidlDeviceService.Stub.asInterface(service);
            Log.d(TAG,"gz mDeviceService" + mDeviceService);
            isBind = true;
            Log.i(TAG,"onServiceConnected  :  " + mDeviceService);
        }

        @SuppressLint("LongLogTag")
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG,"onServiceDisconnected  :  " + mDeviceService);
            mDeviceService = null;
            isBind = false;
        }
    };

    @SuppressLint("LongLogTag")
    public void getDeviceService() {
        if(mDeviceService == null) {
            mDeviceService =  AidlDeviceService.Stub.asInterface(getService(ACTION_DEVICE_SERVICE));
        }
        Log.i(TAG,"onServiceDisconnected  :  " + mDeviceService);
    }

    private static IBinder getService(String serviceName) {
        IBinder binder = null;
        try {
            ClassLoader cl = PriceScanApplication.getContext().getClassLoader();
            Class serviceManager = cl.loadClass("android.os.ServiceManager");
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;
            Method get = serviceManager.getMethod("getService", paramTypes);
            Object[] params = new Object[1];
            params[0] = serviceName;
            binder = (IBinder) get.invoke(serviceManager, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return binder;
    }


    public AidlSystem getSystemManager() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlSystem.Stub.asInterface(mDeviceService.getSystemService());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }



    public AidlCameraScanCode getCameraManager() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlCameraScanCode.Stub.asInterface(mDeviceService.getCameraManager());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
//
//    public Bundle expandFunction(Bundle param) {
//        try {
//            getDeviceService();
//            if (mDeviceService != null) {
//                return mDeviceService.expandFunction(param);
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    // zhongfeiyu add pm by 2022/1/11 @{
//    public AidlPM getPm() {
//        try {
//            getDeviceService();
//            if (mDeviceService != null) {
//                return AidlPM.Stub.asInterface(mDeviceService.getPM());
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//    // @}
//
//    //finger detect
//    public AidlFingerprint getFingerprint(){
//        try {
//            getDeviceService();
//            if (mDeviceService != null) {
//                return AidlFingerprint.Stub.asInterface(mDeviceService.getFingerprint());
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}