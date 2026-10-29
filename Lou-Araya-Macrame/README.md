# Lou Araya Macramé — Catálogo móvil (MVP académico)

Adaptación del proyecto Android Lou Araya Macramé al caso **Lou Araya Macramé SPA** para DSY1105 Desarrollo de Aplicaciones Móviles.

## Funcionalidades implementadas

- Catálogo de piezas ornamentales con código, descripción, material, dimensiones y precio ficticio.
- Filtros por campaña: Matrimonios, Festivales, Interiores y Viajes; búsqueda por nombre/material.
- Vista de detalle de producto.
- Favoritos persistidos en Room/SQLite.
- Formulario de solicitud/cotización con validaciones de negocio independientes de Compose.
- Historial local de solicitudes con alias, evento, notas, fecha y estado inicial Pendiente.
- Arquitectura MVVM: `MacrameViewModel` + `MacrameRepository` + `MacrameDao` + Room.
- Animaciones de entrada y navegación inferior Material 3.
- Recursos nativos: selector de fotos de Android y notificaciones locales de campañas (solicita permiso en Android 13+).
- Capa Retrofit inicial con contratos de productos y solicitudes.

## Importante sobre el backend

La cátedra aún no ha entregado la URL ni el contrato real del backend. Por ello, el MVP trabaja con catálogo de demostración y persistencia local. `MacrameApi.kt` contiene contratos iniciales (`GET products`, `POST requests`), **que deben ajustarse a las rutas y campos oficiales** antes de declarar integración remota funcional. No hay URL productiva ni sincronización remota simulada.

## Ejecutar

1. Abrir la carpeta raíz `Lou Araya Macramé-main` desde Android Studio.
2. Usar JDK 17 y permitir que Android Studio sincronice Gradle.
3. Ejecutar la configuración `app` en un emulador Android (API 26 o superior) o dispositivo.
4. La primera ejecución inserta seis productos ficticios en la base `lou_araya_macrame.db`.

Las imágenes del catálogo usan URLs de demostración y requieren conexión a internet. La navegación, datos, favoritos y solicitudes usan almacenamiento local.

## Estructura añadida

```text
app/src/main/java/cl/louarayamacrame/app/macrame/
  data/
    MacrameApi.kt
    MacrameDatabase.kt
    MacrameRepository.kt
  domain/
    MacrameValidators.kt
    MacrameViewModel.kt
  ui/
    MacrameScreen.kt
```

## Lista pendiente para completar la evaluación

- [ ] Recibir la documentación real de la API y conectar Retrofit al repositorio.
- [ ] Añadir pruebas unitarias de validadores y pruebas de integración/repositorio.
- [ ] Verificar la ejecución en el emulador y revisar permisos/notificaciones en Android 13+.
- [ ] Crear el repositorio remoto del equipo y registrar commits reales de ambos integrantes.
- [ ] Crear tablero Trello con tareas, responsables y estados reales.
- [ ] Sustituir imágenes de demostración por el set autorizado por la cátedra, si está disponible.

No incluir nombres, RUT, correos reales ni credenciales productivas. Utilizar solo datos ficticios o anonimizados.
