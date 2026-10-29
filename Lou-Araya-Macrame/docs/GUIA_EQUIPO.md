# Guía de equipo: GitHub, Trello y entrega

> Esta guía es para ustedes. No se sube nada automáticamente: **los commits y el tablero deben ser reales y hechos por ambos**, porque el docente revisa actividad y distribución.

## 1. Trello

Creen un tablero **Lou Araya Macramé – EP2** con las columnas `Backlog · Por hacer · En progreso · En revisión · Hecho` e inviten al docente y a la pareja. Tarjetas sugeridas (asignen cada una a una persona):

| # | Tarjeta | Sugerencia de responsable |
|---|---|---|
| 1 | Configurar proyecto Gradle, dependencias y repositorio | A |
| 2 | Tema Material 3 (paleta, formas, modo oscuro) | A |
| 3 | Navegación (NavHost + barra inferior) | A |
| 4 | Splash animado | A |
| 5 | Entidades, DAOs y base de datos Room | B |
| 6 | DataStore: sesión y preferencias | B |
| 7 | Validators + tests unitarios | B |
| 8 | Login y registro (UI + ViewModel) | B |
| 9 | Home: lista de mascotas | A |
| 10 | Formulario de mascota + cámara | B |
| 11 | Detalle de mascota + cuidados (bottom sheet) | A |
| 12 | Recordatorios (pantalla + WorkManager + notificaciones) | B |
| 13 | Perfil y ajustes | A |
| 14 | Animaciones y pulido visual | A |
| 15 | README, pruebas finales y entrega (GitHub + AVA) | A + B |

## 2. Flujo de Git recomendado

```
git init
git branch -M main
git remote add origin <URL>
```

- Una rama por tarea: `feature/room-database`, `feature/login-ui`, etc. y *Pull Request* hacia `main`.
- Hagan commits **pequeños y técnicos**, cada uno al terminar una pieza de trabajo (no todos juntos al final).
- Formato: `tipo: descripción técnica en infinitivo`. Ejemplos válidos:

```
chore: configurar proyecto Gradle con Compose, Room y Navigation
feat: definir tema Material 3 con esquemas claro y oscuro
feat: agregar entidades y DAOs de Room para usuarios, mascotas y cuidados
feat: implementar DataStore para sesión y preferencias de tema
feat: crear Validators con reglas de correo, contraseña y peso
test: agregar pruebas unitarias de Validators
feat: implementar LoginViewModel con validación en tiempo real
feat: construir LoginScreen con campos validados y mensajes animados
feat: agregar registro de usuario con hash de contraseña
feat: configurar NavHost y barra de navegación inferior
feat: implementar HomeScreen con lista de mascotas desde Room
feat: crear formulario de mascota con selector de fecha y especie
feat: integrar cámara con FileProvider y solicitud de permiso
feat: implementar detalle de mascota y registro de cuidados
feat: programar recordatorios con WorkManager y notificaciones
feat: agregar pantalla de perfil con modo oscuro y cierre de sesión
refactor: extraer componentes reutilizables de formularios
docs: completar README con integrantes y pasos de ejecución
```

Evitar: `cambios`, `arreglos`, `listo`, `asdf`, `update`.

## 3. Checklist de entrega

- [ ] Repositorio **público** en GitHub con acceso otorgado al docente y a ambos integrantes.
- [ ] README con nombres reales, equipo y enlace a Trello completados.
- [ ] Commits de **ambos** integrantes, repartidos en las 5 semanas.
- [ ] Tablero Trello con tareas asignadas y movidas entre columnas.
- [ ] App probada en emulador/dispositivo: login, crear mascota con foto, agregar cuidado, notificación.
- [ ] Un solo integrante sube el código fuente al **enlace AVA grupal**.
- [ ] **Cada integrante activa su enlace AVA individual** (nombre, apellido, número de equipo y nombre de la app).
- [ ] ⚠️ **No hacer ningún commit después de la entrega y antes de la evaluación** (nota 1.0 automática).
