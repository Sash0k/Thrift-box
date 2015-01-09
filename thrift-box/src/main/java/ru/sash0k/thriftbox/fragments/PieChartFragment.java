package ru.sash0k.thriftbox.fragments;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.Legend.LegendPosition;

import java.util.ArrayList;
import java.util.Map;

import ru.sash0k.thriftbox.R;
import ru.sash0k.thriftbox.Utils;
import ru.sash0k.thriftbox.database.DB;


public class PieChartFragment extends Fragment {

    public static Fragment newInstance() {
        return new PieChartFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chart, container, false);

        PieChart mChart = (PieChart) v.findViewById(R.id.pieChart1);
        mChart.setDescription("");
        
//      Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
//      mChart.setValueTypeface(tf);
//      mChart.setCenterTextTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Light.ttf"));
        mChart.setUsePercentValues(false);
        mChart.setCenterText("Quarterly\nRevenue");
        mChart.setCenterTextSize(22f);
         
        // radius of the center hole in percent of maximum radius
        mChart.setHoleRadius(45f); 
        mChart.setTransparentCircleRadius(50f);
        // enable / disable drawing of x- and y-values
        //mChart.setDrawYValues(false);
        mChart.setDrawXValues(false);
        mChart.setData(generatePieData());
        
        Legend l = mChart.getLegend();
        l.setPosition(LegendPosition.PIECHART_CENTER);
        
        return v;
    }


    private PieData generatePieData() {
        String[] categories = getResources().getStringArray(R.array.categories);

        ArrayList<Entry> entries1 = new ArrayList<Entry>(); // значения
        ArrayList<String> xVals = new ArrayList<String>(); // названия категорий
        Map<Integer, Float> data = DB.getMonth(getActivity(), Utils.getTimestamps()[2]);
        for (Integer key: data.keySet()) {
            xVals.add(categories[key]);
            entries1.add(new Entry(data.get(key), key));
        }

        PieDataSet ds1 = new PieDataSet(entries1, null); // второй параметр это описание легенды
        ds1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        ds1.setSliceSpace(2f);

        PieData d = new PieData(xVals, ds1);
        return d;
    }

}
