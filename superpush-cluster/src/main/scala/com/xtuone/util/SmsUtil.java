package com.xtuone.util;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import scala.math.Ordering;

public class SmsUtil {
	//==========测试账号===========
	// 梦网帐号
	private static final String MW_ACCOUNT = "***";
	// 密码
	private static final String MW_PASSWORD = "***";
	// 主机
	private static final String MW_HOST = "***";
	// 接口 -- 发送信息
	private static final String MW_API_SEND_MESSAGE = "/MWGate/wmgw.asmx/MongateCsSpSendSmsNew";
	// 接口 -- 查询余额
	private static final String MW_API_QUERY_BALANCE = "/MWGate/wmgw.asmx/MongateQueryBalance";
	// 接口 -- 发送状态
	private static final String MW_API_QUERY_STATUS_REPORT = "/MWGate/wmgw.asmx/MongateCsGetStatusReportExEx";
	//接口 -- 接收短信
	private static final String MW_API_RECEIVE_MESSAGE = "/MWGate/wmgw.asmx/MongateCsGetSmsExEx";


//	// 梦网帐号
//	private static final String MW_ACCOUNT = "j01270";
//	// 密码
//	private static final String MW_PASSWORD = "663521";
//	// 主机
//	private static final String MW_HOST = "http://61.145.229.29:9003";
//	// 接口 -- 发送信息
//	private static final String MW_API_SEND_MESSAGE = "/MWGate/wmgw.asmx/MongateCsSpSendSmsNew";
//	// 接口 -- 查询余额
//	private static final String MW_API_QUERY_BALANCE = "/MWGate/wmgw.asmx/MongateQueryBalance";
//	// 接口 -- 发送状态
//	private static final String MW_API_QUERY_STATUS_REPORT = "/MWGate/wmgw.asmx/MongateCsGetStatusReportExEx";
//	//接口 -- 接收短信
//	private static final String MW_API_RECEIVE_MESSAGE = "/MWGate/wmgw.asmx/MongateCsGetSmsExEx";


//	// 梦网帐号
//	private static final String MW_ACCOUNT = "J24594";
//	// 密码
//	private static final String MW_PASSWORD = "632203";
//	// 主机
//	private static final String MW_HOST = "http://61.145.229.29:7903";
//	// 接口 -- 发送信息
//	private static final String MW_API_SEND_MESSAGE = "/MWGate/wmgw.asmx/MongateCsSpSendSmsNew";
//	// 接口 -- 查询余额
//	private static final String MW_API_QUERY_BALANCE = "/MWGate/wmgw.asmx/MongateQueryBalance";
//	// 接口 -- 发送状态
//	private static final String MW_API_QUERY_STATUS_REPORT = "/MWGate/wmgw.asmx/MongateCsGetStatusReportExEx";
//	// 接口 -- 接收短信
//	private static final String MW_API_RECEIVE_MESSAGE = "/MWGate/wmgw.asmx/MongateCsGetSmsExEx";

	// 表单字段
	private static final String MW_PARAMETER_USERID = "userId";
	private static final String MW_PARAMETER_PASSWORD = "password";
	private static final String MW_PARAMETER_MOBILES = "pszMobis";
	private static final String MW_PARAMETER_MESSAGE = "pszMsg";
	private static final String MW_PARAMETER_MOBILECOUNT = "iMobiCount";
	private static final String MW_PARAMETER_SUBMIT_PORT = "pszSubPort";

	//接收短信通知的号码
	private static final String mobileNumbers = "18027326692,18102215296";
	//数量
	private static final int num = 2;
	//短信内容
	private static final String timeOutContent = "在这十分钟内推送到gopush的消息都失败了，快去检查下吧";

	//
	private static final String overFailureLimitContent = "连续10次推送到gopush的消息失败了，快去检查下吧";

	// 发送内容
	private static final String SUPER_CAPPTCHA_MESSAGE = "（超级课程表验证码） 为了保护您的帐号安全，验证短信请勿转发给其他人";

	/**
	 * 发送超时短信
	 * @return
	 */
	public static boolean sendTimeoutMsg(){
		return sendMsg(mobileNumbers,timeOutContent,num);
	}

	public static boolean sendOverFailureLimit(){
		return sendMsg(mobileNumbers,overFailureLimitContent,num);
	}

	/**
	 * 发送短信验证码
	 *
	 * @param mobileNumber
	 * @param content
	 * @return
	 */
	public static boolean sendMsg(String mobileNumber, String content,int num) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(MW_HOST + MW_API_SEND_MESSAGE);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair(MW_PARAMETER_USERID, MW_ACCOUNT));
		nvps.add(new BasicNameValuePair(MW_PARAMETER_PASSWORD, MW_PASSWORD));
		nvps.add(new BasicNameValuePair(MW_PARAMETER_MOBILES, mobileNumber));
		nvps.add(new BasicNameValuePair(MW_PARAMETER_MESSAGE, content ));
		nvps.add(new BasicNameValuePair(MW_PARAMETER_MOBILECOUNT, num+""));
		nvps.add(new BasicNameValuePair(MW_PARAMETER_SUBMIT_PORT, "1"));

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			System.out.println(EntityUtils.toString(httpEntity));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

		return true;
	}

	/**
	 * 查询余额
	 * @return
	 */
	public static float checkBalance() {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(MW_HOST + MW_API_QUERY_BALANCE);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair(MW_PARAMETER_USERID, MW_ACCOUNT));
		nvps.add(new BasicNameValuePair(MW_PARAMETER_PASSWORD, MW_PASSWORD));

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			System.out.println(EntityUtils.toString(httpEntity));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

		return 1;
	}

	/**
	 * 查询余额
	 * @return
	 */
	public static float getStatusReport() {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(MW_HOST + MW_API_QUERY_STATUS_REPORT);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair(MW_PARAMETER_USERID, MW_ACCOUNT));
		nvps.add(new BasicNameValuePair(MW_PARAMETER_PASSWORD, MW_PASSWORD));

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			System.out.println(EntityUtils.toString(httpEntity));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

		return 1;
	}





	public static void main(String[] args) {
		//yd27
//		List<SmsRevMessage> list = SMSUtil.getSms();
//		String str = CaptchaUtil.buildAutoCaptcha();
//		System.out.println("---->"+str);
		//sms:mobile:binding:13929543058
//		SMSUtil.sendCaptcha("13580455968",str );
//		SMSUtil.getSms();
//		SMSUtil.checkBalance();
//		SMSUtil.getStatusReport();

//		try {
//			Document doc = XMLUtils.parse(new File("NewFile.xml"));
//			List<SmsRevMessage> list = new ArrayList<SmsRevMessage>();
//			parseNode(list,doc);
//			System.out.println(list.size());
////			Node node = XMLUtils.getSingleNodeByTag(doc, "ArrayOfString");
////			Node[] arr = XMLUtils.getChildElements(node);
////			//NodeList list = n.getChildNodes();
////			System.out.println("----length---"+arr.length);
////			System.out.println("---"+arr[0].getNodeName());
////			String str = arr[0].getTextContent();
////			System.out.println("---"+str);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
	}
}
