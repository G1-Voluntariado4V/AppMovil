package org.cuatrovientos.voluntariado4v.api;

import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
import org.cuatrovientos.voluntariado4v.Models.HistorialResponse;
import org.cuatrovientos.voluntariado4v.Models.LoginRequest;
import org.cuatrovientos.voluntariado4v.Models.LoginResponse;
import org.cuatrovientos.voluntariado4v.Models.MensajeResponse;
import org.cuatrovientos.voluntariado4v.Models.VoluntarioResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VoluntariadoApiService {

        @POST("api/auth/login")
        Call<LoginResponse> login(@Body LoginRequest request);

        @POST("api/auth/register")
        Call<LoginResponse> register(@Body LoginRequest request);

        @GET("actividades")
        Call<List<ActividadResponse>> getActividades();

        @GET("actividades")
        Call<List<ActividadResponse>> getActividadesFiltradas(
                        @Query("ods_id") Integer odsId,
                        @Query("tipo_id") Integer tipoId);

        @GET("actividades/{id}")
        Call<ActividadResponse> getActividadDetalle(@Path("id") int id);

        @GET("api/voluntarios/{id}")
        Call<VoluntarioResponse> getVoluntario(@Path("id") int id);

        @GET("api/voluntarios/{id}/recomendaciones")
        Call<List<ActividadResponse>> getRecomendaciones(@Path("id") int id);

        @GET("api/voluntarios/{id}/historial")
        Call<HistorialResponse> getHistorial(@Path("id") int id);

        @POST("api/voluntarios/{idVol}/actividades/{idAct}")
        Call<MensajeResponse> inscribirse(
                        @Path("idVol") int idVoluntario,
                        @Path("idAct") int idActividad);

        @DELETE("api/voluntarios/{idVol}/actividades/{idAct}")
        Call<MensajeResponse> desapuntarse(
                        @Path("idVol") int idVoluntario,
                        @Path("idAct") int idActividad);
}
