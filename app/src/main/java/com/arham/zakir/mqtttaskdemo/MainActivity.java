package com.arham.zakir.mqtttaskdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.Image;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.arham.zakir.mqtttaskdemo.interfaces.IMessageListener;
import com.arham.zakir.mqtttaskdemo.services.MChatService;
import com.arham.zakir.mqtttaskdemo.services.MServiceBinder;

public class MainActivity extends AppCompatActivity implements IMessageListener{

    private EditText edtMessage;
    private TextView tvMessage;
    private ScrollView mScrollContainer;
    private ImageButton btnSend;
    private MChatService mChatService;
    private boolean isServiceBind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(v);
            }
        });

    }

    private void initView() {
//        initializing views
        edtMessage = (EditText) findViewById(R.id.edtMessage);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        mScrollContainer = (ScrollView) findViewById(R.id.mScrollContainer);
        btnSend = (ImageButton) findViewById(R.id.btnSend);

//        initializing service
        Intent serviceIntent = new Intent(this, MChatService.class);
        startService(serviceIntent);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent serviceIntent = new Intent(this, MChatService.class);
        bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isServiceBind){
            unbindService(mServiceConnection);
            isServiceBind = false;
        }
    }

    @Override
    public void messageArrive(String msg) {
        tvMessage.setText(msg);
        mScrollContainer.post(new Runnable() {
            @Override
            public void run() {
                mScrollContainer.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MServiceBinder mServiceBinder  = (MServiceBinder) service;
            mChatService = mServiceBinder.getService();
            mServiceBinder.setMessageArrivedListener(MainActivity.this);
            isServiceBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBind = false;
        }
    };

    public void sendMessage(View v) {
        mChatService.sendMessage(edtMessage.getText().toString());
        edtMessage.setText("");
    }
}
