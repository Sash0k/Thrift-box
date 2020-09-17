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

import java.util.ArrayList;
import java.util.List;

import ru.sash0k.thriftbox.ExpensesAdapter;
import ru.sash0k.thriftbox.R;
import ru.sash0k.thriftbox.StatisticsActivity;
import ru.sash0k.thriftbox.database.DB;
import ru.sash0k.thriftbox.ExpensesDetail;
import ru.sash0k.thriftbox.ExpensesGroup;

/**
 * Список расходов
 * Created by sash0k on 28.04.14.
 */
public class ExpensesFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<ExpensesGroup>>, ExpensesAdapter.ChildClickListener {
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
        getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
    }
    // ============================================================================

    @Override
    public void onChildClick(ExpensesDetail item) {
        DeleteConfirmDialog dialog = DeleteConfirmDialog.newInstance(item.getId(), item.getDate(), item.getCategory(), item.getValue());
        dialog.show(getFragmentManager(), DeleteConfirmDialog.TAG);
    }
    // ============================================================================

    @Override
    public Loader<List<ExpensesGroup>> onCreateLoader(int id, Bundle args) {
        return new GroupLoader(getActivity());
    }
    // ============================================================================

    @Override
    public void onLoadFinished(Loader<List<ExpensesGroup>> loader, List<ExpensesGroup> data) {
        mAdapter = new ExpensesAdapter(data, this);
        recyclerView.setAdapter(mAdapter);

        if (mAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
    // ============================================================================

    @Override
    public void onLoaderReset(Loader<List<ExpensesGroup>> loader) {
        mAdapter = null;
    }
    // ============================================================================

    static class GroupLoader extends AsyncTaskLoader<List<ExpensesGroup>> {
        final String table = DB.EXPENSES_VIEW;
        final String[] columns = {DB.ID, "SUM(" + DB.VALUE + ") AS " + DB.VALUE, DB.DATE};
        final String selection = DB.DATE + " = " + DB.DATE + " GROUP BY (" + DB.DATE + ")"; // hack: GROUP BY
        final String order = DB.TIMESTAMP + " DESC";

        public GroupLoader(Context context) {
            super(context);
        }

        @Override
        public List<ExpensesGroup> loadInBackground() {
            List<ExpensesGroup> result = new ArrayList<>();
            Cursor cursor = DB.INSTANCE.getReadableDatabase().query(table, columns, selection, null, null, null, order);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    ExpensesGroup item = new ExpensesGroup(
                            cursor.getString(cursor.getColumnIndex(DB.DATE)),
                            cursor.getLong(cursor.getColumnIndex(DB.VALUE))
                    );
                    result.add(item);
                }
                cursor.close();
            }
            return result;
        }
    }
}