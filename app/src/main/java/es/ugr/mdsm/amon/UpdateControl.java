package es.ugr.mdsm.amon;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;

public interface UpdateControl {
    String ENDPOINT = "https://mdsm1.ugr.es/";

    @GET("last-version")
    Observable<Response<Version>> getLastVersion();

}
