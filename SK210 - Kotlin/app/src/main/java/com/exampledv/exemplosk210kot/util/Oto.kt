package com.exampledv.exemplosk210kot.util

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.topwise.cloudpos.aidl.AidlDeviceService
import com.topwise.cloudpos.aidl.printer.AidlPrinter

class Oto private constructor() {

    private var mContext: Context? = null
    private var mDeviceService: AidlDeviceService? = null
    var isBind: Boolean = false
        private set

    @SuppressLint("LongLogTag")
    fun bindDeviceService(context: Context): Boolean {
        Log.i(TAG, "bindDeviceService")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Handling for API level 26 and above
            return true
        }
        this.mContext = context.applicationContext
        val intent = Intent(ACTION_DEVICE_SERVICE).apply {
            setClassName(DEVICE_SERVICE_PACKAGE_NAME, DEVICE_SERVICE_CLASS_NAME)
        }

        return try {
            val bindResult = mContext?.bindService(intent, mConnection, Context.BIND_AUTO_CREATE) ?: false
            Log.i(TAG, "bindResult = $bindResult")
            bindResult
        } catch (e: Exception) {
            Log.e(TAG, "Failed to bind device service", e)
            false
        }
    }

    @SuppressLint("LongLogTag")
    fun unBindDeviceService() {
        Log.i(TAG, "unBindDeviceService")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return
        }
        try {
            if (isBind) {
                mContext?.unbindService(mConnection)
                isBind = false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unbind device service", e)
        }
    }

    private val mConnection = object : ServiceConnection {
        @SuppressLint("LongLogTag")
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            mDeviceService = AidlDeviceService.Stub.asInterface(service)
            Log.d(TAG, "Device service connected: $mDeviceService")
            isBind = true
        }

        @SuppressLint("LongLogTag")
        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.i(TAG, "Device service disconnected")
            mDeviceService = null
            isBind = false
        }
    }

    @get:SuppressLint("LongLogTag")
    val deviceService: Unit
        get() {
            if (mDeviceService == null) {
                mDeviceService = AidlDeviceService.Stub.asInterface(getService(ACTION_DEVICE_SERVICE))
            }
            Log.i(TAG, "Device service: $mDeviceService")
        }

    private fun getService(serviceName: String): IBinder? {
        return try {
            val serviceManager = Class.forName("android.os.ServiceManager")
            val getServiceMethod = serviceManager.getMethod("getService", String::class.java)
            getServiceMethod.invoke(null, serviceName) as? IBinder
        } catch (e: Exception) {
//            Log.e(TAG, "Failed to get service binder", e)
            null
        }
    }

    val printManager: AidlPrinter?
        get() {
            return try {
                deviceService
                mDeviceService?.let {
                    AidlPrinter.Stub.asInterface(it.printer)
                }
            } catch (e: RemoteException) {
//                Log.e(TAG, "Failed to get print manager", e)
                null
            }
        }

    companion object {
        private const val TAG = "PriceScan/DeviceServiceManager"

        private const val DEVICE_SERVICE_PACKAGE_NAME = "com.android.topwise.topusdkservice"
        private const val DEVICE_SERVICE_CLASS_NAME = "com.android.topwise.topusdkservice.service.DeviceService"
        private const val ACTION_DEVICE_SERVICE = "topwise_cloudpos_device_service"

        @Volatile
        private var instance: Oto? = null

        @JvmStatic
        fun getInstance(): Oto {
            return instance ?: synchronized(this) {
                instance ?: Oto().also { instance = it }
            }
        }
    }
}
