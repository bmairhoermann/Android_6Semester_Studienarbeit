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
            // TODO: Discuss Color for Speed
            if(position.speed <1.5) {
                map.addPolyline(new PolylineOptions()
                        .add(lastPosition.toGoogleLatLng(), currentPosition.toGoogleLatLng())
                        .width(10)
                        .color(Color.BLUE));
            }else{
                map.addPolyline(new PolylineOptions()
                        .add(lastPosition.toGoogleLatLng(), currentPosition.toGoogleLatLng())
                        .width(10)
                        .color(Color.GREEN));
            }
        }
    }

    public void showTrack(TrackModel trackModel){
        followUser = false;
        clearMap();

        // Set Markers
        setMarker("Startpunkt", trackModel.firstPosition);
        setMarker("Endpunkt", trackModel.lastPosition);

        // Set Path
        //LatitudeLongitudeModel[] tmp = (LatitudeLongitudeModel[])trackModel.trackList.toArray();
        for (int i=1; i < trackModel.trackList.size();i++){
            map.addPolyline(new PolylineOptions().color(Color.RED).width(10).add(trackModel.trackList.get(i-1).toGoogleLatLng(), trackModel.trackList.get(i).toGoogleLatLng()));
        }

        // Set Camerabounds
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        boundsBuilder.include(trackModel.firstPosition.toGoogleLatLng()).include(trackModel.lastPosition.toGoogleLatLng());
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 10));
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
