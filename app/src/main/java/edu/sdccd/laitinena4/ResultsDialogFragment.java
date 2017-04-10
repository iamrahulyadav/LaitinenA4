package edu.sdccd.laitinena4;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;

/**
 * Created by Tuulikki Laitinen on 4/9/2017.
 */

public class ResultsDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private AlertDialog.Builder builder;
    private MainActivityFragment mainActivityFragment;
    private int num;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static ResultsDialogFragment newInstance(int num) {
        ResultsDialogFragment f;
        f = new ResultsDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.num = getArguments().getInt("num");
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        super.onCreateDialog(bundle);
        //this.mainActivityFragment = mainActivityFragment;

        builder =
                new AlertDialog.Builder(getActivity());

        return builder.create(); // return the AlertDialog
    }

    public void dialogSetMessage(int totalGuesses) {
        builder.setMessage(
                getString(R.string.results,
                        totalGuesses,
                        (1000 / (double) totalGuesses)));

    }

    public void setMyActivity(MainActivityFragment mainActivityFragment) {

        this.mainActivityFragment = mainActivityFragment;
    }

    @Override
    public void onClick(DialogInterface dialog, int id) {
        this.mainActivityFragment.resetQuiz();
    }


    public void dialogSetPositiveB() {

        // "Reset Quiz" Button
        builder.setPositiveButton(R.string.reset_quiz,
                (DialogInterface.OnClickListener)this);
    }

    public void show(FragmentManager fragmentManager, String s) {
        super.show(fragmentManager, s);

    }
/*
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        builder.setMessage(
                getString(R.string.results,
                        totalGuesses,
                        (1000 / (double) totalGuesses)));

        // "Reset Quiz" Button
        builder.setPositiveButton(R.string.reset_quiz,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        resetQuiz();
                    }
                }
        );

        return builder.create(); // return the AlertDialog
    }*/
}
