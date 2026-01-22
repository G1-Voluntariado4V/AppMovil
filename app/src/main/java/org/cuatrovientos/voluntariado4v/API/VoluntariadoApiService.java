package org.cuatrovientos.voluntariado4v.API;

import org.cuatrovientos.voluntariado4v.Models.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VoluntariadoApiService {

        // ═══════════════════════════════════════════════════════════════════
        // AUTHENTICATION
        // ═══════════════════════════════════════════════════════════════════

        @POST("auth/login")
        Call<LoginResponse> login(@Body LoginRequest request);

        // Registro estándar (crea recurso voluntario)
        @POST("voluntarios")
        Call<VoluntarioResponse> register(@Body RegisterRequest request);

        // ═══════════════════════════════════════════════════════════════════
        // CATALOGOS (Auxiliares para formularios)
        // ═══════════════════════════════════════════════════════════════════

        @GET("catalogos/cursos")
        Call<List<CursoResponse>> getCursos();

        @GET("catalogos/idiomas")
        Call<List<IdiomaResponse>> getIdiomas();

        @GET("catalogos/tipos-voluntariado")
        Call<List<TipoVoluntariadoResponse>> getTiposVoluntariado();

        @GET("catalogos/ods")
        Call<List<OdsResponse>> getOds();

        // ═══════════════════════════════════════════════════════════════════
        // ACTIVIDADES (Búsqueda y Detalle)
        // ═══════════════════════════════════════════════════════════════════

        // Listar todas (User Explore) - Puede usarse el endpoint de catálogo o el genérico
        @GET("actividades")
        Call<List<ActividadResponse>> getActividades();

        @GET("actividades")
        Call<List<ActividadResponse>> getActividadesFiltradas(
                @Query("ods_id") Integer odsId,
                @Query("tipo_id") Integer tipoId);

        @GET("actividades/{id}")
        Call<ActividadResponse> getActividadDetalle(@Path("id") int id);

        // Obtener lista de inscritos a una actividad (Para Organizaciones)
        @GET("actividades/{id}/inscripciones")
        Call<List<InscripcionResponse>> getInscritos(@Path("id") int idActividad);

        // ═══════════════════════════════════════════════════════════════════
        // GESTIÓN DE ACTIVIDADES (Creación/Edición/Borrado)
        // ═══════════════════════════════════════════════════════════════════

        // Opción A: Crear vinculada a la organización en la URL
        @POST("organizaciones/{id}/actividades")
        Call<ActividadResponse> crearActividad(
                @Path("id") int idOrganizacion,
                @Body ActividadCreateRequest request);

        // Opción B: Crear actividad genérica (si el backend lo soporta así)
        @POST("actividades")
        Call<MensajeResponse> createActividadGenerica(@Body ActividadCreateRequest request);

        @PUT("actividades/{id}")
        Call<ActividadResponse> updateActividad(
                @Path("id") int idActividad,
                @Body ActividadUpdateRequest request);

        @DELETE("actividades/{id}")
        Call<Void> deleteActividad(@Path("id") int id);

        @POST("actividades/{id}/imagenes")
        Call<MensajeResponse> addImagenActividad(
                @Path("id") int idActividad,
                @Body ImagenRequest request);

        // ═══════════════════════════════════════════════════════════════════
        // VOLUNTARIOS (Perfil propio y acciones)
        // ═══════════════════════════════════════════════════════════════════

        @GET("voluntarios/{id}")
        Call<VoluntarioResponse> getVoluntarioDetail(@Path("id") int id);

        // Actualización del propio perfil (User Context)
        @PUT("voluntarios/{id}")
        Call<VoluntarioResponse> updateVoluntario(
                @Path("id") int id,
                @Header("X-User-Id") int userId,
                @Body VoluntarioUpdateRequest request);

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
        Call<HistorialApiResponse> getHistorial(
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

        // ═══════════════════════════════════════════════════════════════════
        // ORGANIZACIONES (Perfil público y Gestión propia)
        // ═══════════════════════════════════════════════════════════════════

        @GET("organizaciones/{id}")
        Call<OrganizacionResponse> getOrganizationDetail(@Path("id") int id);

        @GET("organizaciones/{id}/actividades")
        Call<List<ActividadResponse>> getActividadesOrganizacion(@Path("id") int id);

        @GET("organizaciones/top-voluntarios")
        Call<List<OrganizacionResponse>> getTopOrganizaciones();

        // Actualización propia (Owner Context)
        @PUT("organizaciones/{id}")
        Call<OrganizacionResponse> updateOrganizacion(
                @Path("id") int id,
                @Body OrganizacionUpdateRequest request);

        // ═══════════════════════════════════════════════════════════════════
        // COORDINADOR / ADMIN (Gestión global)
        // ═══════════════════════════════════════════════════════════════════

        @GET("coord/stats")
        Call<CoordinatorStatsResponse> getCoordinatorStats(@Header("X-Admin-Id") int adminId);

        @GET("coordinadores/{id}")
        Call<CoordinadorResponse> getCoordinadorDetail(
                @Path("id") int id,
                @Header("X-Admin-Id") int adminId
        );

        @GET("usuarios")
        Call<List<UserResponse>> getAllUsers();

        // Cambiar estado (Aprobar/Rechazar/Bloquear)
        @PATCH("coord/{rol}/{id}/estado")
        Call<MensajeResponse> updateUserStatus(
                @Header("X-Admin-Id") int adminId,
                @Path("rol") String rolPath,
                @Path("id") int userId,
                @Body EstadoRequest request);

        // Añade esto en la sección de COORDINADOR en VoluntariadoApiService.java

        @PUT("coordinadores/{id}")
        Call<CoordinadorResponse> updateCoordinador(
                @Path("id") int id,
                @Header("X-Admin-Id") int adminId,
                @Body CoordinadorUpdateRequest request
        );

        // CAMBIAR ROL (Endpoint PUT específico)
        // Ruta: /usuarios/{id}/rol
        @PUT("usuarios/{id}/rol")
        Call<MensajeResponse> updateUserRole(
                @Header("X-Admin-Id") int adminId,
                @Path("id") int userId,
                @Body RoleUpdateRequest request);

        // Edición Admin de Voluntario (Admin Context)
        @PUT("voluntarios/{id}")
        Call<MensajeResponse> updateVoluntarioAdmin(
                @Header("X-Admin-Id") int adminId,
                @Path("id") int id,
                @Body VoluntarioUpdateRequest request);

        // Edición Admin de Organización (Admin Context)
        @PUT("organizaciones/{id}")
        Call<MensajeResponse> updateOrganizacionAdmin(
                @Header("X-Admin-Id") int adminId,
                @Path("id") int id,
                @Body OrganizacionUpdateRequest request);

        // ═══════════════════════════════════════════════════════════════════
        // GESTIÓN DE ACTIVIDADES (COORDINADOR)
        // ═══════════════════════════════════════════════════════════════════

        // Obtener TODAS las actividades (incluidas pendientes, rechazadas, etc.)
        @GET("coord/actividades")
        Call<List<ActividadResponse>> getAllActivitiesCoord(@Header("X-Admin-Id") int adminId);

        // Cambiar estado (Publicar/Rechazar/Cancelar)
        @PATCH("coord/actividades/{id}/estado")
        Call<MensajeResponse> updateActivityStatus(
                @Header("X-Admin-Id") int adminId,
                @Path("id") int idActividad,
                @Body EstadoRequest request);

        // Borrado forzoso (Admin)
        @DELETE("coord/actividades/{id}")
        Call<Void> deleteActivityCoord(
                @Header("X-Admin-Id") int adminId,
                @Path("id") int idActividad);

        // ═══════════════════════════════════════════════════════════════════
        // GESTIÓN DE CATÁLOGOS
        // ═══════════════════════════════════════════════════════════════════

        // --- ODS ---
        @POST("ods")
        Call<OdsResponse> createOds(@Body OdsResponse ods);

        @PUT("ods/{id}")
        Call<OdsResponse> updateOds(@Path("id") int id, @Body OdsResponse ods);

        @DELETE("ods/{id}")
        Call<Void> deleteOds(@Path("id") int id);

        // --- TIPOS DE VOLUNTARIADO ---
        // Nota: He puesto 'tipos-voluntariado' para coincidir con tu GET existente.
        // Si tu backend usa 'tipo_voluntariado' o 'tipos', ajústalo aquí.
        @POST("tipos-voluntariado")
        Call<TipoVoluntariadoResponse> createTipoVoluntariado(@Body TipoVoluntariadoResponse tipo);

        @PUT("tipos-voluntariado/{id}")
        Call<TipoVoluntariadoResponse> updateTipoVoluntariado(@Path("id") int id, @Body TipoVoluntariadoResponse tipo);

        @DELETE("tipos-voluntariado/{id}")
        Call<Void> deleteTipoVoluntariado(@Path("id") int id);
}