# Hábitos — fase 1

App personal de hábitos, entrenamiento, estudio, finanzas y retos.
Esta entrega es la **fase 1**: proyecto base compilable con el estilo
"vidrio de niebla", navegación y pantallas listas para llenarse.

## Qué incluye

- Proyecto Gradle (Kotlin DSL), `minSdk 26`, `compileSdk 35`.
- Jetpack Compose + Material 3, sin librerías externas de UI.
- Tema claro y oscuro, con cinco colores de acento (`AccentOption`).
- Fondo con manchas de color (degradados radiales: se ven igual en
  cualquier versión de Android, sin depender de `Modifier.blur`).
- `GlassCard`, `GlassList` y `GlassBottomBar` reutilizables.
- Navegación con las cinco secciones: Hoy, Hábitos, Entreno, Finanzas, Más.

## Cómo abrirlo

1. Android Studio → *Open* → elige esta carpeta.
2. Deja que sincronice Gradle (bajará el wrapper y las dependencias).
3. Run sobre tu teléfono con depuración USB activada.

Si Android Studio pide el Gradle wrapper, acepta que lo genere,
o ejecuta `gradle wrapper --gradle-version 8.9` si tienes Gradle instalado.

## Cambiar el color de acento

En `MainActivity.kt`:

```kotlin
HabitosTheme(accent = AccentOption.Violeta) { AppNavigation() }
```

En la fase 7 esto se elige desde Ajustes y se guarda en DataStore.

## Usar la tipografía Plus Jakarta Sans

1. Descarga los `.ttf` de Google Fonts.
2. Cópialos a `app/src/main/res/font/` con nombres en minúsculas
   (por ejemplo `plus_jakarta_sans_bold.ttf`).
3. En `ui/theme/Type.kt`, cambia `appFontFamily` por un `FontFamily(...)`
   que declare esos archivos.

## Qué funciona ya

- **Hoy:** sesión que toca (A o B), hábitos del día marcables con un toque y temas de repaso pendientes.
- **Hábitos:** crear hábitos con nombre, ancla y días; anillo de fuerza que sube y baja pero nunca se reinicia; lista de espera con activación.
- **Entreno:** alterna A y B según la última sesión, marca ejercicios, guarda el esfuerzo (fácil, justo, difícil) e historial.
- **Finanzas:** ingresos y gastos, balance del mes y lista de movimientos.
- **Retos:** mascota, nivel, puntos, escalera de siete rangos con su símbolo, retos de proceso y recompensas propias.
- **Estudio:** temas con repetición espaciada (SM-2 simplificado), recuperación activa e intercalado de materias.
- **Agenda:** bloques por día y periodo.
- **Revisión semanal:** conteos, automatismo del 1 al 5, reflexión y aviso de cuándo agregar un hábito nuevo.
- **Notas:** diario libre con lugar.
- **Ajustes:** color de acento, tema claro/oscuro/sistema, respaldo en JSON (copiar, compartir, importar) y reinicio.

Todo se guarda en el teléfono (SharedPreferences con JSON). Sin cuentas, sin internet.

## Plan de fases

1. Primer uso y estados vacíos. **Hecho.**
2. Hoy completo: bloques del día y accesos a Notas y Ajustes. **Hecho.**
3. Hábitos: editar, añadir a la lista de espera, confirmar antes de borrar. **Hecho.**
4. Entreno: editar los ejercicios de A y B, historial detallado. **Hecho.**
5. Agenda con fechas reales, íconos por tipo y tiempo libre destacado. **Hecho.**
6. Revisión semanal con todos los hábitos y desbloqueo real. **Hecho.**
7. Retos: progreso ligado a eventos reales y mascota por rango. **Hecho.**
8. Estudio: apuntes guardados por tema. **Hecho.**
9. Recordatorios (WorkManager) y calendario del teléfono.
10. Pulido: sombras, transiciones e ícono **hechos**; falta la tipografía Plus Jakarta Sans y una pasada de accesibilidad.
