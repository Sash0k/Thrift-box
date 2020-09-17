package ru.sash0k.thriftbox.fragments;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.sash0k.thriftbox.R;
import ru.sash0k.thriftbox.StatisticsActivity;
import ru.sash0k.thriftbox.database.DB;
import ru.sash0k.thriftbox.ExpensesAdapter;

/**
 * Список расходов
 * Created by sash0k on 28.04.14.
 */
public class ExpensesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ExpensesFragment";
    private static final int LOADER_ID = -1;

    private RecyclerView recyclerView;
    private TextView emptyView;

    // Адаптер списка
    protected ExpensesAdapter mAdapter;

    public static ExpensesFragment newInstance() {
        ExpensesFragment f = new ExpensesFragment();
        //Bundle arguments = new Bundle();
        //arguments.putBoolean(TAG, widgetMode);
        //f.setArguments(arguments);
        return f;
    }
    // ============================================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ExpensesAdapter(getActivity());
    }
    // ============================================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View rootView = inflater.inflate(R.layout.fragment_expenses, container, false);
        if (rootView != null) {
            Context context = getActivity();
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);

            emptyView = rootView.findViewById(R.id.expenses_empty_view);

            recyclerView = rootView.findViewById(R.id.expenses_recycler);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(mAdapter);

            FloatingActionButton fab = rootView.findViewById(R.id.expenses_fab);
            fab.setOnClickListener(view -> {
                Intent i = new Intent();
                i.setClass(getActivity(), StatisticsActivity.class);
                startActivity(i);
            });

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx,int dy){
                    super.onScrolled(recyclerView, dx, dy);

                    if (dy > 0) {
                        if (fab.isShown()) {
                            fab.hide();
                        }
                    }
                    else if (dy < 0) {
                        if (!fab.isShown()) {
                            fab.show();
                        }
                    }
                }
            });
        }

        return rootView;
    }
    // ============================================================================

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        setHasOptionsMenu(false);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }
    // ============================================================================

//    @Override
//    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//        super.onChildClick(parent, v, groupPosition, childPosition, id);
//        Cursor cursor = ((AdapterExpenses) getListAdapter()).getChild(groupPosition, childPosition);
//        final int row_id = cursor.getInt(cursor.getColumnIndex(DB.ID));
//        final String date = cursor.getString(cursor.getColumnIndex(DB.DATE));
//        final int category = cursor.getInt(cursor.getColumnIndex(DB.CATEGORY));
//        final long value = cursor.getLong(cursor.getColumnIndex(DB.VALUE));
//        //cursor.close(); TODO?
//        Utils.log("onChildClick id = " + row_id);
//
//        DeleteConfirmDialog dialog = DeleteConfirmDialog.newInstance(row_id, date, category, value);
//        dialog.show(getFragmentManager(), DeleteConfirmDialog.TAG);
//        return true;
//    }
//    // ============================================================================

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] columns = {DB.ID, "SUM(" + DB.VALUE + ") AS " + DB.VALUE, DB.DATE};
        final String selection = DB.DATE + " = " + DB.DATE + " GROUP BY (" + DB.DATE + ")"; // hack: GROUP BY
        return new DataLoader(getActivity(), DB.EXPENSES_VIEW, columns, selection, DB.TIMESTAMP + " DESC");
    }
    // ============================================================================

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.setGroupCursor(data);

        if (mAdapter.getGroupCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
    // ============================================================================

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setGroupCursor(null);
    }
    // ============================================================================

    static class DataLoader extends AsyncTaskLoader<Cursor> {
        private final String table;
        private final String[] columns;
        private final String selection;
        private final String order;

        public DataLoader(Context context, String table, String[] columns, String selection, String order) {
            super(context);
            this.table = table;
            this.columns = columns;
            this.selection = selection;
            this.order = order;
        }

        @Override
        public Cursor loadInBackground() {
            return DB.INSTANCE.getReadableDatabase().query(table, columns, selection, null, null, null, order);
        }
    }
}