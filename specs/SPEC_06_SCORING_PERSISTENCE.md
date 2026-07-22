# SPEC 06 — Scoring & Persistence

## 1. Système de Score

⚠️ **État actuel : Deux systèmes coexistent, dont un seul est persisté.**
C'est un bug/fragilité documenté — à corriger.

### 1.1 Barème Fixe (persisté dans `submitRaceResults`)

`ScoreViewModel.submitRaceResults()` (`ScoreViewModel.kt:118`) applique le calcul suivant :

1. **ELO pairwise** entre tous les participants classés
2. K = `32.0 / (n - 1)` où n = nombre de participants
3. Scale = 400 (formule standard : `1 / (1 + 10^((ratingB - ratingA) / 400))`)
4. Score attendu vs réel :
   - posA < posB → A gagne (actualA=1.0, actualB=0.0)
   - posA > posB → B gagne (actualA=0.0, actualB=1.0)
   - égalité → 0.5 chacun
5. Delta = `scaledK × (actual - expected)`
6. `currentMonthScore = max(0, player.currentMonthScore + delta)`

**Tous les participants** de la course reçoivent un delta (même le dernier).

### 1.2 Statistiques mises à jour par course

| Champ | Condition |
|---|---|
| `runNumbers` | Toujours +1 |
| `victoryNumbers` | Si position == 1 |
| `timesInPodium` | Si position ≤ 3 |
| `top3Maps` | Si position ≤ 3, ajoute/incrémente la map dans `top3Maps` |

### 1.3 handleTop3Maps — `ScoreViewModel.kt:170`

```kotlin
private fun handleTop3Maps(position: Int, player: PlayerProfile, currentMapId: Int): List<Top3Maps>
```

- `currentMapId` = `trackViewModel.selectedTrack.value?.start?.text ?: 0`
  - ⚠️ C'est le `R.string.xxx` de la piste, pas un identifiant stable
- Si déjà présent dans `player.top3Maps` → incrémente `timeInTop3`
- Sinon → ajoute `Top3Maps(mapId = currentMapId, timeInTop3 = 1)`

## 2. Persistance — DataStore + Gson

### 2.1 DataStore Scores

| Fichier | Nom DataStore | Clé | Type |
|---|---|---|---|
| `ScoreViewModel.kt` | `scores_prefs` | `players_list` | JSON → `List<PlayerProfile>` |

**Initialisation :**

```kotlin
private val Context.dataStore by preferencesDataStore(name = "scores_prefs")
```

**Lecture (`init` block) :**

```kotlin
context.dataStore.data.collect { prefs ->
    val json = prefs[PLAYERS_KEY] ?: ""
    if (json.isNotEmpty()) {
        val type = object : TypeToken<List<PlayerProfile>>() {}.type
        val savedList: List<PlayerProfile> = gson.fromJson(json, type)
        _players.value = savedList
    }
}
```

**Écriture (`persistData`) :**

```kotlin
private fun persistData(newList: List<PlayerProfile>) {
    viewModelScope.launch {
        _players.value = newList
        context.dataStore.edit { prefs ->
            prefs[PLAYERS_KEY] = gson.toJson(newList)
        }
    }
}
```

### 2.2 DataStore Thème

| Fichier | Nom DataStore | Clé | Type |
|---|---|---|---|
| `SettingsViewModel.kt` | `settings_prefs` | `theme_mode` | String (`SYSTEM`/`LIGHT`/`DARK`) |

### 2.3 Fonctions de persistance

| Fonction | Effet |
|---|---|
| `resetMonthlyScores()` | Remet tous les `currentMonthScore` à 3000 |
| `saveProfile(profile)` | Upsert joueur + persist |
| `submitRaceResults(...)` | Calcule scores + persist |
| `resetUsers()` | Vide la liste + persist |
| `deletePlayer(player)` | Supprime **en mémoire uniquement** — ⚠️ **NE PERSISTE PAS** |

## 3. Fragilités & Bugs du Scoring

| Problème | Détail |
|---|---|
| **ELO non persisté** | L'algo ELO inline dans `RaceResultScreen` (mentionné AGENTS.md) modifie `currentMonthScore` localement mais les résultats sont écrasés par `submitRaceResults()`. |
| **currentMonthScore ambigu** | Sert à la fois de score mensuel ET de rating ELO — pas de champ distinct. |
| **Top3Maps.mapId fragile** | Stocke `R.string.xxx` → si une string resource est renommée, les données historiques sont mortes. |
| **deletePlayer() oublie persistData** | `ScoreViewModel.kt:198-203` : supprime de la liste mais n'appelle pas `persistData()` → la suppression est perdue au redémarrage. |
| **mapId = 0 en fallback** | `submitRaceResults()` passe `trackViewModel.selectedTrack.value?.start?.text ?: 0` — si `selectedTrack` est null, `mapId = 0` → `mapId == 0` ne correspond à rien de valide. |
