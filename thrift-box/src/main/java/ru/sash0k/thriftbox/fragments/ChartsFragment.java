package ru.sash0k.thriftbox.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import ru.sash0k.thriftbox.R;
import ru.sash0k.thriftbox.Utils;
import ru.sash0k.thriftbox.database.DB;

/**
 * Отображение графиков
 * Created by sash0k on 03.07.15.
 */
public class ChartsFragment extends Fragment {
    private static final int ANIMATION_TIME = 1500;

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

        // горизонтально - отображение статистики за текущий месяц
        BarChart hBarChart = (BarChart) v.findViewById(R.id.hor_bar_chart);
        setupBarChartView(hBarChart);
        hBarChart.setData(generateMonthlyData());
        hBarChart.animateY(ANIMATION_TIME);
/*
        // вертикально - вся статистика по месяцам
        BarChart barChart = (BarChart) v.findViewById(R.id.bar_chart);
        barChart.setDescription("");
        barChart.setNoDataText(getString(R.string.charts_no_data));
        //barChart.setData(generateAllData());
        barChart.getXAxis().disableGridDashedLine();
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(true);
        //barChart.getAxisLeft().setValueFormatter(new FinanceFormatter());

        barChart.animateY(ANIMATION_TIME);
*/
        return v;
    }
    // ============================================================================

    /**
     * Расходы за месяц - настройка внешнего вида графика
     */
    private void setupBarChartView(BarChart barChart) {
        barChart.setNoDataText(getString(R.string.charts_no_data));

        //setDrawValuesForWholeStack(false); // true
        barChart.setDrawValueAboveBar(false);
        barChart.setPinchZoom(false);
        //barChart.setHighlightEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setScaleEnabled(false);
        barChart.setDoubleTapToZoomEnabled(false);

        barChart.getLegend().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false); // сверху
        barChart.getAxisRight().setEnabled(false); // снизу
        barChart.getXAxis().setEnabled(false); // горизонтальные
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
        ds.setColors(ColorTemplate.JOYFUL_COLORS);


        final int[] THEME_COLORS = {
                getResources().getColor(R.color.accent),
                getResources().getColor(R.color.primary)};
        ds.setColors(THEME_COLORS);


        // подписи к линиям графика
        BarData d = new BarData(bars, ds);
        //d.setValueTextColor(Color.WHITE);
        d.setValueTextSize(12f);
        //d.setValueFormatter(new FinanceFormatter());
        return d;
    }
    // ============================================================================

    /**
     * Заполнение графика данными (вся статистика)
     */
//    private BarData generateAllData() {
//        final Context ctx = getActivity();
//
//        // получение списка месяцев {рассматриваемого периода}
//        long first_timestamp = DB.getFirstTimestamp(ctx, card);
//        final long now = System.currentTimeMillis();
//        List<Long> months = getMonths(first_timestamp, now);
//
//        List<BarDataSet> data = new ArrayList<>();
//        data.add(fillGroup(months, DB.getStatsByType(ctx, card, DB.TYPE_PLUS), 2, R.color.primary));
//        data.add(fillGroup(months, DB.getStatsByType(ctx, card, DB.TYPE_BUY), 1, R.color.accent));
//        data.add(fillGroup(months, DB.getStatsByType(ctx, card, DB.TYPE_CASH), 0, R.color.primary_text));
//
//        // привести месяцы к читаемому виду
//        final SimpleDateFormat SDF_MONTH = new SimpleDateFormat("MM.yy");
//        List<String> months_names = new ArrayList<>(months.size());
//        for (long month : months) months_names.add(SDF_MONTH.format(month));
//
//        BarData d = new BarData(months_names, data);
//        d.setDrawValues(false);
//        return d;
//    }
    // ============================================================================


}
