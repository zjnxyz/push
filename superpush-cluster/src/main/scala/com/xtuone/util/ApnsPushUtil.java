package com.xtuone.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.typesafe.config.ConfigFactory;
import com.xtuone.util.model.AnpsMessage;
import com.xtuone.util.model.AnpsPushResult;
import org.json.JSONException;

import javapns.communication.exceptions.CommunicationException;
import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Zz on 2015/1/13.
 */
public class ApnsPushUtil {

    private static Logger logBack = LoggerFactory.getLogger(ApnsPushUtil.class);

//    private static String certificatePath = "F:/超表推送证书导出(开发).p12";
//    private static String certificatePath = "/mnt/super/config/超表推送证书导出(开发).p12";
    private static String certificatePath = "/mnt/super/config/iosdev.p12";
    private static String sound = "default";
    private static String certificatePassword = "123";// 此处注意导出的证书密码不能为空因为空密码会报错

    private static PushNotificationManager pushManager;
    private static boolean sendCount = true;
    //是否为生产环境
    private static boolean isProduction = true;
    //推送完是否关闭
    private static boolean closeAfter = false;

    static {
        certificatePath = ConfigFactory.load().getString("apns.certificatePath");
        certificatePassword = ConfigFactory.load().getString("apns.certificatePassword");
        isProduction = ConfigFactory.load().getBoolean("apns.isProduction");

        logBack.info("初始化参数：certificatePath"+certificatePath+" certificatePassword:"+certificatePassword+" isProduction" +isProduction);
    }

    public static AnpsPushResult push(AnpsMessage anpsMessage,String... deviceToken ){
        List<PushedNotification> notifications = new ArrayList<PushedNotification>();
        if(sendCount){
            //一个个推送
            Device device;
            for(int i=0;i<deviceToken.length;i++){
                device = new BasicDevice();
                device.setToken(deviceToken[i]);
                try {
                    PushedNotification notification =initPushManager().sendNotification(device,buildIosNoticationPayload(anpsMessage) , closeAfter);
                    if(!notification.isSuccessful()){
                        //如果失败后，重新连接
                        restartConnection();
                    }
                    notifications.add(notification);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            List<Device> devices = new ArrayList<Device>();
            try {
                for(int i=0;i<deviceToken.length;i++){
                    devices.add(new BasicDevice(deviceToken[i]));
                }
                notifications = initPushManager().sendNotifications(buildIosNoticationPayload(anpsMessage), devices);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return handlePushResult(notifications, anpsMessage.getServerId());
    }

    public static AnpsPushResult push(AnpsMessage anpsMessage,List<String> deviceTokens){

        List<PushedNotification> notifications = new ArrayList<PushedNotification>();
        if(sendCount){
            //一个个推送
            Device device;
            for(String deviceToken:deviceTokens){
                device = new BasicDevice();
                device.setToken(deviceToken);
                try {
                    PushedNotification notification =initPushManager().sendNotification(device,buildIosNoticationPayload(anpsMessage) , closeAfter);
                    logBack.info( "apns 推送状态 :"+notification.isSuccessful() );
                    notifications.add(notification);
                } catch (CommunicationException e) {
                    e.printStackTrace();
                }
            }
        }else{
            List<Device> devices = new ArrayList<Device>();
            try {
                for(String deviceToken:deviceTokens){
                    devices.add(new BasicDevice(deviceToken));
                }
                notifications = initPushManager().sendNotifications(buildIosNoticationPayload(anpsMessage), devices);

            }catch (Exception  e) {
                e.printStackTrace();
            }
        }

        return handlePushResult(notifications, anpsMessage.getServerId());
    }

    public static AnpsPushResult handlePushResult(List<PushedNotification> notifications,int serverId){
        AnpsPushResult anpsPushResult = new AnpsPushResult();
        anpsPushResult.setServerId(serverId);
        anpsPushResult.setFailedNotifications(PushedNotification.findFailedNotifications(notifications));
        anpsPushResult.setSuccessfulNotifications(PushedNotification.findSuccessfulNotifications(notifications));
        return anpsPushResult;
    }

    private static PushNotificationPayload buildIosNoticationPayload(AnpsMessage anpsMessage){
        PushNotificationPayload payload = PushNotificationPayload.alert(anpsMessage.getAlert());
        try {
            payload.addBadge(anpsMessage.getBadge() == 0 ? 1 :  anpsMessage.getBadge());
            payload.addSound(anpsMessage.getSound() == null ? sound : anpsMessage.getSound());
            if(anpsMessage.getExtras() != null && anpsMessage.getExtras().size() > 0){
                Map<String, String> map = anpsMessage.getExtras();
                for(String key:map.keySet()){
                    payload.addCustomDictionary(key, map.get(key) == null ? "" :  map.get(key));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return payload;
    }

    private static PushNotificationManager initPushManager(){

        if(pushManager == null){
            pushManager = new PushNotificationManager();
            try {
                // true：表示的是产品发布推送服务 false：表示的是产品测试推送服务
                pushManager.initializeConnection(new AppleNotificationServerBasicImpl(certificatePath, certificatePassword, isProduction));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return pushManager;
    }

    private static void restartConnection(){
        if(pushManager == null){
            pushManager = new PushNotificationManager();
            try {
                // true：表示的是产品发布推送服务 false：表示的是产品测试推送服务
                pushManager.initializeConnection(new AppleNotificationServerBasicImpl(certificatePath, certificatePassword, isProduction));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try{
                pushManager.restartConnection(new AppleNotificationServerBasicImpl(certificatePath, certificatePassword, isProduction));
            }catch (Exception e){
                e.printStackTrace();
                //重新初始化
                try {
                    pushManager.initializeConnection(new AppleNotificationServerBasicImpl(certificatePath, certificatePassword, isProduction));
                }catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

        }
    }

}
