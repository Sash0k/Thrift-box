package ru.sash0k.thriftbox.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.widget.EditText;

import ru.sash0k.thriftbox.R;

/**
 * Ввод комментария к расходу
 * Created by sash0k on 25.08.14.
 */
public class CommentDialog extends DialogFragment {
    public static final String TAG = "CommentDialog";
    public static final int CODE = 1;
    private EditText editComment;

    public static CommentDialog newInstance(CharSequence value) {
        CommentDialog f = new CommentDialog();
        Bundle arguments = new Bundle();
        arguments.putCharSequence(TAG, value);
        f.setArguments(arguments);
        return f;
    }
    // ============================================================================

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        editComment = new EditText(context);
        editComment.setText(getArguments().getCharSequence(TAG));

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context)
                .setMessage(R.string.comment)
                .setView(editComment)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String value = editComment.getText().toString().trim();
                        Intent result = new Intent();
                        result.putExtra(TAG, value);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), which, result);
                    }
                })
                .setNegativeButton(android.R.string.no, null);
        return builder.create();
    }
    // ============================================================================
}
