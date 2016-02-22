package pa.com.poroto.panatransandroid.fragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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

        mGoogleMap = getMap();
        setupMap();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Start Loading
        Toast.makeText(this.getActivity(), "Loading Bus Stops...", Toast.LENGTH_LONG).show();
        getLoaderManager().initLoader(0, null, this);
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

        // An error occured
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
        mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (location != null && !mIsZoomedToUser) {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(), location.getLongitude()),
                            13
                    ));

                    //Autozoom only once
                    mIsZoomedToUser = true;
                }
            }
        });

    }
}
