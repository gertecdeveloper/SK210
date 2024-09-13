package com.example.escposprinter.connection.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import androidx.annotation.Nullable;

import java.util.Collection;

public class UsbConnections {
    protected UsbManager usbManager;

    /**
     * Create a new instance of UsbConnections
     *
     * @param context Application context
     */
    public UsbConnections(Context context) {
        this.usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }
    
    /**
     * Get a list of USB devices available.
     * @return Return an array of UsbConnection instance
     */
    @Nullable
    public UsbConnection[] getList() {
        if (this.usbManager == null) {
            return null;
        }

        Collection<UsbDevice> devicesList = this.usbManager.getDeviceList().values();
        UsbConnection[] usbDevices = new UsbConnection[devicesList.size()];

        if (devicesList.size() > 0) {
            int i = 0;
            for (UsbDevice device : devicesList) {
                usbDevices[i++] = new UsbConnection(this.usbManager, device);
            }
        }
        
        return usbDevices;
    }

}
