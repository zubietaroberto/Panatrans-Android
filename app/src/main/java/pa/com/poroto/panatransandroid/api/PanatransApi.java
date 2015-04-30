package pa.com.poroto.panatransandroid.api;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by zubietaroberto on 04/29/15.
 */
public interface PanatransApi {

    String sURL = "http://test-panatrans.herokuapp.com/v1";

    @GET("/stops/")
    Observable<Response> getStops();

    @GET("/stops/{id}/")
    Observable<Response> getStopById(@Path("id") String id);
}
