package ru.sash0k.thriftbox;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ru.sash0k.thriftbox.database.DB;

public class InputActivity extends Activity {

    private TextView valueTV;
    private Categories categories;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.fragment_input);
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
     * Очистка введённого значения
     */
    public void clearClick(View view) {
        ((TextView) view).setText("");
    }
    // ============================================================================
}
