package edu.sdccd.laitinena4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // keys for reading data from SharedPreferences
    public static final String CHOICES = "pref_numberOfChoices";
    public static final String TYPES = "pref_typesToInclude";
    public static final String SOUND = "pref_soundOnOff";
    public static final String QUESTIONS = "pref_nOfQuestions";

    private boolean phoneDevice = true; // used to force potrait mode
    private boolean preferencesChanged = true; // did preferences change

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set default values in the app's SharedPreferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // determine screen size
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        // if device is a tablet, set phoneDevice to false
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
            screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {

            phoneDevice = false;
        }

        // if running on phone-sized device, allow only potrait orientation
        if (phoneDevice == true) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (preferencesChanged == true ) {

            preferencesChanged = false; // don't need this flag anymore

            //startFragment
            MainActivityFragment guessFragment = (MainActivityFragment)
                                                  getSupportFragmentManager().findFragmentById(
                                                  R.id.guessFragment);

            guessFragment.updateGuessRows(
                    PreferenceManager.getDefaultSharedPreferences(this));
            guessFragment.updateTypes(
                    PreferenceManager.getDefaultSharedPreferences(this));
            guessFragment.updateSound(
                    PreferenceManager.getDefaultSharedPreferences(this));
            guessFragment.updatenOfQuestions(
                    PreferenceManager.getDefaultSharedPreferences(this));

            guessFragment.resetQuiz();
        }

    }
}
