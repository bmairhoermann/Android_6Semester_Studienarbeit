package de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.R;

public class TrackDetailActivity extends AppCompatActivity {
    private GoogleMap detailmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);
    }
}
