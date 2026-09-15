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

L'onglet sélectionné est dérivé de la route courante via `navController.currentBackStackEntryAsState()` (voir section 4) : plus d'état local.

## 3. Transitions Programmatiques

| Action | Depuis | Vers | Déclencheur |
|---|---|---|---|
| Paramètres | MainScreen | `"settings"` | Clic icône engrenage |
| Saisie scores | MainScreen | `"scoreSelection"` | Clic "Saisir les scores" |
| Saisie scores | SpinningTrackPhase | `"scoreSelection"` | Clic "Saisir les scores" |
| Saisie scores | DualSpinnerPhase | `"scoreSelection"` | Clic "Saisir les scores" |
| Retour score → main | RaceResultScreen | popBackStack | "Enregistrer" cliqué |

## 4. État selectedTab

`selectedTab` est un `val` dérivé de `navController.currentBackStackEntryAsState()`, dans `MainActivity.kt` :

| Route | selectedTab |
|---|---|
| `"selection"` | 1 |
| `"score"` | 2 |
| `"main"`, `"settings"`, `"scoreSelection"` | 0 (défaut) |

Il se re-synchronise automatiquement à chaque navigation (back système, `popBackStack()`, transitions programmatiques).
