package com.example.dp.webserv;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;





//import org.join.ws.serv.WebServer.OnWebServListener;
//import org.join.ws.service.WebService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.result.ParsedResultType;





//package com.example.dp.webserv;



/*
import org.join.web.serv.R;
import org.join.ws.Constants.Config;
import org.join.ws.WSApplication;
import org.join.ws.receiver.OnWsListener;
import org.join.ws.receiver.WSReceiver;
import org.join.ws.serv.WebServer;
import org.join.ws.util.CommonUtil;
import org.join.zxing.CaptureActivity;
import org.join.zxing.Contents;
import org.join.zxing.Intents;
import org.join.zxing.encode.QRCodeEncoder;
*/



        import android.app.AlertDialog;
        import android.app.Dialog;
        import android.content.ActivityNotFoundException;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.res.Configuration;
        import android.graphics.Bitmap;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.text.ClipboardManager;
        import android.util.Log;
        import android.view.Display;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.WindowManager;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;
        import android.widget.ToggleButton;

        import com.google.zxing.BarcodeFormat;
        import com.google.zxing.WriterException;
        import com.google.zxing.client.result.ParsedResultType;


        import com.example.dp.webserv.Constants.Config;

import org.join.ws.util.CommonUtil;

/**
 * @brief 主活动界面
 * @details If you want a totally web server, <a href="https://code.google.com/p/i-jetty/">i-jetty</a> may be your choice.
 * @author join
 */
@SuppressWarnings("deprecation")
public class MainActivity extends WebServActivity implements View.OnClickListener, OnWsListener {

    static final String TAG = "MainActivity";
    static final boolean DEBUG = false || Constants.Config.DEV_MODE;

    private CommonUtil mCommonUtil;

    private ToggleButton toggleBtn;
    private Button stop_webserv_Btn;
    private Button quit_webserv_btn;

    private TextView urlText;
    private ImageView qrCodeView;
    private LinearLayout contentLayout;

    private String ipAddr;

    private boolean needResumeServer = false;


    private static final int W_START = 0x0101;
    private static final int W_STOP = 0x0102;
    private static final int W_ERROR = 0x0103;

    private static final int DLG_SERV_USELESS = 0x0201;
    private static final int DLG_PORT_IN_USE = 0x0202;
    private static final int DLG_TEMP_NOT_FOUND = 0x0203;
    private static final int DLG_SCAN_RESULT = 0x0204;

    private static final int REQ_CAPTURE = 0x0001;
    private String lastResult;

    RelativeLayout layout;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            RelativeLayout.LayoutParams params;
            Log.i(TAG, "msg = 0x" + Integer.toHexString(msg.what));
            switch (msg.what) {
                case W_START:
                    setUrlText(ipAddr);
                    params = (RelativeLayout.LayoutParams) toggleBtn.getLayoutParams();
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    contentLayout.setVisibility(View.VISIBLE);
                    //contentLayout.setVisibility(View.INVISIBLE);
                    toggleBtn.setVisibility(View.INVISIBLE);
                    toggleBtn.setVisibility(View.VISIBLE);
                    //不行就这样了
                    break;
                case W_STOP:
                    urlText.setText("");
                    qrCodeView.setImageResource(0);
                    params = (RelativeLayout.LayoutParams) toggleBtn.getLayoutParams();
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    contentLayout.setVisibility(View.GONE);
                    //contentLayout.setVisibility(View.VISIBLE);
                    break;
                case W_ERROR:
                    switch (msg.arg1) {
                        case WebServer.ERR_PORT_IN_USE:
                            showDialog(DLG_PORT_IN_USE);
                            break;
                        case WebServer.ERR_TEMP_NOT_FOUND:
                            showDialog(DLG_TEMP_NOT_FOUND);
                            break;
                        case WebServer.ERR_UNEXPECT:
                        default:
                            Log.e(TAG, "ERR_UNEXPECT");
                            break;
                    }
                    doStopClick();
                    return;
            }
            toggleBtn.setEnabled(true);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate!");

        setContentView(R.layout.main);
        initObjs(savedInstanceState);
        // layout = (RelativeLayout) findViewById(R.id.main_ctrl);
        //Log.i(TAG, "savedInstanceState = " + savedInstanceState);
        initViews(savedInstanceState);

        //服务启动之引子
        //WSApplication.getInstance().startWsService();


        Intent mIntent = new Intent();
        mIntent.setAction(WSService.ACTION);//你定义的service的action
        mIntent.setPackage( getPackageName() );//这里你需要设置你应用的包名
        startService(mIntent);

        //Log.i(TAG, "mIntent1 = " + mIntent);
        //Log.i( TAG, "getPackageName() = " + getPackageName() );

        WSReceiver.register(this, this);

        toggleBtn.performClick();
    }

    private void initObjs(Bundle state) {
        mCommonUtil = CommonUtil.getSingleton();
    }

    private void initViews(Bundle state) {
        toggleBtn = (ToggleButton) findViewById(R.id.toggleBtn);

        stop_webserv_Btn = (Button) findViewById(R.id.stop_webserv_btn);

        quit_webserv_btn = (Button) findViewById(R.id.quit_webserv_btn);

        toggleBtn.setOnClickListener(this);
        stop_webserv_Btn.setOnClickListener(this);
        quit_webserv_btn.setOnClickListener(this);

        urlText = (TextView) findViewById(R.id.urlText);
        qrCodeView = (ImageView) findViewById(R.id.qrCodeView);
        contentLayout = (LinearLayout) findViewById(R.id.contentLayout);
        toggleBtn.setEnabled(true);
        toggleBtn.setChecked(false);
        //Log.i(TAG, "state = " + state);

        if (state != null) {
            ipAddr = state.getString("ipAddr");
            needResumeServer = state.getBoolean("needResumeServer", false);
            boolean isRunning = state.getBoolean("isRunning", false);
            if (isRunning) {
                toggleBtn.setChecked(true);
                setUrlText(ipAddr);
                doBindService();
            }
        }
    }

    private void setUrlText(String ipAddr) {
        String temp;
        temp = getResources().getString(R.string.text_op_prompt);
        //Log.i(TAG, "temp = " + temp );
        String url = temp + "" + "http://" + ipAddr + ":" + Constants.Config.PORT + "/";
        urlText.setText(url);
        url = "http://" + ipAddr + ":" + Constants.Config.PORT + "/";
        generateQRCode(url);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("ipAddr", ipAddr);
        outState.putBoolean("needResumeServer", needResumeServer);
        boolean isRunning = webService != null && webService.isRunning();
        outState.putBoolean("isRunning", isRunning);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (DEBUG)
            Log.d(TAG,
                    newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? "ORIENTATION_LANDSCAPE"
                            : "ORIENTATION_PORTRAIT");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WSReceiver.unregister(this);
        WSApplication.getInstance().stopWsService();

        Intent mIntent = new Intent();
        mIntent.setAction(WSService.ACTION);//你定义的service的action
        mIntent.setPackage(getPackageName());//这里你需要设置你应用的包名
        stopService(mIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_scan_barcode:
            toCaptureActivity();
            break;
        case R.id.action_preferences:
            toPreferActivity();
            break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CAPTURE) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra(Intents.Scan.RESULT);
                ParsedResultType type = ParsedResultType.values()[data.getIntExtra(
                        Intents.Scan.RESULT_TYPE, ParsedResultType.TEXT.ordinal())];
                boolean isShow = false;
                try {
                    if (type == ParsedResultType.URI) {
                        toBrowserActivity(result);
                    } else {
                        isShow = true;
                    }
                } catch (ActivityNotFoundException e) {
                    isShow = true;
                } finally {
                    lastResult = result;
                    if (isShow)
                        showDialog(DLG_SCAN_RESULT);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {

        if( v == toggleBtn ) {
            toggleBtn.setEnabled(false);
            boolean isChecked = toggleBtn.isChecked();
            //Log.i(TAG, "isChecked = " + isChecked);
            //showDialog(DLG_SERV_USELESS);
            if (isChecked) {
                if (!isWebServAvailable()) {
                    Log.i(TAG, "bukeyongc");
                    toggleBtn.setChecked(false);
                    urlText.setText("");
                    showDialog(DLG_SERV_USELESS);
                    return;
                }
                doStartClick();
               // layout.setVisibility(View.GONE);
            } else {
                doStopClick();
            }
            needResumeServer = false;
        }else if( v == stop_webserv_Btn ){
            //android.os.Process.killProcess( android.os.Process.myPid() );
            //finish();
            moveTaskToBack(false);
        }else if( v == quit_webserv_btn ){
            finish();
            android.os.Process.killProcess( android.os.Process.myPid() );
            //finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i( TAG, "onKeyDown" + event + keyCode );
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Log.i( TAG, "onBackPressed" );
        moveTaskToBack(false);
        super.onBackPressed();

    }
    private void doStartClick() {
        //Log.i( TAG, "doStartClick" );
        ipAddr = mCommonUtil.getLocalIpAddress();
        //Log.i( TAG, "ipAddr = " + ipAddr );
        if( ipAddr == null ){
            toggleBtn.setChecked(false);
            urlText.setText("");
            toast(getString(R.string.info_net_off));
            return;
        }
        toggleBtn.setChecked(true);
        doBindService();
        mHandler.sendEmptyMessage(W_START);
    }

    private void doStopClick() {
        Log.i( TAG, "doStopClick" );
        toggleBtn.setChecked(false);
        doUnbindService();
        ipAddr = null;
        //mHandler.sendEmptyMessage(W_STOP);
        //toggleBtn.setEnabled(true);
    }

    private boolean isWebServAvailable() {
        return mCommonUtil.isNetworkAvailable() && mCommonUtil.isExternalStorageMounted();
    }

    @Override
    public void onStarted() {
        Log.i(TAG, "onStarted");
        mHandler.sendEmptyMessage(W_START);
    }

    @Override
    public void onStopped() {
        Log.i(TAG, "onStopped");

        //CommonUtil.getSingleton().stack_dump();

        mHandler.sendEmptyMessage(W_STOP);
    }

    @Override
    public void onError(int code) {
        Log.i( TAG, "onError" );
        Message msg = mHandler.obtainMessage(W_ERROR);
        msg.arg1 = code;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onServAvailable() {
        Log.i( TAG, "onServAvailable" );
        Log.i( TAG, "needResumeServer = " + needResumeServer );

        if (needResumeServer) {
            doStartClick();
            needResumeServer = false;
        }
    }

    @Override
    public void onServUnavailable() {
        Log.i( TAG, "onServUnavailable" );
        if (webService != null && webService.isRunning()) {
            doStopClick();
            needResumeServer = true;
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    // DialogFragment needs android-support.jar in API-8.
    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        switch (id) {
        case DLG_SERV_USELESS:
            return createConfirmDialog(android.R.drawable.ic_dialog_info,
                    R.string.tit_serv_useless, R.string.msg_serv_useless, null);
        case DLG_PORT_IN_USE:
            return createConfirmDialog(android.R.drawable.ic_dialog_info, R.string.tit_port_in_use,
                    R.string.msg_port_in_use, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toPreferActivity();
                        }
                    });
        case DLG_TEMP_NOT_FOUND:
            return createConfirmDialog(android.R.drawable.ic_dialog_info,
                    R.string.tit_temp_not_found, R.string.tit_temp_not_found,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toPreferActivity();
                        }
                    });
        case DLG_SCAN_RESULT:
            AlertDialog dialog = createConfirmDialog(android.R.drawable.ic_dialog_info,
                    R.string.tit_scan_result, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            copy2Clipboard(lastResult);
                        }
                    });
            dialog.setMessage(lastResult);
            return dialog;
        }
        return super.onCreateDialog(id, args);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        switch (id) {
        case DLG_SCAN_RESULT:
            ((AlertDialog) dialog).setMessage(lastResult);
            break;
        }
        super.onPrepareDialog(id, dialog, args);
    }

    private AlertDialog createConfirmDialog(int iconId, int titleId, int messageId,
            DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (iconId > 0)
            builder.setIcon(iconId);
        if (titleId > 0)
            builder.setTitle(titleId);
        if (messageId > 0)
            builder.setMessage(messageId);
        builder.setPositiveButton(android.R.string.ok, positiveListener);
        return builder.create();
    }

    private void toPreferActivity() {
        try {
            Intent intent = new Intent(this, PreferActivity.class);
            intent.putExtra("isRunning", webService == null ? false : webService.isRunning());
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void toCaptureActivity() {
        /*
        try {
            Intent intent = new Intent(this, CaptureActivity.class);
            intent.setAction(Intents.Scan.ACTION);
            startActivityForResult(intent, REQ_CAPTURE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
        */
    }

    private void toBrowserActivity(String uri) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
    }

    private void copy2Clipboard(String text) {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.setText(text);
    }

    private void generateQRCode(String text) {
        Intent intent = new Intent(Intents.Encode.ACTION);
        intent.putExtra(Intents.Encode.FORMAT, BarcodeFormat.QR_CODE.toString());
        intent.putExtra(Intents.Encode.TYPE, Contents.Type.TEXT);
        intent.putExtra(Intents.Encode.DATA, text);
        try {
            int dimension = getDimension();
            QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(this, intent, dimension, false);
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            if (bitmap == null) {
                Log.w(TAG, "Could not encode barcode");
            } else {
                qrCodeView.setImageBitmap(bitmap);
            }
        } catch (WriterException e) {
        }
    }

    private int getDimension() {
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        int dimension = width < height ? width : height;
        dimension = dimension * 3 / 4;
        return dimension;
    }

}