package ru.sash0k.thriftbox;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FinanceFormatter implements ValueFormatter, YAxisValueFormatter {
    private static final String ROUBLE = Utils.hasLollipop() ? String.valueOf(Utils.ROUBLE) : "р.";
    //private final NumberFormat mFormat;
    private final NumberFormat mAxisFormat;
    
    public FinanceFormatter() {
        //mFormat = new DecimalFormat("#,###.##");
        mAxisFormat = new DecimalFormat("#,###");
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return entry.getData().toString();
        //return mFormat.format(value) + ROUBLE;
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return mAxisFormat.format(value) + ROUBLE;
    }
}
