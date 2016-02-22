package pa.com.poroto.panatransandroid.rx;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zubietaroberto on 02/22/16.
 */
public class RxUtils {

    public static void setupSchedulers(@NonNull final Observable pObservable){
        pObservable
            .observeOn(Schedulers.newThread())
            .subscribeOn(Schedulers.newThread());
    }
}
