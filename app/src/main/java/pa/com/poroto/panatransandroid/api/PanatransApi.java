package pa.com.poroto.panatransandroid.api;

import retrofit.client.Response;
import retrofit.http.GET;
import rx.Observable;

/**
 * Created by zubietaroberto on 04/29/15.
 */
public interface PanatransApi {

    String sURL = "http://test-panatrans.herokuapp.com/v1";

    @GET("/stops/")
    Observable<Response> getStops();
}
