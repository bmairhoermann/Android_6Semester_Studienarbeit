package de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Activity.Dialog;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.R;

/**
 * Created by brianmairhoermann on 27.06.16.
 */
public class DialogSaveTrack extends DialogFragment {

    public DialogSaveTrack() {}

    public interface DialogSaveTrackListener {
        void onFinishSaveDialog(boolean save, String title);
    }

    private EditText editText;
    private FloatingActionButton abbortButton;
    private FloatingActionButton saveButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_save_track, container);
        editText = (EditText)view.findViewById(R.id.saveDialogEditText);
        abbortButton = (FloatingActionButton) view.findViewById(R.id.saveDialogDontSave);
        saveButton = (FloatingActionButton) view.findViewById(R.id.saveDialogSave);
        getDialog().setTitle("Track speichern");

        // Abbortfunction
        abbortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSaveTrackListener activity = (DialogSaveTrackListener) getActivity();
                activity.onFinishSaveDialog(false, "");
                DialogSaveTrack.this.dismiss();
            }
        });

        // Savefunction
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSaveTrackListener activity = (DialogSaveTrackListener) getActivity();
                activity.onFinishSaveDialog(true, editText.getText().toString());
                DialogSaveTrack.this.dismiss();
            }
        });

        return view;
    }
}
