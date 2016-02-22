package pa.com.poroto.panatransandroid.loaders;

import android.content.Context;
import android.support.v4.content.Loader;
import android.util.Log;

import pa.com.poroto.panatransandroid.api.PanatransApi;
import pa.com.poroto.panatransandroid.api.QueryStationModel;
import pa.com.poroto.panatransandroid.rx.RxUtils;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zubietaroberto on 02/22/16.
 */
public class StationItemLoader extends Loader<QueryStationModel> {

    private final String mID;
    private Subscription mSubscription;
    public StationItemLoader(final Context context, final String pId) {
        super(context);
        mID = pId;
    }

    @Override
    protected void onStartLoading() {

        //Get API Object
        final PanatransApi.PanatransApiInterface api = PanatransApi.build();
        final Observable<QueryStationModel> obs = api
                .getStopById(mID)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        mSubscription = obs.subscribe(new Observer<QueryStationModel>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("Loader Error", e.getLocalizedMessage(), e);
                deliverResult(null);
            }

            @Override
            public void onNext(QueryStationModel queryStationModel) {
                deliverResult(queryStationModel);
            }
        });
    }

    @Override
    protected void onReset() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }
        super.onReset();
    }

    @Override
    protected boolean onCancelLoad() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }
        return super.onCancelLoad();
    }
}
