package com.example.escpos_sk210.async;

import android.content.Context;

public class AsyncTcpEscPosPrint extends AsyncEscPosPrint {
    public AsyncTcpEscPosPrint(Context context) {
        super(context);
    }

    public AsyncTcpEscPosPrint(Context context, OnPrintFinished onPrintFinished) {
        super(context, onPrintFinished);
    }
}
