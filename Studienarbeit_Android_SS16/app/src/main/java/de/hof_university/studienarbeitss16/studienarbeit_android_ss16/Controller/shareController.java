package de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Controller;

import android.location.Location;
import android.os.Debug;

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

    public void check(){

        locationA.setLatitude(50.3255215);
        locationA.setLongitude(11.9905178);

        locationB.setLatitude(40.3255215);
        locationB.setLongitude(11.9905178);
    }

    public void shareTrack(){
            check();
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
                //Dauer
        test.putInt("fitness:duration:value",10);
        test.putString("fitness:duration:units", "s");
                //Entfernung
        test.putInt("fitness:distance:value",122);
        test.putString("fitness:distance:units","km");

                //Geschwindigkeit

        test.putDouble("fitness:speed:value",10.21);
        test.putString("fitness:speed:units","m/s");
        for(int i = 0; i <10 ; i++){
            test.putDouble("fitness:metrics["+i+"]:location:latitude",i+10.1023912);
            test.putDouble("fitness:metrics["+i+"]:location:longitude",(-i*2)+11.9905178);
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
