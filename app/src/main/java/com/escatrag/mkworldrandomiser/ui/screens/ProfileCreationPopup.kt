package com.escatrag.mkworldrandomiser.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.escatrag.mkworldrandomiser.R
import com.escatrag.mkworldrandomiser.viewmodels.PlayerProfile

@Composable
fun ProfileCreationPopup(
    profile: PlayerProfile,
    onDismiss: () -> Unit,
    onSave: (PlayerProfile) -> Unit
) {
    // États locaux pour la popup avant sauvegarde
    var tempName by remember { mutableStateOf(profile.name) }
    var tempAvatar by remember { mutableStateOf(profile.avatarRes) }
    var tempColor by remember { mutableStateOf(profile.composeColor) }

    // Liste des avatars disponibles (28 personnages de Mario Kart World)
    val availableAvatars = listOf(
        R.drawable.mario, R.drawable.luigi, R.drawable.peach, R.drawable.yoshi,
        R.drawable.bowser, R.drawable.toad, R.drawable.toadette, R.drawable.koopa,
        R.drawable.wario, R.drawable.waluigi, R.drawable.baby_mario, R.drawable.baby_luigi,
        R.drawable.baby_peach, R.drawable.baby_daisy, R.drawable.baby_rosalina, R.drawable.pauline,
        R.drawable.shy_guy, R.drawable.donkey_kong, R.drawable.daisy, R.drawable.rosalina,
        R.drawable.lakitu, R.drawable.birdo, R.drawable.king_boo, R.drawable.bowser_jr,
        R.drawable.para_biddybud, R.drawable.peepa, R.drawable.swoop, R.drawable.stingby
    )

    // Liste des couleurs de fond disponibles
    val availableColors = listOf(
        Color(0xFFFFCDD2), Color(0xFFE1BEE7), Color(0xFFBBDEFB), Color(0xFFC8E6C9),
        Color(0xFFFFF9C4), Color(0xFFFFCC80)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth().verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(stringResource(R.string.creer_pilote), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                // 1. Champ Nom
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text(stringResource(R.string.prenom)) },
                    placeholder = { Text(stringResource(R.string.ex_prenom)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Text(stringResource(R.string.choisis_personnage), style = MaterialTheme.typography.titleMedium)

                // 2. Grille d'Avatars (toujours 4 colonnes, s'adapte à la largeur)
                availableAvatars.chunked(4).forEach { rowAvatars ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowAvatars.forEach { avatarRes ->
                            val isSelected = tempAvatar == avatarRes
                            Image(
                                painter = painterResource(id = avatarRes),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                    .clickable { tempAvatar = avatarRes }
                                    .padding(if (isSelected) 4.dp else 0.dp)
                            )
                        }
                    }
                }

                Text(stringResource(R.string.couleur_fond), style = MaterialTheme.typography.titleMedium)

                // 3. Choix de couleur
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableColors.forEach { color ->
                        val isSelected = tempColor == color
                        Box(
                            modifier = Modifier
                                .size(35.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) Color.Black else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { tempColor = color }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 4. Boutons d'action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.annuler)) }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(profile.copy(
                                name = tempName,
                                avatarRes = tempAvatar,
                                profileColor = tempColor.toArgb()
                            ))
                        },
                        enabled = tempName.isNotBlank(), // Oblige à mettre un nom
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.sauvegarder))
                    }
                }
            }
        }
    }
}