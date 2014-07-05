package ru.sash0k.thriftbox.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.ContextThemeWrapper;

import ru.sash0k.thriftbox.R;
import ru.sash0k.thriftbox.Utils;
import ru.sash0k.thriftbox.database.DB;

/**
 * Диалог подтверждения удаления
 * Created by sash0k on 11.03.14.
 */
public class DeleteConfirmDialog extends DialogFragment {
    public static final String TAG = "DeleteConfirmDialog";
    private static final String divider = " — ";

    public static DeleteConfirmDialog newInstance(int id, String date, int category, long value) {
        DeleteConfirmDialog f = new DeleteConfirmDialog();
        Bundle arguments = new Bundle();
        arguments.putInt(BaseColumns._ID, id);
        arguments.putString(DB.DATE, date);
        arguments.putInt(DB.CATEGORY, category);
        arguments.putLong(DB.VALUE, value);
        f.setArguments(arguments);
        return f;
    }
    // ============================================================================

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        String[] categories = context.getResources().getStringArray(R.array.categories);

        final Bundle args = getArguments();
        final int id = args.getInt(BaseColumns._ID);
        final String date = args.getString(DB.DATE);
        final int category = args.getInt(DB.CATEGORY);
        final long value = args.getLong(DB.VALUE);
        final String msg = context.getString(R.string.delete_msg) +
                "<br><b>" + date + divider + categories[category] +
                divider + Utils.formatValue(value) + context.getString(R.string.ruble) + "</b>?";

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context)
                .setTitle(R.string.delete_label)
                .setMessage(Html.fromHtml(msg))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DB.deleteItem(context, id);
                        Utils.updateWidgets(context);
                    }
                })
                .setNegativeButton(android.R.string.no, null);
        return builder.create();
    }
    // ============================================================================

}
