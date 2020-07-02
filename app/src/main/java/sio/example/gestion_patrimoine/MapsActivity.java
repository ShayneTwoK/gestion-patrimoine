package sio.example.gestion_patrimoine;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapsActivity
        extends AppCompatActivity
        implements LocationListener {

    // CONSTANTES
    private static final String TAGMap = "MapActivity";
    private static final String TAGLocalisation = "Localisation";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    // VARIABLES
    private GoogleMap mMap;
    private LocationManager lm;
    private SupportMapFragment supportMapFragment;


//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        Toast.makeText(this, "Map Prête", Toast.LENGTH_SHORT).show();
//        Log.d(TAGMap, "onMapReady: map correctement afficher");
//        mMap = googleMap;
//
//        if (permissionsAccordee) {
//            getLocalisationDevice();
//            // Bouton focus,zoom sur la position actuel
//            mMap.setOnMyLocationButtonClickListener(this);
//            // Event OnClick activé du boutton "Focus"
//            mMap.setOnMyLocationClickListener(this);
//        }
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        supportMapFragment = (SupportMapFragment) supportFragmentManager.findFragmentById(R.id.map);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isServicesOK()) {
            verificationDesPermissions();
        }
    }

    private void verificationDesPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        }
        if (lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10000, 0, this);
        }
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
        }

        initMap();
    }

    ///// RETOUR DE LA DEMANDE DE PERMISSION DU GPS /////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAGLocalisation, "onRequestPermissionsResult: Appel de la méthode");

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            verificationDesPermissions();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (lm != null) {
            lm.removeUpdates(this);
        }
    }

    ///// INITIALISE LA MAP /////
    @SuppressWarnings("MissingPermission")
    private void initMap() {
        Log.d(TAGMap, "initMap: Map Initialisé");
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAGMap, "onMapReady: map correctement afficher");
                MapsActivity.this.mMap = googleMap;
                // Localisation de Lyon
                CameraUpdate Lyon = CameraUpdateFactory.newLatLngZoom(new LatLng(45.75, 4.85), 12f);
                // Init de la Map sur Lyon
                googleMap.moveCamera(Lyon);
                googleMap.setMyLocationEnabled(true);
            }
        });
    }

    ///// VERIFICATION DES SERVICES GOOGLE FONCTIONNELS /////
    public boolean isServicesOK() {
        Log.d(TAGMap, "isServicesOK: Check de la version des Google Services");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            // le user peut faire des MapRequest
            Log.d(TAGMap, "isServicesOK: Google Play Services fonctionne");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAGMap, "isServicesOK: Erreur récuperation des services Google");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapsActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Requete sur la Map impossible", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
//        double latitude = location.getLatitude();
//        double longitude = location.getLongitude();

        // Ramene le focus de la fenetre sur la position de l'user
//        if (mMap != null) {
//            LatLng googleLocation = new LatLng(latitude, longitude);
//            CameraUpdate positionActuelleUser = CameraUpdateFactory.newLatLng(googleLocation);
//            mMap.moveCamera(positionActuelleUser);
//        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}