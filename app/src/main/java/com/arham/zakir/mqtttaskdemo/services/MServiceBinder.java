package com.arham.zakir.mqtttaskdemo.services;

import android.os.Binder;

import com.arham.zakir.mqtttaskdemo.interfaces.IMessageListener;

/**
 * Created by zakir.pervez on 12/6/17.
 */

public class MServiceBinder extends Binder {
    private MChatService mChatService;
    private IMessageListener mListener;

    public MServiceBinder(MChatService mChatService) {
        this.mChatService = mChatService;
    }

    public MChatService getService() {
        return mChatService;
    }

    public void setMessageArrivedListener(IMessageListener listener) {
        mListener = listener;
    }

    public void messageArrived(String msg) {
        if (mListener != null)
            mListener.messageArrive(msg);
    }
}
