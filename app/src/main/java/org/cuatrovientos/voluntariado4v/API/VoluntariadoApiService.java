package org.cuatrovientos.voluntariado4v.API;

import org.cuatrovientos.voluntariado4v.Models.ActividadResponse;
import org.cuatrovientos.voluntariado4v.Models.CursoResponse;
import org.cuatrovientos.voluntariado4v.Models.HistorialResponse;
import org.cuatrovientos.voluntariado4v.Models.IdiomaRequest;
import org.cuatrovientos.voluntariado4v.Models.LoginRequest;
import org.cuatrovientos.voluntariado4v.Models.LoginResponse;
import org.cuatrovientos.voluntariado4v.Models.MensajeResponse;
import org.cuatrovientos.voluntariado4v.Models.RegisterRequest;
import org.cuatrovientos.voluntariado4v.Models.VoluntarioResponse;
import org.cuatrovientos.voluntariado4v.Models.VoluntarioUpdateRequest;
import org.cuatrovientos.voluntariado4v.Models.OrganizacionResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VoluntariadoApiService {

        @POST("auth/login")
        Call<LoginResponse> login(@Body LoginRequest request);

        @POST("voluntarios")
        Call<VoluntarioResponse> register(@Body RegisterRequest request);

        @GET("catalogos/cursos")
        Call<List<CursoResponse>> getCursos();

        @GET("catalogos/idiomas")
        Call<List<org.cuatrovientos.voluntariado4v.Models.IdiomaResponse>> getIdiomas();

        @GET("catalogos/tipos-voluntariado")
        Call<List<org.cuatrovientos.voluntariado4v.Models.TipoVoluntariadoResponse>> getTiposVoluntariado();

        @GET("actividades")
        Call<List<ActividadResponse>> getActividades();

        @GET("actividades")
        Call<List<ActividadResponse>> getActividadesFiltradas(
                        @Query("ods_id") Integer odsId,
                        @Query("tipo_id") Integer tipoId);

        @GET("actividades/{id}")
        Call<ActividadResponse> getActividadDetalle(@Path("id") int id);

        @GET("voluntarios/{id}")
        Call<VoluntarioResponse> getVoluntario(@Path("id") int id);

        @PUT("voluntarios/{id}")
        Call<VoluntarioResponse> updateVoluntario(
                        @Path("id") int id,
                        @Header("X-User-Id") int userId,
                        @Body VoluntarioUpdateRequest request);

        // Idiomas del voluntario
        @POST("voluntarios/{idVol}/idiomas")
        Call<MensajeResponse> addIdioma(
                        @Path("idVol") int idVoluntario,
                        @Body IdiomaRequest request);

        @DELETE("voluntarios/{idVol}/idiomas/{idIdioma}")
        Call<MensajeResponse> deleteIdioma(
                        @Path("idVol") int idVoluntario,
                        @Path("idIdioma") int idIdioma);

        @GET("voluntarios/{id}/recomendaciones")
        Call<List<ActividadResponse>> getRecomendaciones(@Path("id") int id);

        @GET("voluntarios/{id}/historial")
        Call<org.cuatrovientos.voluntariado4v.Models.HistorialApiResponse> getHistorial(
                        @Path("id") int id,
                        @Header("X-User-Id") int userId);

        @POST("voluntarios/{idVol}/actividades/{idAct}")
        Call<MensajeResponse> inscribirse(
                        @Path("idVol") int idVoluntario,
                        @Header("X-User-Id") int userId,
                        @Path("idAct") int idActividad);

        @DELETE("voluntarios/{idVol}/actividades/{idAct}")
        Call<MensajeResponse> desapuntarse(
                        @Path("idVol") int idVoluntario,
                        @Header("X-User-Id") int userId,
                        @Path("idAct") int idActividad);

        @GET("organizaciones/{id}")
        Call<OrganizacionResponse> getOrganizacion(@Path("id") int id);

        @GET("organizaciones/{id}/actividades")
        Call<List<ActividadResponse>> getActividadesOrganizacion(@Path("id") int id);

        @GET("actividades/{id}/inscripciones")
        Call<List<VoluntarioResponse>> getInscritos(@Path("id") int idActividad);
}
