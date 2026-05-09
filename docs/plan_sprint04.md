# Sprint 04 - Planning Document

**Project:** Hermes Travel App  
**Sprint Duration:** 05/05/2026 - 25/05/2026  
**Team:** Ivan Gil Cañizares, Marco Beruet Morelli  
**Delivery Deadline:** 25/05/2026 23:55

---

## Sprint Goal

Integrar Retrofit para conectar la app con una API REST de reservas de hoteles, alojada en `http://15.224.84.148:8090`, incluyendo:
- Configurar Retrofit con el cliente HTTP y los modelos de datos necesarios
- Implementar pantallas de búsqueda de hoteles y reserva de habitaciones
- Guardar la información de reservas localmente en Room como nuevos viajes
- Gestionar una galería de imágenes por viaje, guardadas localmente
- Listar y cancelar reservas existentes

**Target:** Deliver v4.0.0 release with remote persistence and image gallery

---

## Sprint Backlog

### T1. Configuración de Retrofit (3 puntos)

#### T1.1 - Añadir dependencia Retrofit y configurar el cliente HTTP

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Añadir dependencias en build.gradle | Retrofit, OkHttp, Gson/Moshi converter | Marco |
| Crear RetrofitClient / NetworkModule | Objeto singleton con la URL base de la API | Marco |
| Configurar OkHttpClient | Timeouts, interceptores de logs (HttpLoggingInterceptor) | Ivan |

#### T1.2 - Crear modelos de datos e interfaces de la API

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Crear data classes de respuesta | HotelResponse, RoomResponse, ReservationResponse, etc. | Ivan |
| Crear interfaz HotelApiService | Endpoints: búsqueda de disponibilidad, reserva, cancelación | Marco |
| Implementar estructura MVVM | Asegurar que los modelos remotos están separados de los de dominio | Ambos |

#### T1.3 - Crear capa Repository para abstraer el uso de la API

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Crear interfaz HotelRepository | Contrato con los métodos de negocio (searchHotels, bookRoom, cancelReservation) | Ivan |
| Crear HotelRepositoryImpl | Implementación que llama a HotelApiService y mapea resultados | Ivan |
| Inyectar repositorio con Hilt | Binding en RepositoryModule | Marco |

#### T1.4 - Crear tests unitarios mockeando la conexión remota

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Configurar MockWebServer o MockK | Dependencias de test para simular la API | Marco |
| Tests para HotelRepositoryImpl | Verificar búsqueda, reserva y cancelación con respuestas mock | Marco |
| Tests para HotelApiService | Verificar que los endpoints y parámetros son correctos | Ivan |

---

### T2. Pantallas de Búsqueda y Reserva (5 puntos)

#### T2.1 - Pantalla de búsqueda de hoteles

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Crear HotelSearchScreen.kt | Formulario con selector de ciudad (London, Paris, Barcelona) y date pickers de inicio y fin | Ivan |
| Crear HotelSearchViewModel | Estado de búsqueda, llamada al repositorio, manejo de errores | Ivan |
| Validar campos del formulario | Ciudad requerida, fecha inicio < fecha fin | Marco |
| Navegar a resultados | Al buscar, navegar a la lista de hoteles disponibles | Marco |

#### T2.2 - Mostrar hoteles y habitaciones devueltos por la API

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Crear HotelListScreen.kt | Lista de hoteles con nombre, descripción y precio | Marco |
| Crear HotelDetailScreen.kt | Detalle de un hotel con sus habitaciones (normalmente 3) | Marco |
| Crear componentes de UI reutilizables | HotelCard, RoomCard con imágenes y precios | Ivan |
| Mostrar estado de carga y errores | CircularProgressIndicator y mensajes de error de red | Ivan |

#### T2.3 - Permitir reservar una habitación y guardarla localmente

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Crear ReservationEntity.kt | Entidad Room con ID, hotel, habitación, precio, fechas, tripId | Marco |
| Crear ReservationDao.kt | DAO con CRUD de reservas | Marco |
| Implementar lógica de reserva | Llamar al endpoint de reserva y guardar resultado en Room como nuevo viaje | Ivan |
| Actualizar AppDatabase | Añadir ReservationEntity a la base de datos (nueva versión) | Ivan |
| Navegar a confirmación | Mostrar pantalla o Snackbar de reserva confirmada | Marco |

#### T2.4 - Mostrar imágenes del hotel y las habitaciones

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Añadir dependencia Coil | Librería de carga de imágenes asíncrona para Compose | Ivan |
| Mostrar imágenes en HotelDetailScreen | Cargar imágenes remotas con Coil (AsyncImage) | Ivan |
| Mostrar imágenes en HotelListScreen | Thumbnail del hotel en cada card de la lista | Marco |

---

### T3. Galería de Imágenes por Viaje (4 puntos)

#### T3.1 - Permitir al usuario adjuntar múltiples imágenes a un viaje

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Añadir botón de galería en TripOverviewScreen | Acceso a la galería o cámara del dispositivo | Marco |
| Implementar selector de imágenes | ActivityResultContracts para seleccionar imágenes de la galería | Marco |
| Integrar en la UI del viaje | Permitir selección múltiple de imágenes | Ivan |

#### T3.2 - Guardar imágenes localmente

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Crear TripImageEntity.kt | Entidad Room con id, tripId y uri de la imagen | Ivan |
| Crear TripImageDao.kt | DAO con insert, delete y query por tripId | Ivan |
| Implementar TripImageRepository | Interfaz e implementación para gestionar imágenes | Marco |
| Copiar imagen al almacenamiento interno | Guardar URI permanente en el directorio de la app | Marco |

#### T3.3 - Mostrar galería de imágenes en el detalle del viaje

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Crear TripGallerySection en TripOverviewScreen | Grid o LazyRow de imágenes del viaje | Ivan |
| Cargar imágenes con Coil | AsyncImage desde URI local | Ivan |
| Permitir eliminar imágenes de la galería | Botón de eliminar con confirmación | Marco |

---

### T4. Listar y Cancelar Reservas (3 puntos)

#### T4.1 - Pantalla de listado de todas las reservas locales

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Crear ReservationsScreen.kt | Lista de reservas con hotel, habitación, fechas y viaje asociado | Marco |
| Crear ReservationsViewModel | Estado con Flow de reservas desde Room | Marco |
| Añadir navegación a la pantalla | Accesible desde el menú de perfil o desde Trips | Ivan |

#### T4.2 - Funcionalidad de cancelar reserva

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Implementar cancelación local | Eliminar ReservationEntity de Room | Ivan |
| Implementar cancelación via API | Llamar al endpoint DELETE/cancelación si está disponible | Ivan |
| Diálogo de confirmación | AlertDialog antes de cancelar | Marco |

#### T4.3 - Mostrar imágenes del hotel en la lista de reservas

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Mostrar thumbnail del hotel | AsyncImage con Coil en cada item de reserva | Marco |
| Mostrar imagen de la habitación reservada | Imagen secundaria en el card de reserva | Ivan |

#### T4.4 - Actualizar pantalla "Mis Viajes" para mostrar reservas

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Indicar si un viaje tiene reserva de hotel | Badge o icono en TripCard | Marco |
| Mostrar detalles de la reserva en el viaje | Nombre del hotel y número de habitación en TripOverviewScreen | Ivan |

---

## Deliverables

| Entregable | Descripción | Asignado |
|------------|-------------|----------|
| Release v4.0.0 | GitHub release con tag v4.x.x | Ambos |
| Demo video | Grabación mostrando todas las funcionalidades | Ivan |
| plan_sprint04.md | Este documento de planificación | Ambos |
| final_sprint04.md | Retrospectiva del sprint | Ambos |
| design.md actualizado | Esquema actualizado con nuevas entidades y arquitectura Retrofit | Ambos |

**Requisitos del vídeo:**
- Ubicación: `/docs/evidence/v4.0.0/demo.mp4`
- Mostrar todas las tareas implementadas
- Usar grabación del emulador de Android Studio o del teléfono

---

## Distribución de Tareas

### Responsabilidades de Ivan (50%)
**Retrofit y red:**
- Configurar OkHttpClient con timeouts e interceptores
- Crear data classes de respuesta de la API
- Crear interfaz `HotelRepository` e implementación `HotelRepositoryImpl`
- Tests unitarios para `HotelApiService`

**Búsqueda y reserva:**
- `HotelSearchScreen.kt` y `HotelSearchViewModel`
- Lógica de reserva (llamada a API + persistencia en Room)
- Actualizar `AppDatabase` con `ReservationEntity`
- Mostrar imágenes con Coil en `HotelDetailScreen`

**Galería de imágenes:**
- `TripImageEntity.kt` y `TripImageDao.kt`
- `TripGallerySection` en `TripOverviewScreen`
- Cargar imágenes locales con Coil

**Reservas:**
- Cancelación local y via API
- Imagen de habitación en lista de reservas
- Mostrar detalles de reserva en `TripOverviewScreen`

### Responsabilidades de Marco (50%)
**Retrofit y red:**
- Añadir dependencias en `build.gradle`
- Crear `NetworkModule` con Hilt (RetrofitClient, URL base)
- Crear interfaz `HotelApiService` con endpoints
- Inyectar repositorio en `RepositoryModule`
- Tests unitarios para `HotelRepositoryImpl` con MockWebServer

**Búsqueda y reserva:**
- `HotelListScreen.kt` y `HotelDetailScreen.kt`
- `ReservationEntity.kt` y `ReservationDao.kt`
- Componentes UI: `HotelCard`, `RoomCard`
- Thumbnail de hotel en `HotelListScreen`
- Navegación a confirmación de reserva

**Galería de imágenes:**
- Botón de galería en `TripOverviewScreen`
- Selector de imágenes con `ActivityResultContracts`
- `TripImageRepository` interfaz e implementación
- Copiar imagen al almacenamiento interno
- Eliminar imágenes de la galería

**Reservas:**
- `ReservationsScreen.kt` y `ReservationsViewModel`
- Diálogo de confirmación de cancelación
- Thumbnail del hotel en lista de reservas
- Badge de reserva en `TripCard`

### Responsabilidades Compartidas
- Mappers entre modelos remotos y entidades de dominio
- Actualización de `design.md`
- Integración de Hilt en nuevos módulos
- Code reviews
- Corrección de bugs
- Grabación del vídeo demo
- Documentos del sprint (plan y retrospectiva)

---

## Definition of Done

Una tarea se considera completada cuando:
- El código sigue el patrón MVVM con arquitectura Repository
- **Hilt** se usa como librería de inyección de dependencias
- **Retrofit** gestiona todas las llamadas a la API REST
- **Room** persiste localmente reservas e imágenes de viajes
- Las imágenes remotas se cargan con **Coil**
- La UI se actualiza dinámicamente ante cambios en la base de datos
- Los tests unitarios de repositorio y API están escritos y pasan
- El código incluye logs apropiados en Logcat
- `design.md` refleja el esquema actualizado con las nuevas entidades
- Los cambios están commiteados con mensajes descriptivos
- Los cambios están en GitHub bajo la release v4.x.x

---

## Riesgos y Mitigación

| Riesgo | Impacto | Estrategia de Mitigación |
|--------|---------|--------------------------|
| API del profesor no disponible o inestable | Alto | Implementar MockWebServer para tests; usar respuestas hardcodeadas como fallback durante desarrollo |
| Permisos de almacenamiento para galería de imágenes | Medio | Solicitar permisos en runtime correctamente; usar FileProvider para URIs |
| Migración de Room al añadir nuevas entidades | Alto | Incrementar versión de DB y usar `fallbackToDestructiveMigration` en desarrollo |
| Carga lenta de imágenes remotas | Bajo | Coil gestiona caché automáticamente; añadir placeholders y estados de error |
| Compatibilidad entre modelos remotos y de dominio | Medio | Crear mappers claros entre DTOs de Retrofit y entidades de Room/dominio |
| Tests con MockWebServer complejos | Medio | Usar respuestas JSON de ejemplo en `assets/test/` |

---

## Timeline del Sprint

**Semana 1 (05 May - 11 May):**
- Añadir dependencias de Retrofit, OkHttp y Coil
- Crear `NetworkModule` con Hilt y `RetrofitClient`
- Definir `HotelApiService` con los endpoints de la API
- Crear data classes de respuesta (DTOs)
- Explorar la API del profesor con Postman o similar

**Semana 2 (12 May - 18 May):**
- Implementar `HotelRepository` e `HotelRepositoryImpl`
- Crear `HotelSearchScreen` y `HotelListScreen`
- Implementar `HotelDetailScreen` con imágenes (Coil)
- Crear `ReservationEntity`, `ReservationDao` y lógica de reserva
- Implementar selector de imágenes y galería por viaje

**Semana 3 (19 May - 25 May):**
- Crear `ReservationsScreen` con listado y cancelación
- Actualizar `TripsScreen` con badge de reserva
- Escribir tests unitarios con MockWebServer/MockK
- Actualizar `design.md` con nuevas entidades y flujos
- Corrección de bugs, pulir navegación y UX
- Crear release v4.0.0 y grabar vídeo demo

---

## Notas

- **Hilt es obligatorio** como librería de inyección de dependencias
- **Retrofit** debe seguir la estructura MVVM vista en clase (RemotePersistence.zip)
- La API base es `http://15.224.84.148:8090` — confirmar endpoints disponibles con el profesor
- Las ciudades disponibles para búsqueda son: **London, Paris y Barcelona**
- Los date pickers son **obligatorios** para todos los campos de fecha
- El vídeo demo es **obligatorio** para la entrega
- El nombre del proyecto en Android Studio debe estar **correcto**

---

**Creado:** Inicio del Sprint 04  
**Última actualización:** Fase de planificación  
**Próxima revisión:** Retrospectiva Sprint 04 (final_sprint04.md)
