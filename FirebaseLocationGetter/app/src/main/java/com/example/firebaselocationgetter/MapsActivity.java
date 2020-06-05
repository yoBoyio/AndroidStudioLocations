package com.example.firebaselocationgetter;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Display;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener,OnMapReadyCallback {
    //map
    private GoogleMap mMap;
    //base
    private FirebaseFirestore firebaseFirestore;
    //base document
    private DocumentReference documentReference;
    //xml
    private TextView sensor;
    private TextView lat;
    private TextView longt;
    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //match xml tags
        lat=findViewById(R.id.long_lat);
        description=findViewById(R.id.description);

        //init firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        //init base objects
        for(int i=0;i<5;i++){
            documentReference=this.firebaseFirestore.collection("Map").document("location"+i);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        String latitude = documentSnapshot.getString("Latitude");
                        String longitude = documentSnapshot.getString("Longitude");
                        String color = documentSnapshot.getString("Color");
                        String sensor = documentSnapshot.getString("Sensor");
                        String description = documentSnapshot.getString("Description");
                        //set latitude and longitude
                        LatLng latlng = new LatLng(Float.parseFloat(latitude), Float.parseFloat(longitude));

                        //depends to color set values to marker ,sensor,description ,latlng
                        switch (color) {
                            case "YELLOW":
                                mMap.addMarker(new MarkerOptions().position(latlng).icon(
                                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).
                                        title(description + ", light:" + sensor));
                                break;
                            case "PINK":
                                mMap.addMarker(new MarkerOptions().position(latlng).icon(
                                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)).
                                        title(description + ", light:" + sensor));
                                break;
                            case "RED":
                                mMap.addMarker(new MarkerOptions().position(latlng).icon(
                                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).
                                        title(description + ", light:" + sensor));
                                break;
                            case "GREEN":
                                mMap.addMarker(new MarkerOptions().position(latlng).icon(
                                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).
                                        title(description + ", light: " + sensor));
                                break;
                            case "BLUE":
                                mMap.addMarker(new MarkerOptions().position(latlng).icon(
                                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).
                                        title(description + ", light: " + sensor));
                                break;
                            default:
                                mMap.addMarker(new MarkerOptions().position(latlng).
                                        title(description + ", light: " + sensor));
                                break;
                        }
                        //if load from database succeed display text
                        Toast.makeText(MapsActivity.this,"Load successfully",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //if load from database fails display text
                    Toast.makeText(MapsActivity.this,"Load fails",Toast.LENGTH_SHORT).show();

                }
            });
        }

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
    //load map and send events on marker click
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //set text boxes values for every marker
            lat.setText(marker.getPosition().toString());
            description.setText(marker.getTitle());
        return false;
    }

}

