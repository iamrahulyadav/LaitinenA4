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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set default values in the app's SharedPreferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // register listener for SharedPreferences changes
        PreferenceManager.getDefaultSharedPreferences(this).
                registerOnSharedPreferenceChangeListener(
                        preferencesChangeListener);

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

        // if app just started, show view with Play button
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

    // show menu if app is running on a phone or a portrait-oriented tablet
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // get the device's current orientation
        int orientation = getResources().getConfiguration().orientation;

        // display the app's menu only in portrait orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // inflate the menu
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }
        else
            return false;
    }

    // displays the SettingsActivity when running on a phone
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        startActivity(preferencesIntent);
        return super.onOptionsItemSelected(item);
    }

    // listener for changes to the app's SharedPreferences
    private OnSharedPreferenceChangeListener preferencesChangeListener =
            new OnSharedPreferenceChangeListener() {
                // called when the user changes the app's preferences
                @Override
                public void onSharedPreferenceChanged(
                        SharedPreferences sharedPreferences, String key) {
                    preferencesChanged = true; // user changed app setting

                    MainActivityFragment quizFragment = (MainActivityFragment)
                            getSupportFragmentManager().findFragmentById(
                                    R.id.guessFragment);

                    if (key.equals(CHOICES)) { // # of choices to display changed
                        quizFragment.updateGuessRows(sharedPreferences);
                        quizFragment.resetQuiz();
                    }
                    else if (key.equals(TYPES)) { // regions to include changed
                        Set<String> types =
                                sharedPreferences.getStringSet(TYPES, null);

                        if (types != null && types.size() > 0) {
                            quizFragment.updateTypes(sharedPreferences);
                            quizFragment.resetQuiz();
                        }
                        else {
                            // must select one region--set North America as default
                            SharedPreferences.Editor editor =
                                    sharedPreferences.edit();
                            types.add(getString(R.string.default_type));
                            editor.putStringSet(TYPES, types);
                            editor.apply();

                            Toast.makeText(MainActivity.this,
                                    R.string.default_animal_type_message,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if (key.equals(SOUND)) {
                        quizFragment.updateSound(sharedPreferences);
                    }
                    else if (key.equals(QUESTIONS)) {
                        quizFragment.updatenOfQuestions(sharedPreferences);
                    }

                    Toast.makeText(MainActivity.this,
                            R.string.restarting_quiz,
                            Toast.LENGTH_SHORT).show();
                }
            };
}
