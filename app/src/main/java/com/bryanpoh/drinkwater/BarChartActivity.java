//package com.bryanpoh.drinkwater;
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.RectF;
//import android.net.Uri;
//import android.os.Bundle;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.content.ContextCompat;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.WindowManager;
//import android.widget.SeekBar;
//import android.widget.SeekBar.OnSeekBarChangeListener;
//import android.widget.TextView;
//
//import com.github.mikephil.charting.charts.BarChart;
//import com.github.mikephil.charting.components.Legend;
//import com.github.mikephil.charting.components.Legend.LegendForm;
//import com.github.mikephil.charting.components.XAxis;
//import com.github.mikephil.charting.components.XAxis.XAxisPosition;
//import com.github.mikephil.charting.components.YAxis;
//import com.github.mikephil.charting.components.YAxis.AxisDependency;
//import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
//import com.github.mikephil.charting.data.BarData;
//import com.github.mikephil.charting.data.BarDataSet;
//import com.github.mikephil.charting.data.BarEntry;
//import com.github.mikephil.charting.data.Entry;
//import com.github.mikephil.charting.formatter.ValueFormatter;
//import com.github.mikephil.charting.highlight.Highlight;
//import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
//import com.github.mikephil.charting.interfaces.datasets.IDataSet;
//import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
//import com.github.mikephil.charting.utils.MPPointF;
//import com.bryanpoh.drinkwater.custom.DayAxisValueFormatter;
//import com.bryanpoh.drinkwater.custom.MyValueFormatter;
//import com.bryanpoh.drinkwater.custom.XYMarkerView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class BarChartActivity extends AppCompatActivity implements OnSeekBarChangeListener,
//        OnChartValueSelectedListener {
//
//    private BarChart chart;
//    private SeekBar seekBarX, seekBarY;
//    private TextView tvX, tvY;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_barchart);
//
//        setTitle("Your DrinkWater History");
//
//        tvX = findViewById(R.id.tvXMax);
//        tvY = findViewById(R.id.tvYMax);
//
//        seekBarX = findViewById(R.id.seekBar1);
//        seekBarY = findViewById(R.id.seekBar2);
//
//        seekBarY.setOnSeekBarChangeListener(this);
//        seekBarX.setOnSeekBarChangeListener(this);
//
//        chart = findViewById(R.id.chart1);
//        chart.setOnChartValueSelectedListener(this);
//
//        chart.setDrawBarShadow(false);
//        chart.setDrawValueAboveBar(true);
//
//        chart.getDescription().setEnabled(false);
//
//        // if more than 60 entries are displayed in the chart, no values will be
//        // drawn
//        chart.setMaxVisibleValueCount(60);
//
//        // scaling can now only be done on x- and y-axis separately
//        chart.setPinchZoom(false);
//
//        chart.setDrawGridBackground(false);
//        // chart.setDrawYLabels(false);
//
//        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);
//
//        XAxis xAxis = chart.getXAxis();
//        xAxis.setPosition(XAxisPosition.BOTTOM);
//        xAxis.setDrawGridLines(false);
//        xAxis.setGranularity(1f); // only intervals of 1 day
//        xAxis.setLabelCount(7);
//        xAxis.setValueFormatter(xAxisFormatter);
//
////        ValueFormatter custom = new MyValueFormatter("$");
//
//        YAxis leftAxis = chart.getAxisLeft();
//        leftAxis.setLabelCount(8, false);
////        leftAxis.setValueFormatter(custom);
//        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
//        leftAxis.setSpaceTop(15f);
//        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//
//        YAxis rightAxis = chart.getAxisRight();
//        rightAxis.setDrawGridLines(false);
//        rightAxis.setLabelCount(8, false);
////        rightAxis.setValueFormatter(custom);
//        rightAxis.setSpaceTop(15f);
//        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//
//        Legend l = chart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(false);
//        l.setForm(LegendForm.SQUARE);
//        l.setFormSize(9f);
//        l.setTextSize(11f);
//        l.setXEntrySpace(4f);
//
//        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
//        mv.setChartView(chart); // For bounds control
//        chart.setMarker(mv); // Set the marker to the chart
//
//        // setting data
//        seekBarY.setProgress(50);
//        seekBarX.setProgress(12);
//
//        // chart.setDrawLegend(false);
//    }
//
//    private void setData(int count, float range) {
//
//        float start = 1f;
//
//        ArrayList<BarEntry> values = new ArrayList<>();
//
//        for (int i = (int) start; i < start + count; i++) {
//            float val = (float) (Math.random() * (range + 1));
//
//            if (Math.random() * 100 < 25) {
////                values.add(new BarEntry(i, val, getResources().getDrawable(R.drawable.star)));
//                values.add(new BarEntry(i, val));
//            } else {
//                values.add(new BarEntry(i, val));
//            }
//        }
//
//        BarDataSet set1;
//
//        if (chart.getData() != null &&
//                chart.getData().getDataSetCount() > 0) {
//            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
//            set1.setValues(values);
//            chart.getData().notifyDataChanged();
//            chart.notifyDataSetChanged();
//
//        } else {
//            set1 = new BarDataSet(values, "Progress in the year 2020");
//
//            set1.setDrawIcons(false);
//
//            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
//            dataSets.add(set1);
//
//            BarData data = new BarData(dataSets);
//            data.setValueTextSize(10f);
//            data.setBarWidth(0.9f);
//
//            chart.setData(data);
//        }
//    }
//
//    /*@Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.bar, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.viewGithub: {
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/BarChartActivity.java"));
//                startActivity(i);
//                break;
//            }
//            case R.id.actionToggleValues: {
//                for (IDataSet set : chart.getData().getDataSets())
//                    set.setDrawValues(!set.isDrawValuesEnabled());
//
//                chart.invalidate();
//                break;
//            }
//            case R.id.actionToggleIcons: {
//                for (IDataSet set : chart.getData().getDataSets())
//                    set.setDrawIcons(!set.isDrawIconsEnabled());
//
//                chart.invalidate();
//                break;
//            }
//            case R.id.actionToggleHighlight: {
//                if (chart.getData() != null) {
//                    chart.getData().setHighlightEnabled(!chart.getData().isHighlightEnabled());
//                    chart.invalidate();
//                }
//                break;
//            }
//            case R.id.actionTogglePinch: {
//                if (chart.isPinchZoomEnabled())
//                    chart.setPinchZoom(false);
//                else
//                    chart.setPinchZoom(true);
//
//                chart.invalidate();
//                break;
//            }
//            case R.id.actionToggleAutoScaleMinMax: {
//                chart.setAutoScaleMinMaxEnabled(!chart.isAutoScaleMinMaxEnabled());
//                chart.notifyDataSetChanged();
//                break;
//            }
//            case R.id.actionToggleBarBorders: {
//                for (IBarDataSet set : chart.getData().getDataSets())
//                    ((BarDataSet) set).setBarBorderWidth(set.getBarBorderWidth() == 1.f ? 0.f : 1.f);
//
//                chart.invalidate();
//                break;
//            }
//            case R.id.animateX: {
//                chart.animateX(2000);
//                break;
//            }
//            case R.id.animateY: {
//                chart.animateY(2000);
//                break;
//            }
//            case R.id.animateXY: {
//                chart.animateXY(2000, 2000);
//                break;
//            }
//            case R.id.actionSave: {
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                    saveToGallery();
//                } else {
//                    requestStoragePermission(chart);
//                }
//                break;
//            }
//        }
//        return true;
//    }*/
//
//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//        tvX.setText(String.valueOf(seekBarX.getProgress()));
//        tvY.setText(String.valueOf(seekBarY.getProgress()));
//
//        setData(seekBarX.getProgress(), seekBarY.getProgress());
//        chart.invalidate();
//    }
//
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {}
//
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {}
//
//    private final RectF onValueSelectedRectF = new RectF();
//
//    @Override
//    public void onValueSelected(Entry e, Highlight h) {
//
//        if (e == null)
//            return;
//
//        RectF bounds = onValueSelectedRectF;
//        chart.getBarBounds((BarEntry) e, bounds);
//        MPPointF position = chart.getPosition(e, AxisDependency.LEFT);
//
//        Log.i("bounds", bounds.toString());
//        Log.i("position", position.toString());
//
//        Log.i("x-index",
//                "low: " + chart.getLowestVisibleX() + ", high: "
//                        + chart.getHighestVisibleX());
//
//        MPPointF.recycleInstance(position);
//    }
//
//    @Override
//    public void onNothingSelected() { }
//}
