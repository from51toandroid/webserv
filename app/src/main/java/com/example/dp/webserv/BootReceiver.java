package com.example.dp.webserv;

/**
 * Created by ASUS on 2017/1/16.
 */



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // TODO Auto-generated method stub

        Intent mBootIntent;

        Log.d("WWWWWWWWWWWWWWWWWWWWW", "WWWWWWWWWWWWWWWWWWWWWWWW");
        //Intent mBootIntent = new Intent(arg0, TestService.class);
        //arg0.startService(mBootIntent);

       // Intent mIntent = new Intent();
        //mIntent.setAction(WSService.ACTION);//你定义的service的action
        //mIntent.setPackage( getPackageName() );//这里你需要设置你应用的包名
        //startService(mIntent);
        //com.example.dp.webserv.WebService
        //Intent mBootIntent = new Intent(arg0, WSService.class);
        //arg0.startService(mBootIntent);
        mBootIntent = new Intent(arg0, WebService.class);
        arg0.startService(mBootIntent);

        Log.d("CCCCCCCCCCCCCCCCCCCCC", "CCCCCCCCCCCCCCCCCCCCCCCC");
    }
}


