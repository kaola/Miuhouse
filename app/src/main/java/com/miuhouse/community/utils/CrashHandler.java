package com.miuhouse.community.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

import com.miuhouse.community.application.ActivityManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by khb on 2015/12/28.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final boolean DEBUG = true;
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/MiuhouseCrash/log/";
    private static final String FILE_NAME = "crash_log";
    private static final String FILE_NAME_SUFFIX = ".log";
    private static CrashHandler sInstance = new CrashHandler();
    private static Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;

    private CrashHandler(){

    }

    public static CrashHandler getInstance(){
        return sInstance;
    }

    public void init(Context context){
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (mDefaultCrashHandler != null && !hasHandledException(ex)){
            mDefaultCrashHandler.uncaughtException(thread, ex);
        }else {
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Toast.makeText(mContext,"程序发生异常，即将退出", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }).start();
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ActivityManager.getInstance().finishAll();
        }
    }

    private boolean hasHandledException(Throwable ex){
        if (ex == null){
            return false;
        }
        //        导出异常信息到SD卡中
        dumpExceptionToSDCard(ex);
        uploadExceptionToServer();
        ex.printStackTrace();


        return true;
    }

    private void dumpExceptionToSDCard(Throwable ex){
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            L.w("sdcard unmounted, skip dump exception");
            return;
        }
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        StringBuffer sb = new StringBuffer();
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null){
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        sb.append(writer.toString());

        try {
            long current = System.currentTimeMillis();
            String time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date(current));
            File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println(sb.toString());
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 手机硬件软件信息
     * @param pw
     * @throws PackageManager.NameNotFoundException
     */
    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),PackageManager.GET_ACTIVITIES);

        pw.print("App Version: ");
        pw.print(pi.versionName);
        pw.print("_");
        pw.println(pi.versionCode);

        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);

        pw.print("Model: ");
        pw.println(Build.MODEL);

        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);

    }

    /**
     * 上传错误日志到服务器
     */
    private void uploadExceptionToServer() {
        
    }


}
