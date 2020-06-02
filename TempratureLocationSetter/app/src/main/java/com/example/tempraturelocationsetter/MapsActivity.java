package com.example.tempraturelocationsetter;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements SensorEventListener,GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    private boolean LocationExists=false;
    //sensor manager
    private SensorManager sensorManager;
    private  Sensor gravity;
    //longitude latitude
    private double latitude;
    private double longitude;
    //firebase
    public static FirebaseFirestore db;
    //xml elements
    private EditText input;
    private Button button;
    private Spinner spinner;
    private TextView sensorText;
    private ArrayList<String> values = new ArrayList<>();
    //map
    private GoogleMap mMap;
    //location
     LocationListener locationListener;
     //location counter
    private int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //manage  sensors
        sensorManager= (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //initialize base
        db = FirebaseFirestore.getInstance();
        //matching xml tags by id
        sensorText=findViewById(R.id.sensorView);
        input = findViewById(R.id.input);
        button = findViewById(R.id.btn);
        spinner = findViewById(R.id.spinner);
        //initialize spinner colors
        values.add("Blue");
        values.add("Red");
        values.add("Yellow");
        values.add("Green");
        values.add("Pink");
        ArrayAdapter<String> colors=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,  values);
        spinner.setAdapter(colors);

        //location permissions
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String lat_value=latitude+"";
                final String long_value=longitude+"";
                final String color_value=spinner.getSelectedItem().toString().toUpperCase();
                final String desc_value=input.getText().toString();
                final String sensor_value=sensorText.getText().toString();

                if (LocationExists && !input.getText().toString().trim().equals("")) {
                    Map<String, Object> location = new HashMap<>();
                    //adding the right values to the location
                    location.put("Latitude", lat_value);
                    location.put("Longitude", long_value);
                    location.put("Color", color_value);
                    location.put("Description", desc_value);
                    location.put("Sensor", sensor_value);
                    counter++;
                    //clean text box
                    input.setText("");
                    //sent option to database
                        db.collection("Map").document("location"+counter).set(location).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            //if it succeeds
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MapsActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            //if it fails
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MapsActivity.this, "Failed...", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
                else {
                    Toast.makeText(MapsActivity.this, "Write something or select location", Toast.LENGTH_SHORT).show();

                }
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
        mMap=googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //location initialized
                LocationExists=true;
                //marker
                MarkerOptions markerOptions=new MarkerOptions();
                //set marker position
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude+ " : "+ latLng.longitude);
                mMap.clear();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                mMap.addMarker(markerOptions);
                latitude= latLng.latitude;
                longitude=latLng.longitude;
                 String col =  spinner.getSelectedItem().toString().toUpperCase();

                switch (col) {
                    case "YELLOW":
                        mMap.addMarker(markerOptions.icon(
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        break;
                    case "RED":
                        mMap.addMarker(markerOptions.icon(
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        break;
                    case "BLUE":
                        mMap.addMarker(markerOptions.icon(
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        break;
                    case "GREEN":
                        mMap.addMarker(markerOptions.icon(
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        break;
                    case "PINK":
                        mMap.addMarker(markerOptions.icon(
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                        break;
                    default:
                        mMap.addMarker(markerOptions);
                        break;
                }
            }
        });


    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }
    @Override
    //on pause,set sensor off on pause
    public void onPause( ) {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    //set type of sensor and check if exist on device
    public void onResume(){
        super.onResume();
        gravity=sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if(gravity!=null){
            sensorManager.registerListener(this,gravity,SensorManager.SENSOR_DELAY_NORMAL);
        }else {
            Toast.makeText(this,"No gravity sensor found on device",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {

        //set text view value
        sensorText.setText(Float.toString(event.values[0]) );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
