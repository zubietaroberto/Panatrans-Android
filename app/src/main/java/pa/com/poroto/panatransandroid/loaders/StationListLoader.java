package pa.com.poroto.panatransandroid.loaders;

import android.content.Context;
import android.support.v4.content.Loader;

import java.util.ArrayList;
import java.util.List;

import pa.com.poroto.panatransandroid.api.PanatransApi;
import pa.com.poroto.panatransandroid.api.QueryStationListModel;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zubietaroberto on 02/22/16.
 */
public class StationListLoader extends Loader<List<QueryStationListModel.Station>> {

    private Subscription mSubscription;

    public StationListLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        //Get the API object
        final PanatransApi.PanatransApiInterface api = PanatransApi.build();

        //Start Request
        mSubscription = api.getStops()

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

                // Deliver Results
                .subscribe(new Observer<QueryStationListModel.Station>() {

                    final List<QueryStationListModel.Station> mList = new ArrayList<>();

                    @Override
                    public void onCompleted() {
                        deliverResult(mList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        deliverResult(null);
                    }

                    @Override
                    public void onNext(QueryStationListModel.Station station) {
                        mList.add(station);
                    }
                });
    }

    @Override
    protected void onReset() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }
    }
}
