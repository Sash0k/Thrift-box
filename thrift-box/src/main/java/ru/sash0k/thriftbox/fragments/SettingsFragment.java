package ru.sash0k.thriftbox.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

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
    public static final String PREF_VERSION = "version";
    public static final String PREF_DEVELOPER = "developer";


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

        // Изменение цвета текста виджета
        findPreference(PREF_WIDGET_TEXT_COLOR_KEY).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final Context context = SettingsFragment.this.getActivity();
                if (context != null) Utils.updateWidgets(context);
                return true;
            }
        });

        // версия приложения
        String version;
        final Context c = getActivity();
        try {
            version = c.getString(R.string.version) + " " +
                    c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionName +
                    " " + c.getString(R.string.license_info);
        } catch (PackageManager.NameNotFoundException e) {
            version = "";
        }
        findPreference(PREF_VERSION).setSummary(version);

        // другие приложения разработчика
        findPreference(PREF_DEVELOPER).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri
                        .parse(c.getString(R.string.url_other_apps)));
                startActivity(intent);
                return true;
            }
        });
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
        if (PREF_WIDGET_TRANSPARENCY_KEY.equals(tag)) {
            setPrefenceTitle(tag);
            final Context context = SettingsFragment.this.getActivity();
            if (context != null) Utils.updateWidgets(context);
        }
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
