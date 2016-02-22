package pa.com.poroto.panatransandroid.fragment;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import pa.com.poroto.panatransandroid.StationActivity;
import pa.com.poroto.panatransandroid.api.QueryStationListModel;
import pa.com.poroto.panatransandroid.loaders.StationListLoader;

/**
 * Created by zubietaroberto on 05/04/15.
 */
public class CustomMapFragment extends SupportMapFragment implements LoaderManager.LoaderCallbacks<List<QueryStationListModel.Station>> {

    final private HashMap<Marker, String> mMarkerList = new HashMap<>();
    private boolean mIsZoomedToUser = false;
    private GoogleMap mGoogleMap;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Load Map Asynchronously
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                //Build the map
                mGoogleMap = googleMap;
                setupMap();

                // Load data
                Toast.makeText(getActivity(), "Loading Bus Stops...", Toast.LENGTH_LONG).show();
                getLoaderManager().initLoader(0, null, CustomMapFragment.this);
            }
        });
    }

    /*
     * LoaderCallbacks overrides
     */
    @Override
    public Loader<List<QueryStationListModel.Station>> onCreateLoader(int id, Bundle args) {
        return new StationListLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<QueryStationListModel.Station>> loader, @Nullable List<QueryStationListModel.Station> data) {

        // An error occurred
        if (data == null){
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
            return;
        }

        if (mGoogleMap != null) {

            // Clear the Map
            removeAllMarkers();

            // Build Markers
            for (QueryStationListModel.Station station: data){
                //Add marker for each station
                final LatLng position = new LatLng(station.lat, station.lon);
                final Marker marker = mGoogleMap.addMarker(
                        new MarkerOptions()
                                .position(position)
                                .snippet(station.name)
                );

                //Add Marker to map
                mMarkerList.put(marker, station.id);
            }

        }

        Toast.makeText(getActivity(), "Load Complete", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onLoaderReset(Loader<List<QueryStationListModel.Station>> loader) {

        // Remove everything
        removeAllMarkers();
    }

    /*
     * Custom private methods
     */
    private void removeAllMarkers(){
        for (Marker marker: mMarkerList.keySet()){
            marker.remove();
        }
        mMarkerList.clear();
    }

    final void setupMap() {
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final Intent intent = StationActivity.launchMe(mMarkerList.get(marker), getActivity());
                startActivity(intent);
                return false;
            }
        });

        // Get the LocationManagerService
        final LocationManager locationManager =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Ask for a single update for a single zoom
        locationManager.requestSingleUpdate(new Criteria(), new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Do Nothing
            }

            @Override
            public void onProviderEnabled(String provider) {
                // Do Nothing
            }

            @Override
            public void onProviderDisabled(String provider) {
                // Do Nothing
            }
        }, Looper.getMainLooper());

    }

    private void setLocation(@Nullable final Location location) {
        if (location != null && !mIsZoomedToUser){
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()),
                    13
            ));

            //Autozoom only once
            mIsZoomedToUser = true;
        }

    }
}
