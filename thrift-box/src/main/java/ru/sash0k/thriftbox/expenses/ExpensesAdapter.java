package ru.sash0k.thriftbox.expenses;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.sash0k.thriftbox.MainActivity;
import ru.sash0k.thriftbox.R;
import ru.sash0k.thriftbox.Utils;
import ru.sash0k.thriftbox.database.DB;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ViewHolder> {
    private final Context context;
    private Cursor dataCursor;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView date;
        private final TextView value;

        ViewHolder(View v) {
            super(v);
            date = v.findViewById(R.id.expense_date);
            value = v.findViewById(R.id.expense_value);
        }
    }

    public ExpensesAdapter(Activity mContext, Cursor cursor) {
        dataCursor = cursor;
        context = mContext;
    }

    @Override
    public ExpensesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_expense, parent, false);
        return new ViewHolder(view);
    }

    public Cursor swapCursor(Cursor cursor) {
        if (dataCursor == cursor) {
            return null;
        }
        Cursor oldCursor = dataCursor;
        this.dataCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        dataCursor.moveToPosition(position);

        String date = dataCursor.getString(dataCursor.getColumnIndex(DB.DATE));
        long value = dataCursor.getLong(dataCursor.getColumnIndex(DB.VALUE));

        holder.date.setText(date);
        holder.value.setText(((MainActivity)context).parseRouble(Utils.formatValue(value) + Utils.ROUBLE));
    }

    @Override
    public int getItemCount() {
        return (dataCursor == null) ? 0 : dataCursor.getCount();
    }
}
