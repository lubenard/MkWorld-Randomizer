package com.escatrag.mkworldrandomiser.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.escatrag.mkworldrandomiser.ui.theme.MinecraftFontFamily

@Composable
fun TitleComposable(text: String, fontSize: TextUnit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopStart
    ) {
        // 1. Le texte du dessous (décalé de 3px en rouge)
        Text(
            text = text,
            fontFamily = MinecraftFontFamily,
            fontSize = fontSize,
            color = Color.Red,
            modifier = Modifier.offset(x = 3.dp, y = 3.dp)
        )

        // 2. Le texte du dessus (en jaune)
        Text(
            text = text,
            fontFamily = MinecraftFontFamily,
            fontSize = fontSize,
            color = Color.Yellow
        )
    }
}