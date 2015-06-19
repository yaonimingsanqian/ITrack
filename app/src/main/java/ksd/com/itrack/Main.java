package ksd.com.itrack;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.tencent.map.geolocation.TencentGeofenceManager;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import ksd.com.itrack.common.Common;
import ksd.com.itrack.common.ILog;
import ksd.com.itrack.dialog.CustomDialogWithInput;


public class Main extends Activity implements TencentLocationListener {


    private String TAG = "Main";
    private String serverIP = "192.168.1.113";
    private String serverName = "zhao";
    XmppConnectionManager xmppConnectionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TencentLocationRequest request  = TencentLocationRequest.create();
        request.setRequestLevel(1);
        request.setAllowCache(true);
        request.setInterval(10000);
        TencentLocationManager.getInstance(this).requestLocationUpdates(request,this);
        String imei = Common.getIMEI(this);
        if(imei == null){
            Common.showTast(this,getResources().getString(R.string.notsupportyourdevice));
        }else{
            initXMPP();
        }
        KSDNetCheck.hack(this);
        CustomDialogWithInput.Builder builder = new CustomDialogWithInput.Builder(this);
        builder.create().show();

    }

    public void initXMPP(){

        if(xmppConnectionManager == null){
            xmppConnectionManager = new XmppConnectionManager(serverIP,5222,Main.this);
            xmppConnectionManager.setLoginOpenfireState(new XmppConnectionManager.LoginOpenfireState() {
                @Override
                public void loginSuccess() {
                    ILog.w(TAG,"登录成功");
                }
                @Override
                public void loginFail(String desc){
                    ILog.v("登陆失败",desc);
                    try{
                        xmppConnectionManager.regist(Common.getIMEI(Main.this),Common.getIMEI(Main.this),serverName);
                    }catch (Exception e){

                    }

                }
            });

            xmppConnectionManager.registerState = new XmppConnectionManager.RegisterState() {
                @Override
                public void registerSuccess() {
                   ILog.v("注册成功","registerSuccess,执行登陆");
                   resetXmpp();

                }

                @Override
                public void registerFail(String desc) {
                    Log.v("注册失败",desc);
                }
            };

            xmppConnectionManager.setOnReceiveMsg(new XmppConnectionManager.OnReceiveMsg() {
                @Override
                public void receiveMsg(final String msg,int color, final String size) {

                }
                @Override
                public void receiveClearMsg(String msg){

                }
                @Override
                public void receiveBindMsg(String msg){

                }
                @Override
                public void receiveUnBindMsg(String msg){

                }
                @Override
                public void receiveGooff(String msg){

                }
                @Override
                public void receiveOnline(String msg){

                }
                @Override
                public void receiveAdmin(String msg){

                }
                @Override
                public void receivePush(final String msg,final String size){


                }
                @Override
                public void receiveOpenfirePush(final String msg,final String size,int color){

                }

            });

            xmppConnectionManager.setConnectOpenfireSuccess(new XmppConnectionManager.ConnectOpenfireSuccess() {
                @Override
                public void connectSuccess() {
                    xmppConnectionManager.login(Common.getIMEI(Main.this),Common.getIMEI(Main.this));
                }
                @Override
                public void connectFailed() {

                }
            });
        }
        xmppConnectionManager.resetConnection();

    }
    @Override
    public void onLocationChanged(TencentLocation location, int error, String reason) {
        // do your work
        Log.w(TAG,location.getAddress());
    }

    @Override
    public void onStatusUpdate(String name, int status, String desc) {
        // do your work
    }

    private void resetXmpp(){
        if(xmppConnectionManager != null){
            xmppConnectionManager.xmppConnection.disconnect();
        }
        xmppConnectionManager.xmppConnection = null;
        xmppConnectionManager = null;
        initXMPP();
    }
}
