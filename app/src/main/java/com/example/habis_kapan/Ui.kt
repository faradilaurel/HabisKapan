package com.example.habis_kapan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

// ---------------- Warna (disesuaikan dengan desain) ----------------
val BluePrimary = Color(0xFF2563EB)
val BlueLight = Color(0xFFEAF1FF)
val BlueBorder = Color(0xFFBFD3FF)
val TextDark = Color(0xFF0F172A)
val TextGray = Color(0xFF64748B)
val BgScreen = Color(0xFFF7F8FC)
val BorderColor = Color(0xFFE2E8F0)
val RedDanger = Color(0xFFDC2626)
val RedBorder = Color(0xFFFCA5A5)

/** Tombol ikon kotak bergaris (back / edit / kalender) seperti di desain */
@Composable
fun IconBox(icon: ImageVector, description: String, onClick: () -> Unit = {}) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(shape)
            .background(Color.White)
            .border(1.dp, BorderColor, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = description, tint = TextDark, modifier = Modifier.size(22.dp))
    }
}

/** Gambar produk dari URL; kalau kosong/gagal tampil kotak biru muda + ikon */
@Composable
fun ProductImage(url: String?, modifier: Modifier, shape: RoundedCornerShape) {
    if (url.isNullOrBlank()) {
        Box(
            modifier = modifier.clip(shape).background(BlueLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Inventory2, contentDescription = null, tint = BluePrimary)
        }
    } else {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = modifier.clip(shape),
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(BlueLight),
            error = ColorPainter(BlueLight)
        )
    }
}