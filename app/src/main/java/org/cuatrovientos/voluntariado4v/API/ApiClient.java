package org.cuatrovientos.voluntariado4v.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8000/";
    private static Retrofit retrofit = null;
    private static VoluntariadoApiService service = null;

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static VoluntariadoApiService getService() {
        if (service == null) {
            service = getRetrofit().create(VoluntariadoApiService.class);
        }
        return service;
    }
}
