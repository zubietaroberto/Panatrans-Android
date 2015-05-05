package pa.com.poroto.panatransandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pa.com.poroto.panatransandroid.api.PanatransApi;
import pa.com.poroto.panatransandroid.api.QueryStationModel;
import pa.com.poroto.panatransandroid.ui.RoutesRecyclerAdapter;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
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

        // Setup Recycler View
        mAdapter = new RoutesRecyclerAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        setupData(getIntent().getExtras().getString(sStation_ID));

    }

    private void setupData(String pID){

        //Get API Object
        final PanatransApi.PanatransApiInterface api = PanatransApi.build();

        //UI feedback
        setIsProgressShown(true);

        AndroidObservable

                //Request Data
                .bindActivity(this, api.getStopById(pID))

                //Setup Threads
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

                //Subscribe
                .subscribe(new Observer<QueryStationModel>() {
                    @Override
                    public void onCompleted() {

                        //UI Feedback
                        if (mAdapter.getItemCount() > 0) {
                            setIsProgressShown(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(QueryStationModel station) {

                        //Add Route to Adapter
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
