package pa.com.poroto.panatransandroid.fragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import pa.com.poroto.panatransandroid.StationActivity;
import pa.com.poroto.panatransandroid.api.PanatransApi;
import pa.com.poroto.panatransandroid.api.QueryStationListModel;
import rx.Observable;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zubietaroberto on 05/04/15.
 */
public class CustomMapFragment extends MapFragment {

    final private HashMap<Marker, String> mMarkerList = new HashMap<>();
    private boolean mIsZoomedToUser = false;
    private GoogleMap mGoogleMap;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mGoogleMap = getMap();

        Toast.makeText(this.getActivity(), "Loading Bus Stops...", Toast.LENGTH_LONG).show();
        setupData();
        setupMap();
    }

    private void setupData() {

        //Get the API object
        final PanatransApi.PanatransApiInterface api = PanatransApi.build();

        AndroidObservable

                //Start Request
                .bindFragment(this, api.getStops())

                //Get individual stations
                .flatMap(new Func1<QueryStationListModel, Observable<QueryStationListModel.Station>>() {
                    @Override
                    public Observable<QueryStationListModel.Station> call(QueryStationListModel station) {
                        return Observable.from(station.data);
                    }
                })

                //Setup threads
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

                //Subscribe
                .subscribe(new Subscriber<QueryStationListModel.Station>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(getActivity(), "Load Complete", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(QueryStationListModel.Station station) {

                        if (mGoogleMap != null) {
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
                });

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
