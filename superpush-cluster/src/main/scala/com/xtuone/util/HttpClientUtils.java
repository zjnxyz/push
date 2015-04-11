package com.xtuone.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class HttpClientUtils {
	
	private HttpClientUtils(){
		
	}
	
	private static HttpClientUtils httpClientUtils;
	
	public static HttpClientUtils init(){
		if(httpClientUtils == null){
			httpClientUtils = new HttpClientUtils();
		}
		return httpClientUtils;
	}

	// 超时间隔
	private static int connectTimeOut = 3000;
	// 让connectionmanager管理httpclientconnection时是否关闭连接
	private static boolean alwaysClose = false;
	// 返回数据编码格式

	/**
	 * 用法： HttpRequestProxy hrp = new HttpRequestProxy();
	 * hrp.doRequest("http://www.163.com",null,null,"gbk");
	 *
	 */
	public String doGet(String uri) {
		String responseString = null;
		// 头部请求信息
		HttpClient client = new HttpClient();
		// 设置链接超时时间
		//client.getParams().setConnectionManagerTimeout(connectTimeOut);
		 /*设置请求超时*/
		client.getParams().setSoTimeout(connectTimeOut);
		GetMethod getMethod = new GetMethod(uri);
	
		// 使用系统提供的默认的恢复策略
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		try {
			// 执行getMethod
			int statusCode = client.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				for(int i =0 ;i<3; i++){
					if(i==2){
						break;
					}
					 statusCode = client.executeMethod(getMethod);
				}
			   responseString="{\"success\":\"failure\"}";
				System.err.println("Method failed: " + getMethod.getStatusLine());
			}else{
			// 处理内容
			responseString = getMethod.getResponseBodyAsString();
			}
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			System.out.println("Please check your provided http address!");
			e.printStackTrace();
		} catch (IOException e) {
			// 发生网络异常
			e.printStackTrace();
		} finally {
			// 释放连接
			getMethod.releaseConnection();
		}
		return responseString;

	}
	
	
	public String doPost(NameValuePair[] arg,String url){
		
		
		PostMethod postRequest = new PostMethod(url.trim());  
		Map<String,String> header = new HashMap<String,String>();  
        header.put("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 1.7; .NET CLR 1.1.4322; CIBA; .NET CLR 2.0.50727)");  
        Header[] headers = null;  
        if(header != null){  
        	Set entrySet = header.entrySet();  
            int dataLength = entrySet.size();  
             headers= new Header[dataLength];  
            int i = 0;  
            for(Iterator itor = entrySet.iterator();itor.hasNext();){  
             Map.Entry entry = (Map.Entry)itor.next();  
             headers[i++] = new Header(entry.getKey().toString(),entry.getValue().toString());  
            }  
        }  
        
        if(headers != null){  
            for(int i = 0;i < headers.length;i++){  
             postRequest.setRequestHeader(headers[i]);  
            }  
         }
//        postRequest.setRequestHeader("Content-Type", "text/html;charset=utf-8");
        postRequest.setRequestBody(arg);
		String retVal = this.executeMethod(postRequest, "UTF-8");
		
        return retVal;
	}
	
	
	private String executeMethod(HttpMethod request, String encoding) {

		String responseContent = "";
		
		InputStream responseStream = null;
		BufferedReader rd = null;
		HttpClient client = new HttpClient();
		client.getParams().setSoTimeout(connectTimeOut);
		client.getParams().setContentCharset("UTF-8");
		try {
				int statusCode = client.executeMethod(request);
				
//				if(statusCode != 200){
//					//服务器错误
//					retVal.setMessage("服务器错误！");
//					responseContent = JSON.toJSONString(retVal);
//				}else{
					if (encoding != null) {
						
						responseStream = request.getResponseBodyAsStream();
						
						rd = new BufferedReader(new InputStreamReader(responseStream, encoding));
						
						String tempLine = rd.readLine();
						StringBuffer tempStr = new StringBuffer();
						String crlf = System.getProperty("line.separator");
						while (tempLine != null) {
							tempStr.append(tempLine);
							tempStr.append(crlf);
							tempLine = rd.readLine();
						}
						responseContent = tempStr.toString();
						System.out.println("错误信息："+responseContent);
						
					} else {
						responseContent = "";
					}
//				}
	
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (rd != null)
				try {
					rd.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (responseStream != null)
				try {
					responseStream.close();
				} catch (IOException e) {
					e.printStackTrace();

				}
		}
		return responseContent;
	}
	
	
}