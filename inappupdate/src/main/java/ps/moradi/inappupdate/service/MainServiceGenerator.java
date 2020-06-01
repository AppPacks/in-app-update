package ps.moradi.inappupdate.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import ps.moradi.inappupdate.App.App;
import ps.moradi.inappupdate.help.SharedKeys;
import ps.moradi.inappupdate.help.SharedPrefrence;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainServiceGenerator {

    private static final String TAG = MainServiceGenerator.class.getSimpleName();
    public static final String API_BASE_URL = "https://appcdn.cpay.ir/";

    //setup cache
    private static File httpCacheDirectory = new File(App.context.getCacheDir(), "responses");
    private static int cacheSize = 200 * 1024 * 1024; // 10 MiB
    private static Cache cache = new Cache(httpCacheDirectory, cacheSize);

    static OkHttpClient httpClient = new OkHttpClient.Builder()
            .addInterceptor(getOfflineInterceptor())
            .addInterceptor(loggingInterceptor())
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .cache(cache)
            .build();

    public static Interceptor getOfflineInterceptor() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //1:Create orginal request
                Request originalRequest = chain.request();

                String token = SharedPrefrence.getString(App.context, SharedKeys.SAVE_TOKEN, null);

                if (token != null) {
                    originalRequest = originalRequest.newBuilder()
                            .method(originalRequest.method(), originalRequest.body())
                            .header("Authorization", "Bearer " + SharedPrefrence.getString(App.context, SharedKeys.SAVE_TOKEN, null))
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json; charset=UTF-8")
                            .build();
                } else {
                    originalRequest = originalRequest.newBuilder()
                            .method(originalRequest.method(), originalRequest.body())
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json; charset=UTF-8")
                            .build();
                }

                Response response = chain.proceed(originalRequest);

                return response;
            }
        };
        return interceptor;
    }

    public static HttpLoggingInterceptor loggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

    private static List<Protocol> getProtocolList() {
        List<Protocol> protocolList = new ArrayList<Protocol>();
        protocolList.add(Protocol.HTTP_1_1);
        protocolList.add(Protocol.HTTP_2);
        return protocolList;
    }

    private static Retrofit.Builder builder;

    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static <S> S createService(Class<S> serviceClass) {

        builder = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, String baseUrl) {

        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(serviceClass);
    }

}
