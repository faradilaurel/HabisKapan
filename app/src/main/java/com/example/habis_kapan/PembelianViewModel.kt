package com.example.habis_kapan

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class PembelianViewModel : ViewModel() {

    private val api by lazy { SupabaseClient.api }

    var daftar by mutableStateOf<List<Pembelian>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // state untuk aksi insert / update / delete
    var isSaving by mutableStateOf(false)
        private set
    var aksiError by mutableStateOf<String?>(null)
        private set

    init {
        muatDaftar()
    }

    // ============ READ (list) ============
    fun muatDaftar() {
        viewModelScope.launch { refresh() }
    }

    private suspend fun refresh() {
        isLoading = true
        errorMessage = null
        try {
            daftar = api.getAll()
        } catch (e: Exception) {
            errorMessage = pesan(e)
        }
        isLoading = false
    }

    // ============ READ (detail) ============
    suspend fun ambilDetail(id: Long): Pembelian? = try {
        api.getById("eq.$id").firstOrNull()
    } catch (e: Exception) {
        null
    }

    // ============ INSERT ============
    fun tambah(req: PembelianRequest, onSukses: () -> Unit) {
        viewModelScope.launch {
            isSaving = true
            aksiError = null
            try {
                api.insert(req)
                refresh()
                onSukses()
            } catch (e: Exception) {
                aksiError = pesan(e)
            }
            isSaving = false
        }
    }

    // ============ UPDATE ============
    fun ubah(id: Long, req: PembelianRequest, onSukses: () -> Unit) {
        viewModelScope.launch {
            isSaving = true
            aksiError = null
            try {
                api.update("eq.$id", req)
                refresh()
                onSukses()
            } catch (e: Exception) {
                aksiError = pesan(e)
            }
            isSaving = false
        }
    }

    // ============ DELETE ============
    fun hapus(id: Long, onSukses: () -> Unit) {
        viewModelScope.launch {
            isSaving = true
            aksiError = null
            try {
                api.delete("eq.$id")
                onSukses()
                refresh()
            } catch (e: Exception) {
                aksiError = pesan(e)
            }
            isSaving = false
        }
    }

    fun bersihkanAksiError() {
        aksiError = null
    }

    private fun pesan(e: Exception): String = when (e) {
        is HttpException -> {
            val detail = try { e.response()?.errorBody()?.string() } catch (x: Exception) { null }
            val petunjuk = when (e.code()) {
                404 -> "Tabel 'pembelian' tidak ditemukan / URL salah."
                401, 403 -> "Key salah atau policy (RLS) belum dibuat."
                else -> "Permintaan ditolak."
            }
            "Error ${e.code()}: $petunjuk ${detail.orEmpty()}".trim()
        }
        is IOException -> "Tidak ada koneksi internet."
        else -> e.message ?: "Terjadi kesalahan."
    }
}