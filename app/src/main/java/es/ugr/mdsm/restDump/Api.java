package es.ugr.mdsm.restDump;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Api {

    @POST("/devices")
    Call<ResponseBody> postDevice(@Body Device device);

    @POST("/flows")
    Call<ResponseBody> postFlows(@Body FlowDump flowDump);
}
