# Sprint 03 - Planning Document

**Project:** Hermes Travel App  
**Sprint Duration:** 23/03/2026 - 13/04/2026  
**Team:** Ivan Gil Cañizares, Marco Beruet Morelli  
**Delivery Deadline:** 13/04/2026 23:55

---

## Sprint Goal

Integrar persistencia de datos con SQLite (Room) y autenticación con Firebase en la Hermes Travel App, incluyendo:
- Reemplazar el almacenamiento in-memory del Sprint 02 por Room Database
- Autenticación con Firebase (login, logout, registro, recuperación de contraseña)
- Persistencia de datos de usuario en base de datos local
- Soporte multi-usuario en la tabla de viajes
- Testing de DAOs y operaciones de base de datos

**Target:** Deliver v3.0.0 release with persistent storage and Firebase authentication

---

## Sprint Backlog

### T1. Implementar SQLite con Room (5 puntos)

#### T1.1 - Crear la clase Room Database

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Crear AppDatabase.kt | Clase principal de Room con anotación @Database | Marco |
| Configurar versión y entidades | Registrar entidades Trip e ItineraryItem | Marco |
| Configurar singleton de la base de datos | Patrón singleton con fallbackToDestructiveMigration | Ivan |

#### T1.2 - Definir Entities para Trip e ItineraryItem

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Crear TripEntity.kt | Entidad Room para viajes (debe incluir campo datetime, text e integer) | Ivan |
| Crear ItineraryItemEntity.kt | Entidad Room para actividades | Marco |
| Definir claves primarias y foráneas | Relación Trip → ItineraryItem | Ivan |
| Definir TypeConverters | Para LocalDate, LocalTime y otros tipos complejos | Marco |

**Campos mínimos requeridos en entidades:**
- Al menos un campo datetime
- Al menos un campo text
- Al menos un campo integer

#### T1.3 - Crear Data Access Objects (DAOs)

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Crear TripDao.kt | Interfaz DAO con queries CRUD para viajes | Marco |
| Crear ItineraryItemDao.kt | Interfaz DAO con queries CRUD para actividades | Ivan |
| Implementar @Query, @Insert, @Update, @Delete | Anotaciones Room para cada operación | Ambos |
| Implementar Flow/LiveData en DAOs | Observabilidad reactiva para la UI | Ivan |

#### T1.4 - Implementar CRUD usando DAO

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Implementar addTrip() via DAO | Inserción en base de datos | Ivan |
| Implementar editTrip() via DAO | Actualización en base de datos | Ivan |
| Implementar deleteTrip() via DAO | Eliminación en base de datos | Marco |
| Implementar addActivity() via DAO | Inserción de actividad | Marco |
| Implementar updateActivity() via DAO | Actualización de actividad | Marco |
| Implementar deleteActivity() via DAO | Eliminación de actividad | Ivan |

#### T1.5 - Modificar ViewModels para usar Room

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Actualizar TripViewModel | Sustituir FakeTripDataSource por Room DAO | Ivan |
| Actualizar ActivityViewModel | Sustituir FakeActivityDataSource por Room DAO | Ivan |
| Usar coroutines/Flow en ViewModels | Operaciones asíncronas con Room | Marco |
| Inyectar repositorios con Hilt | Dependencias via @HiltViewModel | Ambos |

#### T1.6 - Asegurar actualizaciones de UI

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Observar Flow desde Room en UI | collectAsState() en Composables | Marco |
| Verificar actualización dinámica de listas | Tras cada CRUD la UI refleja cambios | Ambos |

---

### T2. Login y Logout con Firebase (3 puntos)

#### T2.1 - Conectar la app a Firebase

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Crear proyecto en Firebase Console | Configurar app Android | Marco |
| Añadir google-services.json | Integrar en el proyecto | Marco |
| Añadir dependencias de Firebase Auth | build.gradle actualizado | Ivan |

#### T2.2 - Diseñar pantalla de Login

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Crear LoginScreen.kt | Formulario con email y contraseña | Ivan |
| Añadir botón de Login | Acción de autenticación | Ivan |
| Añadir navegación a Registro | Link desde login | Marco |
| Añadir navegación a Recuperar contraseña | Link desde login | Marco |

#### T2.3 - Implementar Login con Firebase

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Implementar AuthRepository | Interfaz de autenticación | Marco |
| Implementar AuthRepositoryImpl | Lógica Firebase en repositorio | Marco |
| Implementar signInWithEmailAndPassword() | Autenticación con Firebase | Ivan |
| Redirigir a pantalla principal tras login | Navegación post-autenticación | Ivan |
| Comprobar sesión activa al iniciar la app | Si hay sesión, ir directo al menú principal | Ivan |

#### T2.4 - Implementar Logout

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Añadir opción de cerrar sesión | Botón accesible desde la app | Marco |
| Implementar signOut() | Cierre de sesión Firebase | Marco |
| Redirigir a LoginScreen tras logout | Navegación post-logout | Ivan |

#### T2.5 - Logging con Logcat

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Logs de operaciones de login/logout | DEBUG e INFO según resultado | Marco |
| Logs de errores de autenticación | ERROR con mensaje descriptivo | Marco |

---

### T3. Registro y Recuperación de Contraseña (4 puntos)

#### T3.1 - Diseñar pantalla de Registro

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Crear RegisterScreen.kt | Formulario de registro de usuario | Ivan |
| Campos del formulario | Email, contraseña, confirmación de contraseña y datos de perfil | Ivan |
| Validación de campos en UI | Campos requeridos, formato email, contraseñas coinciden | Marco |

#### T3.2 - Implementar Registro con Firebase

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Implementar createUserWithEmailAndPassword() | Registro en Firebase Auth | Marco |
| Implementar verificación de email | Envío de email de verificación tras registro | Marco |
| Patrón Repository para registro | AuthRepositoryImpl gestiona la lógica | Marco |
| Feedback al usuario | Mensajes de éxito y error claros | Ivan |

#### T3.3 - Implementar Recuperación de Contraseña

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Crear ForgotPasswordScreen.kt | Pantalla con campo de email | Ivan |
| Implementar sendPasswordResetEmail() | Envío de email de recuperación via Firebase | Ivan |
| Navegación desde LoginScreen | Link a pantalla de recuperación | Marco |
| Feedback al usuario | Confirmación de envío del email | Ivan |

---

### T4. Persistir Información de Usuario y Viajes (3 puntos)

#### T4.1 - Persistir información de usuario en DB local

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Crear UserEntity.kt | Entidad Room para usuarios | Marco |
| Crear UserDao.kt | DAO con operaciones CRUD de usuario | Marco |
| Campos mínimos de usuario | login, username, birthdate (date), address, country, phone, acceptEmails | Ivan |
| Validar username único | Comprobar si el username ya existe en DB antes de insertar | Ivan |

#### T4.2 - Multi-usuario en la tabla de viajes

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Añadir userId a TripEntity | Clave foránea al usuario propietario | Ivan |
| Filtrar viajes por usuario logado | Solo mostrar viajes del usuario actual | Ivan |
| Actualizar TripDao queries | WHERE userId = :currentUserId | Marco |

#### T4.3 - Actualizar design.md con esquema de base de datos

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Documentar esquema de tablas | Diagrama o descripción de entidades y relaciones | Ambos |
| Documentar estrategia de migración | Versiones de DB y migraciones | Marco |

#### T4.4 - Persistir accesos de usuario (log de sesiones)

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Crear AccessLogEntity.kt | Entidad para registrar logins/logouts | Marco |
| Crear AccessLogDao.kt | DAO para insertar registros de acceso | Marco |
| Insertar registro en cada login | userId + datetime + tipo (IN/OUT) | Ivan |
| Insertar registro en cada logout | userId + datetime + tipo (IN/OUT) | Ivan |

---

### T5. Testing y Debugging (2 puntos)

#### T5.1 - Unit tests para DAOs

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Tests para TripDao | Insert, update, delete, query | Marco |
| Tests para ItineraryItemDao | CRUD completo | Ivan |
| Tests para UserDao | Insert, query, validación de username único | Marco |
| Usar base de datos en memoria para tests | Room in-memory DB en tests | Ambos |

#### T5.2 - Validación de datos

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Prevenir nombres de viaje duplicados | Validación en ViewModel o DAO | Ivan |
| Validar fechas correctas | Consistencia de fechas en DB | Marco |
| Mantener validaciones del Sprint 02 | startDate < endDate, actividades dentro del rango | Ambos |

#### T5.3 - Logging con Logcat para DB

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Logs de operaciones CRUD en DB | DEBUG para cada operación | Marco |
| Logs de errores de base de datos | ERROR con stack trace | Marco |
| Logs de operaciones Firebase | INFO para auth, ERROR para fallos | Ivan |

#### T5.4 - Actualizar documentación

| Tarea | Descripción | Asignado |
|-------|-------------|----------|
| Actualizar design.md | Esquema DB, migraciones, arquitectura actualizada | Ambos |
| Documentar resultados de tests | Cobertura y casos probados | Marco |
| Documentar bugs resueltos | Registro de incidencias y soluciones | Ambos |

---

## Deliverables

| Entregable | Descripción | Asignado |
|------------|-------------|----------|
| Release v3.0.0 | GitHub release con tag v3.x.x | Ambos |
| Demo video | Grabación mostrando todas las funcionalidades | Ivan |
| plan_sprint03.md | Este documento de planificación | Ambos |
| final_sprint03.md | Retrospectiva del sprint | Ambos |
| design.md actualizado | Esquema de BD y arquitectura | Ambos |

**Requisitos del vídeo:**
- Ubicación: `/docs/evidence/v3.0.0/demo.mp4`
- Mostrar todas las tareas implementadas
- Usar grabación del emulador de Android Studio o del teléfono

---

## Distribución de Tareas

### Responsabilidades de Ivan (50%)
**Base de datos (Room):**
- AppDatabase.kt (singleton)
- TripEntity.kt
- ItineraryItemDao.kt
- deleteActivity() via DAO
- TypeConverters para tipos complejos

**ViewModels:**
- Actualizar TripViewModel para Room
- Actualizar ActivityViewModel para Room

**Firebase Auth:**
- Integración Login (signInWithEmailAndPassword)
- Redirección post-login y comprobación de sesión activa
- Redirección post-logout
- ForgotPasswordScreen + sendPasswordResetEmail()
- Logs de Firebase en Logcat

**Multi-usuario:**
- Añadir userId a TripEntity
- Filtrar viajes por usuario logado
- Insertar registros de acceso (login/logout)
- Validar username único

**UI:**
- LoginScreen.kt
- RegisterScreen.kt (campos)
- ForgotPasswordScreen.kt

**Testing:**
- Tests para ItineraryItemDao

### Responsabilidades de Marco (50%)
**Base de datos (Room):**
- AppDatabase.kt (configuración de entidades y versión)
- ItineraryItemEntity.kt
- TripDao.kt
- addActivity(), updateActivity() via DAO
- TypeConverters

**Firebase Auth:**
- Configuración Firebase (google-services.json, dependencias)
- AuthRepository + AuthRepositoryImpl
- signInWithEmailAndPassword() (repositorio)
- signOut()
- Registro: createUserWithEmailAndPassword() + verificación email
- Navegación a Registro y Recuperación desde LoginScreen
- Logs de login/logout en Logcat

**Persistencia de usuario:**
- UserEntity.kt
- UserDao.kt
- AccessLogEntity.kt + AccessLogDao.kt
- TripDao queries filtradas por userId

**Documentación:**
- Esquema de DB en design.md
- Estrategia de migración
- Resultados de tests

**Testing:**
- Tests para TripDao
- Tests para UserDao

### Responsabilidades Compartidas
- Configuración Hilt como DI
- Actualización de ViewModels (coroutines/Flow)
- Verificar actualizaciones dinámicas de UI
- Mantener validaciones del Sprint 02
- Actualización de design.md
- Code reviews
- Corrección de bugs
- Grabación del vídeo demo
- Documentos del sprint (plan y retrospectiva)

---

## Definition of Done

Una tarea se considera completada cuando:
- El código sigue el patrón MVVM con arquitectura Repository
- **Hilt** se usa como librería de inyección de dependencias
- Room Database reemplaza completamente el almacenamiento in-memory
- Firebase Auth gestiona login, logout y registro correctamente
- La UI se actualiza dinámicamente ante cambios en la base de datos
- La validación de datos se mantiene desde Sprint 02
- Los tests unitarios de DAOs están escritos y pasan
- El código incluye logs apropiados en Logcat
- design.md refleja el esquema actualizado de la base de datos
- Los cambios están commiteados con mensajes descriptivos
- Los cambios están en GitHub bajo la release v3.x.x

---

## Riesgos y Mitigación

| Riesgo | Impacto | Estrategia de Mitigación |
|--------|---------|--------------------------|
| Migraciones de Room incorrectas | Alto | Planificar esquema de DB antes de implementar; usar fallbackToDestructiveMigration en desarrollo |
| Conflictos entre Firebase y Room | Medio | Separar capas de autenticación y persistencia local |
| Pérdida de datos al migrar de in-memory a Room | Alto | Testear migración con dataset del Sprint 02 |
| Configuración incorrecta de Hilt | Medio | Seguir documentación oficial; revisar anotaciones @HiltViewModel, @Inject |
| Verificación de email Firebase compleja | Bajo | Consultar documentación Firebase y ejemplos |
| Tests de DAO con base de datos en memoria | Medio | Usar `Room.inMemoryDatabaseBuilder` en los tests |
| Tiempo de configuración Firebase | Medio | Configurar Firebase al inicio del sprint |

---

## Timeline del Sprint

**Semana 1 (13 Abr - 19 Abr):**
- Configurar Firebase y añadir dependencias de Room e Hilt
- Crear entidades Room (TripEntity, ItineraryItemEntity, UserEntity)
- Crear DAOs y AppDatabase
- Implementar LoginScreen y lógica básica de autenticación

**Semana 2 (20 Abr - 26 Abr):**
- Reemplazar in-memory storage por Room en ViewModels
- Implementar Registro y verificación de email
- Implementar ForgotPasswordScreen
- Implementar tabla de accesos (AccessLogEntity)
- Filtrar viajes por usuario logado

**Semana 3 (27 Abr - 03 May):**
- Escribir tests unitarios para DAOs
- Actualizar design.md con esquema de BD
- Corregir bugs y pulir navegación
- Crear documentación y vídeo demo
- Crear release v3.0.0

---

## Notas

- **Hilt es obligatorio** como librería de inyección de dependencias
- **Room reemplaza completamente** el almacenamiento in-memory del Sprint 02
- El patrón **Repository** debe mantenerse en la arquitectura
- **Firebase Auth** gestiona exclusivamente la autenticación
- La información de usuario se persiste también en **Room** (tabla local)
- El esquema de DB debe documentarse en **design.md**
- El vídeo demo es **obligatorio** para la entrega

---

**Creado:** Inicio del Sprint 03  
**Última actualización:** Fase de planificación  
**Próxima revisión:** Retrospectiva Sprint 03 (final_sprint03.md)
