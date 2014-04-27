package ru.sash0k.thriftbox;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ru.sash0k.thriftbox.database.DB;

public class InputActivity extends Activity {

    private TextView valueTV;
    private Categories categories;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_input);
        valueTV = (TextView) findViewById(R.id.enter_value);
        categories = (Categories) findViewById(R.id.categories);

        if (state != null) {
          valueTV.setText(state.getString(DB.VALUE));
          categories.setSelected(state.getInt(DB.CATEGORY));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString(DB.VALUE, valueTV.getText().toString());
        state.putInt(DB.CATEGORY, categories.getSelected());
    }

    /**
     * Обработка ввода виртуальной клавиатуры
     */
    public void virtualKeyboardClick(View view) {
        final String digit = view.getTag().toString();
        if (valueTV != null) {
            String query = valueTV.getText().toString();
            if (getString(R.string.backspace).equals(digit)) {
                final int len = query.length() - 1;
                query = query.substring(0, (len < 0) ? 0 : len);
            } else query += digit;
            valueTV.setText(query);
        }
    }
    // ============================================================================

    /**
     * Внесение суммы
     *
     * @param view
     */
    public void enterClick(View view) {
        if (valueTV != null) {
            final String textValue = valueTV.getText().toString();
            final int value = Utils.parseCurrency(textValue);
            if (value > 0) {
                DB.insertItem(this, value, categories.getSelected());
                Toast.makeText(this, getString(R.string.enter_value_done) + " " + textValue + " " + getString(R.string.ruble) + ".", Toast.LENGTH_SHORT).show();
                valueTV.setText("");
                // update widget
                Intent intent = new Intent(this, Widget.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), Widget.class));
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                sendBroadcast(intent);

            } else
                Toast.makeText(this, getString(R.string.enter_value_invalid), Toast.LENGTH_SHORT).show();
        }
    }
    // ============================================================================

    /**
     * Очистка введённого значения
     */
    public void clearClick(View view) {
        ((TextView) view).setText("");
    }
    // ============================================================================
}
