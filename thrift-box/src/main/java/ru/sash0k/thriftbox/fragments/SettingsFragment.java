package ru.sash0k.thriftbox.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.preference.PreferenceFragment;

import ru.sash0k.thriftbox.R;
import ru.sash0k.thriftbox.Utils;

/**
 * Настройки приложения
 * Created by sash0k on 20.06.14.
 */
public final class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = "SettingsFragment";

    /**
     * Ключи настроек
     */
    public static final String PREF_WIDGET_TRANSPARENCY_KEY = "pref_widget_transparency_key";
    public static final String PREF_WIDGET_TEXT_COLOR_KEY = "pref_widget_text_color_key";


    public static SettingsFragment newInstance() {
        SettingsFragment f = new SettingsFragment();
        //Bundle arguments = new Bundle();
        //arguments.putBoolean(TAG, widgetMode);
        //f.setArguments(arguments);
        return f;
    }
    // ============================================================================

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // Обработчик на изменение настроек
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);
    }
    // ============================================================================

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        getListValues(PREF_WIDGET_TRANSPARENCY_KEY,
                R.array.widget_transparency_title, R.array.widget_transparency_value);
    }
    // ============================================================================

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String tag) {
        setPrefenceTitle(tag);
        final Context context = SettingsFragment.this.getActivity();
        if ((context != null) && (tag.contains("widget"))) Utils.updateWidgets(context);
    }
    // ============================================================================

    /**
     * Установка заголовка списка
     */
    private void setPrefenceTitle(String tag) {
        final Preference preference = findPreference(tag);
        if (preference instanceof ListPreference) {
            if (((ListPreference) preference).getEntry() != null) {
                final CharSequence title = ((ListPreference) preference).getEntry();
                preference.setSummary(title);
            }

        } else if (preference instanceof EditTextPreference) {
            preference.setTitle(((EditTextPreference) preference).getText());
        }
    }
    // ============================================================================

    /**
     * Заполнение списков
     */
    private void getListValues(String tag, int titlesId, int valuesId) {
        ListPreference listprefence = (ListPreference) findPreference(tag);
        if (listprefence != null) {
            listprefence.setEntries(titlesId);
            listprefence.setEntryValues(valuesId);
        }
        setPrefenceTitle(tag);
    }
    // ============================================================================

}
