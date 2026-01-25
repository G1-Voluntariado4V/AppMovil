# 📱 Voluntariado 4V - Aplicación Móvil (Android)

Bienvenido al repositorio de la aplicación móvil para el proyecto **Voluntariado 4V**. Esta aplicación permite a voluntarios, organizaciones y coordinadores gestionar actividades y perfiles desde sus dispositivos Android.

## ⚠️ Requisitos Críticos para la Ejecución

Para que la aplicación funcione correctamente, es **IMPERATIVO** cumplir con los siguientes requisitos previos. Si falta alguno, la app no compilará o fallará al realizar peticiones.

### 1. Fichero de Configuración de Firebase (`google-services.json`) 🔑
Esta aplicación utiliza servicios de Google (Firebase) para la autenticación y notificaciones. El fichero de configuración que contiene las claves privadas **NO se incluye en el repositorio** por motivos de seguridad.

> [!IMPORTANT]
> **Debes solicitar el fichero `google-services.json`**

**Pasos una vez obtenido el fichero:**
1.  Copia el fichero `google-services.json`.
2.  Pégalo dentro de la carpeta `app/` de este proyecto (ruta: `ProyectoIntermodular/app/google-services.json`).
3.  Sincroniza el proyecto con Gradle (`File > Sync Project with Gradle Files`).

### 2. API Backend en Ejecución 🌐
La aplicación móvil actúa como un cliente que consume datos de una API REST. **La API debe estar ejecutándose localmente o en un servidor accesible.**

*   Si estás ejecutando todo en local, asegúrate de haber levantado el servidor backend del proyecto web.
*   Consulta el **README del proyecto web** (`d:\Intermodular voluntario\README.md`) para ver las instrucciones de instalación y puesta en marcha de la API (NestJS/Node).
*   Asegúrate de que la IP configurada en `ApiClient` o `Retrofit` apunte a tu máquina servidor (ej. `10.0.2.2` para emulador Android accediendo a localhost).

## 🚀 Instalación y Puesta en Marcha

1.  **Clonar el repositorio**:
    ```bash
    git clone <url-del-repo>
    ```
2.  **Abrir en Android Studio**:
    *   Abre Android Studio (versión recomendada: Ladybug o superior).
    *   Selecciona "Open" y busca la carpeta `d:\mov\ProyectoIntermodular`.
3.  **Configurar `google-services.json`**:
    *   Sigue las instrucciones del punto 1 arriba mencionado.
4.  **Compilar y Ejecutar**:
    *   Conecta un dispositivo físico o inicia un emulador.
    *   Pulsa el botón **Run** (▶️).

## 🛠️ Tecnologías

*   **Lenguaje**: Java / Kotlin
*   **Arquitectura**: MVVM (Model-View-ViewModel)
*   **Red**: Retrofit 2
*   **UI**: XML Layouts + Material Design
*   **Autenticación**: Firebase Auth

## 🤝 Contribución

Si encuentras problemas con la API o la configuración, contacta primero con el equipo de backend para verificar que el servidor está operativo.
