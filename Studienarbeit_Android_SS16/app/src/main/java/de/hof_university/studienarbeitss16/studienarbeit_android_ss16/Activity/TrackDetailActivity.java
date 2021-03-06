package de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Activity.Dialog.DialogEditTrack;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Controller.ShareController;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.LatitudeLongitudeModel;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.TrackCollection;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.TrackModel;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.R;

public class TrackDetailActivity extends AppCompatActivity implements GoogleMap.OnMapLoadedCallback, OnMapReadyCallback, DialogEditTrack.DialogEditTrackListener{
    private GoogleMap detailmap;
    private String FILENAME = "PersitencyTrack";
    private int position;
    private TrackModel track;

    //View Elements
    private TextView loadingText;

    private TextView meterTextView;
    private TextView minutesTextView;
    private TextView averageTextView;
    private TextView highSpeedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detailMap);
        mapFragment.getMapAsync(this);

        loadingText = (TextView)findViewById(R.id.detailMapLoadingText);
        meterTextView = (TextView)findViewById(R.id.textViewMeter);
        minutesTextView = (TextView)findViewById(R.id.textViewMinutes);
        averageTextView = (TextView) findViewById(R.id.textViewAverageSpeed);
        highSpeedTextView =(TextView) findViewById(R.id.textViewHighSpeed);

        position = getIntent().getIntExtra("position", 0);

        TrackCollection tmpCollection = readTrackModelCollectionFromMemory();
        track = tmpCollection.trackCollectionList.get(position);

        displayStatics();
    }
    //##################################################################################
    //############################__ButtonFunctions__###################################
    //##################################################################################

    public void displayStatics(){
        ShareController cs = new ShareController(this, track);
        try{
            meterTextView.setText(Double.toString(cs.calculateDistance()).substring(0,4) + " km");
        }catch (Exception e){
            meterTextView.setText("0.0 km");
        }
        try{
            averageTextView.setText(Double.toString(cs.calculateSpeed()).substring(0,4) + " km/h");
        }catch (Exception e){
            averageTextView.setText("0.0 km/h");
        }
        try{
            highSpeedTextView.setText(Double.toString(cs.highSpeed()).substring(0,4) + " km/h");
        }catch (Exception e){
            highSpeedTextView.setText("0.0 km/h");
        }
        minutesTextView.setText(Double.toString(cs.calculateDuration()/60)+ " min");
    }

    //##################################################################################
    //############################__ButtonFunctions__###################################
    //##################################################################################
    public void share(View view){
        ShareController sharecontroller = new ShareController(this,track);
        sharecontroller.shareTrack();
    }
    public void updateName(View view){
        // Call dialog for naming the Track
        DialogEditTrack dialogEditTrack = new DialogEditTrack();
        dialogEditTrack.trackModel = track;
        dialogEditTrack.show(getFragmentManager(), "fragment_edit_track");

    }

    public void deleteTrack(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Wollen Sie diesen Track wirklich löschen?")
                .setCancelable(false)
                .setTitle(track.title + " löschen")
                .setPositiveButton("Ja",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                TrackCollection tmpCollection = readTrackModelCollectionFromMemory();
                                tmpCollection.trackCollectionList.remove(position);
                                writeTrackModelCollectionToMemory(tmpCollection);
                                dialog.cancel();
                                finish();
                            }
                        })
                .setNegativeButton("Nein",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //##################################################################################
    //############################__MapFunctions__######################################
    //##################################################################################

    @Override
    public void onMapReady(GoogleMap googleMap){
        detailmap = googleMap;
        detailmap.setOnMapLoadedCallback(this);
        loadingText.setVisibility(View.VISIBLE);

    }

    @Override
    public void onMapLoaded(){
        loadingText.setVisibility(View.INVISIBLE);
        showTrack(track);
    }


    public void showTrack(TrackModel trackModel){
        detailmap.clear();

        // Set Markers
        setMarker("Startpunkt", trackModel.firstPosition);
        setMarker("Endpunkt", trackModel.lastPosition);

        // Set Path and set up bounds
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        boundsBuilder.include(trackModel.firstPosition.toGoogleLatLng());
        boundsBuilder.include(trackModel.lastPosition.toGoogleLatLng());
        for (int i=1; i < trackModel.trackList.size();i++){
            // Drawline: Below 60 km/h = green, between 60 and 120 km/h = yellow, above 120 km/h = red
            if(trackModel.trackList.get(i).speed <17) {
                detailmap.addPolyline(new PolylineOptions().color(Color.GREEN).width(10).add(trackModel.trackList.get(i-1).toGoogleLatLng(), trackModel.trackList.get(i).toGoogleLatLng()));
                boundsBuilder.include(trackModel.trackList.get(i).toGoogleLatLng());
            }else if (trackModel.trackList.get(i).speed > 17 && trackModel.trackList.get(i).speed < 33){
                detailmap.addPolyline(new PolylineOptions().color(Color.YELLOW).width(10).add(trackModel.trackList.get(i-1).toGoogleLatLng(), trackModel.trackList.get(i).toGoogleLatLng()));
                boundsBuilder.include(trackModel.trackList.get(i).toGoogleLatLng());
            }else {
                detailmap.addPolyline(new PolylineOptions().color(Color.RED).width(10).add(trackModel.trackList.get(i-1).toGoogleLatLng(), trackModel.trackList.get(i).toGoogleLatLng()));
                boundsBuilder.include(trackModel.trackList.get(i).toGoogleLatLng());
            }

        }

        // Set Camerabounds
        detailmap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 250));
    }

    public void setMarker(String title, LatitudeLongitudeModel position){
        detailmap.addMarker(new MarkerOptions().position(position.toGoogleLatLng()).title(title));
    }

    //##################################################################################
    //############################__Dialog_Fragment_Functions__#########################
    //##################################################################################

    // Called by DialogSaveTrack when User is done
    @Override
    public void onFinishEditDialog(boolean save, String title){
        if (save){
            track.title = title;
            TrackCollection tmp = readTrackModelCollectionFromMemory();
            tmp.trackCollectionList.remove(position);
            tmp.trackCollectionList.add(position, track);
            writeTrackModelCollectionToMemory(tmp);
        }
    }

    //##################################################################################
    //############################__PersistencyFunctions__##############################
    //##################################################################################

    // Method for Saving TrackCollection
    private void writeTrackModelCollectionToMemory(TrackCollection tmp){
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(tmp);
            objectOutputStream.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method for Loading TrackCollection
    private TrackCollection readTrackModelCollectionFromMemory(){
        FileInputStream fileInputStream;
        TrackCollection tmp = new TrackCollection();
        try {
            fileInputStream = openFileInput(FILENAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            tmp = (TrackCollection) objectInputStream.readObject();
            objectInputStream.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (StreamCorruptedException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();

        }
        return tmp;
    }
}
