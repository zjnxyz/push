package com.xtuone.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Zz on 2015/1/12.
 */
public class HttpUtils {

    private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);
    private static final String KEYWORDS_CONNECT_TIMED_OUT = "connect timed out";
    private static final String KEYWORDS_READ_TIMED_OUT = "Read timed out";

    //设置连接超时时间
    public static final int DEFAULT_CONNECTION_TIMEOUT = (5 * 1000); // milliseconds

    //设置读取超时时间
    public static final int DEFAULT_READ_TIMEOUT = (30 * 1000); // milliseconds


    public static final String CHARSET = "UTF-8";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    public static final String GOPUSH_USER_AGENT = "GOPUSH-API-Java-Client";

    public static String getURL(String protocol, String host, Integer port, String path, Object... query) {
        assert query != null && query.length % 2 == 0;

        StringBuilder result = new StringBuilder();
        result.append(protocol).append("://").append(host).append(":").append(port).append("/").append(path);
        if (query != null) {
            for (int i = 0; i < query.length; i += 2) {
                if (i == 0) {
                    result.append("?");
                } else {
                    result.append("&");
                }
                result.append(query[i]).append("=").append(query[i + 1]);
            }
        }

        return result.toString();
    }

    public static String get(String url) throws IOException {
        assert url != null && url.trim().length() != 0;

        HttpURLConnection huc = null;
        try {
            huc = getHttpURLConnection(url, "GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(huc.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                result.append(line);
                result.append("\r\n");
            }

            return result.toString();
        } finally {
            if (huc != null) {
                huc.disconnect();
            }
        }
    }

    public static String post(String url, String content) throws IOException {
        assert url != null && url.trim().length() != 0;

        String errorMessage = "";

        HttpURLConnection conn = null;
        OutputStream out = null;
        StringBuffer sb = new StringBuffer();
        try {
            conn = getHttpURLConnection(url,"POST");
            byte[] data = content.getBytes(CHARSET);
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            out = conn.getOutputStream();
            out.write(data);
            out.flush();
            int status = conn.getResponseCode();
            InputStream in = null;
            if (status == 200) {
                in = conn.getInputStream();
            } else {
                in = conn.getErrorStream();
            }
            InputStreamReader reader = new InputStreamReader(in, CHARSET);
            char[] buff = new char[1024];
            int len;
            while ((len = reader.read(buff)) > 0) {
                sb.append(buff, 0, len);
            }

            String responseContent = sb.toString();
            if (status == 200) {
                LOG.debug("Succeed to get response - 200 OK");
                LOG.debug("Response Content - " + responseContent);
                return responseContent;
            } else if (status > 200 && status < 400) {
                LOG.warn("Normal response but unexpected - responseCode:" + status + ", responseContent:" + responseContent);
            } else {
                LOG.warn("Got error response - responseCode:" + status + ", responseContent:" + responseContent);
            }
        } catch (SocketTimeoutException e) {
            errorMessage = e.getMessage();
            if (e.getMessage().contains(KEYWORDS_CONNECT_TIMED_OUT)) {
                throw e;
            } else if (e.getMessage().contains(KEYWORDS_READ_TIMED_OUT)) {
                throw new SocketTimeoutException(KEYWORDS_READ_TIMED_OUT);
            }
        } catch (IOException e) {
            errorMessage = e.getMessage();
            LOG.info("IOException:"+e.getMessage());
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOG.error("Failed to close stream.", e);
                }
            }
            if (null != conn) {
                conn.disconnect();
            }
        }
//        HttpURLConnection huc = null;
//        try {
//            huc = getHttpURLConnection(url, "POST");
//
//            huc.setDoOutput(true);
//            huc.setDoInput(true);
//
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(huc.getOutputStream(), "UTF-8"));
////            System.out.println(data);
//            writer.write(data);
//            writer.flush();
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(huc.getInputStream()));
//            StringBuilder result = new StringBuilder();
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                result.append(line);
//                result.append("\r\n");
//            }
//
//            return result.toString();
//        } finally {
//            if (huc != null) {
//                huc.disconnect();
//            }
//        }
        return  errorMessage;
    }

    private static HttpURLConnection getHttpURLConnection(String url, String method) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
        conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
        conn.setUseCaches(false);
        conn.setRequestMethod(method);
        conn.setRequestProperty("User-Agent", GOPUSH_USER_AGENT);
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Accept-Charset", CHARSET);
        conn.setRequestProperty("Charset", CHARSET);
        conn.setRequestProperty("Content-Type", CONTENT_TYPE_JSON);
        if("POST".equals(method)){
            conn.setDoOutput(true);
        }

//        huc.setRequestMethod(method);
//        huc.setUseCaches(false);
//        huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//        huc.setRequestProperty("Charset", "UTF-8");
//        huc.setConnectTimeout(connectTimeOut);
        return conn;
    }

}
