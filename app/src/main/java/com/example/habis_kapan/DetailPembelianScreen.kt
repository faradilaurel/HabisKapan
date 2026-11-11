package com.example.habis_kapan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * HALAMAN 2 - DETAIL PEMBELIAN
 * Menampilkan: nama produk, tanggal, jumlah, harga per item, total harga, toko.
 */
@Composable
fun DetailPembelianScreen(
    pembelian: Pembelian?,
    isLoading: Boolean,
    aksiError: String?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(containerColor = BgScreen) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(horizontal = 18.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))

            // ---------- Top bar ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconBox(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", onBack)
                Text(
                    "Detail Pembelian",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark
                )
                IconBox(Icons.Outlined.Edit, "Edit", onEdit)
            }

            Spacer(Modifier.height(16.dp))

            if (pembelian == null) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) CircularProgressIndicator(color = BluePrimary)
                    else Text("Data tidak ditemukan", color = TextGray)
                }
            } else {
                // ---------- Kartu 1: gambar + nama ----------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        ProductImage(
                            url = pembelian.imageUrl,
                            modifier = Modifier.fillMaxWidth().height(130.dp),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(pembelian.namaProduk, fontSize = 22.sp, color = TextDark)
                                if (!pembelian.merek.isNullOrBlank()) {
                                    Spacer(Modifier.height(2.dp))
                                    Text(pembelian.merek, fontSize = 12.sp, color = TextGray)
                                }
                            }
                            if (!pembelian.kategori.isNullOrBlank()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFDCE8FF))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        pembelian.kategori,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BluePrimary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // ---------- Kartu 2: informasi produk ----------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                        Text("Informasi Produk", fontSize = 14.sp, color = TextDark)
                        Spacer(Modifier.height(4.dp))
                        InfoRow("Tanggal pembelian", formatTanggal(pembelian.tanggal))
                        HorizontalDivider(color = BorderColor)
                        InfoRow("Jumlah", "${pembelian.jumlah} item")
                        HorizontalDivider(color = BorderColor)
                        InfoRow("Harga per item", formatRupiah(pembelian.hargaPerItem))
                        HorizontalDivider(color = BorderColor)
                        InfoRow("Total harga", formatRupiah(pembelian.totalHarga))
                        HorizontalDivider(color = BorderColor)
                        InfoRow("Toko", pembelian.toko)
                    }
                }

                Spacer(Modifier.height(14.dp))

                if (aksiError != null) {
                    Text(aksiError, color = RedDanger, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                }

                // ---------- Tombol Edit & Hapus ----------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(2.4f).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, BlueBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = BluePrimary
                        )
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Edit Data", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    OutlinedButton(
                        onClick = { showDialog = true },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, RedBorder),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = RedDanger
                        )
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Hapus", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    // ---------- Dialog konfirmasi hapus ----------
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Hapus pembelian?") },
            text = { Text("Data yang dihapus tidak bisa dikembalikan.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onDelete()
                }) { Text("Hapus", color = RedDanger, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = TextGray)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
    }
}