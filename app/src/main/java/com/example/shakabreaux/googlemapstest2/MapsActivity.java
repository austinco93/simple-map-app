package com.example.shakabreaux.googlemapstest2;

import android.app.Activity;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import android.widget.AdapterView.OnItemSelectedListener;

import org.json.JSONObject;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ToggleButton;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnItemSelectedListener {

    private GoogleMap mMap;
    private Marker marker;
    private HashSet<Marker> allMarkersMap = new HashSet<Marker>();
    private Spinner spinner;
    private EditText et;
    private ToggleButton drawBtn;
    private boolean draw = false;
    private List<Polyline> polylines = new ArrayList<Polyline>();

    private LatLng start = null;
    private LatLng finish = null;

    private Button retBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setupUI(findViewById(R.id.parent));
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
        mMap = mapFragment.getMap();
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        et = (EditText)findViewById(R.id.editText);
        drawBtn = (ToggleButton) findViewById(R.id.drawBtn);
        drawBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    draw = true;
                } else {
                    draw = false;
                }
            }
        });

        retBtn = (Button) findViewById(R.id.retBtn);
        retBtn.setOnClickListener(mClickListener);

        List<String> categories = new ArrayList<String>();
        categories.add("Location");
        categories.add("Vancouver");
        categories.add("Bellingham");
        categories.add("Waikiki");
        categories.add("Sydney");
        categories.add("New York City");
        categories.add("Orlando");
        categories.add("Paris");
        categories.add("London");
        categories.add("Tokyo");
        categories.add("Moscow");
        categories.add("Hong Kong");
        categories.add("Berlin");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);


        if(mMap != null) {
            mMap.setOnMapLongClickListener(new
                GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick (LatLng latLng){
                        MarkerOptions options = new MarkerOptions()
                                .title(latLng.latitude + ", "+latLng.longitude)
                                .position(new LatLng(latLng.latitude, latLng.longitude));

                        allMarkersMap.add(mMap.addMarker(options));
                    }
                });
            mMap.setOnMarkerClickListener(new
                GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick (Marker marker){
                        if(draw == true) {
                            if (start == null) {
                                start = marker.getPosition();
                            } else {
                                finish = marker.getPosition();
                                drawLine(start, finish);
                                start = finish;
                            }
                        }else{
                            marker.showInfoWindow();
                        }
                        return true;
                    }
                });
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void drawLine(LatLng start, LatLng finish) {

        polylines.add(mMap.addPolyline((new PolylineOptions())
                        .add(start, finish).width(5).color(Color.BLUE)
                        .geodesic(true)));
        // move camera to zoom on map
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMap.getCameraPosition().target,
                mMap.getCameraPosition().zoom));
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(MapsActivity.this);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }
    public void findLocation(View v) throws IOException {

        String location = et.getText().toString();
        if(location.equals("") || location.isEmpty()){
            Toast.makeText(MapsActivity.this, "Enter a Location First", Toast.LENGTH_SHORT).show();
        }else {
            et.setText("");
            et.clearFocus();
            Geocoder geocoder = new Geocoder(this);
            List<android.location.Address> list = geocoder.getFromLocationName(location, 1);
            android.location.Address add = list.get(0);
            String locality = add.getLocality();
            LatLng ll = new LatLng(add.getLatitude(), add.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
            mMap.moveCamera(update);
        /*if(marker != null)
            marker.remove();*/
            MarkerOptions markerOptions = new MarkerOptions()
                    .title(add.getLatitude() + ", "+add.getLongitude())
                    .position(new LatLng(add.getLatitude(), add.getLongitude()));
            //marker = mMap.addMarker(markerOptions);
            allMarkersMap.add(mMap.addMarker(markerOptions));
        }
    }

    public void clearMarkers(View v) throws IOException {
        Collection<Marker> markers = new LinkedList<Marker>(allMarkersMap);
        for(Marker marker : allMarkersMap){
            marker.remove();
            markers.add(marker);
        }
        allMarkersMap.removeAll(markers);
        et.setText("");
        spinner.setSelection(0);

    }

    public void clearPaths(View v) {
        for(Polyline polyline : polylines){
            polyline.remove();
        }
        start = null;
        finish = null;
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
        this.mMap = googleMap;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        if(item.equals("Location")){
            //do nothing
        }else {
            Geocoder geocoder = new Geocoder(this);
            List<android.location.Address> list = null;
            try {
                list = geocoder.getFromLocationName(item, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            android.location.Address add = list.get(0);
            String locality = add.getLocality();
            LatLng ll = new LatLng(add.getLatitude(), add.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
            mMap.moveCamera(update);
            MarkerOptions markerOptions = new MarkerOptions()
                    .title(add.getLatitude() + ", "+add.getLongitude())
                    .position(new LatLng(add.getLatitude(), add.getLongitude()));
            allMarkersMap.add(mMap.addMarker(markerOptions));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //do nothing
    }
}
