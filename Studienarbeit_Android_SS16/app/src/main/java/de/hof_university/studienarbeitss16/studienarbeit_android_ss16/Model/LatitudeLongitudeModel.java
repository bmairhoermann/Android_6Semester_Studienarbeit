package de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by brianmairhoermann on 13.06.16.
 */
public class LatitudeLongitudeModel implements Serializable{
    public double latitude;
    public double longitude;
    public float speed;
    public long timeStamp;

    public LatitudeLongitudeModel(double latitude, double longitude, float speed, long timeStamp){
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.timeStamp = timeStamp;
    }

    public LatLng toGoogleLatLng(){
        return new LatLng(latitude, longitude);
    }
}
