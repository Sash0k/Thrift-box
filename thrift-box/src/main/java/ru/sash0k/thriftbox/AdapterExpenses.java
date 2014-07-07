package ru.sash0k.thriftbox;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget_fixed.CursorTreeAdapter;

import ru.sash0k.thriftbox.database.DB;

/**
 * Адаптер списка расходов
 * Created by sash0k on 28.04.14.
 */
public class AdapterExpenses extends CursorTreeAdapter {
    private static final int TOKEN_CHILD = 1;
    private final QueryHandler mQueryHandler;

    private final LayoutInflater mInflater;
    private final String[] categories;

    public AdapterExpenses(Context context) {
        super(null, context, true);
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.categories = context.getResources().getStringArray(R.array.categories);
        this.mQueryHandler = new QueryHandler(context, this);
    }
    // ============================================================================

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean b, ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.listitem_expense, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.date = (TextView) view.findViewById(R.id.expense_date);
        viewHolder.value = (TextView) view.findViewById(R.id.expense_value);
        viewHolder.date_col = cursor.getColumnIndexOrThrow(DB.DATE);
        viewHolder.value_col = cursor.getColumnIndexOrThrow(DB.VALUE);
        view.setTag(viewHolder);
        return view;
    }
    // ============================================================================

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean b) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.date.setText(cursor.getString(viewHolder.date_col));
        viewHolder.value.setText(Utils.formatValue(cursor.getLong(viewHolder.value_col)) + context.getString(R.string.ruble));
    }
    // ============================================================================

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean b, ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.listitem_expense, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.category = (TextView) view.findViewById(R.id.expense_category);
        viewHolder.value = (TextView) view.findViewById(R.id.expense_value);

        viewHolder.category_col = cursor.getColumnIndexOrThrow(DB.CATEGORY);
        viewHolder.value_col = cursor.getColumnIndexOrThrow(DB.VALUE);
        view.setTag(viewHolder);
        return view;
    }
    // ============================================================================

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean b) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.category.setText(categories[cursor.getInt(viewHolder.category_col)]);
        viewHolder.value.setText(Utils.formatValue(cursor.getLong(viewHolder.value_col)) + context.getString(R.string.ruble));
    }
    // ============================================================================

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        final String date = groupCursor.getString(groupCursor.getColumnIndex(DB.DATE));
        mQueryHandler.startQuery(TOKEN_CHILD, groupCursor.getPosition(),
                DB.getUri(DB.EXPENSES_VIEW), null, DB.DATE + "=?", new String[]{date}, DB.TIMESTAMP + " DESC");
        return null;
    }
    // ============================================================================

    private static final class QueryHandler extends AsyncQueryHandler {
        private CursorTreeAdapter mAdapter;

        public QueryHandler(Context context, CursorTreeAdapter adapter) {
            super(context.getContentResolver());
            this.mAdapter = adapter;
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (token == TOKEN_CHILD) {
                if (cursor != null && cursor.moveToFirst()) {
                    Utils.log("setChildrenCursor count:" + cursor.getCount());
                    int groupPosition = (Integer) cookie;
                    mAdapter.setChildrenCursor(groupPosition, cursor);
                }
            } else {
                if (cursor != null && cursor.moveToFirst()) {
                    Utils.log("setGroupCursor count:" + cursor.getCount());
                    mAdapter.setGroupCursor(cursor);
                }
            }
        }
    }
    // ============================================================================

    private static class ViewHolder {
        private TextView category;
        private TextView date;
        private TextView value;
        private int date_col, category_col, value_col;
    }
    // ============================================================================
}
