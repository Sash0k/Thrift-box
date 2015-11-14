package ru.sash0k.thriftbox.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.sash0k.thriftbox.R;
import ru.sash0k.thriftbox.Utils;
import ru.sash0k.thriftbox.charting.FinanceFormatter;
import ru.sash0k.thriftbox.charting.MyMarkerView;
import ru.sash0k.thriftbox.database.DB;

/**
 * Отображение графиков
 * Created by sash0k on 03.07.15.
 */
public class ChartsFragment extends Fragment {
    private static final int ANIMATION_TIME = 1500;
    private static final DateFormat SDF_MONTH = new SimpleDateFormat("LLLL yyyy", new Locale("ru"));

    public static ChartsFragment newInstance() {
        ChartsFragment f = new ChartsFragment();
        Bundle arguments = new Bundle();
        // начало текущего месяца
        arguments.putLong(DB.TIMESTAMP, Utils.getTimestamps()[2]);
        f.setArguments(arguments);
        return f;
    }
    // ============================================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    // ============================================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.charts_fragment, container, false);

        TextView month = (TextView) v.findViewById(R.id.current_month);
        long ts = getArguments().getLong(DB.TIMESTAMP);
        month.setText(SDF_MONTH.format(new Date(ts * 1000)));

        // отображение статистики за текущий месяц
        BarChart barChart = (BarChart) v.findViewById(R.id.bar_chart);
        barChart.setDescription("");

        Paint mInfoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInfoPaint.setColor(getResources().getColor(R.color.primary));
        mInfoPaint.setTextAlign(Paint.Align.CENTER);
        mInfoPaint.setTextSize(com.github.mikephil.charting.utils.Utils.convertDpToPixel(16f));
        barChart.setPaint(mInfoPaint, Chart.PAINT_INFO);
        barChart.setNoDataText(getString(R.string.charts_no_data));

        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawValueAboveBar(false);

        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
        barChart.setDoubleTapToZoomEnabled(false);

        barChart.getXAxis().disableGridDashedLine();
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setEnabled(false);

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisLeft().setEnabled(false);

        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().removeAllLimitLines();
        barChart.getAxisRight().setValueFormatter(new FinanceFormatter());

        barChart.getLegend().setEnabled(false);
        barChart.setData(generateMonthlyData());
        // если данных нет, не показывать ось
        barChart.getAxisRight().setEnabled(!barChart.isEmpty());

        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);
        mv.setX(mv.getMeasuredWidth());
        barChart.setMarkerView(mv);


        Utils.log("getYMin = " + barChart.getBarData().getYMin());
        Utils.log("getYMax = " + barChart.getBarData().getYMax());

        barChart.animateY(ANIMATION_TIME);
        return v;
    }
    // ============================================================================

    /**
     * Заполнение графика данными
     */
    private BarData generateMonthlyData() {
        final Context ctx = getActivity();

        // получение категорий
        final String[] bars = ctx.getResources().getStringArray(R.array.categories);

        // получение данных
        final long month = getArguments().getLong(DB.TIMESTAMP);
        List<Float> stats = DB.getStatData(ctx, month, bars.length); // Статистика за текущий месяц

        final int count = stats.size();
        ArrayList<BarEntry> entries = new ArrayList<>();
        final ArrayList<String> usedBars = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < count; i++) {
            float value = stats.get(i);
            if (value > 0f) {
                usedBars.add(bars[i]);
                entries.add(new BarEntry(value, j, bars[i]));
                j++;
            }
        }

        // данные графика
        BarDataSet ds = new BarDataSet(entries, null);
        ds.setDrawValues(true);
        ds.setColor(getResources().getColor(R.color.accent));

        // подписи к линиям графика
        BarData d = new BarData(usedBars, ds);
        d.setValueTextColor(getResources().getColor(R.color.primary_text));
        d.setValueTextSize(14f);
        d.setValueFormatter(new FinanceFormatter());
        return d;
    }
    // ============================================================================

}
