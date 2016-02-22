package pa.com.poroto.panatransandroid.api;

import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by zubietaroberto on 05/05/15.
 */
public class PanatransApi {

    public static final String sURL = "http://test-panatrans.herokuapp.com/v1/";

    public static PanatransApiInterface build(){
        final RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(PanatransApi.sURL)
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
