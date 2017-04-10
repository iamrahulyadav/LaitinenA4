package edu.sdccd.laitinena4;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Tuulikki Laitinen on 4/9/2017.
 */

public class MyResultDialogFragment extends DialogFragment{

    private CustomFieldsFragmentAlertDialog listener;
    private AlertDialog.Builder builder;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static MyResultDialogFragment newInstance(int totalGuesses,
                                                     int correctAnswers) {
        MyResultDialogFragment frag = new MyResultDialogFragment();
        Bundle args = new Bundle();
        args.putInt("totalGuesses", totalGuesses);
        args.putInt("correctAnswers", correctAnswers);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int totalGuesses = getArguments().getInt("totalGuesses");
        int correctAnswers = getArguments().getInt("correctAnswers");

        builder = new AlertDialog.Builder(getActivity());

        return builder.setPositiveButton(R.string.results,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((CustomFieldsFragmentAlertDialog)getTargetFragment()).doPositiveClick();
                            }
                        }
                ).setMessage(
                getString(R.string.results,
                        totalGuesses,
                        (correctAnswers / (double)totalGuesses)*100))
                .setPositiveButton("Reset Quiz",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (listener != null) {

                                    listener.doPositiveClick();
                                }
                            }
                        }
                )
                .create();
    }

    public void attachParentFragment(MainActivityFragment fragment) {

        listener = (CustomFieldsFragmentAlertDialog) fragment;
    }
}
