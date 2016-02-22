package pa.com.poroto.panatransandroid.api;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by zubietaroberto on 05/05/15.
 */
public class PanatransApi {

    public static final String sURL = "http://test-panatrans.herokuapp.com/v1/";

    public static PanatransApiInterface build(){
        final Retrofit adapter = new Retrofit.Builder()
                .baseUrl(PanatransApi.sURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return adapter.create(PanatransApi.PanatransApiInterface.class);
    }

    /**
     * Created by zubietaroberto on 04/29/15.
     */
    public interface PanatransApiInterface {

        @GET("stops/")
        Observable<QueryStationListModel> getStops();

        @GET("stops/{id}/")
        Observable<QueryStationModel> getStopById(@Path("id") String id);
    }
}
