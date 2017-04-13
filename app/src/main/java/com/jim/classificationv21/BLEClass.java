package com.jim.classificationv21;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jim on 16/11/2016.
 */

public class BLEClass extends StrapDeviceClass
{
    public final static String BROADCAST_STATUS_GATT_CONNECTED =              "MENRVA.bluetooth.le.STATUS_GATT_CONNECTED";
    public final static String BROADCAST_STATUS_GATT_CONNECTING =             "MENRVA.bluetooth.le.STATUS_GATT_CONNECTING";
    public final static String BROADCAST_STATUS_GATT_DISCONNECTED =           "MENRVA.bluetooth.le.STATUS_GATT_DISCONNECTED";
    public final static String BROADCAST_STATUS_GATT_SERVICES_DISCOVERED =    "MENRVA.bluetooth.le.STATUS_GATT_SERVICES_DISCOVERED";
    public final static String BROADCAST_STATUS_INIT_COMPLETE =               "MENRVA.bluetooth.le.STATUS_INIT_COMPLETE";

    public final static String BROADCAST_ACTION_GATT_CONNECT =                "MENRVA.bluetooth.le.ACTION_GATT_CONNECT";
    public final static String BROADCAST_ACTION_GATT_DISCONNECT =             "MENRVA.bluetooth.le.ACTION_GATT_DISCONNECT";
    public final static String BROADCAST_ACTION_DATA_AVAILABLE =              "MENRVA.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String BROADCAST_ACTION_SEND_DATA =                   "MENRVA.bluetooth.le.ACTION_SEND_DATA";
    public final static String BROADCAST_ACTION_GET_STATUS =                  "MENRVA.bluetooth.le.ACTION_GET_STATUS";
    public final static String BROADCAST_ACTION_JSON_DATA_AVAILABLE =         "MENRVA.bluetooth.le.ACTION_JSON_DATA_AVAILABLE";
    public final static String BROADCAST_ACTION_JSON_DATA_SEND =              "MENRVA.bluetooth.le.ACTION_JSON_SEND_DATA";
    public final static String BROADCAST_EXTRA_DATA =                         "MENRVA.bluetooth.le.EXTRA_DATA";

    Boolean dataCollectionActive;

    BLEClass(Activity parent)
    {
        super(parent);
    }

    public void registerDataReceiver()
    {
        parent.registerReceiver(mGattUpdateReceiver, mGattUpdateReceiverIntentFilter());
    }

    public void unregisterDataReceiver()
    {
        stopDataCollection();
        parent.unregisterReceiver(mGattUpdateReceiver);
    }

    public void startDataCollection()
    {

        if(parent instanceof TrainingAndClassifyingActivity)
        {
            // why is this needed?
            // latency at 10Hz is too high, so it needs to be set to 3Hz
            if( ((TrainingAndClassifyingActivity)parent).dataCollectionMode == TrainingAndClassifyingActivity.DataCollectionMode.TRAINING )
            {
                sendData("{message: \"settings\", \"enable fsr\": true, \"fsr delay\": 50}");
                sendData("{message: \"settings\", \"enable imu\": true, \"imu delay\": 50}");
            }
            else
            {
                sendData("{message: \"settings\", \"enable fsr\": true, \"fsr delay\": 50}");
                sendData("{message: \"settings\", \"enable imu\": true, \"imu delay\": 50}");
            }
        }
        if(parent instanceof RawDataActivity)
        {
            sendData("{message: \"settings\", \"enable fsr\": true, \"fsr delay\": 50}");
            sendData("{message: \"settings\", \"enable imu\": true, \"imu delay\": 50}");
        }



        dataCollectionActive = true;
    }
    public void stopDataCollection()
    {
        //$real,enable;
        //sendData("{message: \"settings\", \"enable fsr\": false}");
        //sendData("{message: \"settings\", \"enable imu\": false}");

        dataCollectionActive = false;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BROADCAST_STATUS_GATT_CONNECTED.equals(action)) {

            } else if (BROADCAST_STATUS_GATT_DISCONNECTED.equals(action)) {

            } else if (BROADCAST_STATUS_GATT_SERVICES_DISCOVERED.equals(action)) {

            } else if (BROADCAST_ACTION_DATA_AVAILABLE.equals(action)) {

            } else if (BROADCAST_ACTION_JSON_DATA_AVAILABLE.equals(action)){
                try
                {
                    JSONObject obj = new JSONObject(intent.getStringExtra(BROADCAST_EXTRA_DATA));

                    switch(obj.getString("message")){
                        case "fsr data":{
                            JSONArray arr = obj.getJSONArray("fsr");
                            // new data received
                            ArrayList data = new ArrayList();

                            for (int i = 0; i < arr.length(); i++)
                            {
                                int input = arr.getInt(i);
                                data.add(input);
                            }

                            if( dataCollectionActive )
                            {
                                if(parent instanceof TrainingAndClassifyingActivity)
                                {
                                    ((TrainingAndClassifyingActivity)parent).dataReceived(data);
                                }
                                if(parent instanceof RawDataActivity)
                                {
                                    ((RawDataActivity)parent).dataReceived(data);
                                }
                                //parent.dataReceived(data);
                            }
                            break;
                        }
                        case "imu data":
                        {
                            double roll = obj.getDouble("roll");
                            double pitch = obj.getDouble("pitch");
                            double yaw = obj.getDouble("yaw");

                            //Log.i("roll pitch yaw",""+ roll + " " + pitch + " " + yaw);

                            if(parent instanceof RawDataActivity)
                            {
                                ((RawDataActivity)parent).imuReceived(roll,pitch,yaw);
                            }
                            break;
                        }
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    };

    private void sendData(String data){
        final Intent intent = new Intent(BROADCAST_ACTION_JSON_DATA_SEND);
        intent.putExtra(BROADCAST_EXTRA_DATA, data);
        parent.sendBroadcast(intent);
    }

    private static IntentFilter mGattUpdateReceiverIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_STATUS_GATT_CONNECTED);
        intentFilter.addAction(BROADCAST_STATUS_GATT_DISCONNECTED);
        intentFilter.addAction(BROADCAST_STATUS_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BROADCAST_ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BROADCAST_ACTION_JSON_DATA_AVAILABLE);
        return intentFilter;
    }
}

