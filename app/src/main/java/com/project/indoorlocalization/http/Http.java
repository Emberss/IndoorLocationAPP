package com.project.indoorlocalization.http;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import com.project.indoorlocalization.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljm on 2017/4/13.
 */
public class Http {
    private static final String base = "http://192.168.191.1:8080/IndoorLocServer";
    //"http://120.25.91.6:8080/IndoorLocServer"
    private static final String url1 = "/uploadSensorMsg";
    private static final String url2 = "/uploadimage";

    public static String uploadInfo(String [] paths, Context context) {
        List<String> keys = new ArrayList<>(), values = new ArrayList<>();
        List<String> img_keys = new ArrayList<>(), img_paths = new ArrayList<>();
        img_keys.add("a");
        img_keys.add("b");
        img_keys.add("c");
        for (int i = 0; i < paths.length; ++i) {
            img_paths.add(paths[i]);
        }
        //Utils.setToast(context.getApplicationContext(), img_keys.size()+"  "+ img_paths.size());
        Log.v("###### key:", img_keys.size()+"");
        Log.v("###### path:", img_paths.size()+"");
        //return "";
        return postHelper(base + url2, keys, values, img_keys, img_paths);
    }

    public static String getInfo(String[] sensorInfos, String[] paths) {
        List<String> keys = new ArrayList<>(), values = new ArrayList<>();
        keys.add("acceleration");
        keys.add("gyroscope");
        keys.add("magnetic");
        keys.add("compass");
        values.add(sensorInfos[0]);
        values.add(sensorInfos[1]);
        values.add(sensorInfos[2]);
        values.add(sensorInfos[3]);
        List<String> img_keys = new ArrayList<>(), img_paths = new ArrayList<>();
        img_keys.add("aa");
        for (int i = 0; i < paths.length; ++i) img_paths.add(paths[i]);
        return postHelperSensorInfo(base + url1, keys, values)
                + "\n|"
                + postHelperImg(base + url2, img_keys, img_paths);
    }


    private static String postHelperSensorInfo(String url, List<String> keys, List<String> vals) {
        HttpPost httpPost = new HttpPost(url);
        List<BasicNameValuePair> params = new ArrayList<>();
        for (int i = 0; i < keys.size(); ++i) {
            params.add(new BasicNameValuePair(keys.get(i), vals.get(i)));
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {//判断服务器状态
                String result = EntityUtils.toString(httpResponse.getEntity());
                return result;
            } else {
                return "";
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String postHelperImg(String url, List<String> img_keys, List<String> img_paths) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        MultipartEntity multipartEntity = new MultipartEntity();
        for (int i = 0; i < img_paths.size(); ++i) {
            File file = new File(img_paths.get(i));
            ContentBody contentBody = new FileBody(file);
            multipartEntity.addPart(img_keys.get(i), contentBody);
        }
        httpPost.setEntity(multipartEntity);

        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return "";
    }

    private static String postHelper(String url, List<String> keys, List<String> vals,
                                     List<String> img_keys, List<String> img_paths) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        MultipartEntity multipartEntity = new MultipartEntity();
        for (int i = 0; i < img_paths.size(); ++i) {
            File file = new File(img_paths.get(i));
            ContentBody contentBody = new FileBody(file);
            multipartEntity.addPart(img_keys.get(i), contentBody);
        }
        Log.v("###### key1:", img_keys.size()+"");
        Log.v("###### path1:", img_paths.size()+"");
        httpPost.setEntity(multipartEntity);
        for (int i = 0; i < keys.size(); ++i) {
            try {
                multipartEntity.addPart(keys.get(i),
                        new StringBody(vals.get(i), Charset.defaultCharset()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(httpResponse.getEntity());
            } else {
                return httpResponse.getStatusLine().getStatusCode()+"";
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return "";
    }
}
