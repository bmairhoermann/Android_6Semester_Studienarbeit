package de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Controller;

import android.location.Location;
import android.os.Debug;
import android.util.Log;

import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.Barcode.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Activity.MapsActivity;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Activity.TrackDetailActivity;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.LatitudeLongitudeModel;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.TrackModel;

/**
 * Created by philippwinterholler on 26.06.16.
 */
public class ShareController {

    private int test;
    private TrackDetailActivity mapsActivity;
    private TrackModel trackModel;

    Location locationA = new Location("a");

    Location locationB = new Location("b");


    public ShareController(TrackDetailActivity mapsActivitym, TrackModel trackModel){
        this.mapsActivity = mapsActivitym;
        this.trackModel = trackModel;
    }

    public TrackModel check(){
        TrackModel trackModel = new TrackModel();

        trackModel.title = "MyFirstTrack";
        trackModel.firstPosition = new LatitudeLongitudeModel(50.32578791, 11.9195146, 0.0f, 1466951089000l);
        trackModel.lastPosition = new LatitudeLongitudeModel(50.32578791, 11.9195146, 0.0f, 1466951098000l+4104000l);
        /*
        Firstposition: 50.32578791, 11.94006134, speed: 0.0, time 1466951089000
        Lastposition: 50.32576226, 11.94002498, speed: 0.0, time 1466951098000
        Listitem: 50.32578791, 11.94006134, speed: 0.0, time 1466951089000
        Listitem: 50.3257799, 11.94005303, speed: 0.0, time 1466951090000
        Listitem: 50.32577574, 11.9400563, speed: 1.031746, time 1466951091000
        Listitem: 50.32576825, 11.94003625, speed: 0.0, time 1466951092000
        Listitem: 50.32576173, 11.94001859, speed: 0.0, time 1466951094000
        Listitem: 50.32576763, 11.94003366, speed: 0.0, time 1466951096000
        Listitem: 50.32576226, 11.94002498, speed: 0.0, time 1466951098000
        Listitem: 50.32576226, 11.94002498, speed: 0.0, time 1466951098000
            */
        trackModel.trackList.add(new LatitudeLongitudeModel(50.3179066, 11.9195146, 0.0f, 1466951089000l));
        trackModel.trackList.add(new LatitudeLongitudeModel(50.3179066, 11.9115146, 0.0f, 1466951091000l));
        trackModel.trackList.add(new LatitudeLongitudeModel(50.3257799, 11.94005303, 1.02f, 1466951095000l));
        trackModel.trackList.add(new LatitudeLongitudeModel(50.3257799, 12.94005303, 1.02f, 1466951095000l));
        return trackModel;

    }
    public double calculateDistance(){
        //TrackModel personalTrackModel = trackModel;

        double distance = 0;

        for (int i = 0 ; i < trackModel.trackList.size()-1;i++){
            Location tempLocation = new Location("PointA");

            tempLocation.setLongitude(trackModel.trackList.get(i).longitude);
            tempLocation.setLatitude(trackModel.trackList.get(i).latitude);

            Location temp2Location = new Location("PointB");

            temp2Location.setLongitude(trackModel.trackList.get(i+1).longitude);
            temp2Location.setLatitude(trackModel.trackList.get(i+1).latitude);

            distance += tempLocation.distanceTo(temp2Location);

            Log.d("FLOAT:WERTE", "Entfernung: " + distance +" METER!" + "Entfernung:" +((distance/1000))+ " KILOMETER");

        }
        //Entfernung in Kilometern
        return (distance/1000);
    }

    public long calculateDuration(){
        float duration=0;

        duration =  (trackModel.trackList.get(trackModel.trackList.size()-1).timeStamp)-(trackModel.trackList.get(0).timeStamp);

        Log.d("Float: Werte", "calculateDuration: " + duration/1000);
        //Entfernung in Sekunden
        return (long)(duration/1000);
    }

    public double calculateSpeed(){
        double speed = 0.0;
        speed = ((calculateDistance())/calculateDuration());
        return speed;
    }
    public double highSpeed(){
        double highspeed = 0.0;
        for (int i = 0 ; i < trackModel.trackList.size()-1;i++) {
            double tempSpeed = trackModel.trackList.get(i).speed;
            if (tempSpeed > highspeed){
                highspeed = tempSpeed;
            }

        }
        return highspeed;
    }
    public void shareTrack(){
        ShareOpenGraphObject.Builder test = new ShareOpenGraphObject.Builder();
        //
        test.putString("og:type","fitness.course");
        test.putString("og:title","Motorrad Tour");
        test.putString("og:description","Ist mit der GPS-Tracker App eine Motorrad Tour gefahren");
        //Dauer
        test.putLong("fitness:duration:value",calculateDuration());
        test.putString("fitness:duration:units", "s");

        //Entfernung
        test.putDouble("fitness:distance:value",calculateDistance());
        test.putString("fitness:distance:units","km");

        //Geschwindigkeit

        test.putDouble("fitness:speed:value",calculateSpeed());
        test.putString("fitness:speed:units","m/s");

        // Wegpunkte
        for(int i = 0; i < trackModel.trackList.size();  i++){
            test.putDouble("fitness:metrics["+i+"]:location:latitude",trackModel.trackList.get(i).latitude);
            test.putDouble("fitness:metrics["+i+"]:location:longitude",trackModel.trackList.get(i).longitude);
        }


        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()

                .setActionType("fitness.bike")
                .putObject("fitness:course", test.build())
                .build();

        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("fitness:course")
                .setAction(action)
                .build();

        ShareDialog.show(mapsActivity, content);

    }
}

