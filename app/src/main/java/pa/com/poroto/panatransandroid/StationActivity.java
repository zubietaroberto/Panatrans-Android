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

import butterknife.ButterKnife;
import butterknife.InjectView;
import pa.com.poroto.panatransandroid.api.PanatransApi;
import pa.com.poroto.panatransandroid.api.QueryStationModel;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zubietaroberto on 04/29/15.
 */
public class StationActivity extends AppCompatActivity {

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
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<QueryStationModel>() {
                    @Override
                    public void onCompleted() {
                        if (!mAdapter.mRouteList.isEmpty()) {
                            setIsProgressShown(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(QueryStationModel station) {
                        if (station != null && station.data != null) {
                            StationActivity.this.setTitle(station.data.name);
                            mAdapter.setupAdapter(station.data.routes);
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
