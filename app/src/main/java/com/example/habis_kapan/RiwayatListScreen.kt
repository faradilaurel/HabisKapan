package com.example.habis_kapan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.collections.filter
import kotlin.collections.firstOrNull

/**
 * HALAMAN 1 - LIST RIWAYAT PEMBELIAN
 * Daftar transaksi ditampilkan dengan LazyColumn.
 */
@Composable
fun RiwayatListScreen(
    daftar: List<Pembelian>,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    onItemClick: (Long) -> Unit,
    onTambah: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var tabIndex by rememberSaveable { mutableIntStateOf(1) } // 1 = "Produk"

    val filtered = remember(daftar, query) {
        if (query.isBlank()) daftar
        else daftar.filter {
            it.namaProduk.contains(query, ignoreCase = true) ||
                    it.toko.contains(query, ignoreCase = true)
        }
    }
    val bulanLabel = filtered.firstOrNull()?.let { bulanTahun(it.tanggal) } ?: bulanTahun(todayIso())

    Scaffold(
        containerColor = BgScreen,
        bottomBar = { BottomNavBar() },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onTambah,
                containerColor = BluePrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Tambah", fontWeight = FontWeight.Bold)
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(horizontal = 18.dp)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(12.dp))

            // ---------- Header ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        "CATATAN HARIAN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BluePrimary,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(2.dp))
                    Text("Aktivitas", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(Modifier.height(2.dp))
                    Text("Pantau pemakaian dan keuanganmu", fontSize = 13.sp, color = TextGray)
                }
                IconBox(Icons.Outlined.CalendarMonth, "Kalender")
            }

            Spacer(Modifier.height(18.dp))

            // ---------- Tab Pemakaian | Produk | Keuangan ----------
            SegmentedTabs(selected = tabIndex, onSelect = { tabIndex = it })

            Spacer(Modifier.height(14.dp))

            // ---------- Search ----------
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                placeholder = { Text("Cari produk atau toko...", color = TextGray, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = TextGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = BorderColor,
                    focusedBorderColor = BluePrimary
                )
            )

            Spacer(Modifier.height(14.dp))

            // ---------- Chip bulan + jumlah pembelian ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val chipShape = RoundedCornerShape(12.dp)
                Row(
                    modifier = Modifier
                        .clip(chipShape)
                        .background(Color.White)
                        .border(1.dp, BorderColor, chipShape)
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = TextDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(bulanLabel, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }
                Text("${filtered.size} pembelian", fontSize = 12.sp, color = TextGray)
            }

            Spacer(Modifier.height(12.dp))

            // ---------- Isi ----------
            when {
                tabIndex != 1 -> InfoKosong("Fitur ini segera hadir")

                isLoading && daftar.isEmpty() -> Box(
                    Modifier.fillMaxWidth().padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = BluePrimary) }

                errorMessage != null && daftar.isEmpty() -> Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(errorMessage, color = RedDanger, textAlign = TextAlign.Center, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) { Text("Coba lagi") }
                }

                filtered.isEmpty() -> InfoKosong("Belum ada pembelian")

                else -> Card(
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    // LazyColumn untuk daftar transaksi
                    LazyColumn {
                        itemsIndexed(filtered, key = { _, item -> item.id }) { index, item ->
                            PembelianRow(item) { onItemClick(item.id) }
                            if (index < filtered.lastIndex) {
                                HorizontalDivider(color = BorderColor)
                            }
                        }
                        item { Spacer(Modifier.height(if (filtered.size > 4) 72.dp else 0.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PembelianRow(item: Pembelian, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProductImage(
            url = item.imageUrl,
            modifier = Modifier.size(70.dp),
            shape = RoundedCornerShape(14.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                item.namaProduk,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "${formatTanggal(item.tanggal)} • ${item.toko}",
                fontSize = 11.sp,
                color = TextGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "${item.jumlah} item × ${formatRupiah(item.hargaPerItem)}",
                fontSize = 11.sp,
                color = TextGray
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            formatRupiah(item.totalHarga),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
    }
}

@Composable
private fun SegmentedTabs(selected: Int, onSelect: (Int) -> Unit) {
    val tabs = listOf("Pemakaian", "Produk", "Keuangan")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE5EAF3))
            .padding(4.dp)
    ) {
        tabs.forEachIndexed { i, label ->
            val isSelected = i == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Color.White else Color.Transparent)
                    .clickable { onSelect(i) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) BluePrimary else TextGray
                )
            }
        }
    }
}

@Composable
private fun InfoKosong(teks: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
        contentAlignment = Alignment.Center
    ) { Text(teks, color = TextGray, fontSize = 14.sp) }
}

@Composable
private fun BottomNavBar() {
    val items = listOf(
        "Beranda" to Icons.Outlined.Home,
        "Produk" to Icons.Outlined.Inventory2,
        "Belanja" to Icons.Outlined.ShoppingCart,
        "Aktivitas" to Icons.Outlined.BarChart,
        "Profil" to Icons.Outlined.Person
    )
    NavigationBar(containerColor = Color.White) {
        items.forEachIndexed { i, (label, icon) ->
            NavigationBarItem(
                selected = i == 3, // "Aktivitas" aktif
                onClick = {},
                icon = { Icon(icon, contentDescription = label) },
                label = {
                    Text(
                        label,
                        fontSize = 11.sp,
                        fontWeight = if (i == 3) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BluePrimary,
                    selectedTextColor = BluePrimary,
                    indicatorColor = BlueLight,
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray
                )
            )
        }
    }
}