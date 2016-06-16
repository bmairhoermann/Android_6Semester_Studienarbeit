package de.hof_university.studienarbeitss16.studienarbeit_android_ss16;

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
import android.util.Log;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.LatitudeLongitudeModel;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.TrackCollection;
import de.hof_university.studienarbeitss16.studienarbeit_android_ss16.Model.TrackModel;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager=null;
    private LocationListener locationListener=null;
    private Boolean isTracking = false;
    private Boolean isFirstPosition = true;
    private Boolean isLastPosition = false;
    private Boolean followUser = true;
    private Button trackButton;
    private TrackCollection trackCollection;
    private TrackModel trackModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        trackButton = (Button)findViewById(R.id.trackButton);

        trackCollection = new TrackCollection();
        locationListener = new MyLocationListener();

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

        // Add a marker in Sydney and move the camera

        /*
        LatLng hof = new LatLng(50.3113026,11.8542196);
        mMap.addMarker(new MarkerOptions().position(hof).title("Marker in Hof"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hof, (float) 15));
        */

        // Enable usertracking if GPS is enabled
        if(displayGpsStatus()){
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, locationListener);
            }catch (SecurityException e){
                Log.d("MapsActivity", "onMapReady: Not able to request location updates with Exception: " + e.toString());
            }
        }else {
            alertbox("GPS Status", "GPS ist: AUS");
        }
    }

    public void startOrEndTrack(View view){
        if(isTracking){
            // Clean for next Track
            isLastPosition = true;
            trackButton.setText("Route starten");

            Log.d("EndedTracK", "startOrEndTrack: Collection Count: " + trackCollection.trackCollectionList.size());
        }else {
            if (displayGpsStatus()){
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, locationListener);
                }catch (SecurityException e){
                    Log.d("MapsActivity", "onMapReady: Not able to request location updates with Exception: " + e.toString());
                }
                trackButton.setText("Route beenden");
                isTracking = true;
                isFirstPosition = true;
                isLastPosition = false;
                followUser = true;
                trackModel = new TrackModel();
            }else{
                alertbox("GPS Status", "GPS ist: AUS");
            }

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





    public class MyLocationListener implements LocationListener {

        int GPS_STATUS = 0;

        LatitudeLongitudeModel lastLatLng;
        LatitudeLongitudeModel currentLatLng;
        LatitudeLongitudeModel newLatLong;

        @Override
        public  void onLocationChanged(Location loc){

            // Move Camera with User
            if(followUser) {
                LatLng currentPosition = new LatLng(loc.getLatitude(), loc.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(currentPosition, (float) 17, (float) 0, (float) 0)));
            }

            // Handle Tracking
            if(isTracking){
                if(isFirstPosition){
                    lastLatLng = new LatitudeLongitudeModel(loc.getLatitude(), loc.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(lastLatLng.toGoogleLatLng()).title("Startpunkt"));
                    isFirstPosition = false;
                    trackModel.firstPosition = new LatitudeLongitudeModel(lastLatLng.latitude, lastLatLng.longitude);

                }else {
                    newLatLong = new LatitudeLongitudeModel(loc.getLatitude(), loc.getLongitude());

                    mMap.addPolyline(new PolylineOptions()
                            .add(lastLatLng.toGoogleLatLng(), newLatLong.toGoogleLatLng())
                            .width(10)
                            .color(Color.RED));
                    trackModel.trackList.add(newLatLong);
                    lastLatLng = newLatLong;

                    if(isLastPosition){
                        // Set isTracking false here, so no more updates for the Track will be made
                        isTracking = false;
                        trackModel.lastPosition = newLatLong;
                        trackModel.title = "Test";
                        mMap.addMarker(new MarkerOptions().position(newLatLong.toGoogleLatLng()).title("Endpunkt"));
                        trackCollection.trackCollectionList.add(trackModel);
                    }
                }
            }
        }


        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Status:
            //public static final int OUT_OF_SERVICE = 0;
            //public static final int TEMPORARILY_UNAVAILABLE = 1;
            //public static final int AVAILABLE = 2;
            GPS_STATUS = status;

            // Catch when User looses GPS-Connection and turns of Tracking
            if (GPS_STATUS == 1 || GPS_STATUS == 0){
                if (isLastPosition == true){
                    isTracking = false;
                    trackModel.lastPosition = newLatLong;
                    trackModel.title = "Test";
                    mMap.addMarker(new MarkerOptions().position(newLatLong.toGoogleLatLng()).title("Endpunkt"));
                    // Saving Track to Collection
                    trackCollection.trackCollectionList.add(trackModel);
                }
            }

        }

    }
}
