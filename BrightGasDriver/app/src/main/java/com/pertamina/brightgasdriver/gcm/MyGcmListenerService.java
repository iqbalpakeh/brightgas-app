/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pertamina.brightgasdriver.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.pertamina.brightgasdriver.MainActivity;
import com.pertamina.brightgasdriver.R;

//import com.exaditama.xcommerce.ActivityChat;
//import com.exaditama.xcommerce.ActivityOrderDetail;
//import com.exaditama.xcommerce.HomeActivity;
//import com.exaditama.xcommerce.Notification;

public class MyGcmListenerService extends GcmListenerService {

    public static final int TRANSACTION_DETAIL = 1;
    public static final int RATING = 2;
    public static final int TRANSACTION_TRACKER = 3;

    private static final String TAG = "gugum";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d("gugum","DATA TITLE : "+data.getString("title"));
        Log.d(TAG,"Data : "+data.toString());
        Log.d(TAG, "From: " + from);
        String message = data.getString("message");
        Log.d(TAG, "Message: " + message);
        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a description indicating to the user
         * that a message was received.
         */
        // [END_EXCLUDE]
//        int type = Integer.parseInt(data.getString("type"));

//        if(((BaseActivity) getActivity())!=null){
//            Intent intent = new Intent("");
//            intent.putExtra("data", data.getString("data"));
//            intent.putExtra("type", data.getString("type"));
//            sendBroadcast(intent);
//        }
        sendNotification(data.getString("type"), data.getString("title"),message, data.getString("data"));

//        switch (type){
//            case Notification.TYPE_DETAIL_MESSAGE:
//                if(ActivityChat.self!=null){
//                    Intent intent = new Intent(ActivityChat.TAG);
//                    intent.putExtra("message", message);
//                    sendBroadcast(intent);
//                }else{
//                    sendNotification(type, message, data.getString("data"));
//                }
//                break;
//            case Notification.TYPE_DETAIL_ORDER:
//                if(HomeActivity.self!=null){
//                    Intent intent = new Intent(HomeActivity.TAG_NOTIFICATION);
//                    intent.putExtra("message", message);
//                    sendBroadcast(intent);
//                }else{
//                    sendNotification(type, message, data.getString("data"));
//                }
//                break;
//        }

    }
    // [END receive_message]

    /**
     * Create and show a simple description containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String type,String title,String message,String data) {
        Class mClass;
        Log.d("gugum","TYPE : "+type);
//        switch (type){
//            case Notification.TYPE_DETAIL_ORDER:
//                Log.d("gugum","GO TO DETAIL ORDER");
//                mClass = ActivityOrderDetail.class;
//                break;
//            case Notification.TYPE_DETAIL_MESSAGE:
//                mClass = ActivityChat.class;
//                break;
//            default:
//                Log.d("gugum","GO TO MAIN ACTIVITY");
//                mClass = MainActivity.class;
//                break;
//        }

        mClass = MainActivity.class;

        Intent intent = new Intent(this, mClass);
        intent.putExtra("data",data);
        intent.putExtra("type",type);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            notificationBuilder
                    .setSmallIcon(R.drawable.ic_check_mark)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        }else{
            notificationBuilder
                    .setSmallIcon(R.mipmap.ic_launcher);
        }
        notificationBuilder
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of description */, notificationBuilder.build());
    }
}