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

## Siguientes fases

2. Room, repositorios y datos iniciales (tus hábitos y las sesiones A y B).
3. Hoy y Hábitos funcionando: marcar, anillo de fuerza, lista de espera.
4. Entreno: alternancia A/B, registro por ejercicio e historial.
5. Agenda y calendario del teléfono.
6. Revisión semanal y regla de desbloqueo.
7. Notas, Ajustes, tema oscuro y exportar/importar.
8. Estudio (repetición espaciada), finanzas y retos.

## Subirlo a GitHub

```bash
git init
git add .
git commit -m "Fase 1: base visual y navegación"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/habitos.git
git push -u origin main
```

Crea el repositorio vacío en GitHub primero (sin README ni .gitignore,
porque este proyecto ya los trae).

## Compilar el APK sin Android Studio

El flujo `.github/workflows/android.yml` compila el APK en cada push a `main`.
Entra a la pestaña **Actions** del repositorio, abre la ejecución más reciente
y descarga el artefacto `habitos-debug-apk`. Ese APK se instala directo en tu
teléfono activando "instalar apps de origen desconocido".
