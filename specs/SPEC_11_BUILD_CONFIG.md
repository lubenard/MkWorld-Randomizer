# SPEC 11 — Build System & Configuration

## 1. Gradle

| Outil | Version |
|---|---|
| Gradle | 8.13 |
| Android Gradle Plugin (AGP) | 8.13.2 |
| Kotlin | 2.3.10 |
| Java | 21 (source/target) |

**Wrapper :** `gradle/wrapper/gradle-wrapper.properties`

## 2. Version Catalog — `gradle/libs.versions.toml`

### Versions clés

| Dépendance | Version |
|---|---|
| Compose BOM | 2024.09.00 |
| Navigation Compose | 2.9.7 |
| Material3 | 1.4.0 |
| DataStore Preferences | 1.2.1 |
| Gson | 2.14.0 |
| Konfetti Compose | 2.0.5 |
| Core KTX | 1.17.0 |
| Lifecycle Runtime KTX | 2.9.3 |
| Activity Compose | 1.11.0 |

### Plugins

```toml
[plugins]
android-application = "com.android.application:8.13.2"
kotlin-android = "org.jetbrains.kotlin.android:2.3.10"
kotlin-compose = "org.jetbrains.kotlin.plugin.compose:2.3.10"
```

## 3. App Module — `app/build.gradle.kts`

### Android Config

```kotlin
namespace = "com.escatrag.mkworldrandomiser"
compileSdk = 36
minSdk = 24
targetSdk = 36
versionCode = 1
versionName = "1.0"
```

### Product Flavors

Dimension : `environnement`

| Flavor | Application ID Suffix | App Name | COMMIT_SHA |
|---|---|---|---|
| `developp` | `.dev` | `debug - MK Randomizer` | `getGitHash()` |
| `production` | (aucun) | `MK World Randomizer` | `getGitHash()` |

**`getGitHash()` :** exécute `git rev-parse --short HEAD` au moment du build.

### Build Types

```kotlin
release {
    isMinifyEnabled = false
    signingConfig = signingConfigs.getByName("debug") // ⚠️ À remplacer par un vrai keystore
}
```

## 4. BuildConfig

Activé via `android.buildFeatures.buildConfig = true`.  
Injecte `BuildConfig.COMMIT_SHA` (utilisé dans `SettingsScreen.kt:76`).

## 5. Dépendances Complètes

```kotlin
// AndroidX
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.lifecycle.runtime.ktx)
implementation(libs.androidx.activity.compose)

// Compose (via BOM)
implementation(platform(libs.androidx.compose.bom))
implementation(libs.androidx.compose.ui)
implementation(libs.androidx.compose.ui.graphics)
implementation(libs.androidx.compose.ui.tooling.preview)
implementation(libs.androidx.compose.material3)
implementation(libs.androidx.material3)           // M3 standalone
implementation(libs.androidx.compose.material.icons.extended)

// Navigation
implementation(libs.androidx.navigation.compose)

// DataStore + JSON
implementation(libs.androidx.datastore.preferences)
implementation(libs.gson)

// Animation
implementation(libs.konfetti.compose)

// Tests
testImplementation(libs.junit)
androidTestImplementation(libs.androidx.junit)
androidTestImplementation(libs.androidx.espresso.core)
androidTestImplementation(platform(libs.androidx.compose.bom))
androidTestImplementation(libs.androidx.compose.ui.test.junit4)
debugImplementation(libs.androidx.compose.ui.tooling)
debugImplementation(libs.androidx.compose.ui.test.manifest)
```

## 6. Ressources

| Type | Nombre | Dossier |
|---|---|---|
| Drawables (icônes circuits) | ~60 (30 small + 30 large) | `res/drawable/` |
| Font | 1 (`minecraft.ttf`) | `res/font/` |
| String resources | 30 (29 pistes + app_name) | `res/values/strings.xml` |
| Launcher icons | Adaptives (anydpi-v26) | `res/mipmap-anydpi-v26/` |

## 7. ProGuard

Fichier : `proguard-rules.pro` (vide — `isMinifyEnabled = false`).  
Aucune optimisation ProGuard/R8 appliquée pour le moment.

## 8. Actions de Build

```bash
./gradlew assembleDevelop        # Build debug (appId: .dev)
./gradlew assembleProduction     # Build production
./gradlew test                   # Tests unitaires
./gradlew connectedAndroidTest   # Tests instrumentés (appareil/émulateur requis)
```
