package de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.ListPopupWindow;
import android.util.Log;
import android.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Controller.Adapter.TrackListAdapter;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Activity.Dialog.DialogSaveTrack;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Controller.LocationController;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Controller.MapController;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Controller.TrackController;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.TrackCollection;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.TrackModel;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.R;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMapLoadedCallback, OnMapReadyCallback, AdapterView.OnItemClickListener, DialogSaveTrack.DialogSaveTrackListener {

    //##################################################################################
    //############################__Class_Variables__###################################
    //##################################################################################

    // ViewElements
    private FloatingActionButton trackButton;
    private ProgressBar spinner;
    private TextView spinnerText;
    private FloatingActionButton trackListButton;
    private ListPopupWindow listPopupWindow;
    private FloatingActionButton loginButton;

    // ControllerClasses
    private MapController mapController = null;
    private TrackController trackController = null;
    private LocationController locationController = null;

    // Other
    private GoogleMap mMap;
    private LocationManager locationManager=null;
    private Boolean isTracking = false;
    private TrackModel newTrackModel;
    private String FILENAME = "PersitencyTrack";
    public TrackCollection trackCollection = new TrackCollection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Initialize ViewElements
        trackButton = (FloatingActionButton)findViewById(R.id.trackButton);
        spinner = (ProgressBar) findViewById(R.id.spinnerProgress);
        spinnerText = (TextView)findViewById(R.id.spinnerText);
        trackListButton = (FloatingActionButton) findViewById(R.id.trackListButton);
        loginButton = (FloatingActionButton) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        hideSpinnerProgressWithText(true, "");


        // Initialize listPopupWindow
        listPopupWindow = new ListPopupWindow(MapsActivity.this);
        //listPopupWindow.setAdapter(new TrackListAdapter(MapsActivity.this, R.layout.list_item, (ArrayList<TrackModel>) trackCollection.trackCollectionList));
        listPopupWindow.setAnchorView(trackListButton);
        listPopupWindow.setWidth(900);
        listPopupWindow.setHeight(600);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(MapsActivity.this);
        trackListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackCollection = readTrackModelCollectionFromMemory();
                listPopupWindow.setAdapter(new TrackListAdapter(MapsActivity.this, R.layout.list_item, (ArrayList<TrackModel>) trackCollection.trackCollectionList));
                listPopupWindow.show();
            }
        });

        // Get Persitency
        trackCollection = readTrackModelCollectionFromMemory();
    }

    //##################################################################################
    //############################__MapFunctions__######################################
    //##################################################################################

     // This callback is triggered when the map is ready to be used.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this);

        mapController = new MapController(mMap);
        trackController = new TrackController(mapController, this);
        locationController = new LocationController(trackController, mapController);

        // Enable usertracking if GPS is enabled
        if(displayGpsStatus()){}else {
            gpsAlertbox("GPS Status", "GPS ist: AUS");
        }
    }

    @Override
    public void onMapLoaded(){
        hideSpinnerProgressWithText(true, "");
    }


    //##################################################################################
    //############################__View_Element_Controls__############################
    //##################################################################################

    // Called when User selects TrackListItem
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
        listPopupWindow.dismiss();
        Intent intent = new Intent(MapsActivity.this, TrackDetailActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }


    public void startOrEndTrack(View view){
        if(isTracking){
            // Enable other Buttons
            loginButton.setEnabled(true);
            trackListButton.setEnabled(true);
            trackButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            try {
                locationManager.removeUpdates(locationController);
            }catch (SecurityException e){
                Log.d("MapsActivity", "onMapReady: Not able to request location updates with Exception: " + e.toString());
            }
            trackController.endTrack();
            isTracking = false;
            hideSpinnerProgressWithText(true, "");
        }else {
            if (displayGpsStatus()) {
                // Disable other Buttons
                loginButton.setEnabled(false);
                trackListButton.setEnabled(false);

                trackButton.setImageResource(R.drawable.ic_stop_black_24dp);
                try {
                    // Update every 30 seconds(30000), With a minimum distance of 200 meters -> Set for Motorcycle
                    // Calculated with an average speed of 60 km/h -> 17 m/s
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 200, locationController);
                }catch (SecurityException e){
                    Log.d("MapsActivity", "onMapReady: Not able to request location updates with Exception: " + e.toString());
                }
                trackController.startTrack();
                isTracking = true;
                hideSpinnerProgressWithText(false, "GPS");
            }else{
                gpsAlertbox("GPS Status", "GPS ist: AUS");
            }
            trackController.startTrack();
            isTracking = true;
        }
    }

    // Hides or shows the progress spinner with given Text
    public void hideSpinnerProgressWithText(boolean b, String text){
        if(b){
            spinner.setVisibility(View.INVISIBLE);
            spinnerText.setVisibility(View.INVISIBLE);
        }else{
            spinnerText.setText(text);
            spinner.setVisibility(View.VISIBLE);
            spinnerText.setVisibility(View.VISIBLE);
        }
    }

    // Creates an Alterbox to inform User that GPS is diable
    private void gpsAlertbox(String title, String mymessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mymessage)
                .setCancelable(false)
                .setTitle(title)
                .setPositiveButton("GPS AN",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent myIntent = new Intent(
                                        Settings.ACTION_SECURITY_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Abbrechen",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // Method to Check GPS is enable or disable
    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }

    // Called by DialogSaveTrack when User is done
    @Override
    public void onFinishSaveDialog(boolean save, String title){
        if (save){
            newTrackModel.title = title;
            trackCollection.trackCollectionList.add(newTrackModel);
            writeTrackModelCollectionToMemory(trackCollection);
            Intent intent = new Intent(MapsActivity.this, TrackDetailActivity.class);
            intent.putExtra("position", trackCollection.trackCollectionList.size()-1);
            startActivity(intent);
        }
    }

    //##################################################################################
    //############################__Other_Functions__###################################
    //##################################################################################

    // Called when TrackController finished a Track
    public void addTrackModel(TrackModel trackModel){
        // Call dialog for naming the Track
        FragmentManager fm = getSupportFragmentManager();
        DialogSaveTrack dialogSaveTrack = new DialogSaveTrack();
        dialogSaveTrack.show(fm, "fragment_save_track");
        newTrackModel = trackModel;
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
