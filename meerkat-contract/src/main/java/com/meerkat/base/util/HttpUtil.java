package com.meerkat.base.util;


import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Created by wm on 17/3/2.
 */
public class HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static final String ENCODING = "UTF-8";
    private static final int DEFAULT_TIME_OUT = 10 * 1000;
    private static int TIME_OUT = DEFAULT_TIME_OUT;

    static {
        try {
            String configTime = ConfigPropertiesUtil.getValue("post.http.timeout.second");
            if (StringUtils.isNumeric(configTime)) {
                TIME_OUT = Integer.parseInt(configTime);
            }
        } catch (Exception e) {
            TIME_OUT = DEFAULT_TIME_OUT;
        }
    }

    private static HttpClient getSimpleHttpClient(String url, HttpParams httpParameters) {
        HttpClient httpclient;
        boolean isHTTPS = url.startsWith("https");
        if (isHTTPS) {
            httpclient = newHttpsClient();
        } else {
            httpclient = new DefaultHttpClient(httpParameters);
        }
        return httpclient;
    }


    public static String postResponseWithParameterMap(String url, final Map<String, String> formParams) {

        try {
            HttpParams httpParameters = getHttpParamsWithTimeOut();
            HttpClient httpClient = getSimpleHttpClient(url, httpParameters);
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(convert(formParams), ENCODING);
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(entity);
            HttpResponse response = httpClient.execute(httppost);
            return convertInputStream(response.getEntity().getContent(), ENCODING);
        } catch (IOException e) {
            throw new RuntimeException("与" + url + "通信失败", e);
        }
    }

    public static String postResponseWithParameterMap(String url, final Map<String, String> formParams, Map<String, String> headerMap) {

        try {
            HttpParams httpParameters = getHttpParamsWithTimeOut();
            HttpClient httpClient = getSimpleHttpClient(url, httpParameters);
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(convert(formParams), ENCODING);
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(entity);
            for (Map.Entry<String, String> headerEntry : headerMap.entrySet()) {
                httppost.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
            HttpResponse response = httpClient.execute(httppost);
            return convertInputStream(response.getEntity().getContent(), ENCODING);
        } catch (IOException e) {
            throw new RuntimeException("与" + url + "通信失败", e);
        }
    }

    public static String getGetResponseByUrl(String url) throws
            IOException {
        return getGetResponseWithHeader(url, Collections.EMPTY_MAP);
    }

    public static String getGetResponseWithHeader(String url, Map<String, String> headerMap) throws
            IOException {
        HttpClient httpClient = null;
        HttpGet httpGet = null;
        try {
            HttpParams httpParameters = getHttpParamsWithTimeOut();
            httpClient = getSimpleHttpClient(url, httpParameters);
            httpGet = new HttpGet(url);
            for (Map.Entry<String, String> headerEntry : headerMap.entrySet()) {
                httpGet.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
            HttpResponse response = httpClient.execute(httpGet);
            return convertInputStream(response.getEntity().getContent(), ENCODING);
        } catch (IOException e) {
            throw new RuntimeException("与" + url + "通信失败", e);
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
            if (httpClient != null) {
                httpClient.getConnectionManager().shutdown();
            }
        }
    }

    public static String postResponseWithHeader(String url, String requestBody, Map<String, String> headerMap) {
        try {
            logger.debug("requestBody: " + requestBody);
            HttpParams httpParameters = getHttpParamsWithTimeOut();
            HttpClient httpclient = getSimpleHttpClient(url, httpParameters);
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new StringEntity(requestBody, ENCODING));
            for (Map.Entry<String, String> headerEntry : headerMap.entrySet()) {
                httppost.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
            HttpResponse response = httpclient.execute(httppost);
            return convertInputStream(response.getEntity().getContent(), ENCODING);
        } catch (IOException e) {
            throw new RuntimeException("与" + url + "通信失败", e);
        }
    }

    public static Response getPostResponseWithHeader(String url, String requestBody, Map<String, String> headerMap) {
        return JsonUtil.load(postResponseWithHeader(url, requestBody, headerMap), Response.class);
    }


    private static List<NameValuePair> convert(Map<String, String> map) {
        List<NameValuePair> formParams = new ArrayList<NameValuePair>();
        if (map != null) {
            Set<String> keys = map.keySet();
            for (String key : keys) {
                formParams.add(new BasicNameValuePair(key, map.get(key)));
            }
        }
        return formParams;
    }

    private static HttpParams getHttpParamsWithTimeOut() {
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setSoTimeout(httpParameters, TIME_OUT);
        HttpConnectionParams.setConnectionTimeout(httpParameters, TIME_OUT);
        return httpParameters;
    }

    public static String convertInputStream(HttpEntity httpEntity, String defaultCharset) {
        if (httpEntity == null)
            return null;
        try {
            String value = EntityUtils.toString(httpEntity, defaultCharset);
            logger.debug("responseString: {}", value);
            return value;
        } catch (IOException e) {
            logger.error("将httpEntity转为字符串时出错", e);
            return null;
        }
    }

    private static String convertInputStream(InputStream in, String encoding) {
        if (in == null)
            return null;
        try {
            Reader r = new BufferedReader(new InputStreamReader(new BufferedInputStream(in), encoding), 1024);
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = r.read()) != -1)
                sb.append((char) c);
            String value = sb.toString();
            logger.debug("responseString: {}", value);
            return value;
        } catch (IOException e) {
            return null;
        }
    }

    private static HttpClient newHttpsClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            HttpConnectionParams.setConnectionTimeout(params, TIME_OUT);
            HttpConnectionParams.setSoTimeout(params, TIME_OUT);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    private static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore)
                throws
                NoSuchAlgorithmException,
                KeyManagementException,
                KeyStoreException,
                UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws
                        CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws
                        CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws
                IOException,
                UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws
                IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }
}
