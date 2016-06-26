package de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Controller;

import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;

import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Activity.MapsActivity;

/**
 * Created by philippwinterholler on 26.06.16.
 */
public class shareController {

    private int test;
    private MapsActivity mapsActivity;

    public shareController(MapsActivity mapsActivity){
        this.mapsActivity = mapsActivity;
    }


    public void shareTrack(){
        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("og:type","fitness.course")
                .putString("og:title","Motorrad Tour")
                .putString("og:description","Ist mit der GPS-Tracker App eine Motorrad Tour gefahren")

                .putInt("fitness:duration:value",10)



                .build();
        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("fitness.bike")
                .putObject("fitness:course", object)
                .build();

        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("fitness:course")
                .setAction(action)
                .build();

        ShareDialog.show(mapsActivity, content);

    }
}
