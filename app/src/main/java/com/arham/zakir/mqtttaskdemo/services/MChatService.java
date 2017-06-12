package com.arham.zakir.mqtttaskdemo.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.android.service.MqttTraceHandler;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by zakir.pervez on 12/6/17.
 */

public class MChatService extends Service implements MqttCallback, MqttTraceHandler, IMqttActionListener{

    public static final String CLIENT_ID = "ClientId";
    private static final String TAG = "MChatService";

    private MqttAndroidClient mqttAndroidClient;
    private boolean isConnected;
    private MServiceBinder mServiceBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        mServiceBinder = new MServiceBinder(this);
        try{
            connectToMqtt();
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "connectToMqtt - "+e.getMessage());
        }
    }

    private void connectToMqtt() throws MqttException {
        SharedPreferences clientPreferences = getSharedPreferences("client", MODE_PRIVATE);
        if (!clientPreferences.contains(CLIENT_ID)){
            String clientId = java.util.UUID.randomUUID().toString();
            Log.e("clientId first", "Mqtt Client Id - "+ clientId);
            SharedPreferences.Editor mEditor = clientPreferences.edit();
            mEditor.putString(CLIENT_ID, clientId);
            Log.e("clientId Second", "Pref Client Id - "+ clientId);
            mEditor.apply();
        }

        String strClientId  = clientPreferences.getString(CLIENT_ID, "");
        Log.e("clientId Second", "Pref Client Id - "+ strClientId);
        /*String strServer = "m20.cloudmqtt.com";
        String strPort = "18744";*/

        String mqttUri = "tcp://m20.cloudmqtt.com:18744";

        mqttAndroidClient = new MqttAndroidClient(this, mqttUri, strClientId);
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setConnectionTimeout(10000);
        mqttConnectOptions.setKeepAliveInterval(600000);
        mqttConnectOptions.setUserName("pykswtkr");
        mqttConnectOptions.setPassword("8obiEj8vq-Rn".toCharArray());

        mqttAndroidClient.setCallback(this);
        mqttAndroidClient.setTraceCallback(this);
        isConnected = true;
        mqttAndroidClient.connect(mqttConnectOptions, null, this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mServiceBinder;
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.e(TAG, "connectionLost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.e(TAG, message.toString());
        mServiceBinder.messageArrived(message.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    @Override
    public void traceDebug(String source, String message) {
        Log.e(TAG, "traceDebug - " + message);
    }

    @Override
    public void traceError(String source, String message) {
        Log.e(TAG, "traceError - " + message);
    }

    @Override
    public void traceException(String source, String message, Exception e) {
        Log.e(TAG, "traceException - " + e.getMessage());
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        if(isConnected) {
            isConnected = false;
            subscribe();
            Log.e(TAG, "onSuccess");
        }
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Log.e(TAG, "onFailure - " + exception.toString());
    }

    private void subscribe()
    {
        String topic = "office";
        int qos = 2;

        try {
//            String[] topics = new String[1];
//            topics[0] = topic;
            getClient().subscribe(topic, qos, null, this);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public MqttAndroidClient getClient() {
        return mqttAndroidClient;
    }

    public void sendMessage(String message) {
        String topic = "office";
        int qos = 2;
//        boolean retained = false;

       /* String[] args = new String[2];
        args[0] = message;
        args[1] = topic+";qos:"+qos+";retained:" + false;*/

        try {
            mqttAndroidClient.publish(topic, message.getBytes(), qos, false, null, this);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
