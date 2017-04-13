package com.jim.classificationv21;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class RawDataActivity extends AppCompatActivity
{
    View graphView;
    View cubeView;

    GraphClass graphClass;
    StrapDeviceClass strapDevice;

    OpenGLRenderer openGLRenderer;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raw_data);

        Button resetButton = (Button)findViewById(R.id.reset);
        resetButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(myIntent);
            }
        });


        graphView = findViewById(R.id.lineChart);
        graphClass = new GraphClass(graphView);

        // init the strap device based on previous radio button selection
        if( MainActivity.bluetoothConnection == MainActivity.BluetoothConnection.PEBBLE )
        {
            //PebbleClass
            strapDevice = new PebbleClass(this);
        }
        else if ( MainActivity.bluetoothConnection == MainActivity.BluetoothConnection.BLE )
        {
            //BLE
            strapDevice = new BLEClass(this);
        }
        strapDevice.registerDataReceiver();
        strapDevice.startDataCollection();


        //cubeView = findViewById(R.id.cubeView);
        GLSurfaceView view = new GLSurfaceView(this);
        openGLRenderer = new OpenGLRenderer(this);

        view.setRenderer(openGLRenderer);

        LinearLayout l = (LinearLayout) findViewById(R.id.cubeView);
        l.addView(view);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        strapDevice.registerDataReceiver();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if( strapDevice != null)
        {
            strapDevice.unregisterDataReceiver();
        }
    }

    public void dataReceived(ArrayList data)
    {
        graphClass.dataRecieved(data);
    }
    public void imuReceived(Double roll, Double pitch, Double yaw)
    {
        openGLRenderer.updateRotationValues(roll, pitch, yaw);
    }
}

