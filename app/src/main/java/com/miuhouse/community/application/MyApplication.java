package com.miuhouse.community.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.multidex.MultiDexApplication;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.util.EMLog;
import com.miuhouse.community.bean.UserBean;
import com.miuhouse.community.db.AccountDBTask;
import com.miuhouse.community.utils.Constants;
import com.miuhouse.community.utils.CrashHandler;
import com.miuhouse.community.utils.L;
import com.miuhouse.community.utils.SPUtils;
import com.umeng.common.message.UmengMessageDeviceConfig;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;

import java.util.List;

/**
 * Created by khb on 2015/12/28.
 */
public class MyApplication extends MultiDexApplication {

    private static MyApplication instance;
    private UserBean mUserBean;
    private long deltaBetweenServerAndClientTime;
    private EMMessageListener messageListener;
    private EaseUI easeUI;
    private String city;

    /**
     * 当前用户nickname,为了苹果推送不是userid而是昵称
     */
    public static String currentUserNick = "";
    private PushAgent mPushAgent;

    @Override
    public void onCreate() {
        super.onCreate();
        L.i("Log", "application");
        instance = this;
//        MultiDex.install(this);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        initPlatformConfig();
        EMOptions options = new EMOptions();
//        添加好友不需要验证
        options.setAcceptInvitationAlways(true);
        options.setAutoLogin(true);
        easeUI = EaseUI.getInstance();
        if (easeUI.init(getApplicationContext(), options)) {
//        EMClient.getInstance().init(getApplicationContext(), options);
            EMClient.getInstance().setDebugMode(true);
        }

        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable(mRegisterCallback);
        mPushAgent.onAppStart();
//        setListener();
        String device_token = mPushAgent.getRegistrationId();
        Log.i("Log", "device_token " + device_token);
        SPUtils.saveSPData("token", device_token);
    }

    public IUmengRegisterCallback mRegisterCallback = new IUmengRegisterCallback() {

        @Override
        public void onRegistered(String registrationId) {
            String pkgName = getApplicationContext().getPackageName();
            String info = String.format("enabled:%s  isRegistered:%s  DeviceToken:%s " +
                            "SdkVersion:%s AppVersionCode:%s AppVersionName:%s",
                    mPushAgent.isEnabled(), mPushAgent.isRegistered(),
                    mPushAgent.getRegistrationId(), MsgConstant.SDK_VERSION,
                    UmengMessageDeviceConfig.getAppVersionCode(getApplicationContext()), UmengMessageDeviceConfig.getAppVersionName(getApplicationContext()));
            SPUtils.saveSPData("token", mPushAgent.getRegistrationId());
            Log.i("Log", info);
        }


    };


    /**
     * 初始化分享配置
     * 微博 qq
     */
    private void initPlatformConfig() {
        PlatformConfig.setWeixin("wx7b63852fe9ff7a2e", "af152950bc82c51090089ee879d1a66e");
        PlatformConfig.setQQZone("1105307852", "g5biNXEPJbKXlVnk");
    }


    public static MyApplication getInstance() {
        return instance;
    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    /**
     * 获取手机IMEI号
     *
     * @return
     */
    public String getIMEI() {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }


    /**
     * 获取用户信息
     *
     * @return
     */
    public UserBean getUserBean() {
        if (mUserBean == null) {
            mUserBean = AccountDBTask.getUserBean();
        }
        return mUserBean;
    }

    public void setmUserBean(UserBean mUserBean) {
        this.mUserBean = mUserBean;
    }

    protected void registerEventListener() {
        messageListener = new EMMessageListener() {
            private BroadcastReceiver broadCastReceiver = null;

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMLog.d("TAG", "onMessageReceived id : " + message.getMsgId());
                    //应用在后台，不需要刷新UI,通知栏提示新消息
                    if (!easeUI.hasForegroundActivies()) {
                        L.i("后台收到消息");
                        easeUI.getNotifier().onNewMsg(message);
                    }
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                /*for (EMMessage message : messages) {
                    EMLog.d(TAG, "收到透传消息");
                    //获取消息body
                    EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action();//获取自定义action

                    //获取扩展属性 此处省略
                    //message.getStringAttribute("");
                    EMLog.d(TAG, String.format("透传消息：action:%s,message:%s", action,message.toString()));
                    final String str = appContext.getString(com.hyphenate.easeui.R.string.receive_the_passthrough);

                    final String CMD_TOAST_BROADCAST = "hyphenate.demo.cmd.toast";
                    IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);

                    if(broadCastReceiver == null){
                        broadCastReceiver = new BroadcastReceiver(){

                            @Override
                            public void onReceive(Context context, Intent intent) {
                                // TODO Auto-generated method stub
                                Toast.makeText(appContext, intent.getStringExtra("cmd_value"), Toast.LENGTH_SHORT).show();
                            }
                        };

                        //注册广播接收者
                        appContext.registerReceiver(broadCastReceiver,cmdFilter);
                    }

                    Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
                    broadcastIntent.putExtra("cmd_value", str+action);
                    appContext.sendBroadcast(broadcastIntent, null);
                }*/
            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> messages) {
            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {

            }
        };

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    public void logout() {
        if (AccountDBTask.getUserBean() != null) {
            AccountDBTask.clear();
        }
        this.mUserBean = null;
        Intent intent = new Intent(Constants.INTENT_ACTION_LOGOUT);
        sendBroadcast(intent);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
