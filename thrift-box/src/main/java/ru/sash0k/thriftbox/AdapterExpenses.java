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
    private final LayoutInflater mInflater;

    public AdapterExpenses(Context context) {
        super(context, null, 0);
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    // ============================================================================

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.listitem_expense, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder();

        viewHolder.value = (TextView) view.findViewById(R.id.expense_value);
        //viewHolder.mac = (TextView) view.findViewById(R.id.checkpoint_mac);
        //viewHolder.openButton = view.findViewById(R.id.checkpoint_open);

        viewHolder.value_col = cursor.getColumnIndexOrThrow(DB.VALUE);
        //viewHolder.mac_col = cursor.getColumnIndexOrThrow(DB.MAC);
        //viewHolder.auth_col = cursor.getColumnIndexOrThrow(DB.AUTH);
        view.setTag(viewHolder);
        return view;
    }
    // ============================================================================

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        //final String address = cursor.getString(viewHolder.mac_col);
        viewHolder.value.setText(Utils.formatValue(cursor.getLong(viewHolder.value_col)) + context.getString(R.string.ruble));
        //viewHolder.mac.setText(address);
        //viewHolder.openButton.setTag(R.id.address, address);
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
        public TextView value;
        int value_col;
    }
    // ============================================================================
}
