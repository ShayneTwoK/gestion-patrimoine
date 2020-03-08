package sio.example.gestion_patrimoine;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    // CONSTANTES
    private static final String TAGMap = "MapActivity";
    private static final String TAGLocalisation = "Localisation";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    // VARIABLES
    private Boolean permissionsAccordee = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private MapsActivity MapsActivity;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map Prête", Toast.LENGTH_SHORT).show();
        Log.d(TAGMap, "onMapReady: map correctement afficher");
        mMap = googleMap;

        if (permissionsAccordee) {
            getLocalisationDevice();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (isServicesOK()) {
            getPermissionLocalisation();
        }

    }
    ///// INITIALISE LA MAP /////
    private void initMap() {
        Log.d(TAGMap, "initMap: Map Initialisé");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapsActivity.this);
    }
    ///// RECUPERE LA PERMISSION DONNER PAR LE USER /////
    private void getPermissionLocalisation() {
        Log.d(TAGMap, "getLocationPermission: Permissions de localisation");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                permissionsAccordee = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    ///// RETOUR DE LA DEMANDE DE PERMISSION DU GPS /////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAGMap, "onRequestPermissionsResult: Appel de la méthode");
        permissionsAccordee = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            permissionsAccordee = false;
                            Log.d(TAGMap, "onRequestPermissionsResult: Permission refusé");
                            return;
                        }
                    }
                    Log.d(TAGMap, "onRequestPermissionsResult: Permission accordé");
                    permissionsAccordee = true;
                    // Initialisé la Map Google
                    initMap();
                }
            }
        }
    }
    ///// VERIFICATION DES SERVICES GOOGLE FONCTIONELS /////
    public boolean isServicesOK() {
        Log.d(TAGMap, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAGMap, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAGMap, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapsActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    ///// LOCALISATION DU SMARTPHONE /////
    private void getLocalisationDevice (){
        Log.d(TAGLocalisation, "getLocalisationDevice : Localisation actuelle de l'appareil");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity);

        try {
            if (permissionsAccordee){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Log.d(TAGLocalisation, "onComplete : Localisation actuelle trouvé !");
                            Location localisationCourante = (Location) task.getResult();
                            bougerCamera(new LatLng(localisationCourante.getLatitude(),
                                    localisationCourante.getLongitude()), DEFAULT_ZOOM);
                        } else {
                            Log.d(TAGLocalisation, "onComplete : Localisation actuelle null");
                            Toast.makeText(MapsActivity, "impossible de récupérer la localisation actuelle", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException Se) {
            Log.d(TAGLocalisation,"getDeviceLocation : SecurityException : " + Se.getMessage());
        }
    }

    private void bougerCamera(LatLng latitudeLongitude, float zoom) {
        Log.d(TAGLocalisation, "mouvementCamera : latitude : " + latitudeLongitude.latitude + ", longitude : " + latitudeLongitude.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latitudeLongitude, zoom));
    }

}