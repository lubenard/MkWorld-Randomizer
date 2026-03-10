package com.escatrag.mkworldrandomiser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PlayerGroupingScreen(viewModel: TrackViewModel) {
    val groups by viewModel.groups.collectAsState()

    // États locaux pour la saisie des noms
    var name1 by remember { mutableStateOf("") }
    var name2 by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Nouveau Binôme", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(12.dp))

        // --- SAISIE DES NOMS ---
        OutlinedTextField(
            value = name1,
            onValueChange = { name1 = it },
            label = { Text("Joueur 1") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name2,
            onValueChange = { name2 = it },
            label = { Text("Joueur 2") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                viewModel.addGroup(Pair(name1.trim(), name2.trim()))
                name1 = "" // On vide les champs après l'ajout
                name2 = ""
            },
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
            enabled = name1.isNotBlank() && name2.isNotBlank() // Sécurité
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Former le groupe")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

        // --- LISTE DES GROUPES FORMÉS ---
        Text("Groupes enregistrés", style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(groups) { binome ->
                PairCard(pair = binome, onRemove = { viewModel.removeGroup(binome) })
            }
        }
    }
}

@Composable
fun PairCard(pair: Pair<String, String>, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Équipe", style = MaterialTheme.typography.labelSmall)
                Text(text = "${pair.first} & ${pair.second}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}