package com.jim.classificationv21;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.UUID;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver;
import com.getpebble.android.kit.util.PebbleDictionary;

public class PebbleClass extends StrapDeviceClass
{
    UUID appUuid = UUID.fromString("a7000d5e-d21c-407c-9ed7-8c2738defd66");

    Boolean dataCollectionActive;

    PebbleClass(Activity parent)
    {
        super(parent);
    }

    public void registerDataReceiver()
    {
        PebbleKit.registerReceivedDataHandler(parent.getApplicationContext(),dataReceiver);
    }

    public void unregisterDataReceiver()
    {
       //PebbleKit
        stopDataCollection();

        //unregister
        parent.getApplicationContext().unregisterReceiver(dataReceiver);

    }

    public void startDataCollection()
    {
        Log.i("Pebble", "Send to pebble start");

        dataCollectionActive = true;
        sendData(87, "start");
    }
    public void stopDataCollection()
    {
        Log.i("Pebble", "Send to pebble stop");
        dataCollectionActive = false;
        sendData(88, "stop");
    }



    public PebbleDataReceiver dataReceiver = new PebbleDataReceiver(appUuid)
    {
        @Override
        public void receiveData(Context context, int transaction_id, PebbleDictionary dict)
        {
            // A new AppMessage was received, tell Pebble
            PebbleKit.sendAckToPebble(context, transaction_id);

            // new training data received
            ArrayList data = new ArrayList();

            for (int i = 0; i < 10; i++)
            {
                int input = dict.getInteger(i).intValue();
                data.add(input);
            }
            //Log.i("dataReceiver","data" + data);
            if( dataCollectionActive)
            {
                if(parent instanceof TrainingAndClassifyingActivity)
                {
                    ((TrainingAndClassifyingActivity)parent).dataReceived(data);
                }
                if(parent instanceof RawDataActivity)
                {
                    ((RawDataActivity)parent).dataReceived(data);
                }
                //parent.getClass().dataReceived(data);
            }
        }

    };

    private void sendData(int key, String text)
    {
        PebbleDictionary dict = new PebbleDictionary();

        dict.addString(key, text);

        PebbleKit.sendDataToPebble(parent.getApplicationContext(), appUuid, dict);
    }
}