package ps.moradi.inappupdate.service;

import ps.moradi.inappupdate.model.ApplicationConfig;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIService {

    @GET()
    Call<ApplicationConfig> getApplicationConfig(@Url String url);

}
