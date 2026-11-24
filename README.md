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

| Emulador | Dispositivo | Pantalla | API |
|---|---|---|---|
| 1 (compacto) | _p. ej. Pixel 7_ | _6,3" — 1080×2400_ | _API 3x_ |
| 2 (grande) | _p. ej. Pixel Tablet_ | _10,95" — 2560×1600_ | _API 3x_ |

## Pruebas realizadas

| Nº | Prueba | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| 1 | Crear una tarea válida | Se guarda y aparece en la lista | _pendiente_ |
| 2 | Intentar guardar un título vacío | Error y no se crea la tarea | _pendiente_ |
| 3 | Editar una tarea | Los cambios se muestran | _pendiente_ |
| 4 | Marcar una tarea como completada | Cambia su estado y se guarda | _pendiente_ |
| 5 | Cancelar una eliminación | La tarea no desaparece | _pendiente_ |
| 6 | Eliminar confirmando | La tarea desaparece de la base de datos | _pendiente_ |
| 7 | Cerrar y volver a abrir | Se recuperan las tareas | _pendiente_ |
| 8 | Activar el filtro y reiniciar | Se conserva la preferencia | _pendiente_ |
| 9 | Consultar la API con Internet | Se muestran las tareas recibidas | _pendiente_ |
| 10 | Consultar la API sin conexión | Error y botón Reintentar | _pendiente_ |
| 11 | Compartir una tarea | Se abre el selector de aplicaciones | _pendiente_ |
| 12 | Cambiar la orientación | La app sigue funcionando y conserva los datos | _pendiente_ |

## Capturas de pantalla

_Añadir al menos cuatro capturas (listado, formulario, consulta a la API e interacción relevante), por ejemplo en una carpeta `capturas/`:_

```
![Listado](capturas/listado.png)
![Formulario](capturas/formulario.png)
![API](capturas/api.png)
![Eliminar](capturas/eliminar.png)
```

## Uso de IA

Enlace al chat de IA utilizado: _añadir enlace_

## Incidencias conocidas

- Los resultados de la consulta a la API no se guardan al girar el dispositivo (son datos de ejemplo y no se persisten); basta con volver a pulsar el botón de carga. Las tareas personales y el filtro sí se conservan.
