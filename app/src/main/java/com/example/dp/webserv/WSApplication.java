package com.example.dp.webserv;

/**
 * Created by ASUS on 2016/10/16.
 */


import java.io.IOException;
import java.util.Locale;

import net.asfun.jangod.lib.TagLibrary;
//import net.asfun.jangod.lib.tag.ResColorTag;
//import net.asfun.jangod.lib.tag.ResStrTag;
//import net.asfun.jangod.lib.tag.UUIDTag;
//import org.join.ws.Constants.Config;
//import org.join.ws.serv.TempCacheFilter;
//import org.join.ws.service.WSService;
//import org.join.ws.ui.PreferActivity;
//import org.join.ws.util.CopyUtil;
import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;


import com.example.dp.webserv.Constants.Config;

import org.join.ws.util.CopyUtil;

/**
 * @brief 应用全局
 * @author join
 */
public class WSApplication extends Application {
    static final String TAG = "WSApplication";
    private static WSApplication self;

    private Intent wsServIntent;
    protected void switchLanguage(String language) {
        Resources resources = getResources();
        //Log.i( TAG, "resources = " + resources );
        Configuration config = resources.getConfiguration();
        //Log.i( TAG, "config = " + config );
        DisplayMetrics dm = resources.getDisplayMetrics();
        //Log.i( TAG, "dm = " + dm );
        if (language.equals("en")) {
            config.locale = Locale.ENGLISH;
        } else {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        }
        //Log.i( TAG, "config.locale = " + config.locale );
        resources.updateConfiguration(config, dm);
    }
    @Override
    public void onCreate() {
        //Log.i(TAG, "onCreate");
        super.onCreate();

        self = this;
        wsServIntent = new Intent(WSService.ACTION);

        initAppDir();
        initJangod();
        initAppFilter();
        //switchLanguage("en");
        if (!Config.DEV_MODE) {
            /* 全局异常崩溃处理 */
            new CrashHandler(this);
        }

        PreferActivity.restoreAll();





        //startWsService();
    }

    public static WSApplication getInstance() {
        return self;
    }

    /**
     * @brief 开启全局服务
     */
    public void startWsService() {
        Log.i(TAG, "startWsService");
        startService(wsServIntent);
    }

    /**
     * @brief 停止全局服务
     */
    public void stopWsService() {
        Log.i(TAG, "stopWsService");
        stopService(wsServIntent);
    }

    /**
     * @brief 初始化应用目录
     */
    private void initAppDir() {
        CopyUtil mCopyUtil = new CopyUtil(getApplicationContext());
        // mCopyUtil.deleteFile(new File(Config.SERV_ROOT_DIR)); // 清理服务文件目录
        try {
            // 重新复制到SDCard，仅当文件不存在时
            mCopyUtil.assetsCopy("ws", Config.SERV_ROOT_DIR, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief 初始化Jangod，添加自定义内容
     */
    private void initJangod() {
        /* custom tags */
        TagLibrary.addTag(new ResStrTag());
        TagLibrary.addTag(new ResColorTag());
        TagLibrary.addTag(new UUIDTag());
        /* custom filters */
    }

    /**
     * @brief 初始化应用过滤器
     */
    private void initAppFilter() {
        /* TempCacheFilter */
        TempCacheFilter.addCacheTemps("403.html", "404.html", "503.html");
        /* GzipFilter */
    }

}
