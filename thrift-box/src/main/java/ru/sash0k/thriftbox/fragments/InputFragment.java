package ru.sash0k.thriftbox.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import ru.sash0k.thriftbox.MainActivity;
import ru.sash0k.thriftbox.R;
import ru.sash0k.thriftbox.Utils;
import ru.sash0k.thriftbox.categories.Categories;
import ru.sash0k.thriftbox.database.DB;

/**
 * Ввод сумм
 * Created by saash0k on 28.04.14.
 */
public class InputFragment extends Fragment {
    private static final String TAG = "InputFragment";

    private TextView valueTV;
    private TextView commentTV;
    private Categories categories;

    public static InputFragment newInstance() {
        return new InputFragment();
    }
    // ============================================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        return inflater.inflate(R.layout.fragment_input, container, false);
    }
    // ============================================================================

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        final Activity context = getActivity();
        Toolbar toolbar = (Toolbar) context.findViewById(R.id.toolbar);

        valueTV = (TextView) toolbar.findViewById(R.id.enter_value);
        valueTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view instanceof TextView && ((TextView) view).length() != 0) {
                    showCommentDialog(commentTV.getText());
                }
            }
        });

        commentTV = (TextView) context.findViewById(R.id.comment_value);
        commentTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentDialog(((TextView)view).getText());
            }
        });

        categories = (Categories) context.findViewById(R.id.categories);
        if (state != null) {
            categories.setSelected(state.getInt(DB.CATEGORY));
            valueTV.setText(state.getCharSequence(DB.VALUE));
            CharSequence comment = state.getCharSequence(DB.COMMENT);
            commentTV.setText(comment);
        }

        FloatingActionButton fab = (FloatingActionButton) context.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterValue(context);
            }
        });

        // обработка длинного нажатия
        ImageButton clear = (ImageButton) context.findViewById(R.id.del);
        clear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((MainActivity)context).clearAnimation(view);
                return true;
            }
        });

    }
    // ============================================================================

    private void enterValue(Context context) {
        if (valueTV != null) {
            final String textValue = valueTV.getText().toString();
            final int value = Utils.parseCurrency(textValue);
            if (value > 0) {
                final String comment = commentTV.getText().toString();
                DB.INSTANCE.insertItem(value, categories.getSelected(), comment);
                Toast.makeText(context, ((MainActivity)context).parseRouble(getString(R.string.enter_value_done) + " " + textValue + Utils.ROUBLE + " " + comment), Toast.LENGTH_SHORT).show();
                cleanValues();
                Utils.updateWidgets(context);
            } else
                Toast.makeText(context, getString(R.string.enter_value_invalid), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putCharSequence(DB.VALUE, valueTV.getText());
        state.putCharSequence(DB.COMMENT, commentTV.getText());
        state.putInt(DB.CATEGORY, categories.getSelected());
    }
    // ============================================================================


    /**
     * Получение текста комментария из диалога
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CommentDialog.CODE) {
            if (resultCode == DialogInterface.BUTTON_POSITIVE) {
                final String comment = data.getStringExtra(CommentDialog.TAG);
                if ((comment != null) && (!comment.isEmpty())) {
                    commentTV.setText(comment);
                } else commentTV.setText("");
            }
        }
    }
    // ============================================================================

    /**
     * Диалог редактирования комментария
     * @param value - значение
     */
    private void showCommentDialog(CharSequence value) {
        CommentDialog dialog = CommentDialog.newInstance(value);
        dialog.setTargetFragment(InputFragment.this, CommentDialog.CODE);
        dialog.show(getFragmentManager(), CommentDialog.TAG);
    }
    // ============================================================================


    /**
     * Очистить значение поля ввода и комментария
     */
    private void cleanValues() {
        valueTV.setText("");
        commentTV.setText("");
    }
    // ============================================================================

}
