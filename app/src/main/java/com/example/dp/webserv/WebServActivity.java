package com.example.dp.webserv;

/**
 * Created by ASUS on 2016/10/16.
 */



import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;




/**
 * @brief 绑定Web Service的抽象Activity
 * @author join
 */
public abstract class WebServActivity extends Activity implements WebServer.OnWebServListener {

    static final String TAG = "WebServActivity";

    protected Intent webServIntent;
    protected WebService webService;
    private boolean isBound = false;

    private ServiceConnection servConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected");
            webService = ((WebService.LocalBinder) service).getService();
            Log.i( TAG, "webService = " + webService );
            webService.setOnWebServListener(WebServActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected");
            webService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        webServIntent = new Intent(this, WebService.class);
    }

    protected boolean isBound() {
        return this.isBound;
    }

    protected void doBindService() {
        // Restore configs of port and root here.
        //PreferActivity.restore(PreferActivity.KEY_SERV_PORT, PreferActivity.KEY_SERV_ROOT);
        //????????????????????????????????????????????????-----
        //Log.i(TAG, "doBindService");
        boolean bind_result = false;

        Log.i(TAG, "isBound = " + isBound );

        if( isBound == false ) {
            bind_result = bindService(webServIntent, servConnection, BIND_AUTO_CREATE);
            Log.i(TAG, "bind_result = " + bind_result );
            isBound = true;
        }else{
            Log.i(TAG, "doBindService err");
        }
    }

    protected void doUnbindService() {
        Log.i(TAG, "doUnbindService");
        Log.i(TAG, "isBound = " + isBound );
        Log.i(TAG, "servConnection = " + servConnection );
        if (isBound) {
            unbindService(servConnection);
            isBound = false;
        }else{
            Log.i(TAG, "doUnbindService err");
        }
    }

    @Override
    protected void onDestroy() {
        doUnbindService();
        super.onDestroy();
    }

}
