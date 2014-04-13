package me.ghui.AMS.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import me.ghui.AMS.domain.User;
import me.ghui.AMS.utils.Constants;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ghui on 3/28/14.
 */
public class NetUtils {
    public static String SESSION_ID = "";

    public static Bitmap getValidateCode() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(Constants.VALIDATE_CODE_URL);
        try {
            httpGet.setHeader("Referer", "http://211.84.112.49/lyit/_data/index_LOGIN.aspx");
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                SESSION_ID = httpClient.getCookieStore().getCookies().get(0).getValue();
                Log.e("ghui", "sessionId: " + SESSION_ID);
                InputStream inputStream = response.getEntity().getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }

    public static void login(final User user) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Constants.LOGIN_URL);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("UserID", user.getID()));
        pairs.add(new BasicNameValuePair("PassWord", user.getPassword()));
        pairs.add(new BasicNameValuePair("cCode", user.getValidateCode()));
        pairs.add(new BasicNameValuePair("Sel_Type", "TEA"));
        pairs.add(new BasicNameValuePair("typeName", "教师教辅人员"));
//        pairs.add(new BasicNameValuePair("sbtState", ""));
//        pairs.add(new BasicNameValuePair("pcInfo", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36undefined5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36 SN:NULL"));
        httpPost.setHeader("Cookie", "ASP.NET_SessionId=" + SESSION_ID);
//                httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
//                httpPost.setHeader("Host","211.84.112.49");
//               httpPost.setHeader("Origin","211.84.112.49");
        httpPost.setHeader("Referer", "http://211.84.112.49/lyit/_data/index_LOGIN.aspx");
//                httpPost.setHeader("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36");
//                httpPost.setHeader("Cache-Control","max-age=0");
//                httpPost.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            Log.e("ghui", "login successfully!");
//            try {
////                Log.e("ghui", "content: " + EntityUtils.toString(response.getEntity()));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    public static Document getDataFromServer(String url) {
        Document doc;
        try {
            doc = getConnection(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ghui", "doc is null");
            return getDataFromServer(url);
        }
        return doc;
    }

    public static Document postDataToServer(String url, Map<String, String> requestData) {
        Document doc;
        try {
            doc = getConnection(url).data(requestData).post();
        } catch (IOException e) {
            e.printStackTrace();
            return postDataToServer(url, requestData);
        }
        return doc;
    }

    public static Document postDataToServer(String url, Map<String, String> requestData, String refer) {
        //todo
        Document doc;
        try {
            doc = getConnection(url).data(requestData).referrer(refer).post();
        } catch (IOException e) {
            e.printStackTrace();
            return postDataToServer(url, requestData, refer);
        }
        return doc;
    }

    private static Connection getConnection(String url) {
        return Jsoup.connect(url).cookie("ASP.NET_SessionId", SESSION_ID);
    }

    public static boolean isConnectioned(Document doc) {
        //todo
        return !(doc == null);
    }

}


