package edu.ucsb.cs.cs184.npoon.npoongeotweet;

import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<LocationPoint> messageList = new ArrayList<>();
    ArrayList<Marker> markerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FB INITIALIZE CALLED", "FB INITIALIZE CALLED!@!!!!");
        FirebaseHelper.Initialize(this);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



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
        Log.d("MAP INITIALIZE CALLED", "MAP INITIALIZE CALLED!@!!!!");
        mMap.setMinZoomPreference(15.0f);
        LatLng UCSB = new LatLng(34.412936, -119.847863);
        //mMap.addMarker(new MarkerOptions().position(UCSB).title("UCSB"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(UCSB));

        LocationPoint LP = new LocationPoint();
        LP.setLocationPointListener(new LocationPoint.LocationPointListener() {
            @Override
            public void onObjectReady(String title) {

            }

            @Override
            public void onDataLoaded(LocationPoint data) {
                messageList.add(data);
                String tweetContent = data.getContent();
                Log.d("passedContent", tweetContent);
                double Lat = data.getLatitude();
                Log.d("passedLatitude", String.valueOf(Lat));
                double Long = data.getLongitude();
                Log.d("passedLongitude", String.valueOf(Long));
                LatLng tempLocation = new LatLng(Lat, Long);
                Marker marker = mMap.addMarker(new MarkerOptions().position(tempLocation).title(tweetContent));
                markerList.add(marker);
            }

            @Override
            public void onDataRemoved(LocationPoint data) {
                for (int i = 0; i < markerList.size(); i++)
                {
                    Marker marker = markerList.get(i);
                    if (data.getContent().equals(marker.getTitle()))
                    {
                        marker.remove();
                        markerList.remove(i);
                    }
                }
                messageList.remove(data);

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                String author = "default";
                String content = "default";

                int likes = 0;


                for (int i = 0; i < messageList.size(); i++)
                {
                    if (marker.getTitle().equals(messageList.get(i).getContent()))
                    {
                        author = messageList.get(i).getAuthor();
                        content = marker.getTitle();
                        likes = messageList.get(i).getLikes();
                    }
                }

                String likeStrings = String.valueOf(likes) + "likes";

                FragmentManager fragmentManager = getFragmentManager();

                TweetFragment markerFragment = new TweetFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Author", author);
                bundle.putString("Content", content);
                bundle.putString("Likes", likeStrings);
                markerFragment.setArguments(bundle);
                markerFragment.show(fragmentManager, "markerFragment");
                Log.d("MARKER CLICKED", "MARKER CLICKED");
                return true;
            }
        });

    }

}
