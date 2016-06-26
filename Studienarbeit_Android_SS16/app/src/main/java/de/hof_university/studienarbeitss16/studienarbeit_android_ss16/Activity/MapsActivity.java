package de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.ListPopupWindow;
import android.util.Log;
import android.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.share.model.*;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Controller.Adapter.TrackListAdapter;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Controller.LocationController;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Controller.MapController;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Controller.TrackController;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Controller.shareController;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.LatitudeLongitudeModel;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.TrackCollection;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.TrackModel;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemClickListener{

    // ViewElements
    private Button trackButton;
    private ProgressBar spinner;
    private TextView spinnerText;
    private Button trackListButton;
    private ListPopupWindow listPopupWindow;

    // ControllerClasses
    private MapController mapController = null;
    private TrackController trackController = null;
    private LocationController locationController = null;

    // Other
    private GoogleMap mMap;
    private LocationManager locationManager=null;
    private Boolean isTracking = false;
    public TrackCollection trackCollection = new TrackCollection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Initialize ViewElements
        trackButton = (Button)findViewById(R.id.trackButton);
        spinner = (ProgressBar) findViewById(R.id.spinnerProgress);
        spinnerText = (TextView)findViewById(R.id.spinnerText);
        trackListButton = (Button) findViewById(R.id.trackListButton);

        // Initialize listPopupWindow
        listPopupWindow = new ListPopupWindow(MapsActivity.this);
        listPopupWindow.setAdapter(new TrackListAdapter(MapsActivity.this, R.layout.list_item, (ArrayList<TrackModel>) trackCollection.trackCollectionList));
        listPopupWindow.setAnchorView(trackListButton);
        listPopupWindow.setWidth(500);
        listPopupWindow.setHeight(600);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(MapsActivity.this);
        trackListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listPopupWindow.show();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mapController = new MapController(mMap);
        trackController = new TrackController(mapController, this);
        locationController = new LocationController(trackController, mapController);

        // Enable usertracking if GPS is enabled
        if(displayGpsStatus()){
            try {
                //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0.5f, locationController);
            }catch (SecurityException e){
                Log.d("MapsActivity", "onMapReady: Not able to request location updates with Exception: " + e.toString());
            }
        }else {
            alertbox("GPS Status", "GPS ist: AUS");
        }
    }

    // Catch User-Short-Tap on TrackListItem -> display Track on Map
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        mapController.showTrack(trackCollection.trackCollectionList.get(position));
    }

    public void shareTrack(View view){
            shareController test = new shareController(this);
            test.shareTrack();

    }

    public void startOrEndTrack(View view){
        if(isTracking){
            trackController.endTrack();
            isTracking = false;
            trackButton.setText("Route starten");
            hideSpinnerProgress(true);
        }else {
            trackController.startTrack();
            isTracking = true;
            trackButton.setText("Route beenden");
            hideSpinnerProgress(false);
        }

    }

    /*----------Method to create an AlertBox ------------- */
    protected void alertbox(String title, String mymessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mymessage)
                .setCancelable(false)
                .setTitle(title)
                .setPositiveButton("GPS AN",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
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

    /*----Method to Check GPS is enable or disable ----- */
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

    public TrackModel displaySaveDialog(final TrackModel trackModel){
        new AlertDialog.Builder(this)
                .setTitle("Save Track")
                .setMessage("Startpoint: " + trackModel.firstPosition + ", Entpoint: " + trackModel.lastPosition + ", Tracksize: " + trackModel.trackList.size())
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        mapController.clearMap();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        mapController.clearMap();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        return trackModel;
    }

    public void addTrackModel(TrackModel trackModel){
        trackModel.title = Integer.toString(trackCollection.trackCollectionList.size());
        trackCollection.trackCollectionList.add(trackModel);
    }

    public void hideSpinnerProgress(boolean b){
        if(b){
            spinner.setVisibility(View.INVISIBLE);
            spinnerText.setVisibility(View.INVISIBLE);
        }else{
            spinner.setVisibility(View.VISIBLE);
            spinnerText.setVisibility(View.VISIBLE);
        }
    }
}
