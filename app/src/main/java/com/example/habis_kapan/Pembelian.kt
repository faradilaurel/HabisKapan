package com.example.habis_kapan

import com.google.gson.annotations.SerializedName
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Model data = 1 baris di tabel "pembelian" Supabase */
data class Pembelian(
    val id: Long = 0,
    @SerializedName("nama_produk") val namaProduk: String = "",
    val merek: String? = null,
    val kategori: String? = null,
    val toko: String = "",
    val tanggal: String = "",            // format yyyy-MM-dd
    val jumlah: Int = 1,
    @SerializedName("harga_per_item") val hargaPerItem: Long = 0,
    @SerializedName("image_url") val imageUrl: String? = null
) {
    val totalHarga: Long get() = jumlah * hargaPerItem
}

/** Body untuk INSERT / UPDATE (tanpa id, karena id dibuat otomatis oleh Supabase) */
data class PembelianRequest(
    @SerializedName("nama_produk") val namaProduk: String,
    val merek: String?,
    val kategori: String?,
    val toko: String,
    val tanggal: String,
    val jumlah: Int,
    @SerializedName("harga_per_item") val hargaPerItem: Long,
    @SerializedName("image_url") val imageUrl: String?
)

// ---------------- Helper format ----------------
private val localeId: Locale = Locale.forLanguageTag("id-ID")

/** 42800 -> "Rp 42.800" */
fun formatRupiah(value: Long): String =
    "Rp " + NumberFormat.getNumberInstance(localeId).format(value)

/** "2026-06-10" -> "10 Juni 2026" */
fun formatTanggal(iso: String): String = try {
    val masuk = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val keluar = SimpleDateFormat("d MMMM yyyy", localeId)
    keluar.format(masuk.parse(iso)!!)
} catch (e: Exception) {
    iso
}

/** "2026-06-10" -> "Juni 2026" */
fun bulanTahun(iso: String): String = try {
    val masuk = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val keluar = SimpleDateFormat("MMMM yyyy", localeId)
    keluar.format(masuk.parse(iso)!!)
} catch (e: Exception) {
    iso
}

/** Tanggal hari ini format yyyy-MM-dd */
fun todayIso(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())