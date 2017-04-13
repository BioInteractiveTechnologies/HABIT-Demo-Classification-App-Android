package com.jim.classificationv21;

import android.app.Activity;

abstract class StrapDeviceClass
{
    Activity parent;

    StrapDeviceClass(Activity parent)
    {
        this.parent = parent;
    }

    abstract void registerDataReceiver();
    abstract void unregisterDataReceiver();
    abstract void startDataCollection();
    abstract void stopDataCollection();
}
