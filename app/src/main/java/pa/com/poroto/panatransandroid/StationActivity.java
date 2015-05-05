package pa.com.poroto.panatransandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pa.com.poroto.panatransandroid.api.PanatransApi;
import pa.com.poroto.panatransandroid.api.QueryStationModel;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zubietaroberto on 04/29/15.
 */
public class StationActivity extends AppCompatActivity {

    final private GsonConverter mConverter = new GsonConverter(new Gson());

    static public String sStation_ID = "pa.com.poroto.pantransandroid.stationID";

    @InjectView(R.id.recycler_view)
    public RecyclerView mRecyclerView;

    @InjectView(R.id.progress)
    public ProgressBar mProgressBar;

    private RoutesRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stationdetails);
        ButterKnife.inject(this);

        mAdapter = new RoutesRecyclerAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        final String id = getIntent().getExtras().getString(sStation_ID);

        final PanatransApi.PanatransApiInterface api = PanatransApi.build();

        setIsProgressShown(true);

        AndroidObservable
                .bindActivity(this, api.getStopById(id))
                .map(new Func1<Response, QueryStationModel.StationData>() {
                    @Override
                    public QueryStationModel.StationData call(Response response) {

                        try {
                            final QueryStationModel status = (QueryStationModel) mConverter.fromBody(
                                    response.getBody(),
                                    QueryStationModel.class);

                            if (status != null && TextUtils.equals(status.status, "success")) {
                                return status.data;
                            }
                        } catch (ConversionException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<QueryStationModel.StationData>() {
                    @Override
                    public void onCompleted() {
                        if (!mAdapter.mRouteList.isEmpty()){
                            setIsProgressShown(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(QueryStationModel.StationData individualStop) {
                        if (individualStop != null) {
                            StationActivity.this.setTitle(individualStop.name);
                            mAdapter.setupAdapter(individualStop.routes);
                        }
                    }
                });



    }

    public void setIsProgressShown(final boolean pShowProgress){
        if (pShowProgress){
            mProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public static Intent launchMe(final String pStationID, final Context pContext){
        final Intent intent = new Intent(pContext, StationActivity.class);
        final Bundle bundle = new Bundle();
        bundle.putString(StationActivity.sStation_ID, pStationID);
        intent.putExtras(bundle);
        return intent;
    }
}
