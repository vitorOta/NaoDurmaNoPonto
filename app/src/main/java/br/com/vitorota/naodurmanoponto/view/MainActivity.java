package br.com.vitorota.naodurmanoponto.view;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import br.com.vitorota.naodurmanoponto.MyApp;
import br.com.vitorota.naodurmanoponto.R;
import br.com.vitorota.naodurmanoponto.service.DespertaService;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
    private EditText etLatitude, etLongitude, etDistancia;
    private Switch swVibrar, swPiscar;
    private Button bLigar;


    private PendingIntent mGeofencePendingIntent;

    protected GoogleApiClient mGoogleApiClient;

    private boolean ativado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ativado = MyApp.getConfig(MyApp.Configs.CONFIG_ATIVADO, false);
        initComponents();

        double lat = -19.512871;
        double lng = -42.611650;

        lat = MyApp.getConfig(MyApp.Configs.CONFIG_LATITUDE,lat);
        lng = MyApp.getConfig(MyApp.Configs.CONFIG_LONGITUDE,lng);

        etLatitude.setText("" + lat);
        etLongitude.setText("" + lng);
        mGeofencePendingIntent = null;
        buildGoogleApiClient();
        try {
            if (getIntent().getExtras().getBoolean("PARAR", false)) {
                removeGeoference();
            }
        } catch (Exception e) {
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    private void initComponents() {
        etLatitude = (EditText) findViewById(R.id.main_etLatitude);
        etLongitude = (EditText) findViewById(R.id.main_etLongitude);
        etDistancia = (EditText) findViewById(R.id.main_etDistancia);
        swVibrar = (Switch) findViewById(R.id.main_swVibrar);
        swPiscar = (Switch) findViewById(R.id.main_swPiscar);
        bLigar = (Button) findViewById(R.id.main_bLigar);

        etLatitude.setText(String.valueOf(MyApp.getConfig(MyApp.Configs.CONFIG_LATITUDE, "")));
        etLongitude.setText(String.valueOf(MyApp.getConfig(MyApp.Configs.CONFIG_LONGITUDE, "")));
        etDistancia.setText(String.valueOf(MyApp.getConfig(MyApp.Configs.CONFIG_DISTANCIA, "")));

        swVibrar.setChecked(MyApp.getConfig(MyApp.Configs.CONFIG_VIBRAR, true));
        swPiscar.setChecked(MyApp.getConfig(MyApp.Configs.CONFIG_PISCAR, false));

        configBLigar();
    }

    private void configBLigar() {
        bLigar.setText(ativado ? "Desligar" : "Ligar");
        bLigar.setBackgroundColor(ativado ? Color.parseColor("#FF7979") : Color.parseColor("#79FF79"));


        swPiscar.setEnabled(!ativado);
        swVibrar.setEnabled(!ativado);
        etLongitude.setEnabled(!ativado);
        etLatitude.setEnabled(!ativado);
        etDistancia.setEnabled(!ativado);
    }

    public void bLigarClick(View view) {
        ativado = !ativado;
        MyApp.putConfig(MyApp.Configs.CONFIG_ATIVADO, ativado);


        MyApp.putConfig(MyApp.Configs.CONFIG_LATITUDE, Double.valueOf(etLatitude.getText().toString()));
        MyApp.putConfig(MyApp.Configs.CONFIG_LONGITUDE, Double.valueOf(etLongitude.getText().toString()));
        MyApp.putConfig(MyApp.Configs.CONFIG_DISTANCIA, Integer.parseInt(etDistancia.getText().toString()));

        MyApp.putConfig(MyApp.Configs.CONFIG_VIBRAR, swVibrar.isChecked());
        MyApp.putConfig(MyApp.Configs.CONFIG_PISCAR, swPiscar.isChecked());
        configBLigar();

        Intent i = new Intent(this, DespertaService.class);
        if (MyApp.isMyServiceRunning(DespertaService.class)) {
            stopService(i);
        }

        if (ativado) {
            addGeoference();
        } else {
            removeGeoference();
        }
    }

    private void addGeoference() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Não conectado", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Log.e("", securityException.getMessage());
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        List<Geofence> mGeofenceList = new ArrayList<>();
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("1")

                // Set the circular region of this geofence.
                .setCircularRegion(
                -19.512871,
                        -42.611650,
                        300
                )
                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(Geofence.NEVER_EXPIRE)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)

                // Create the geofence.
                .build());
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }


    private void removeGeoference() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Não conectado", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Log.e("", securityException.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, DespertaService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(Status status) {

    }
}
