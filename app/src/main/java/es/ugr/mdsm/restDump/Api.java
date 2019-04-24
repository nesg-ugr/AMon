package es.ugr.mdsm.restDump;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Api {
    String ENDPOINT = "https://127.0.0.1:3000/";

    @POST("devices")
    Observable<Response<Void>> postDevice(@Body Device device);

    @POST("flows")
    Observable<Response<Void>> postFlows(@Body FlowDump flowDump);
}
