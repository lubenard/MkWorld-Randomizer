# SPEC 09 — Thème & Styling

## 1. Couleurs

**Fichier :** `ui/theme/Color.kt`

```kotlin
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80    = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40   = Color(0xFF7D5260)
```

Ces couleurs sont utilisées dans les `darkColorScheme` / `lightColorScheme` de Material3 comme valeurs de fallback (quand les couleurs dynamiques Android 12+ ne sont pas disponibles).

## 2. Palette Dynamique (Android 12+)

Sur Android 12+ (SDK 31+), le thème utilise `dynamicDarkColorScheme(context)` / `dynamicLightColorScheme(context)` — les couleurs sont extraites du wallpaper de l'utilisateur.

```kotlin
val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        if (darkTheme) dynamicDarkColorScheme(context)
        else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
}
```

## 3. Typographie

**Fichier :** `ui/theme/Type.kt`

```kotlin
val MinecraftFontFamily = FontFamily(Font(R.font.minecraft, FontWeight.Normal))

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
```

La police **Minecraft** est utilisée de manière sélective dans les composables :
- `TitleComposable` : tous les titres (MARIO KART, CIRCUITS, CLASSEMENT)
- `SpinWheel` : nom du circuit sélectionné
- `RecommenderButton` : texte "RECOMMENCER"
- `PodiumSection` : nom et score du joueur
- `DualSpinnerPhase` : texte "vers"
- `TrackSelectionScreen` boutons : "Tout activer", "Tout désactiver"
- `RaceResultActions` : "Saisir les scores"

La police par défaut (`FontFamily.Default`) est utilisée pour le contenu général.

## 4. ThemeMode

**Fichier :** `viewmodels/SettingsViewModel.kt:16`

```kotlin
enum class ThemeMode { SYSTEM, LIGHT, DARK }
```

Le mode est :
1. Persisté dans DataStore (`settings_prefs` / `theme_mode`)
2. Chargé au démarrage dans `SettingsViewModel.init`
3. Consommé dans `MainActivity` pour passer `darkTheme` à `MkWorldRandomiserTheme`

```kotlin
val isDarkTheme = when (themeMode) {
    ThemeMode.DARK -> true
    ThemeMode.LIGHT -> false
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
}
```

## 5. Theme — `ui/theme/Theme.kt`

```kotlin
@Composable
fun MkWorldRandomiserTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
)
```

Utilise le colorScheme déterminé (dynamique ou fallback), la `Typography` définie, et enveloppe le contenu dans `MaterialTheme`.

## 6. Couleurs Utilisées dans les Composables

| Usage | Valeur | Code |
|---|---|---|
| Fond du bloc ? | `0xFFFFC107` (jaune) | `HomeUI.kt:135` |
| Bouton Recommencer | `0xFFFFE401` (jaune) | `SpinWheel.kt:249` |
| Bouton Saisir scores | `0xFFFFE401` (jaune) | `RaceResultActions.kt:41` |
| Texte titre (ombre) | `Color.Red` | `TitleComposable.kt:24` |
| Texte titre (dessus) | `Color.Yellow` | `TitleComposable.kt:30` |
| Fond avatars défaut | `Color.Gray` | `ScoreViewModel.kt:33` |
| Barre progression | Gradient `0xFFFF5100` → `0xFFFCD676` | `TrackSelectionScreen.kt:149-153` |

## 7. Background

`MainActivity.kt:99-104` : Image de fond plein écran `R.drawable.map` avec `alpha(0.7f)`, placée en arrière du `NavHost`.
