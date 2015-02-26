package com.example.luchano.wildliferecording;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    private Context context = this;
    GoogleMap googlemap;
    MarkerDataSource data;

//    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_activity2);
        initMap();
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = lm.getBestProvider(new Criteria(), true);

        if (provider == null) {
            onProviderDisabled(provider);
        }
        data = new MarkerDataSource(context);
        try {
            data.open();

        } catch (Exception e) {
        }

        List<MyMarkerObj> m = data.getMyMarkers();
        for (int i = 0; i < m.size(); i++) {
            String[] slatlng = m.get(i).getPosition().split(" ");
            LatLng lat = new LatLng(Double.valueOf(slatlng[0]), Double.valueOf(slatlng[1]));
            googlemap.addMarker(new MarkerOptions()
                            .title(m.get(i).getTitle())
                            .snippet(m.get(i).getSnippet())
                            .position(lat)
            );

        }


        googlemap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            public void onInfoWindowClick(Marker marker) {
                marker.remove();
                data.deleteMarker(new MyMarkerObj(marker.getTitle(), marker.getSnippet(), marker.getPosition().latitude + " " + marker.getPosition().longitude));
            }
        });
        googlemap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            public void onMapLongClick(final LatLng latlng) {
                LayoutInflater li = LayoutInflater.from(context);
                final View v = li.inflate(R.layout.alertlayout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(v);
                builder.setCancelable(false);

                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        EditText title = (EditText) v.findViewById(R.id.ettitle);
                        EditText snippet = (EditText) v.findViewById(R.id.etsnippet);
                        googlemap.addMarker(new MarkerOptions()
                                        .title(title.getText().toString())
                                        .snippet(snippet.getText().toString())
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                                        .position(latlng)
                        );
                        String sll = latlng.latitude + " " + latlng.longitude;
                        data.addMarker(new MyMarkerObj(title.getText().toString(), snippet.getText().toString(), sll));
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });
    }

    public void onLocationChanged(Location location) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onProviderDisabled(String provider) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Phone is in airplane mode");
        builder.setCancelable(false);
        builder.setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Intent startGps = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(startGps);
            }
        });
        builder.setNegativeButton("Leave GPS off", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initMap() {
        SupportMapFragment mf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googlemap = mf.getMap();

        googlemap.setMyLocationEnabled(true);
        googlemap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    @Override
    protected void onPause() {
        data.close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        try {
            data.open();

        } catch (Exception e) {
        }
        super.onResume();
    }


}


//        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
//            @Override
//            public void onMapLongClick(final LatLng latLng) {
//                LayoutInflater li = LayoutInflater.from(context);
//                final View view = li.inflate(R.layout.alertlayout, null);
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setView(view);
//                builder.setCancelable(false);
//                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        EditText title = (EditText) view.findViewById(R.id.etTitle);
//                        EditText snippet = (EditText) view.findViewById(R.id.etSnippet);
//                        mMap.addMarker(new MarkerOptions()
//                                .position(latLng)
//                                .snippet(snippet.getText().toString())
//                                .title(title.getText().toString()));
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                AlertDialog alert = builder.create();
//                alert.show();
//            }
//        });
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        setUpMapIfNeeded();
//    }
//
//    private void setUpMapIfNeeded() {
//        if (mMap == null) {
//
//            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
//                    .getMap();
//
//            if (mMap != null) {
//                setUpMap();
//            }
//        }
//    }
//
//
//    private void setUpMap() {
//        mMap.addMarker(new MarkerOptions().position(new LatLng(51.896343, 1.796991)).title("Cold Sea"));
//        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//    }
//
//
//
//
//}



