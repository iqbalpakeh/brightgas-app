package com.pertamina.brightgasagen;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gumelartejasukma on 5/20/15.
 */
public class Network {

    static final String GCM_SERVER_API_KEY = "AIzaSyCVDfdiOwhwzSzayOPBhljXQH9LEUKPnS8";
    static final String GCM_SENDER_ID = "719792557232";
    static final String API_KEY = "2ee046072e43bbeb91f88e1a051bfe1656b989779e320";
    static final String IMAGE_LOW_PREFIX = "@2x";
    static final String IMAGE_MED_PREFIX = "@3x";
    static final int IMAGETYPE_ORIGNAL = 0;
    static final int IMAGETYPE_LOW = 1;
    static final int IMAGETYPE_MED = 2;

//    static final String serverAddress = "http://192.168.0.10/syscleaning/";
    static final String serverAddress = "http://103.252.100.29/pertamina/app_brightgas/";

    static final String baseUrl = serverAddress+"api/";
    static final String baseUrlImage = serverAddress+"assets/photo/";

    static final String baseUrlRajaOngkir = "http://api.rajaongkir.com/basic/";
    static final String API_KEY_RAJA_ONGKIR = "91322fcf2fb3fb22f03d8d91d8d8380e";

    public static String getImageUrl(String dir, String fileName){
        return baseUrlImage+dir+"/"+fileName;
    }

    public static String getImageUrl(String dir, String fileName, String ext){
        return getImageUrl(dir,fileName,ext,IMAGETYPE_ORIGNAL);
    }

    public static String getImageUrl(String dir, String fileName, String ext, int type){
        String imageType = "";
        switch (type){
            case IMAGETYPE_LOW:
                imageType = IMAGE_LOW_PREFIX;
                break;
            case IMAGETYPE_MED:
                imageType = IMAGE_MED_PREFIX;
                break;
        }
        return baseUrlImage+dir+"/"+fileName+imageType+"."+ext;
    }

    public static String httpPost(MultipartEntityBuilder entity){
        String result=null;
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(baseUrl);
        entity.addTextBody("apiKey",API_KEY);
        try{
            post.setEntity(entity.build());
            HttpResponse response = client.execute(post);
            HttpEntity resEntityGet = response.getEntity();
            if (resEntityGet != null) {
                try {
                    result = EntityUtils.toString(resEntityGet);
                    Log.d("gugum","network result "+result);
                }catch(Exception ex){
                    Log.d("gugum","network error "+ex.toString());
                }
            }
        }catch(Exception ex){
            Log.d("gugum","network error "+ex.toString());
        }
        return result;
    }

    public static String httpPost(BasicNameValuePair[] valuePairs){
        String result=null;
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(baseUrl);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for(BasicNameValuePair pair : valuePairs){
            pairs.add(pair);
        }
        pairs.add(new BasicNameValuePair("apiKey", API_KEY));
        try{
            post.setEntity(new UrlEncodedFormEntity(pairs));
            HttpResponse response = client.execute(post);
            HttpEntity resEntityGet = response.getEntity();
            if (resEntityGet != null) {
                try {
                    result = EntityUtils.toString(resEntityGet);
                    Log.d("gugum","network result "+result);
                }catch(Exception ex){
                    result = ex.toString();
                    Log.d("gugum","network error "+ex.toString());
                }
            }
        }catch(Exception ex){
            result = ex.toString();
            Log.d("gugum","network error "+ex.toString());
        }
        return result;
    }

    public static String httpPost(MultipartEntityBuilder entity,String controller, String action){
        String result=null;
        HttpClient client = new DefaultHttpClient();
        HttpPost post;
        if(action==null){
            post = new HttpPost(baseUrl+controller);
        }else{
            post = new HttpPost(baseUrl+controller+"/"+action);
        }
        entity.addTextBody("apiKey", API_KEY);
        try{
            post.setEntity(entity.build());
            HttpResponse response = client.execute(post);
            HttpEntity resEntityGet = response.getEntity();
            if (resEntityGet != null) {
                try {
                    result = EntityUtils.toString(resEntityGet);
                    Log.d("gugum","network result "+result);
                }catch(Exception ex){
                    Log.d("gugum","network error "+ex.toString());
                }
            }
        }catch(Exception ex){
            Log.d("gugum","network error "+ex.toString());
        }
        return result;
    }

    public static String httpPost(BasicNameValuePair[] valuePairs,String controller, String action){
        String result=null;
        HttpClient client = new DefaultHttpClient();
        HttpPost post;
        if(action==null){
            post = new HttpPost(baseUrl+controller);
        }else{
            post = new HttpPost(baseUrl+controller+"/"+action);
        }
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for(BasicNameValuePair pair : valuePairs){
            pairs.add(pair);
        }
        pairs.add(new BasicNameValuePair("apiKey",API_KEY));
        try{
            post.setEntity(new UrlEncodedFormEntity(pairs));
            HttpResponse response = client.execute(post);
            HttpEntity resEntityGet = response.getEntity();
            if (resEntityGet != null) {
                try {
                    result = EntityUtils.toString(resEntityGet);
                    Log.d("gugum","network result "+result);
                }catch(Exception ex){
                    result = ex.toString();
                    Log.d("gugum","network error "+ex.toString());
                }
            }
        }catch(Exception ex){
            Log.d("gugum","network error "+ex.toString());
        }
        return result;
    }

    public static String httpPostRajaOngkir(BasicNameValuePair[] valuePairs,String controller){
        String result=null;
        HttpClient client = new DefaultHttpClient();
        HttpPost post;
        post = new HttpPost(baseUrlRajaOngkir+controller);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for(BasicNameValuePair pair : valuePairs){
            pairs.add(pair);
        }
        pairs.add(new BasicNameValuePair("key",API_KEY_RAJA_ONGKIR));
        try{
            post.setEntity(new UrlEncodedFormEntity(pairs));
            HttpResponse response = client.execute(post);
            HttpEntity resEntityGet = response.getEntity();
            if (resEntityGet != null) {
                try {
                    result = EntityUtils.toString(resEntityGet);
                    Log.d("gugum","network result "+result);
                }catch(Exception ex){
                    result = ex.toString();
                    Log.d("gugum","network error "+ex.toString());
                }
            }
        }catch(Exception ex){
//            result = ex.toString();
            Log.d("gugum","network error "+ex.toString());
        }
        return result;
    }

    public static String httpGetRajaOngkir(BasicNameValuePair[] valuePairs,String controller){
        String result = null;
        String url=baseUrlRajaOngkir+controller;
        boolean isFirstGet = false;
        String valName;
        String[] arValName;
        for(BasicNameValuePair pair : valuePairs){
            valName = pair.getName();
            arValName = valName.split("___");
            if(arValName!=null && arValName.length>1){
                if(!isFirstGet){
                    isFirstGet = true;
                    url+="?id=";
                }else{
                    url+="&"+arValName[1]+"=";
                }
                url+=pair.getValue();
            }
        }
        Log.d("gugum",url);
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        for(BasicNameValuePair pair : valuePairs){
            valName = pair.getName();
            arValName = valName.split("___");
            if(arValName == null || arValName.length<1){
                httpGet.addHeader(pair.getName(),pair.getValue());
            }
        }
        httpGet.addHeader("key",API_KEY_RAJA_ONGKIR);
        try{
            HttpResponse response = client.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            if(httpEntity!=null){
                try{
                    result = EntityUtils.toString(httpEntity);
                    Log.d("gugum","network result "+result);
                }catch (Exception ex){
                    Log.d("gugum",ex.toString());
                }
            }
        }catch (Exception ex){
            Log.d("gugum",ex.toString());
        }
        return result;
    }

}
