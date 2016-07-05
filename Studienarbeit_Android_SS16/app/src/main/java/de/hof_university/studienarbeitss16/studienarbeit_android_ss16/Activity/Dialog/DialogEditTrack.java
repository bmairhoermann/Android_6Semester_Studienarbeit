package de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Activity.Dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.TrackModel;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.R;

/**
 * Created by brianmairhoermann on 04.07.16.
 */
public class DialogEditTrack extends DialogFragment {
    public DialogEditTrack() {}

    public interface DialogEditTrackListener {
        void onFinishEditDialog(boolean save, String title);
    }

    private EditText editText;
    private FloatingActionButton abbortButton;
    private FloatingActionButton saveButton;

    public TrackModel trackModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_track, container);
        editText = (EditText)view.findViewById(R.id.editDialogEditText);
        abbortButton = (FloatingActionButton) view.findViewById(R.id.editDialogDontSave);
        saveButton = (FloatingActionButton) view.findViewById(R.id.editDialogSave);
        getDialog().setTitle(trackModel.title + " bearbeiten");

        editText.setText(trackModel.title);

        // Abbortfunction
        abbortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogEditTrackListener activity = (DialogEditTrackListener) getActivity();
                activity.onFinishEditDialog(false, "");
                DialogEditTrack.this.dismiss();
            }
        });

        // Savefunction
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogEditTrackListener activity = (DialogEditTrackListener) getActivity();
                activity.onFinishEditDialog(true, editText.getText().toString());
                DialogEditTrack.this.dismiss();
            }
        });

        return view;
    }

}
