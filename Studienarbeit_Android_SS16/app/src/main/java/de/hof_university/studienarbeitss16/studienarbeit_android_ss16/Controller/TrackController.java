package de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Controller;

import android.os.Debug;

import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Activity.MapsActivity;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.LatitudeLongitudeModel;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.TrackModel;

/**
 * Created by brianmairhoermann on 24.06.16.
 */
public class TrackController {
    public boolean trackingModeStart = false;

    private LatitudeLongitudeModel currentPosition;
    //private LatitudeLongitudeModel startPoint;
    //private LatitudeLongitudeModel endPoint;
    private TrackModel trackModel;
    private boolean isFirstPosition = true;
    private MapController mapController;
    private MapsActivity mapsActivity;

    public TrackController(MapController mapController, MapsActivity mapsActivity){
        this.mapController = mapController;
        this.mapsActivity = mapsActivity;
    }

    public void startTrack(){

        trackingModeStart = true;
        isFirstPosition = true;
        trackModel = new TrackModel();
        mapController.drawLine = true;
    }

    public void endTrack(){
        trackingModeStart = false;
        isFirstPosition = false;
        mapController.drawLine = false;

        // Code for saving....
        // Check before here: Is currentPosition empty? -> take last position from Array --- is array empty -> abbort save
        if(currentPosition != null && trackModel.trackList.size()>1) {
            mapController.setMarker("Endpunkt", currentPosition);
            trackModel.lastPosition = currentPosition;
            trackModel.trackList.add(currentPosition);
            if (trackModel.trackList.size() > 0) {
                System.out.print("FirstPosition: " + trackModel.firstPosition + ", LastPosition: " + trackModel.lastPosition);
                for (LatitudeLongitudeModel l : trackModel.trackList) {
                    System.out.println(l.latitude + ", " + l.longitude);
                }
            }
            mapsActivity.displaySaveDialog(trackModel);
            mapController.clearMap();
        }else {
            mapController.clearMap();
        }
    }

    public void updateUserPosition(LatitudeLongitudeModel latlng){
        currentPosition = latlng;

        // Executed when Track starts and is running
        if (trackingModeStart){
            if(isFirstPosition){
                mapController.setMarker("Startpunkt", latlng);
                trackModel.firstPosition = latlng;
                isFirstPosition = false;
            }
            trackModel.trackList.add(latlng);
        }
    }

}






















