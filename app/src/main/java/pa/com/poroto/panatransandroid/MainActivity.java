package pa.com.poroto.panatransandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import pa.com.poroto.panatransandroid.api.PanatransApi;
import pa.com.poroto.panatransandroid.api.QueryStationListModel;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    final private GsonConverter mConverter = new GsonConverter(new Gson());
    final private HashMap<Marker, String> mMarkerList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MapFragment fragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        final GoogleMap map = fragment.getMap();

        final RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(PanatransApi.sURL)
                .build();

        final PanatransApi api = adapter.create(PanatransApi.class);

        AndroidObservable.bindActivity(this, api.getStops())
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
                        Toast.makeText(MainActivity.this, "Load Complete", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                final Intent intent = new Intent(MainActivity.this, StationActivity.class);
                final String station_id = mMarkerList.get(marker);
                final Bundle bundle = new Bundle();
                bundle.putString(StationActivity.sStation_ID, station_id);
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
