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
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.LatitudeLongitudeModel;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.TrackModel;

/**
 * Created by philippwinterholler on 26.06.16.
 */
public class shareController {

    private int test;
    private MapsActivity mapsActivity;

    Location locationA = new Location("a");

    Location locationB = new Location("b");


    public shareController(MapsActivity mapsActivity){
        this.mapsActivity = mapsActivity;
    }

    public TrackModel check(){
        TrackModel trackModel = new TrackModel();

        trackModel.title = "MyFirstTrack";
        trackModel.firstPosition = new LatitudeLongitudeModel(50.32578791, 11.94006134, 0.0f, 1466951089000l);
        trackModel.lastPosition = new LatitudeLongitudeModel(50.32576226, 11.94002498, 0.0f, 1466951089000l + 6060000l);
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
        trackModel.trackList.add(new LatitudeLongitudeModel(50.32578791, 11.94006134, 0.0f, 1466951089000l));
        trackModel.trackList.add(new LatitudeLongitudeModel(50.32577574, 11.9400563, 1.031746f, 1466951091000l));
        trackModel.trackList.add(new LatitudeLongitudeModel(50.32576173, 11.94001859, 0.0f, 1466951094000l));
        trackModel.trackList.add(new LatitudeLongitudeModel(50.3257799, 11.94005303, 1.02f, 1466951095000l));
        return trackModel;

    }

    public void shareTrack(){
        TrackModel myTrackModel = check();


        /*
        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("og:type","fitness.course")
                .putString("og:title","Motorrad Tour")
                .putString("og:description","Ist mit der GPS-Tracker App eine Motorrad Tour gefahren")
                //Dauer
                .putInt("fitness:duration:value",10)
                .putString("fitness:duration:units", "s")
                //Entfernung
                .putInt("fitness:distance:value",122)
                .putString("fitness:distance:units","km")

                //Geschwindigkeit

                .putDouble("fitness:speed:value",10.21)
                .putString("fitness:speed:units","m/s")

       .putDouble("fitness:metrics["+0+"]:location:latitude",10.1023912)
       .putDouble("fitness:metrics["+0+"]:location:longitude",11.9905178)



            .build();
*/

        ShareOpenGraphObject.Builder test = new ShareOpenGraphObject.Builder();
                test.putString("og:type","fitness.course");
                test.putString("og:title","Motorrad Tour");
            test.putString("og:description","Ist mit der GPS-Tracker App eine Motorrad Tour gefahren");
                //Dauer in Sekunden
        test.putInt("fitness:duration:value",calcTime(myTrackModel.firstPosition.timeStamp,myTrackModel.lastPosition.timeStamp));
        test.putString("fitness:duration:units", "s");

                //Entfernung
        test.putInt("fitness:distance:value",12);
        test.putString("fitness:distance:units","km");

                //Geschwindigkeit

        test.putDouble("fitness:speed:value",10.21);
        test.putString("fitness:speed:units","m/s");
        for(int i = 0; i < myTrackModel.trackList.size();  i++){
            test.putDouble("fitness:metrics["+i+"]:location:latitude",myTrackModel.trackList.get(i).latitude);
            test.putDouble("fitness:metrics["+i+"]:location:longitude",myTrackModel.trackList.get(i).longitude);
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
    public Integer calcTime(long startime, long endTime){

        long different = (endTime-startime);

        long secondsInMilli = 1000;
       /*
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        Log.d("DATUM:","Stunden:" +elapsedHours+ " Minuten: "+elapsedMinutes+" Sekunden: " +elapsedSeconds );
        */

        return (int) different/1000;
    }
}
