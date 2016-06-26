package de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Controller;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.StrictMode;

import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.LatitudeLongitudeModel;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.TrackCollection;

/**
 * Created by brianmairhoermann on 24.06.16.
 */
public class LocationController implements LocationListener {

    private TrackController trackController;
    private MapController mapController;

    public LocationController(TrackController trackCtrl, MapController mapCtrl){
        this.trackController = trackCtrl;
        this.mapController = mapCtrl;
    }

    @Override
    public void onLocationChanged(Location loc){
        // Create LatitudeLongitudeModel
        LatitudeLongitudeModel latlng;
        if (loc.hasSpeed()){
            latlng = new LatitudeLongitudeModel(loc.getLatitude(), loc.getLongitude(), loc.getSpeed(), loc.getTime());
        }else{
            latlng = new LatitudeLongitudeModel(loc.getLatitude(), loc.getLongitude(), 0.0f, loc.getTime());
        }

        // Update Map
        mapController.updateUserPosition(latlng);

        // Update Track
        trackController.updateUserPosition(latlng);
    }

    @Override
    public void onProviderDisabled(String provider){

    }

    @Override
    public void onProviderEnabled(String provider){

    }

    @Override
    public void onStatusChanged(String provider, int Status, Bundle extras){

    }
}
