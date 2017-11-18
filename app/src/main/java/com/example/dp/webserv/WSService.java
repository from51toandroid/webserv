package com.example.dp.webserv;

//import org.join.ws.Constants.Config;
//import org.join.ws.receiver.NetworkReceiver;
//import org.join.ws.receiver.OnNetworkListener;
//import org.join.ws.receiver.OnStorageListener;
//import org.join.ws.receiver.StorageReceiver;
//import org.join.ws.receiver.WSReceiver;
//import org.join.ws.util.CommonUtil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.join.ws.util.CommonUtil;




/**
 * @brief 应用后台服务
 * @author join
 */
public class WSService extends Service implements OnNetworkListener, OnStorageListener {

    static final String TAG = "WSService";
    static final boolean DEBUG = false || Constants.Config.DEV_MODE;

    public static final String ACTION = "org.join.service.WS";

    public boolean isWebServAvailable = false;

    private boolean isNetworkAvailable;
    private boolean isStorageMounted;
    private CommonUtil mCommonUtil;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d( TAG, "onCreate1" );

        NetworkReceiver.register(this, this);
        StorageReceiver.register(this, this);
        mCommonUtil = CommonUtil.getSingleton();
        isNetworkAvailable = mCommonUtil.isNetworkAvailable();
        isStorageMounted = mCommonUtil.isExternalStorageMounted();

        isWebServAvailable = isNetworkAvailable && isStorageMounted;
        notifyWebServAvailable(isWebServAvailable);
    }

    @Override
    public void onDestroy() {
        Log.d( TAG, "onDestroy" );
        super.onDestroy();
        NetworkReceiver.unregister(this);
        StorageReceiver.unregister(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d( TAG, "onBind" );
        return null;
    }

    @Override
    public void onConnected(boolean isWifi) {
        isNetworkAvailable = true;
        //Log.d( TAG, "onConnected 123" );
        //mCommonUtil.stack_dump();
        notifyWebServAvailableChanged();
    }

    @Override
    public void onDisconnected() {
        Log.d( TAG, "onDisconnected" );
        isNetworkAvailable = false;
        notifyWebServAvailableChanged();
    }

    @Override
    public void onMounted() {
        Log.d( TAG, "onMounted" );
        isStorageMounted = true;
        notifyWebServAvailableChanged();
    }

    @Override
    public void onUnmounted() {
        Log.d( TAG, "onUnmounted" );
        isStorageMounted = false;
        notifyWebServAvailableChanged();
    }

    private void notifyWebServAvailable(boolean isAvailable) {
        if (DEBUG) {
            //Log.d(TAG, "notifyWebServAvailable:" + isAvailable);
        }
        // Notify if web service is available.
        String action = isAvailable ? WSReceiver.ACTION_SERV_AVAILABLE
                : WSReceiver.ACTION_SERV_UNAVAILABLE;

        //Log.d(TAG, "action:" + action);

        Intent intent = new Intent(action);
        sendBroadcast(intent, WSReceiver.PERMIT_WS_RECEIVER);


       // WebService
    }

    private void notifyWebServAvailableChanged() {
        //Log.d( TAG, "notifyWebServAvailableChanged" );
        //Log.d(TAG, "isNetworkAvailable:" + isNetworkAvailable);
        //Log.d(TAG, "isStorageMounted:" + isStorageMounted);
        boolean isAvailable = isNetworkAvailable && isStorageMounted;
        //Log.d(TAG, "isWebServAvailable:" + isWebServAvailable);
        //Log.d(TAG, "isAvailable:" + isAvailable);
        if (isAvailable != isWebServAvailable) {
            notifyWebServAvailable(isAvailable);
            isWebServAvailable = isAvailable;
        }
    }

}
