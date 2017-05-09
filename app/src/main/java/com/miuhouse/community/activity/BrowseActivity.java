package com.miuhouse.community.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ZoomButtonsController;

import com.miuhouse.community.R;
import com.miuhouse.community.http.FinalData;
import com.miuhouse.community.utils.MyUtils;
import com.miuhouse.community.widget.StatusCompat;
import java.util.ArrayList;

/**
 * Created by kings on 1/28/2016.
 */
public class BrowseActivity extends BaseActivity {

    private static final String TAG = "BrowseActivity";
    public static final String BROWSER_KEY = "browser_url";

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private String url;
    private String titleStr;
    private boolean isSplash;
    private Toolbar mToolbar;
    private LinearLayout linearWebview;
    private ImageView imgReload;

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private final static int FILECHOOSER_RESULTCODE = 1;

    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initTitle() {
        mToolbar = (Toolbar) findViewById(R.id.titlebar);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.mipmap.back_black);
        setSupportActionBar(mToolbar);
        StatusCompat.compat(this, StatusCompat.COLOR_DEFAULT);
        TextView title = (TextView) findViewById(R.id.title);
        if (titleStr != null) {
            title.setText(titleStr);
        } else {
            title.setText("资讯");
        }
        title.setTextColor(Color.parseColor("#1E2129"));
        title.setClickable(true);
        if (isSplash) {
            mProgressBar.setVisibility(View.GONE);
            mToolbar.setVisibility(View.GONE);
        }
        if (null != savedInstanceState) {
            mWebView.restoreState(savedInstanceState);
        } else {
            mWebView.loadUrl(url);
        }
    }

    @JavascriptInterface
    @Override
    public void initView() {
        setContentView(R.layout.activity_browse);
        mWebView = (WebView) findViewById(R.id.webView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        linearWebview = (LinearLayout) findViewById(R.id.linear_webview);
        mWebView.setWebViewClient(new InnerWebViewClient());
        mWebView.setWebChromeClient(new InnerWebChromeClient());
        WebSettings settings = mWebView.getSettings();
        /*settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);*/
        settings.setDefaultFontSize(15);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        //        webview加載优化
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath("/data/data/" + "com.miuhouse.community" + "/cache");
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= 19) {
            settings.setLoadsImagesAutomatically(true);
        } else {
            settings.setLoadsImagesAutomatically(false);
        }

        int sysVersion = Build.VERSION.SDK_INT;
        if (sysVersion >= 11) {
            settings.setDisplayZoomControls(false);
        } else {
            ZoomButtonsController zbc = new ZoomButtonsController(mWebView);
            zbc.getZoomControls().setVisibility(View.GONE);
        }
        View emptyView = findViewById(R.id.nonetwork);
        if (!MyUtils.isNetworkConnected(this)) {
            linearWebview.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);

            imgReload = (ImageView) emptyView.findViewById(R.id.iv_reload);
            imgReload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWebView.reload();
                    if (MyUtils.isNetworkConnected(BrowseActivity.this)) {
                        if (linearWebview.getVisibility() == View.GONE) {
                            linearWebview.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mWebView != null) {
            mWebView.saveState(outState);
        }
    }

    private void startRefreshAnimation() {
        Animation mAnimation = AnimationUtils.loadAnimation(context, R.anim.refresh);
        imgReload.startAnimation(mAnimation);
    }

    private void closeRefreshAnimation() {
        imgReload.clearAnimation();
    }

    private boolean isPermissionAuthorized() {
        boolean flag = true;
        ArrayList<String> permissionList = new ArrayList<>();
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED)) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
            flag = false;
        }
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED)) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            flag = false;
        }
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED)) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            flag = false;
        }
        String[] permissions = new String[permissionList.size()];
        for (int i = 0; i < permissionList.size(); i++) {
            permissions[i] = permissionList.get(i);
        }
        if (permissions != null && permissions.length > 0) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
        return flag;
    }

    private class InnerWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.equals(FinalData.URL_HEAD + "/mobile/miuhouse.com")) {
                if (isPermissionAuthorized()) {

                    Intent intent = new Intent(BrowseActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else if (url.contains("http://cloud.miuhouse.com/wap/huxingDetail/")) {

                view.loadUrl(url);
            } else {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (mWebView != null && !mWebView.getSettings().getLoadsImagesAutomatically()) {
                mWebView.getSettings().setLoadsImagesAutomatically(true);
            }
            Log.i("TAG", "onPageFinished");
            if (MyUtils.isNetworkConnected(BrowseActivity.this)) {
                if (linearWebview.getVisibility() == View.GONE) {
                    linearWebview.setVisibility(View.VISIBLE);
                }
            }
            super.onPageFinished(view, url);
        }
    }

    private class InnerWebChromeClient extends WebChromeClient {

        //关键代码，以下函数是没有API文档的，所以在Eclipse中会报错，如果添加了@Override关键字在这里的话。
        //以下为webview调用html的上传代码
        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {

            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(
                Intent.createChooser(i, "File Browser"),
                FILECHOOSER_RESULTCODE);
        }

        //For Android 4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType,
            String capture) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
        }

        // For Android 5.0+
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
            WebChromeClient.FileChooserParams fileChooserParams) {
            mUploadCallbackAboveL = filePathCallback;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(
                Intent.createChooser(i, "File Browser"),
                FILECHOOSER_RESULTCODE);
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            super.onProgressChanged(view, newProgress);

            if (!mProgressBar.isShown()) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            mProgressBar.setProgress(newProgress);
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onDestroy() {

        if (mWebView != null) {
            mWebView.stopLoading();
            mWebView.clearCache(true);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {

        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onPause() {

        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void initData() {

    }

    @Override
    public void initVariables() {
        url = getIntent().getStringExtra(BROWSER_KEY);
        titleStr = getIntent().getStringExtra("title");
        if (url == null) {
            isSplash = true;
            url = FinalData.URL_INDEX;
        }
    }

    @Override
    public String getTag() {
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //flipscreen not loading again
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // 捕捉“回退”按键，让WebView能回退到上一页，而不是直接关闭Activity。
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //处理选择图片后返回的uri，然后发送给后台
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null
                : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (null != mUploadMessage) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE
            || mUploadCallbackAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null) {
                    results = new Uri[] {Uri.parse(dataString)};
                }
            }
        }
        mUploadCallbackAboveL.onReceiveValue(results);
        mUploadCallbackAboveL = null;
        return;
    }
}
