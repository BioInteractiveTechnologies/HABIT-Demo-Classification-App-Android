package com.jim.classificationv21;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;

import smile.classification.LDA;

public class TrainingAndClassifyingActivity extends AppCompatActivity
{
    Button trainGestureButton;
    Button startClassifyingGesturesButton;
    Button stopClassifyingGesturesButton;
    Button resetModelButton;
    TextView trainingFeedbackText;
    TextView classificationFeedbackText;
    TextView classificationFeedbackTextLarge;

    View graphView;

    GraphClass graphClass;
    MachineLearningClass machineLearningClass;
    StrapDeviceClass strapDevice;

    public enum DataCollectionMode
    {
        TRAINING,
        CLASSIFYING;
    }
    DataCollectionMode dataCollectionMode;

    int currentNumberOfGestures;
    int currentGesture;
    int currentTrial;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_and_classifying);

        init();
    }

    void init()
    {

        initButtonsAndViews();
        handleButtonClicks();

        currentNumberOfGestures = 0;
        currentGesture = 0;
        currentTrial = 0;

        graphClass = new GraphClass(graphView);
        machineLearningClass = new MachineLearningClass(this);

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

    void initButtonsAndViews()
    {
        // get button and textviews
        trainGestureButton = (Button)findViewById(R.id.train_gesture_button);
        startClassifyingGesturesButton = (Button)findViewById(R.id.start_classifying_gestures_button);
        stopClassifyingGesturesButton = (Button)findViewById(R.id.stop_classifying_gestures_button);
        resetModelButton = (Button)findViewById(R.id.reset_model_button);

        trainGestureButton.setEnabled(true);
        startClassifyingGesturesButton.setEnabled(false);
        stopClassifyingGesturesButton.setEnabled(false);
        resetModelButton.setEnabled(true);

        trainingFeedbackText = (TextView) findViewById(R.id.training_feedback_text);
        classificationFeedbackText = (TextView) findViewById(R.id.classification_feedback_text);
        classificationFeedbackTextLarge = (TextView) findViewById(R.id.classification_feedback_text_large);

        graphView = findViewById(R.id.GraphView);
        graphView.setVisibility(View.INVISIBLE);
    }



    // ==================================================


    void handleButtonClicks()
    {
        trainGestureButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                trainGesture();
            }
        });
        startClassifyingGesturesButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startClassifyingGestures();
            }
        });
        stopClassifyingGesturesButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                stopClassifyingGestures();
            }
        });

        resetModelButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                resetModel();
            }
        });
    }


    // ==================================================

    // Button Functions

    void trainGesture()
    {
        // disable buttons
        trainGestureButton.setEnabled(false);
        startClassifyingGesturesButton.setEnabled(false);
        stopClassifyingGesturesButton.setEnabled(false);


        // Specify that its in traing mode
        dataCollectionMode = DataCollectionMode.TRAINING;
        //send start message to bluetooth device
        strapDevice.startDataCollection();

        trainGestureButton.setText("Training Gesture # "+  (currentNumberOfGestures % MainActivity.numberGestures + 1));
        //Set text
        trainingFeedbackText.setText( "Please Hold Current Gesture \nSamples Received: 0/" + MainActivity.trainingSamples );
    }


    void startClassifyingGestures()
    {
        trainGestureButton.setEnabled(false);
        startClassifyingGesturesButton.setEnabled(false);
        stopClassifyingGesturesButton.setEnabled(true);

        trainGestureButton.setVisibility(View.INVISIBLE);
        trainingFeedbackText.setVisibility(View.INVISIBLE);

        graphView.setVisibility(View.VISIBLE);

        // Turn on data collection
        dataCollectionMode = DataCollectionMode.CLASSIFYING;
        strapDevice.startDataCollection();
    }

    void stopClassifyingGestures()
    {
        startClassifyingGesturesButton.setEnabled(true);
        stopClassifyingGesturesButton.setEnabled(false);

        strapDevice.stopDataCollection();

        classificationFeedbackText.setText("Classification complete");
    }

    void resetModel()
    {
        //deinit strap
        //handled in onPause
        //strapDevice.unregisterDataReceiver();
        //strapDevice = null;

        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
        //finish();
    }

    // ==================================================

    public void dataReceived(ArrayList data)
    {
        Log.i("dataReceived","data:" + data.toString());
        if( dataCollectionMode == DataCollectionMode.TRAINING )
        {
            machineLearningClass.addTrainingData(data);
        }
        else if ( dataCollectionMode == DataCollectionMode.CLASSIFYING )
        {
            String classfyingResult = machineLearningClass.classifyData(data);
            classificationFeedbackText.setText(classfyingResult);

            graphClass.dataRecieved(data);
        }
    }

    // ==================================================

    void trainingComplete()
    {
        currentNumberOfGestures++;

        // Stop data collection
        strapDevice.stopDataCollection();


        if( currentNumberOfGestures == MainActivity.numberGestures * MainActivity.numberTrials)
        {
            trainGestureButton.setText("All Training Samples Collected");
            trainingFeedbackText.setText("Training and validating models. Please Wait");

            //delay needed to give time for trainingFeedbackText.setText to actually update
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        String modelTrainingResults = machineLearningClass.trainModels();
                        trainingFeedbackText.setText("Number of trained gestures: " + currentNumberOfGestures + modelTrainingResults );

                        //trainGestureButton.setEnabled(true);
                        startClassifyingGesturesButton.setEnabled(true);
                    }
                    catch (Exception e)
                    {
                        trainingFeedbackText.setText("Error training the models. The gestures were too similar.\nEnsure the strap is tight against the wrist.\nPlease reset and try again.");
                    }

                }
            }, 100);
        }
        else
        {
            trainGestureButton.setEnabled(true);
            trainGestureButton.setText("Train Gesture # "+  (currentNumberOfGestures % MainActivity.numberGestures + 1));
            trainingFeedbackText.setText( "Please Train Gesture" );
        }
    }
}
