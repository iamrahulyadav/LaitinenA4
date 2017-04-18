package edu.sdccd.laitinena4;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import android.support.v4.app.DialogFragment;
import android.widget.Toast;

/**
 * Created by Tuulikki Laitinen on 4/4/2017.
 */

public class MainActivityFragment extends Fragment implements CustomFieldsFragmentAlertDialog {

    //String used for logging error messages
    private static final String TAG = "AnimalQuiz Activity";

    //private static final int ANIMALS_IN_QUIZ = 2;

    private List<String> fileNameList; // animal file names
    private List<String> quizAnimalsList; // animals in current quiz
    private Set<String> typesSet; // animal types in current quiz
    private String correctAnswer; // correct animal for the current picture
    private int totalGuesses; // number of guesses made
    private int correctAnswers; // number of correct guesses
    private int guessRows; // number of rows displaying guess Buttons
    private SecureRandom random; // used to randomize the quiz
    private Handler handler; // used to delay loading next picture
    private Animation shakeAnimation; // animation for incorrect guess
    private Animation correctAnimation; // animation for correct guess

    private LinearLayout quizLinearLayout; // layout that contains the quiz
    private TextView questionNumberTextView; // shows current question #
    private ImageView animalImageView; // displays a picture of animal
    private LinearLayout[] guessLinearLayouts; // rows of answer Buttons
    private TextView answerTextView; // displays correct answer

    private SoundPool soundPool;
    private int wrongSoundID;
    private Map<String, Integer> animalSoundMap;
    private boolean loaded = false;
    String soundName;
    public static final Semaphore semaphore = new Semaphore(0);
    private boolean soundOn = true;
    private static final String wrong = "wrong";
    private int numberOfQuestions = 10;

    //MainActivityFragment View is created and configured
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        fileNameList = new ArrayList<>();
        quizAnimalsList = new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();

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

        //load wrong sound to soundPool
        animalSoundMap = new HashMap();

        //addSoundsToSoundPool();

        // load the shake animation that's used for incorrect answers
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3); // animation repeats 3 times

        correctAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.correct_shake);
        correctAnimation.setRepeatCount(1); // animation repeats 1 time

        // get references to GUI components
        quizLinearLayout =
                (LinearLayout) view.findViewById(R.id.quizLinearLayout);
        questionNumberTextView =
                (TextView) view.findViewById(R.id.questionNumberTextView);
        animalImageView = (ImageView) view.findViewById(R.id.imageViewAnimal);
        guessLinearLayouts = new LinearLayout[2];
        guessLinearLayouts[0] =
                (LinearLayout) view.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] =
                (LinearLayout) view.findViewById(R.id.row2LinearLayout);
        answerTextView = (TextView) view.findViewById(R.id.answerTextView);

        // configure listeners for the guess Buttons
        for (LinearLayout row : guessLinearLayouts) {
            for (int column = 0; column < row.getChildCount(); column++) {
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }

        // set questionNumberTextView's text
        questionNumberTextView.setText(
                getString(R.string.question, 1, numberOfQuestions));
        return view; // return the fragment's view for display



    }

    private void addToAnimalSoundMap(String name) {

        //while (loaded == false) {

            //animalSoundMap
        //}
        //reset loaded value
        loaded = false;
    }

    /*
    * Load sound to pool if it is not found in animalSoundMap
    * */
    private void loadSoundToSoundPool(String name) {

        boolean found = false;

        //check if found in animal sound map
        for (String nameKey : animalSoundMap.keySet()) {

            if (name.equals(nameKey)) {
                found = true;
            }
        }

        //if not found, get reference to identifier and load sound
        //then put name and id to sound hash map
        if (found == false) {
            int resourceID = getResources().getIdentifier(name, "raw", getContext().getPackageName());
            int tempSoundID = soundPool.load(getContext(), resourceID, 1);
            animalSoundMap.put(name, tempSoundID);
            /*
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }

    /** 
     * Load Sounds into the SoundPool. 
     */
    private void loadSoundsToSoundPool() {

        int tempID = -1;

        animalSoundMap = new HashMap();

        Field[] fields= R.raw.class.getFields();
        for(int count = 0; count < fields.length; count++){
            System.out.println ("Raw Asset: " +  fields[count].getName());
            try {
                 tempID = fields[count].getInt(fields[count]);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            // if we got temporary id, set it to animal sound map<name, soundID>
            if (tempID != -1) {
                String name = fields[count].getName();
                int id = tempID;
                animalSoundMap.put(fields[count].getName(), tempID);
            }
        }
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Plays the sound
     */
    public void playSound(String name) {

        //Log.d("Sound", "Play GameSound " + id + 1 + ".");
        //soundPool.play(wrongSoundID, 1, 1, 1, 0, 1f);
        int streamID = 0;
        //get id from animal sound map
        int tempID = animalSoundMap.get(name);
        //soundPool.play(tempID,1,1,0,0,1);
        do {
            streamID = soundPool.play(tempID, 1, 1, 1, 0, 1f);
        } while(streamID == 0);
        //Log.d("Sound", "GameSound " + id + 1 + " Played");
    }

    // update guessRows based on value in SharedPreferences
    public void updateGuessRows(SharedPreferences sharedPreferences) {
        // get the number of guess buttons that should be displayed
        String choices =
                sharedPreferences.getString(MainActivity.CHOICES, null);
        guessRows = Integer.parseInt(choices) / 2;

        // hide all guess button LinearLayouts
        for (LinearLayout layout : guessLinearLayouts)
            layout.setVisibility(View.GONE);

        // display appropriate guess button LinearLayouts
        for (int row = 0; row < guessRows; row++)
            guessLinearLayouts[row].setVisibility(View.VISIBLE);
    }

    // update animal types for quiz based on values in SharedPreferences
    public void updateTypes(SharedPreferences sharedPreferences) {
        typesSet =
                sharedPreferences.getStringSet(MainActivity.TYPES, null);
    }


    // set up and start the next quiz
    public void resetQuiz() {
        // use AssetManager to get image file names for enabled animal types
        AssetManager assets = getActivity().getAssets();
        fileNameList.clear(); // empty list of image file names

        try {
            // loop through each type
            for (String type : typesSet) {
                // get a list of all animal image files in this type
                String[] paths = assets.list(type);

                for (String path : paths)
                    fileNameList.add(path.replace(".png", ""));
            }
        }
        catch (IOException exception) {
            Log.e(TAG, "Error loading image file names", exception);
        }

        correctAnswers = 0; // reset the number of correct answers made
        totalGuesses = 0; // reset the total number of guesses the user made
        quizAnimalsList.clear(); // clear prior list of quiz animals

        int animalCounter = 1;
        int numberOfAnimals = fileNameList.size();

        // add ANIMALS_IN_QUIZ random file names to the quizAnimalsList
        while (animalCounter <= numberOfQuestions) {
            int randomIndex = random.nextInt(numberOfAnimals);

            // get the random file name
            String filename = fileNameList.get(randomIndex);

            // if the type is enabled and it hasn't already been chosen
            if (!quizAnimalsList.contains(filename)) {
                quizAnimalsList.add(filename); // add the file to the list
                ++animalCounter;
            }
        }

        loadNextAnimal(); // start the quiz by loading the first animal
    }

    // after the user guesses a correct animal, load the next animal
    private void loadNextAnimal() {

        String nextImage = null;

        // get file name of the next animal and remove it from the list
        nextImage = quizAnimalsList.remove(0);
        correctAnswer = nextImage; // update the correct answer

        answerTextView.setText(""); // clear answerTextView

        // display current question number
        questionNumberTextView.setText(getString(
                R.string.question, (correctAnswers + 1), numberOfQuestions));

        // extract the animal type from the next image's name
        String type = nextImage.substring(0, nextImage.indexOf('-'));

        // use AssetManager to load next image from assets folder
        AssetManager assets = getActivity().getAssets();

        // get an InputStream to the asset representing the next animal
        // and try to use the InputStream
        try (InputStream stream =
                     assets.open(type + "/" + nextImage + ".png")) {
            // load the asset as a Drawable and display on the animalImageView
            Drawable animal = Drawable.createFromStream(stream, nextImage);
            animalImageView.setImageDrawable(animal);

            animate(false); // animate the animal onto the screen
        }
        catch (IOException exception) {
            Log.e(TAG, "Error loading " + nextImage, exception);
        }

        Collections.shuffle(fileNameList); // shuffle file names

        // put the correct answer at the end of fileNameList
        int correct = fileNameList.indexOf(correctAnswer);
        fileNameList.add(fileNameList.remove(correct));

        // add 2 or 4 guess Buttons based on the value of guessRows
        for (int row = 0; row < guessRows; row++) {
            // place Buttons in currentTableRow
            for (int column = 0;
                 column < guessLinearLayouts[row].getChildCount();
                 column++) {
                // get reference to Button to configure
                Button newGuessButton =
                        (Button) guessLinearLayouts[row].getChildAt(column);
                newGuessButton.setVisibility(View.VISIBLE);
                newGuessButton.setEnabled(true);

                // get animal name and set it as newGuessButton's text
                String filename = fileNameList.get((row * 2) + column);
                newGuessButton.setText(getAnimalName(filename));
            }
        }

        // randomly replace one Button with the correct answer
        int row = random.nextInt(guessRows); // pick random row
        int column = random.nextInt(2); // pick random column
        LinearLayout randomRow = guessLinearLayouts[row]; // get the row
        String animalName = getAnimalName(correctAnswer);
        ((Button) randomRow.getChildAt(column)).setText(animalName);
    }

    // parses the animal file name and returns the animal name
    private String getAnimalName(String name) {
        return name.substring(name.indexOf('-') + 1).replace('_', ' ');
    }

    // animates the entire quizLinearLayout on or off screen
    private void animate(boolean animateOut) {
        // prevent animation into the the UI for the first animal
        if (correctAnswers == 0)
            return;

        // calculate center x and center y
        int centerX = (quizLinearLayout.getLeft() +
                quizLinearLayout.getRight()) / 2; // calculate center x
        int centerY = (quizLinearLayout.getTop() +
                quizLinearLayout.getBottom()) / 2; // calculate center y

        // calculate animation radius
        int radius = Math.max(quizLinearLayout.getWidth(),
                quizLinearLayout.getHeight());

        Animator animator;

        // if the quizLinearLayout should animate out rather than in
        if (animateOut) {
            // create circular reveal animation
            animator = ViewAnimationUtils.createCircularReveal(
                    quizLinearLayout, centerX, centerY, radius, 0);
            animator.addListener(
                    new AnimatorListenerAdapter() {
                        // called when the animation finishes
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            loadNextAnimal();
                        }
                    }
            );
        }
        else { // if the quizLinearLayout should animate in
            animator = ViewAnimationUtils.createCircularReveal(
                    quizLinearLayout, centerX, centerY, 0, radius);
        }

        animator.setDuration(500); // set animation duration to 500 ms
        animator.start(); // start the animation
    }

    // called when a guess Button is touched
    private View.OnClickListener guessButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Button guessButton = ((Button) v);
            String guess = guessButton.getText().toString();
            String answer = getAnimalName(correctAnswer);
            ++totalGuesses; // increment number of guesses the user has made

            if (guess.equals(answer)) { // if the guess is correct

                //answer was correct, play animation
                //guessButton.setAnimation(bounceAnimation);
                animalImageView.startAnimation(correctAnimation); // rotate

                ++correctAnswers; // increment the number of correct answers

                // display correct answer in green text
                answerTextView.setText(answer + "!");
                answerTextView.setTextColor(
                        getResources().getColor(R.color.correct_answer,
                                getContext().getTheme()));

                disableButtons(); // disable all guess Buttons

                //play sound of animal
                //first load to memory
                if (soundOn == true) {
                    loadSoundToSoundPool(answer.toLowerCase());
                    playSound(answer.toLowerCase());
                }


                // if the user has correctly identified ANIMALS_IN_QUIZ animals
                if (correctAnswers == numberOfQuestions) {
                    // DialogFragment to display quiz stats and start new quiz
                    MyResultDialogFragment quizResults = MyResultDialogFragment.newInstance(
                            totalGuesses, correctAnswers);
                    quizResults.attachParentFragment(MainActivityFragment.this);
                   // quizResults.setMyMessage(totalGuesses);

                    // use FragmentManager to display the DialogFragment
                    quizResults.setCancelable(false);
                    quizResults.show(getFragmentManager(), "quiz results");
                    // DialogFragment to display quiz stats and start new quiz
                    //ResultsDialogFragment quizResults = new ResultsDialogFragment();
                    //quizResults.setMyActivity(MainActivityFragment.this);
                    //quizResults.dialogSetMessage(totalGuesses);
                    //quizResults.dialogSetPositiveB();
                    //FragmentTransaction ft = getFragmentManager().beginTransaction();
                    //ResultsDialogFragment quizResults = ResultsDialogFragment.newInstance(totalGuesses);
                    //quizResults.dialogSetPositiveB(void -> MainActivityFragment.this.resetQuiz()); //pass resetQuiz method

                    // use FragmentManager to display the DialogFragment
                    //quizResults.setCancelable(false);
                    //quizResults.show(ft, "quiz results");
                    //quizResults.show(getFragmentManager(), "quiz results");
                }
                else { // answer is correct but quiz is not over
                    // load the next animal after a 2-second delay
                    handler.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    animate(true); // animate the animal picture off the screen
                                }
                            }, 2000); // 2000 milliseconds for 2-second delay
                }

            }
            else { // answer was incorrect
                animalImageView.startAnimation(shakeAnimation); // play shake

                Context context = getActivity().getApplicationContext();
                CharSequence text = "WRONG!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                guessButton.setVisibility(View.INVISIBLE);

                //play sound wrong
                //while (loaded == false) {
                    //wait loading
                //}
                loaded = false;

                if (soundOn == true) {
                    loadSoundToSoundPool(wrong);
                    playSound(wrong);
                }

                // display "Incorrect!" in red
                //answerTextView.setText(R.string.incorrect_answer);
                //answerTextView.setTextColor(getResources().getColor(
                  //     R.color.incorrect_answer, getContext().getTheme()));
                //guessButton.setEnabled(false); // disable incorrect answer
            }
        }
    };

    // utility method that disables all answer Buttons
    private void disableButtons() {
        for (int row = 0; row < guessRows; row++) {
            LinearLayout guessRow = guessLinearLayouts[row];
            for (int i = 0; i < guessRow.getChildCount(); i++)
                guessRow.getChildAt(i).setEnabled(false);
        }
    }

    @Override
    public void doPositiveClick() {

        resetQuiz();
    }

    public void updateSound(SharedPreferences sharedPreferences) {

        String soundOnOff =
                sharedPreferences.getString(MainActivity.SOUND, null);

        if (soundOnOff.equals("On")) {
            this.soundOn = true;
        }
        else {
            this.soundOn = false;
        }
    }

    public void updatenOfQuestions(SharedPreferences sharedPreferences) {
        this.numberOfQuestions = Integer.parseInt(
                sharedPreferences.getString(MainActivity.QUESTIONS, null));

    }
}

