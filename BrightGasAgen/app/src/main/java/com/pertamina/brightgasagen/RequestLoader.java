package com.pertamina.brightgasagen;

import android.os.AsyncTask;
import android.view.View;

import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RequestLoader {

    private static final String TAG = "request_loader";

    private String mId;

    private String mResult;

    private RequestLoaderInterface mRequestLoaderInterface;

    private int mIndex;

    private boolean mIsRajaOngkir;

    private boolean mIsGet;

    private boolean mIsNotUseCache;

    private boolean mIsNotSendingRedundant;

    private View[] mImpactedViews;

    public RequestLoader(RequestLoaderInterface requestLoaderInterface) {
        this.mRequestLoaderInterface = requestLoaderInterface;
    }

    public void getRequestRajaOngkir(int index, BasicNameValuePair[] valuePairs, String controller) {
        mIsRajaOngkir = true;
        mIsGet = true;
        this.mIndex = index;
        new RequestTask().executeOnExecutor(new ThreadPoolExecutor(5, 10, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()), valuePairs, controller, null);
        //loadRequest(index, valuePairs, controller, null);
    }

    public void postRequestRajaOngkir(int index, BasicNameValuePair[] valuePairs, String controller) {
        mIsRajaOngkir = true;
        this.mIndex = index;
        new RequestTask().executeOnExecutor(new ThreadPoolExecutor(5, 10, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()), valuePairs, controller, null);
        //loadRequest(index, valuePairs, controller, null);
    }

    public void loadRequest(int index, BasicNameValuePair[] valuePairs, String controller, String action) {
        this.mIndex = index;
        //new RequestTask().executeOnExecutor(new ThreadPoolExecutor(5, 10, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()), valuePairs, controller, action);
    }

    public void loadRequest(int index, MultipartEntityBuilder valuePairs, String controller, String action) {
        this.mIndex = index;
        mIsNotUseCache = true;
        //new MultiRequestTask().executeOnExecutor(new ThreadPoolExecutor(5, 10, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()), valuePairs, controller, action);
    }

    public void loadRequest(int index, BasicNameValuePair[] valuePairs, String controller, String action, boolean isNotUseCache) {
        this.mIndex = index;
        this.mIsNotUseCache = isNotUseCache;
        //new RequestTask().executeOnExecutor(new ThreadPoolExecutor(5, 10, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()), valuePairs, controller, action);
    }

    public void loadRequest(int index, BasicNameValuePair[] valuePairs, String controller, String action, boolean isNotUseCache, boolean isNotSendingRedundan) {
        this.mIndex = index;
        this.mIsNotUseCache = isNotUseCache;
        this.mIsNotSendingRedundant = isNotSendingRedundan;
        //new RequestTask().executeOnExecutor(new ThreadPoolExecutor(5, 10, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()), valuePairs, controller, action);
    }

    public void loadRequest(int index, BasicNameValuePair[] valuePairs, String controller, String action, boolean isNotUseCache, View[] impactedViews) {
        this.mIndex = index;
        this.mIsNotUseCache = isNotUseCache;
        this.mImpactedViews = impactedViews;
        //new RequestTask().executeOnExecutor(new ThreadPoolExecutor(5, 10, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()), valuePairs, controller, action);
    }

    private class RequestToLoad {

        public BasicNameValuePair[] valuePairs;
        public String controller;
        public String action;
        public MultipartEntityBuilder entityBuilder;

        public RequestToLoad(BasicNameValuePair[] valuePairs, String controller, String action) {
            this.valuePairs = valuePairs;
            this.controller = controller;
            this.action = action;
        }

        public RequestToLoad(MultipartEntityBuilder entityBuilder, String controller, String action) {
            this.entityBuilder = entityBuilder;
            this.controller = controller;
            this.action = action;
        }

    }

    class MultiRequestTask extends AsyncTask<Object, Void, String> {

        MultipartEntityBuilder valuePairs;
        String controller;
        String action;

        @Override
        protected String doInBackground(Object... params) {
            valuePairs = (MultipartEntityBuilder) params[0];
            controller = (String) params[1];
            action = (String) params[2];
            return null;
        }

        @Override
        protected void onPostExecute(String r) {
            RequestToLoad p = new RequestToLoad(valuePairs, controller, action);
            new DataTask(p).executeOnExecutor(new ThreadPoolExecutor(5, 10, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()), null);
        }

    }

    class RequestTask extends AsyncTask<Object, Void, String> {

        BasicNameValuePair[] valuePairs;
        String controller;
        String action;

        @Override
        protected String doInBackground(Object... params) {
            valuePairs = (BasicNameValuePair[]) params[0];
            controller = (String) params[1];
            action = (String) params[2];
            if (!mIsNotUseCache) {
                mId = controller + action;
                for (BasicNameValuePair pair : valuePairs) {
                    mId += pair.getName() + pair.getValue();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String r) {
            RequestToLoad p = new RequestToLoad(valuePairs, controller, action);
            if (!mIsNotUseCache) {
                mResult = Data.self.getRequest(mId);
                if (mResult != null)
                    mRequestLoaderInterface.setData(mIndex, mResult, mImpactedViews);
            }
            new DataTask(p).executeOnExecutor(new ThreadPoolExecutor(5, 10, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()), null);
        }

    }

    class DataTask extends AsyncTask<String, Void, String> {
        RequestToLoad photoToLoad;

        public DataTask(RequestToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        protected String doInBackground(String... params) {
            if (mIsRajaOngkir) {
                if (mIsGet)
                    return Network.httpGetRajaOngkir(photoToLoad.valuePairs, photoToLoad.controller);
                else
                    return Network.httpPostRajaOngkir(photoToLoad.valuePairs, photoToLoad.controller);
            }
            else {
                if (photoToLoad.valuePairs != null)
                    return Network.httpPost(photoToLoad.valuePairs, photoToLoad.controller, photoToLoad.action);
                else
                    return Network.httpPost(photoToLoad.entityBuilder, photoToLoad.controller, photoToLoad.action);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (mIsNotUseCache) {
                mRequestLoaderInterface.setData(mIndex, s, mImpactedViews);
            } else {
                if (s != null && !s.equals(mResult)) {
                    Data.self.insertRequest(mId, s);
                    mRequestLoaderInterface.setData(mIndex, s, mImpactedViews);
                } else {
                    if (!mIsNotSendingRedundant) {
                        mRequestLoaderInterface.setData(mIndex, s, mImpactedViews);
                    }
                }
            }
        }
    }

}
