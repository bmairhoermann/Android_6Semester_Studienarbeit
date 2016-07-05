package de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Controller;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.LatitudeLongitudeModel;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.TrackModel;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.R;

/**
 * Created by brianmairhoermann on 24.06.16.
 */
public class MapController {
    public boolean drawLine = false;
    public boolean followUser = true;
    public LatitudeLongitudeModel lastPosition;
    public LatitudeLongitudeModel currentPosition;

    private Marker userMarker;

    private GoogleMap map;

    public MapController(GoogleMap map){
        this.map = map;
        userMarker = map.addMarker(new MarkerOptions()
                .visible(false).draggable(false)
                .position(new LatLng(50.3113025, 11.8542196))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_position))
                .anchor(0.5f, 0.5f));
    }

    public void updateUserPosition(LatitudeLongitudeModel position){
        // Set Values for possible Linedrawing
        if (lastPosition == null){
            lastPosition = position;
            currentPosition = position;
        }else{
            lastPosition = currentPosition;
            currentPosition = position;
        }

        // Userfollowing
        if(followUser){
            userMarker.setVisible(true);
            userMarker.setPosition(position.toGoogleLatLng());
            map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(position.toGoogleLatLng(), (float) 17, (float) 0, (float) 0)));
        }else{
            userMarker.setVisible(false);
        }

        // Linedrawing
        if(drawLine){
            // Drawline: Below 60 km/h = green, between 60 and 120 km/h = yellow, above 120 km/h = red
            if(position.speed <17) {
                map.addPolyline(new PolylineOptions()
                        .add(lastPosition.toGoogleLatLng(), currentPosition.toGoogleLatLng())
                        .width(10)
                        .color(Color.GREEN));
            }else if (position.speed > 17 && position.speed < 33){
                map.addPolyline(new PolylineOptions()
                        .add(lastPosition.toGoogleLatLng(), currentPosition.toGoogleLatLng())
                        .width(10)
                        .color(Color.YELLOW));
            }else {
                map.addPolyline(new PolylineOptions()
                        .add(lastPosition.toGoogleLatLng(), currentPosition.toGoogleLatLng())
                        .width(10)
                        .color(Color.RED));
            }
        }
    }

    public void setMarker(String title, LatitudeLongitudeModel position){
        map.addMarker(new MarkerOptions().position(position.toGoogleLatLng()).title(title));
    }

    public void clearMap(){
        map.clear();
        userMarker = map.addMarker(new MarkerOptions()
                .visible(false).draggable(false)
                .position(new LatLng(50.3113025, 11.8542196))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_position))
                .anchor(0.5f, 0.5f));
    }

}
