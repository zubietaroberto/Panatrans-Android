package pa.com.poroto.panatransandroid.fragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import pa.com.poroto.panatransandroid.StationActivity;
import pa.com.poroto.panatransandroid.api.PanatransApi;
import pa.com.poroto.panatransandroid.api.QueryStationListModel;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
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

    final private GsonConverter mConverter = new GsonConverter(new Gson());
    final private HashMap<Marker, String> mMarkerList = new HashMap<>();
    private boolean mIsZoomedToUser = false;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final GoogleMap map = getMap();

        final PanatransApi.PanatransApiInterface api = PanatransApi.build();

        Toast.makeText(this.getActivity(), "Loading Bus Stops...", Toast.LENGTH_LONG).show();
        AndroidObservable.bindFragment(this, api.getStops())
                .map(new Func1<Response, ArrayList<QueryStationListModel.Station>>() {
                    @Override
                    public ArrayList<QueryStationListModel.Station> call(Response response) {
                        try {
                            final QueryStationListModel status = (QueryStationListModel) mConverter.fromBody(
                                    response.getBody(),
                                    QueryStationListModel.class);

                            if (status != null && TextUtils.equals(status.status, "success")) {
                                return status.data;
                            }
                        } catch (ConversionException e) {
                            e.printStackTrace();
                        }

                        return new ArrayList<>();
                    }
                })
                .flatMap(new Func1<ArrayList<QueryStationListModel.Station>, Observable<QueryStationListModel.Station>>() {
                    @Override
                    public Observable<QueryStationListModel.Station> call(ArrayList<QueryStationListModel.Station> stations) {
                        return Observable.from(stations);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
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
                        final LatLng position = new LatLng(station.lat, station.lon);
                        mMarkerList.put(map.addMarker(
                                        new MarkerOptions()
                                                .position(position)
                                                .snippet(station.name)
                                ),
                                station.id
                        );
                    }
                });

        map.setMyLocationEnabled(true);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final Intent intent = new Intent(getActivity(), StationActivity.class);
                final String station_id = mMarkerList.get(marker);
                final Bundle bundle = new Bundle();
                bundle.putString(StationActivity.sStation_ID, station_id);
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }
        });
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (location != null && !mIsZoomedToUser) {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(
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
