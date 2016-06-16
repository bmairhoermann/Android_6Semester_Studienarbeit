package de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brianmairhoermann on 13.06.16.
 */
public class TrackModel implements Serializable{

    public String title;

    public LatitudeLongitudeModel firstPosition;
    public LatitudeLongitudeModel lastPosition;

    public List<LatitudeLongitudeModel> trackList = new ArrayList<>();
}
