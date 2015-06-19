package ksd.com.itrack;

import android.content.Context;
import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;


/**
 * Created by test2 on 14/10/29.
 */
public class XmppConnectionManager {

    public static final String CHAT = "chat";
    public static final String CLEAR = "clear";
    public static final String BIND = "bind";
    public static final String UNBIND = "UNBIND";
    public static final String IMNLINE = "IMNLINE";
    public static final String OFFLINE = "OFFLINE";
    public static final String PUSH = "PUSH";
    public static final String OPENFIREPUSH = "OPENFIREPUSH";

    public  ConnectionConfiguration connConfig;
    public  XMPPConnection xmppConnection;
    public  OnReceiveMsg onReceiveMsg;
    public  ConnectOpenfireSuccess connectOpenfireSuccess;
    public LoginOpenfireState loginOpenfireState;
    public RegisterState registerState;
    public GoOffState goOffState;
    public boolean isLogining;
    public Context mcontext;
    public interface OnReceiveMsg{
        public void receiveMsg(String msg,int color,String size);
        public void receiveClearMsg(String msg);
        public void receiveBindMsg(String msg);
        public void receiveUnBindMsg(String msg);
        public void receiveOnline(String msg);
        public void receiveGooff(String msg);
        public void receiveAdmin(String msg);
        public void receivePush(final String msg,final String size);
        public void receiveOpenfirePush(final String msg,final String size, int color);
    }
    public interface ConnectOpenfireSuccess{
        public void connectSuccess();
        public void connectFailed();
    }
    public interface LoginOpenfireState{
        public void loginSuccess();
        public void loginFail(String desc);
    }
    public interface RegisterState{
        public void registerSuccess();
        public void registerFail(String desc);
    }

    public interface GoOffState{
        public void goffSuccess();
        public void goOffFail();
    }
    public void resetConnection(){
        if(xmppConnection != null){
            xmppConnection.disconnect();
        }
        xmppConnection = new XMPPConnection(connConfig);
        xmppConnection.addPacketListener(new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                if ((packet instanceof Message)){
                    Message message = (Message)packet;
                    if ((message != null) && (message.getBody() != null)) {
                        if(message.getProperty("kind").equals(XmppConnectionManager.CHAT)){
                            if(onReceiveMsg != null){
                                String size = message.getProperty("screenSize").toString();
                                onReceiveMsg.receiveMsg(message.getBody(),Integer.parseInt(String.valueOf(message.getProperty("color"))),size);
                            }
                        }else if(message.getProperty("kind").equals(XmppConnectionManager.CLEAR)){
                            if(onReceiveMsg != null){
                                onReceiveMsg.receiveClearMsg(message.getBody());
                            }
                        }else if(message.getProperty("kind").equals(XmppConnectionManager.BIND)){
                            if(onReceiveMsg != null){
                                onReceiveMsg.receiveBindMsg(message.getBody());
                            }
                        }else if(message.getProperty("kind").equals(XmppConnectionManager.UNBIND)) {
                            if (onReceiveMsg != null) {
                                onReceiveMsg.receiveUnBindMsg(message.getBody());
                            }

                        } else if(message.getProperty("kind").equals(XmppConnectionManager.IMNLINE)){
                            Log.v("onReceiveMsg",XmppConnectionManager.IMNLINE);
                            if(onReceiveMsg != null){
                                onReceiveMsg.receiveOnline(message.getBody());
                            }
                        }else if(message.getProperty("kind").equals(XmppConnectionManager.OFFLINE)){
                            if(onReceiveMsg != null){
                                onReceiveMsg.receiveGooff(message.getBody());
                            }
                        }else if(message.getProperty("kind").equals(XmppConnectionManager.PUSH)){
                            if(onReceiveMsg != null){
                                String size = message.getProperty("screenSize").toString();
                                Log.v("pushMsg",message.getBody());
                                onReceiveMsg.receivePush(message.getBody(),size);
                            }
                        }else if(message.getProperty("kind").equals(XmppConnectionManager.OPENFIREPUSH)){
                            if(onReceiveMsg != null){
                                String size = message.getProperty("screenSize").toString();
                                onReceiveMsg.receiveOpenfirePush(message.getBody(), size, Integer.parseInt(String.valueOf(message.getProperty("color"))));
                               // mcontext.sendBroadcast(new Intent("ksd.ilock.openfirepush"));
                            }
                        }

                        if(message.getFrom().equals("ay130926213600z")){
                            if(onReceiveMsg != null){
                                onReceiveMsg.receiveGooff(message.getBody());
                            }
                        }
                    }
                }
            }
        },new PacketFilter() {
            @Override
            public boolean accept(Packet packet) {
                return true;
            }
        });
        new Thread()
        {
            @Override
            public void run() {
                try {
                    xmppConnection.connect();
                    Log.v("xmpp","链接openfire成功");

                    if(connectOpenfireSuccess != null){
                        connectOpenfireSuccess.connectSuccess();
                    }else{
                        Log.v("connectOpenfireSuccess","connectOpenfireSuccess 是 null");
                    }

                } catch (XMPPException e) {
                    Log.v("err",e.toString());
                    if(connectOpenfireSuccess != null){
                        connectOpenfireSuccess.connectFailed();
                    }else{
                        Log.v("connectOpenfireSuccess","connectOpenfireSuccess 是 null");
                    }
                }
            }
        }.start();
    }

    XmppConnectionManager(String serverIP,int port,Context context){
        mcontext = context;
        connConfig = new ConnectionConfiguration(serverIP, port);
        connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        connConfig.setReconnectionAllowed(true);
        connConfig.setSASLAuthenticationEnabled(true);
    }
    public void login(final String userid, final String pass){

        new Thread()
        {
            @Override
            public void run() {
                try {

                    Log.v("xmpp","开始登陆");
                    xmppConnection.login(userid, pass,"smack");
                    Log.v("xmpp","登陆成功");
                    isLogining = false;
                    if(loginOpenfireState != null){
                        loginOpenfireState.loginSuccess();
                    }
                }catch (XMPPException ex) {


                    Log.v("XMPPException",ex.getMessage());
                    if(loginOpenfireState != null){
                        loginOpenfireState.loginFail(ex.getMessage());
                    }
                }
            }
        }.start();
    }
    public  void setOnReceiveMsg(OnReceiveMsg onRes){
        onReceiveMsg = onRes;
    }
    public  void setConnectOpenfireSuccess(ConnectOpenfireSuccess onSuccess){
        connectOpenfireSuccess = onSuccess;
    }
    public void setLoginOpenfireState(LoginOpenfireState logSucess){
        loginOpenfireState = logSucess;
    };

    public  boolean sendMessage(String message, String to,String kind,String color,String size) {
        if(KSDNetCheck.isMobileConnected(mcontext)||KSDNetCheck.isWifiConnected(mcontext)){
            boolean sendSuccessful = true;
            Message msg = new Message();
            msg.setTo(to);
            msg.setBody(message);
            msg.setProperty("kind",kind);
            msg.setProperty("color",color);
            msg.setProperty("screenSize",size);
            try {
                xmppConnection.sendPacket(msg);
            }catch (Exception e){
                Log.v("提示","发送消息失败");
            }

            return sendSuccessful;
        }else{
            return false;
        }

    }

    public  void regist(final String account, final String password, final String serverName) {

        new Thread()
        {
            @Override
            public void run() {
                if (xmppConnection == null)
                    return;
                Registration reg = new Registration();
                reg.setType(IQ.Type.SET);
                reg.setTo(serverName);
                reg.setUsername(account);// 注意这里createAccount注册时，参数是username，不是jid，是“@”前面的部分。
                reg.setPassword(password);
                reg.addAttribute("android", "geolo_createUser_android");// 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！
                PacketFilter filter = new AndFilter(new PacketIDFilter(
                        reg.getPacketID()), new PacketTypeFilter(IQ.class));
                PacketCollector collector = xmppConnection
                        .createPacketCollector(filter);
                xmppConnection.sendPacket(reg);
                IQ result = (IQ) collector.nextResult(SmackConfiguration
                        .getPacketReplyTimeout());
                // Stop queuing results
                collector.cancel();// 停止请求results（是否成功的结果）
                if (result == null) {
                    Log.e("RegistActivity", "No response from server.");
                    if(registerState != null){
                        registerState.registerFail("No response from server.");
                    }
                    return;
                } else if (result.getType() == IQ.Type.RESULT) {
                    if(registerState != null){
                        registerState.registerSuccess();
                    }
                    return;
                } else {
                    if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
                        if(registerState != null){
                            registerState.registerFail("IQ.Type.ERROR: "
                                    + result.getError().toString());
                        }
                        return;
                    } else {
                        if(registerState != null){
                            registerState.registerFail("IQ.Type.ERROR: "
                                    + result.getError().toString());
                        }
                        return;
                    }
                }
            }
        }.start();


    }

    public  void goOff() {
        if (xmppConnection == null)
            return;
        new Thread()
        {
            @Override
            public void run() {

                try
                {
                    if(xmppConnection.isConnected()){
                        Presence presence = new Presence(Presence.Type.unavailable);
                        xmppConnection.sendPacket(presence);
                    }
                    xmppConnection.disconnect();
                    if(goOffState != null){
                        goOffState.goffSuccess();
                    }
                }catch (Exception e){
                    Log.v("xmpp", "下线异常");
                    if(goOffState != null){
                        goOffState.goOffFail();
                    }
                }
                Log.v("xmpp", "下线");
            }
        }.start();

    }


    public void ping(){
//        ProviderManager.getInstance().addIQProvider("ping", "urn:xmpp:ping",
//                new PingIQProvider());
    }
}
