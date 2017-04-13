package com.jim.classificationv21;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;


public class MainActivity extends AppCompatActivity
{

    static int numberGestures;
    static int numberTrials;
    static int trainingSamples;

    Intent myIntent;

    enum BluetoothConnection
    {
        PEBBLE,
        BLE;
    }

    static BluetoothConnection bluetoothConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handleButtons();
    }

    void handleButtons()
    {
        Button beginButton = (Button)findViewById(R.id.begin_button);
        beginButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                begin();
            }
        });

        Button beginRawButton = (Button)findViewById(R.id.begin_raw_button);
        beginRawButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                beginRaw();
            }
        });
    }

    void begin()
    {

        EditText numberTrialsEdit = (EditText) findViewById(R.id.number_trials_edit);
        numberTrials = Integer.parseInt( numberTrialsEdit.getText().toString() );

        EditText numberGesturesEdit = (EditText) findViewById(R.id.number_gestures_edit);
        int numberOfGestures = Integer.parseInt( numberGesturesEdit.getText().toString() );
        if( numberOfGestures < 3)
        {
            numberOfGestures = 3;
        }
        numberGestures = numberOfGestures;



        EditText trainingSamplesEdit = (EditText) findViewById(R.id.number_training_samples_edit);
        trainingSamples = Integer.parseInt( trainingSamplesEdit.getText().toString() );

        RadioGroup radioButtonGroup = (RadioGroup)findViewById(R.id.bluetooth_connection_radio_group);
        int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
        View radioButton = radioButtonGroup.findViewById(radioButtonID);
        int radioButtonIndex = radioButtonGroup.indexOfChild(radioButton);

        if( radioButtonIndex == 0 )
        {
            bluetoothConnection = BluetoothConnection.PEBBLE;
        }
        else if ( radioButtonIndex == 1 )
        {
            bluetoothConnection = BluetoothConnection.BLE;
        }
        else
        {
            return;
        }

        myIntent = new Intent(this, TrainingAndClassifyingActivity.class);
        startActivity(myIntent);
    }
    void beginRaw()
    {
        RadioGroup radioButtonGroup = (RadioGroup)findViewById(R.id.bluetooth_connection_radio_group);
        int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
        View radioButton = radioButtonGroup.findViewById(radioButtonID);
        int radioButtonIndex = radioButtonGroup.indexOfChild(radioButton);


        if( radioButtonIndex == 0 )
        {
            bluetoothConnection = BluetoothConnection.PEBBLE;
        }
        else if ( radioButtonIndex == 1 )
        {
            bluetoothConnection = BluetoothConnection.BLE;
        }
        else
        {
            return;
        }

        myIntent = new Intent(getApplicationContext(), RawDataActivity.class);
        startActivity(myIntent);
    }
}
