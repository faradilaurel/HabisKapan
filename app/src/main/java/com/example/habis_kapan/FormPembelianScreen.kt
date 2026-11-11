package com.example.habis_kapan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * HALAMAN 3 - FORM TAMBAH / EDIT PEMBELIAN
 * awal == null  -> mode TAMBAH (insert)
 * awal != null  -> mode EDIT   (update)
 */
@Composable
fun FormPembelianScreen(
    awal: Pembelian?,
    isSaving: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onSimpan: (PembelianRequest) -> Unit
) {
    val isEdit = awal != null

    var namaProduk by rememberSaveable { mutableStateOf(awal?.namaProduk ?: "") }
    var merek by rememberSaveable { mutableStateOf(awal?.merek ?: "") }
    var kategori by rememberSaveable { mutableStateOf(awal?.kategori ?: "") }
    var toko by rememberSaveable { mutableStateOf(awal?.toko ?: "") }
    var tanggal by rememberSaveable { mutableStateOf(awal?.tanggal ?: todayIso()) }
    var jumlah by rememberSaveable { mutableStateOf(awal?.jumlah?.toString() ?: "1") }
    var harga by rememberSaveable { mutableStateOf(awal?.hargaPerItem?.toString() ?: "") }
    var imageUrl by rememberSaveable { mutableStateOf(awal?.imageUrl ?: "") }
    var submitted by rememberSaveable { mutableStateOf(false) }

    val tanggalValid = Regex("""\d{4}-\d{2}-\d{2}""").matches(tanggal)
    val jumlahInt = jumlah.toIntOrNull()
    val hargaLong = harga.toLongOrNull()

    val namaError = submitted && namaProduk.isBlank()
    val tokoError = submitted && toko.isBlank()
    val tanggalError = submitted && !tanggalValid
    val jumlahError = submitted && (jumlahInt == null || jumlahInt < 1)
    val hargaError = submitted && (hargaLong == null || hargaLong < 0)

    Scaffold(containerColor = BgScreen) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(horizontal = 18.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconBox(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", onBack)
                Text(
                    if (isEdit) "Edit Pembelian" else "Tambah Pembelian",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark
                )
                Spacer(Modifier.size(44.dp))
            }

            Spacer(Modifier.height(18.dp))

            FormField("Nama Produk *", namaProduk, { namaProduk = it }, "Contoh: Minyak Goreng", namaError, "Nama produk wajib diisi")
            Spacer(Modifier.height(12.dp))
            FormField("Merek", merek, { merek = it }, "Contoh: Bimoli")
            Spacer(Modifier.height(12.dp))
            FormField("Kategori", kategori, { kategori = it }, "Contoh: Dapur")
            Spacer(Modifier.height(12.dp))
            FormField("Toko *", toko, { toko = it }, "Contoh: Super Indo", tokoError, "Nama toko wajib diisi")
            Spacer(Modifier.height(12.dp))
            FormField("Tanggal Pembelian *", tanggal, { tanggal = it }, "yyyy-MM-dd (contoh: 2026-06-10)", tanggalError, "Format harus yyyy-MM-dd")
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.weight(1f)) {
                    FormField("Jumlah *", jumlah, { jumlah = it }, "1", jumlahError, "Minimal 1", KeyboardType.Number)
                }
                Box(Modifier.weight(1.4f)) {
                    FormField("Harga per Item *", harga, { harga = it }, "21400", hargaError, "Isi angka", KeyboardType.Number)
                }
            }
            Spacer(Modifier.height(12.dp))
            FormField("URL Gambar (opsional)", imageUrl, { imageUrl = it }, "https://...", keyboardType = KeyboardType.Uri)

            Spacer(Modifier.height(18.dp))

            if (errorMessage != null) {
                Text(errorMessage, color = RedDanger, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    submitted = true
                    val valid = namaProduk.isNotBlank() && toko.isNotBlank() && tanggalValid &&
                            jumlahInt != null && jumlahInt >= 1 && hargaLong != null && hargaLong >= 0
                    if (valid) {
                        onSimpan(
                            PembelianRequest(
                                namaProduk = namaProduk.trim(),
                                merek = merek.trim().ifBlank { null },
                                kategori = kategori.trim().ifBlank { null },
                                toko = toko.trim(),
                                tanggal = tanggal,
                                jumlah = jumlahInt!!,
                                hargaPerItem = hargaLong!!,
                                imageUrl = imageUrl.trim().ifBlank { null }
                            )
                        )
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        if (isEdit) "Simpan Perubahan" else "Simpan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean = false,
    errorText: String = "",
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(Modifier.fillMaxWidth()) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = isError,
            supportingText = if (isError) {
                { Text(errorText) }
            } else null,
            placeholder = { Text(placeholder, color = TextGray, fontSize = 13.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedBorderColor = BorderColor,
                focusedBorderColor = BluePrimary
            )
        )
    }
}