// Class called when user long clicks on a bubble. Generates a Dialog that allows users to either
// complete, delete, or cancel

package com.example.popstudios;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

public class DeleteButtonDialog extends AppCompatDialogFragment {
    // creates listener to allow user to choose yes or cancel in dialog
    private DeleteButtonDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Complete Goal")
                .setMessage("Did you complete this goal?")
                // Closes window and does nothing when user clicks cancel
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                // Calls listener onNeutralClicked
                .setNeutralButton("Complete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onNeutralClicked();
                    }
                })
                // Calls listener onYesClicked
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onYesClicked();
                    }
                });
        return builder.create();
    }

    // adds methods that require extra action to the listener
    public interface DeleteButtonDialogListener {
        void onYesClicked();
        void onNeutralClicked();
    }

    // watches for exceptions. If one is found it handles it by throwing an exception and delivering
    // a message to implement the proper listener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DeleteButtonDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement DeleteButtonDialogListener");
        }
    }
}
