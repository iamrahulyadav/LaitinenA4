package edu.sdccd.laitinena4;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Tuulikki Laitinen on 4/4/2017.
 */

public class SettingsActivityFragment extends PreferenceFragment {
    // creates preferences GUI from preferences.xml file in res/xml
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.preferences); // load from XML

    }
}
