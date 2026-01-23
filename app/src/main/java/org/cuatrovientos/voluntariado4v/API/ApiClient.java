package org.cuatrovientos.voluntariado4v.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8000/";
    private static Retrofit retrofit = null;
    private static VoluntariadoApiService service = null;

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            // Configurar Logging para ver cuerpo de respuesta y errores
            okhttp3.logging.HttpLoggingInterceptor logging = new okhttp3.logging.HttpLoggingInterceptor();
            logging.setLevel(okhttp3.logging.HttpLoggingInterceptor.Level.BODY);

            // Configurar Cliente HTTP con Timeouts extendidos
            okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                    .build();

            // Configurar Gson Leniente
            com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
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
