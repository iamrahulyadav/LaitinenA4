package edu.sdccd.laitinena4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    // keys for reading data from SharedPreferences
    public static final String SOUND = "pref_soundOnOff";

    private boolean phoneDevice = true; // used to force potrait mode
    private boolean preferencesChanged = true; // did preferences change

    private Button buttonStart;
    private Intent intent;

    private boolean soundOn;
    private SoundPool soundPool;
    private boolean loaded = false;
    private final String startSound = "sheep";
    private int startSoundID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //check if sound is on or off
        updateSound(
                PreferenceManager.getDefaultSharedPreferences(this));

        //load sound
        AudioAttributes audioAttrib = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder().setAudioAttributes(audioAttrib).setMaxStreams(6).build();

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
                //MainActivityFragment.this.animalSoundMap.put(MainActivityFragment.this.soundName, sampleId);
                //semaphore.release();

            }
        });
        loadSoundToSoundPool(startSound);

        //set listener to button
        buttonStart = (Button)findViewById(R.id.buttonPlay);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //play sound
                if (StartActivity.this.soundOn == true) {
                    playSound(StartActivity.this.startSoundID);
                }

                // start main activity
                intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

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

    /*
* Load sound to pool
* */
    private void loadSoundToSoundPool(String name) {

        int resourceID = getResources().getIdentifier(name, "raw", getApplicationContext().getPackageName());
        this.startSoundID = soundPool.load(getApplicationContext(), resourceID, 1);

    }


    /**
     * playSound. soundid should not be -1
     *
     * @param soundID
     */
    public void playSound(int soundID) {

        int streamID = 0;

        if (soundID != -1) {

            do {
                streamID = soundPool.play(soundID, 1, 1, 1, 0, 1f);
            } while (streamID == 0);
        }
    }

    // displays the SettingsActivity when running on a phone
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        startActivity(preferencesIntent);
        return super.onOptionsItemSelected(item);
    }

    // listener for changes to the app's SharedPreferences
    private SharedPreferences.OnSharedPreferenceChangeListener preferencesChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                // called when the user changes the app's preferences
                @Override
                public void onSharedPreferenceChanged(
                        SharedPreferences sharedPreferences, String key) {
                    preferencesChanged = true; // user changed app setting

                    if (key.equals(SOUND)) {
                        StartActivity.this.updateSound(sharedPreferences);
                    }
                }
            };

    private void updateSound(SharedPreferences sharedPreferences) {

        String soundOnOff =
                sharedPreferences.getString(MainActivity.SOUND, null);

        if (soundOnOff.equals("On")) {
            this.soundOn = true;
        }
        else {
            this.soundOn = false;
        }
    }
}
