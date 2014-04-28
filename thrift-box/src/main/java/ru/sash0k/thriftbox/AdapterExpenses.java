package ru.sash0k.thriftbox;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.sash0k.thriftbox.database.DB;

/**
 * Адаптер списка расходов
 * Created by sash0k on 28.04.14.
 */
public class AdapterExpenses extends CursorAdapter {
    private static final String divider = " — ";
    private final LayoutInflater mInflater;
    private final String[] categories;

    public AdapterExpenses(Context context) {
        super(context, null, 0);
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.categories = context.getResources().getStringArray(R.array.categories);
    }
    // ============================================================================

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.listitem_expense, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.date = (TextView) view.findViewById(R.id.expense_date);
        viewHolder.category = (TextView) view.findViewById(R.id.expense_category);
        viewHolder.value = (TextView) view.findViewById(R.id.expense_value);

        viewHolder.date_col = cursor.getColumnIndexOrThrow(DB.DATE);
        viewHolder.category_col = cursor.getColumnIndexOrThrow(DB.CATEGORY);
        viewHolder.value_col = cursor.getColumnIndexOrThrow(DB.VALUE);
        view.setTag(viewHolder);
        return view;
    }
    // ============================================================================

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.date.setText(cursor.getString(viewHolder.date_col));
        viewHolder.category.setText(divider + categories[cursor.getInt(viewHolder.category_col)]);
        viewHolder.value.setText(Utils.formatValue(cursor.getLong(viewHolder.value_col)) + context.getString(R.string.ruble));
    }
    // ============================================================================

    @Override
    public String getItem(int position) {
        String result = null;
        Cursor cursor = getCursor();
        if ((cursor != null) && (cursor.moveToPosition(position))) {
            result = cursor.getString(cursor.getColumnIndex(DB.VALUE));
            cursor.close();
        }
        return result;
    }
    // ============================================================================

    private static class ViewHolder {
        private TextView date;
        private TextView category;
        private TextView value;
        private int date_col, category_col, value_col;
    }
    // ============================================================================
}
