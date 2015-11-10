package ru.sash0k.thriftbox.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.sash0k.thriftbox.FinanceFormatter;
import ru.sash0k.thriftbox.R;
import ru.sash0k.thriftbox.Utils;
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
        month.setText(SDF_MONTH.format(new Date(ts*1000)));

        // отображение статистики за текущий месяц
        BarChart barChart = (BarChart) v.findViewById(R.id.bar_chart);
        barChart.setDescription("");
        barChart.setData(generateMonthlyData());
        barChart.getXAxis().disableGridDashedLine();

        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);

        barChart.getAxisRight().removeAllLimitLines();
        barChart.getLegend().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setValueFormatter(new FinanceFormatter());
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.getXAxis().setEnabled(false); // горизонтальные

        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
        barChart.setDoubleTapToZoomEnabled(false);


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
        ArrayList<BarEntry> entries = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            entries.add(new BarEntry(stats.get(i), i, bars[i]));
        }

        // данные графика
        BarDataSet ds = new BarDataSet(entries, null);
        ds.setDrawValues(true);

        final int[] THEME_COLORS = {
                getResources().getColor(R.color.accent),
                getResources().getColor(R.color.accent_dark)};
        ds.setColors(THEME_COLORS);


        // подписи к линиям графика
        BarData d = new BarData(bars, ds);
        d.setValueTextColor(getResources().getColor(R.color.primary));
        d.setValueTextSize(12f);
        d.setValueFormatter(new FinanceFormatter());
        return d;
    }
    // ============================================================================

}
