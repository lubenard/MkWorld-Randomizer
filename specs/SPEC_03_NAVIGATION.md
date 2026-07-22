# SPEC 03 — Navigation

## 1. NavHost & Routes

La navigation est gérée par `NavHost` dans `MainActivity.kt:106`.  
Routes littérales (string), pas de sealed class.

```
                          ┌──────────────────┐
                          │   MainActivity    │
                          │  (Bottom NavBar)  │
                          └────────┬─────────┘
                                   │
                    ┌──────────────┼──────────────┐
                    │              │              │
               "main"         "selection"    "score"
                    │              │              │
              MainScreen    TrackSelection   MonthlyScore
              (Aléatoire)    Screen          Screen
                    │         (Circuits)      (Scores)
                    │
          ┌─────────┼─────────┐
          │                   │
     "settings"         "scoreSelection"
          │                   │
   SettingsScreen       RaceResultScreen
                            │
                      popBackStack()
                      → retour à "main"
```

### Routes détaillées

| Route | Screen | NavOptions | Paramètres |
|---|---|---|---|
| `"main"` | `MainScreen` | startDestination | trackVM, settingsVM, scoreVM |
| `"selection"` | `TrackSelectionScreen` | — | trackVM |
| `"settings"` | `SettingsScreen` | — | trackVM, settingsVM |
| `"scoreSelection"` | `RaceResultScreen` | — | scoreVM, trackVM, callback |
| `"score"` | `MonthlyScoreScreen` | — | scoreVM, navController |

## 2. Bottom Navigation Bar

3 entrées dans `NavigationBar` (`MainActivity.kt:66-94`) :

| Index | Label | Icône | Route |
|---|---|---|---|
| 0 | Aléatoire | `Icons.Default.Home` | `"main"` |
| 1 | Circuits | `Icons.Default.Map` | `"selection"` |
| 2 | Scores | `Icons.Default.Groups` | `"score"` |

L'état `selectedTab` est un `mutableIntStateOf(0)` local — il n'est pas synchronisé avec `navController` après une navigation programmatique.

## 3. Transitions Programmatiques

| Action | Depuis | Vers | Déclencheur |
|---|---|---|---|
| Paramètres | MainScreen | `"settings"` | Clic icône engrenage |
| Saisie scores | MainScreen | `"scoreSelection"` | Clic "Saisir les scores" |
| Saisie scores | SpinningTrackPhase | `"scoreSelection"` | Clic "Saisir les scores" |
| Saisie scores | DualSpinnerPhase | `"scoreSelection"` | Clic "Saisir les scores" |
| Retour score → main | RaceResultScreen | popBackStack | "Enregistrer" cliqué |

## 4. État selectedTab

Le `selectedTab` est mis à jour manuellement dans chaque `onClick` des `NavigationBarItem`. Si l'utilisateur navigue programmatiquement (ex: depuis Settings vers "main" via le bouton back), le `selectedTab` peut devenir désynchronisé du route actuelle.

**Comportement souhaitable (non implémenté) :** Synchroniser `selectedTab` avec `navController.currentBackStackEntryAsState()`.
