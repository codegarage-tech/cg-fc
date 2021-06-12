package com.rc.facecase.retrofit;

import android.content.Context;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Usage:
 *
 * 1) Asynchronous:
 *
 * APIClient apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);
 * Call<APIResponse<List<Cuisine>>> call = apiInterface.apiGetAllCuisines();
 * call.enqueue(new Callback<APIResponse<List<Cuisine>>>() {
 *     @Override
 *     public void onResponse(Call<APIResponse<List<Cuisine>>> call, Response<APIResponse<List<Cuisine>>> response) {
 *         APIResponse<List<Cuisine>> data = response.body();
 *     }
 *
 *     @Override
 *     public void onFailure(Call<APIResponse<List<Cuisine>>> call, Throwable t) {
 *     }
 * });
 *
 * 2) synchronous:
 *
 * APIClient apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);
 * Call<APIResponse<List<Cuisine>>> call = apiInterface.apiGetAllCuisines();
 *
 * Response response = call.execute();
 * if (response.isSuccessful()) {
 *     //Do your stuff
 * }
 *
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class APIClient {

   // private static final String BASE_URL = "http://iexpresswholesale.com/faceoff-games/index.php/api/";
    private static final String BASE_URL = "http://bizluk.com/faceoff-games/index.php/api/";
    private static Retrofit retrofit = null;
    private static final int REQUEST_TIMEOUT = 15;
    private static OkHttpClient okHttpClient;

    public static Retrofit getClient(Context context) {

        if (okHttpClient == null)
            initOkHttp(context);

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
//                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static void initOkHttp(final Context context) {
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(interceptor);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .addHeader("Accept", "application/json;charset=utf-8")
                        .addHeader("Content-Type", "application/json;charset=utf-8");

                // Adding Authorization token (API Key)
                // Requests will be denied without API key
//                if (!TextUtils.isEmpty(PrefUtils.getApiKey(context))) {
//                    requestBuilder.addHeader("Authorization", PrefUtils.getApiKey(context));
//                }

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        okHttpClient = httpClient.build();
    }
}