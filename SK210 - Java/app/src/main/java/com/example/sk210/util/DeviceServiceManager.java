package com.example.sk210.util;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.topwise.cloudpos.aidl.AidlDeviceService;
import com.topwise.cloudpos.aidl.camera.AidlCameraScanCode;
import com.topwise.cloudpos.aidl.printer.AidlPrinter;
import com.topwise.cloudpos.aidl.system.AidlSystem;
import java.lang.reflect.Method;

public class DeviceServiceManager {
    private static final String TAG = "PriceScan/DeviceServiceManager";

    private static final String DEVICE_SERVICE_PACKAGE_NAME = "com.android.topwise.topusdkservice";
    private static final String DEVICE_SERVICE_CLASS_NAME = "com.android.topwise.topusdkservice.service.DeviceService";
    private static final String ACTION_DEVICE_SERVICE = "topwise_cloudpos_device_service";

    private static DeviceServiceManager instance;
    private static Context mContext;
    private AidlDeviceService mDeviceService;
    private boolean isBind = false;

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

    public AidlPrinter getPrintManager() {
        try {
            getDeviceService();
            if (mDeviceService != null) {
                return AidlPrinter.Stub.asInterface(mDeviceService.getPrinter());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
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
}