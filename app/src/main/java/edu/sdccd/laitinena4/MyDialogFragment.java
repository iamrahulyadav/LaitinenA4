package edu.sdccd.laitinena4;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Tuulikki Laitinen on 4/9/2017.
 */

public class MyDialogFragment extends DialogFragment {

    private DialogInterface.OnClickListener clickListener;
    private AlertDialog.Builder builder;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
         builder = new AlertDialog.Builder(getActivity());

        /*
        builder.setMessage(R.string.results)
                .setPositiveButton(R.string.results, new DialogInterface.OnClickListener() {

                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                */
        // Create the AlertDialog object and return it
        return builder.create();
    }


    public void setMyListener () {

        clickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // FIRE ZE MISSILES!
            }
        };
    }


    public void setMyPositiveButton() {

        // "Reset Quiz" Button
        builder.setPositiveButton(R.string.reset_quiz,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {

                        ((CustomFieldsFragmentAlertDialog)getTargetFragment()).doPositiveClick();
                        //resetQuiz();
                    }
                }
        );
    }

    public void setMessage (int totalGuesses) {

        builder.setMessage(
                getString(R.string.results,
                          totalGuesses,
                          (1000 / (double) totalGuesses)));
    }
}
