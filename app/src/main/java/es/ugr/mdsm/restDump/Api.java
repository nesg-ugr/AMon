package es.ugr.mdsm.restDump;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Api {
    String ENDPOINT = "https://mdsm1.ugr.es/";

    @POST("device")
    Observable<Response<Void>> postDevice(@Body Device device);

    @POST("flow")
    Observable<Response<Void>> postFlows(@Body Flow flow);

    @POST("app")
    Observable<Response<Void>> postApp(@Body App app);

    @POST("sensor")
    Observable<Response<Void>> postSensor(@Body Sensor sensor);

}
