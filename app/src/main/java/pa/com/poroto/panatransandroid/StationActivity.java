package pa.com.poroto.panatransandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import pa.com.poroto.panatransandroid.api.QueryStationModel;
import pa.com.poroto.panatransandroid.loaders.StationItemLoader;
import pa.com.poroto.panatransandroid.ui.RoutesRecyclerAdapter;

/**
 * Created by zubietaroberto on 04/29/15.
 */
public class StationActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<QueryStationModel> {

    static public String sStation_ID = "pa.com.poroto.pantransandroid.stationID";

    @Bind(R.id.recycler_view)
    public RecyclerView mRecyclerView;

    @Bind(R.id.progress)
    public ProgressBar mProgressBar;

    private RoutesRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stationdetails);
        ButterKnife.bind(this);

        // Setup Recycler View
        mAdapter = new RoutesRecyclerAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //Start Loader
        getSupportLoaderManager().initLoader(0, getIntent().getExtras(), this);
    }

    /*
     * Loader Manager Callbacks
     */
    @Override
    public Loader<QueryStationModel> onCreateLoader(int id, Bundle args) {

        //UI feedback
        setIsProgressShown(true);

        return new StationItemLoader(this, args.getString(sStation_ID));
    }

    @Override
    public void onLoadFinished(Loader<QueryStationModel> loader, @Nullable QueryStationModel station) {

        //Add Route to Adapter
        if (station != null && station.data != null) {
            setTitle(station.data.name);
            mAdapter.setupAdapter(station.data.routes);
        }

        //UI Feedback
        if (mAdapter.getItemCount() > 0) {
            setIsProgressShown(false);
        }

    }

    @Override
    public void onLoaderReset(Loader<QueryStationModel> loader) {
        // Do Nothing
    }

    /*
     * Custom Methods
     */
    public void setIsProgressShown(final boolean pShowProgress){
        if (pShowProgress){
            mProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    /*
     * Static Methods
     */
    public static Intent launchMe(final String pStationID, final Context pContext){
        final Intent intent = new Intent(pContext, StationActivity.class);
        final Bundle bundle = new Bundle();
        bundle.putString(StationActivity.sStation_ID, pStationID);
        intent.putExtras(bundle);
        return intent;
    }
}
