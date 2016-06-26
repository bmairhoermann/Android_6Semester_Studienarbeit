package de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Controller.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.TrackModel;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.R;

/**
 * Created by brianmairhoermann on 26.06.16.
 */
public class TrackListAdapter extends ArrayAdapter<TrackModel> {
    public TrackListAdapter(Context context, int textViewResourceId, ArrayList<TrackModel> items) {
        super(context, textViewResourceId, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TrackModel trackModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        TextView trackName = (TextView) convertView.findViewById(R.id.trackListItem);
        // Populate the data into the template view using the data object
        if (trackModel.title != null){
            trackName.setText(trackModel.title);
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
