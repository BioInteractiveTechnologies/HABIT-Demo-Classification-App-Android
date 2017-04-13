package com.jim.classificationv21;

import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class GraphClass
{
    View mView;

    private LineChart mChart;

    int count_dataReceived;

    GraphClass(View mView)
    {
        this.mView = mView;
        setupChart();
    }

    public void setupChart()
    {
        mChart = (LineChart) mView;
        mChart.setDescription("");
        mChart.setTouchEnabled(false);

        count_dataReceived = 0;

        Legend l = mChart.getLegend();

        l.setEnabled(true);
        l.setTextColor(Color.WHITE);

        XAxis xAxis = mChart.getXAxis();

        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setEnabled(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        rightAxis.setTextColor(Color.DKGRAY);
        rightAxis.setDrawGridLines(false);

        mChart.setData(new LineData());

        mChart.invalidate();
    }
    public void dataRecieved(ArrayList data)
    {
        count_dataReceived++;
        for( int i = 0; i <10; i++)
        {
            int reading = (int) data.get(i);
            addDataPoint( i , "FSR: " +   i, count_dataReceived, reading);
        }
    }

    public void addDataPoint(int lineID, String lineName, int xValue, float yValue)
    {
        LineData data = mChart.getData();

        ILineDataSet set = data.getDataSetByIndex(lineID);

        if (set == null)
        {
            set = createSet(lineID, lineName);
            data.addDataSet(set);
        }

        data.addEntry(new Entry(xValue, yValue), lineID);

        if(data.getEntryCount() > 70)
        {
            data.removeEntry(xValue - 7, lineID);
        }

        // let the chart know it's data has changed
        mChart.notifyDataSetChanged();

        mChart.setVisibleXRangeMaximum(6);

        mChart.moveViewTo(data.getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);

        //Log.i("graph class", "addDataPoint: " +data.getEntryCount() );
    }

    private LineDataSet createSet(int lineID, String lineName)
    {
        int[] mColors = ColorTemplate.VORDIPLOM_COLORS;
        int color = mColors[lineID % mColors.length];

        LineDataSet set = new LineDataSet(null, lineName);
        set.setLineWidth(2.5f);
        set.setCircleRadius(4.5f);

        set.setColor(color);
        set.setCircleColor(color);
        set.setHighLightColor(color);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueTextColor(Color.rgb(255,255,255));
        set.setValueTextSize(10f);

        return set;
    }

}
