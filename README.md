# AppBogados - Evaluación Parcial 2 (DSY1105)

## Descripción del Proyecto
**AppBogados** es una aplicación móvil desarrollada para facilitar la gestión de procesos legales y la lectura de documentos jurídicos, tales como contratos, demandas y poderes notariales.

## Equipo de Trabajo
*   **Claudio Figueroa**
*   **Javiera Valdés**

## Funcionalidades Principales
Para construir esta aplicación, organizamos el código de manera que el diseño visual esté separado de las reglas de funcionamiento (usando la estructura MVVM). Esto hace que la app sea más estable y fácil de mejorar a futuro.

Nuestras características principales son:

*   **Navegación sencilla y diseño claro:** Las pantallas están ordenadas de forma lógica para que el usuario pueda moverse fácilmente entre el menú principal, las carpetas de documentos y el lector de archivos.
*   **Formularios y validaciones:** Contamos con formularios que revisan si los datos ingresados son correctos (como validar si el RUT cumple con el formato chileno). Si hay un error, la aplicación avisa inmediatamente mostrando mensajes claros.
*   **Persistencia de datos del usuario:** Se utiliza una base de datos interna (Room) para guardar la información de la aplicación de forma local, permitiendo que datos como las sesiones iniciadas o la imagen de perfil no se pierdan al cerrar la app.
*   **Uso de funciones del teléfono:** La aplicación se conecta de manera segura con el sistema de archivos del celular para que el usuario pueda abrir, leer y descargar los documentos PDF directamente en su dispositivo. También se permite el uso de la cámara del teléfono como medio para tener una foto para el perfil.
*   **Animaciones:** Agregamos una transición visuales al cambiar desde la vista de inicio de sesión a la pantalla principal, haciendo que la experiencia de uso se sienta más fluida y natural.

## Organización y Trabajo en Equipo
*   **Repositorio en GitHub:** https://github.com/ClaudioFigueroa15/Evaluacion2-AppBogados
*   **Planificación en Trello:** https://trello.com/b/oeNDjfTi/
    *(En nuestro tablero dejamos evidencia de cómo nos repartimos las tareas y avanzamos en la construcción de la app).*

## Ejecución de la aplicación
Para ejecutar este proyecto en tu propio computador, sigue estos pasos:

1.  Descarga o clona este repositorio en tu equipo:
    ```bash
    git clone https://github.com/ClaudioFigueroa15/Evaluacion2-AppBogados
    ```
2.  Abre la carpeta del proyecto usando el programa **Android Studio**.
3.  Espera a que el programa termine de cargar y descargar las configuraciones necesarias de *Gradle*.
4.  Inicia un celular virtual (Emulador) en la pantalla de Android Studio o conecta tu celular Android por cable (con la depuración USB activada)
5.  Presiona el botón verde de **Play (Run)** en la parte superior para instalar y abrir la aplicación en el dispositivo.