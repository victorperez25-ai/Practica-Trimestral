# Gestor Personal de Tareas

Aplicación Android desarrollada con **Kotlin** y **Jetpack Compose (Material 3)** para organizar tareas personales, académicas o profesionales. Permite crear, consultar, editar, completar, eliminar y compartir tareas, que se guardan de forma permanente en una base de datos local (Room). Además, consulta tareas de ejemplo de un servicio web REST (JSONPlaceholder).

## Funcionalidades implementadas

- **Listado de tareas** (`LazyColumn`) con título, prioridad y estado. Acciones: completar (Checkbox), editar, compartir y eliminar.
- **Mensajes de estado**: indicador de carga, mensaje cuando no hay tareas y mensaje cuando el filtro no deja ninguna visible.
- **Formulario de creación/edición** con título, descripción, prioridad (menú desplegable), botones Guardar y Cancelar.
- **Validaciones**: título obligatorio, sin solo espacios y con al menos 3 caracteres tras `trim()`; prioridad obligatoria (Baja/Media/Alta); descripción opcional. Los errores se muestran bajo el campo correspondiente.
- **Sin duplicados**: el botón Guardar se deshabilita mientras se guarda.
- **Eliminación con confirmación** mediante `AlertDialog`.
- **Persistencia con Room**: entidad, DAO, base de datos y repositorio. Los datos se recuperan al reabrir la app.
- **Filtro «Mostrar solo pendientes»** con `Switch`, guardado con Preferences DataStore (valor inicial: mostrar todas).
- **Tareas de ejemplo** desde `https://jsonplaceholder.typicode.com/todos?userId=1` con Retrofit + Gson y coroutines: estado inicial, cargando, éxito, lista vacía y error con botón «Reintentar». El botón de carga se deshabilita durante la petición.
- **Compartir tarea** con un Intent implícito `ACTION_SEND` (`text/plain`) y selector de aplicaciones. Si no hay ninguna aplicación compatible se muestra un aviso.
- **Diseño adaptable**: en pantallas anchas (≥ 600 dp) el contenido se centra con un ancho máximo y las tarjetas muestran también la descripción.
- **Iconos locales** (vectores XML en `res/drawable`).

## Organización del proyecto

```
app/src/main/java/com/example/gestorpersonaldetareas/
├── MainActivity.kt                 Única Activity; crea los repositorios y lanza la UI
├── model/                          MODELO
│   ├── Tarea.kt                    Clase de la tarea (también entidad Room)
│   └── Prioridad.kt                Enum con las tres prioridades
├── data/                           ALMACENAMIENTO (Room)
│   ├── TareaDao.kt                 Consultas, inserción, actualización y borrado
│   ├── TareaDatabase.kt            Base de datos (singleton)
│   └── TareaRepositorio.kt         Comunica la base de datos con la interfaz
├── preferences/                    PREFERENCIAS (DataStore)
│   └── PreferenciasRepositorio.kt  Lectura/escritura de «Mostrar solo pendientes»
├── network/                        COMUNICACIONES (Retrofit)
│   ├── TareaRemota.kt              Modelo del JSON recibido
│   └── TareasApi.kt                Interfaz del servicio y cliente Retrofit
├── util/
│   └── CompartirTarea.kt           Intent ACTION_SEND
└── ui/                             INTERFAZ (Compose)
    ├── AppGestorTareas.kt          Navegación sencilla basada en estados
    ├── PantallaListaTareas.kt      Pantalla principal
    ├── PantallaFormularioTarea.kt  Crear / editar tarea
    ├── PantallaTareasEjemplo.kt    Consulta a la API REST
    └── theme/                      Colores, tipografía y tema
```

## Bibliotecas utilizadas

| Biblioteca | Uso |
|---|---|
| Jetpack Compose + Material 3 | Interfaz de usuario |
| Room (runtime, ktx, compiler vía KSP) | Base de datos local |
| DataStore Preferences | Preferencia del filtro |
| Retrofit + converter-gson | Peticiones HTTP y conversión JSON |
| Kotlin Coroutines / Flow | Operaciones asíncronas |
| Lifecycle runtime | Integración con el ciclo de vida |

## Instrucciones para ejecutar

1. Abrir la carpeta del proyecto en Android Studio.
2. Esperar a que Gradle sincronice las dependencias.
3. Seleccionar un emulador o dispositivo (Android 7.0 / API 24 o superior) y pulsar **Run**.
4. Para generar el APK de depuración: *Build > Build APK(s)* o `gradlew assembleDebug`. El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

El APK de depuración ya generado está en [`apk/GestorPersonalDeTareas-debug.apk`](apk/GestorPersonalDeTareas-debug.apk).

Permisos: solo `INTERNET`.

## Emuladores utilizados

| Emulador | Perfil | Resolución | Densidad | RAM | Sistema |
|---|---|---|---|---|---|
| 1 (pantalla compacta) | Medium Phone | 1080×2400 (vertical) | 420 dpi | 2 GB | Android API 37, Google Play, x86_64 |
| 2 (pantalla grande) | Medium Tablet | 2560×1600 (horizontal) | 320 dpi | ~1,9 GB | Android 16 (API 36), Google Play, x86_64 |

## Pruebas realizadas

Todas las pruebas se realizaron en los dos emuladores. Para cerrar la app se forzó su detención (`am force-stop`) y se volvió a abrir; la falta de conexión se simuló con el modo avión.

| Nº | Prueba | Resultado esperado | Móvil | Tablet | Observaciones |
|---|---|---|---|---|---|
| 1 | Crear una tarea válida | Se guarda y aparece en la lista | ✅ | ✅ | Los espacios exteriores del título se eliminan al guardar |
| 2 | Intentar guardar un título vacío | Error y no se crea la tarea | ✅ | ✅ | También probado con título de solo espacios («El título es obligatorio») y con 2 caracteres («al menos 3 caracteres»). Sin prioridad aparece «Selecciona una prioridad» |
| 3 | Editar una tarea | Los cambios se muestran | ✅ | ✅ | El formulario se abre con los datos actuales; se cambiaron título y prioridad |
| 4 | Marcar una tarea como completada | Cambia su estado y se guarda | ✅ | ✅ | El título aparece tachado y el estado pasa a «Completada»; se mantiene tras reiniciar |
| 5 | Cancelar una eliminación | La tarea no desaparece | ✅ | ✅ | |
| 6 | Eliminar confirmando | La tarea desaparece de la base de datos | ✅ | ✅ | Tras reiniciar la app sigue sin aparecer |
| 7 | Cerrar y volver a abrir | Se recuperan las tareas | ✅ | ✅ | |
| 8 | Activar el filtro y reiniciar | Se conserva la preferencia | ✅ | ✅ | El Switch sigue activado y solo se ven las pendientes |
| 9 | Consultar la API con Internet | Se muestran las tareas recibidas | ✅ | ✅ | 20 tareas con id, título y estado. Durante la carga se ve el indicador y el botón queda deshabilitado |
| 10 | Consultar la API sin conexión | Error y botón Reintentar | ✅ | ✅ | Al recuperar la conexión, «Reintentar» carga las tareas |
| 11 | Compartir una tarea | Se abre el selector de aplicaciones | ✅ | ✅ | El texto incluye título, descripción y prioridad |
| 12 | Cambiar la orientación | La app sigue funcionando y conserva los datos | ✅ | ✅ | Probado en la lista y en mitad del formulario: el texto escrito se conserva |

Prueba adicional: pulsar «Guardar» tres veces seguidas muy rápido crea una sola tarea (sin duplicados).

## Capturas de pantalla

### Móvil (Medium Phone)

| Listado vacío | Validación | Selector de prioridad | Formulario relleno |
|---|---|---|---|
| ![](capturas/01_lista_vacia.png) | ![](capturas/02_validacion_titulo_vacio.png) | ![](capturas/03_selector_prioridad.png) | ![](capturas/04_formulario_relleno.png) |

| Editar tarea | Tarea completada | Confirmar eliminación | Filtro «solo pendientes» |
|---|---|---|---|
| ![](capturas/05_editar_tarea_antes.png) | ![](capturas/06_tarea_completada.png) | ![](capturas/07_dialogo_eliminar.png) | ![](capturas/08_filtro_solo_pendientes.png) |

| Tras reiniciar | Compartir | API: cargando | API: resultados | API: sin conexión |
|---|---|---|---|---|
| ![](capturas/09_tareas_tras_reiniciar.png) | ![](capturas/10_compartir_selector.png) | ![](capturas/16_api_cargando.png) | ![](capturas/14_api_resultados.png) | ![](capturas/15_api_sin_conexion.png) |

Orientación horizontal:

![Lista en horizontal](capturas/11_horizontal_lista.png)
![Formulario en horizontal](capturas/12_horizontal_formulario.png)

### Tablet (Medium Tablet)

![Lista en tablet](capturas/tablet/04_lista_con_tareas.png)
![Validación en tablet](capturas/tablet/02_validacion.png)
![API en tablet](capturas/tablet/09_api_resultados.png)
![Sin conexión en tablet](capturas/tablet/10_api_sin_conexion.png)
![Compartir en tablet](capturas/tablet/07_compartir.png)
![Tablet en vertical](capturas/tablet/08_vertical.png)

## Uso de IA

Enlace al chat de IA utilizado: https://gemini.google.com/app/5f62c36144c3ab2f?utm_source=app_launcher&utm_medium=owned&utm_campaign=base_all 

## Incidencias conocidas

- Los resultados de la consulta a la API no se guardan al girar el dispositivo (son datos de ejemplo y no se persisten); basta con volver a pulsar el botón de carga. Las tareas personales y el filtro sí se conservan.
- En el móvil en horizontal, el botón flotante «Nueva tarea» puede tapar parcialmente el icono de eliminar de la segunda tarea visible, porque la pantalla es muy baja. Si hay suficientes tareas para desplazar la lista, el margen inferior permite dejarlo a la vista; en vertical y en la tablet no ocurre.
