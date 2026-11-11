package com.example.habis_kapan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.habis_kapan.BgScreen
import com.example.habis_kapan.BluePrimary
import com.example.habis_kapan.DetailPembelianScreen
import com.example.habis_kapan.FormPembelianScreen
import com.example.habis_kapan.Pembelian
import com.example.habis_kapan.PembelianViewModel
import com.example.habis_kapan.RiwayatListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        setContent { HabisKapanApp() }
    }
}

@Composable
fun HabisKapanApp() {
    MaterialTheme(colorScheme = lightColorScheme(primary = BluePrimary)) {
        val navController = rememberNavController()
        // 1 ViewModel dipakai bersama oleh semua layar
        val vm: PembelianViewModel = viewModel()

        NavHost(navController = navController, startDestination = "list") {

            // ===== 1. Halaman List Riwayat =====
            composable("list") {
                RiwayatListScreen(
                    daftar = vm.daftar,
                    isLoading = vm.isLoading,
                    errorMessage = vm.errorMessage,
                    onRetry = vm::muatDaftar,
                    onItemClick = { id -> navController.navigate("detail/$id") },
                    onTambah = { navController.navigate("form") }
                )
            }

            // ===== 2. Halaman Detail Pembelian =====
            composable(
                route = "detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("id") ?: 0L
                var data by remember { mutableStateOf<Pembelian?>(null) }
                var loading by remember { mutableStateOf(true) }

                // Ambil detail dari Supabase; ambil ulang kalau daftar berubah (setelah edit)
                LaunchedEffect(id, vm.daftar) {
                    data = vm.ambilDetail(id)
                    loading = false
                }

                DetailPembelianScreen(
                    pembelian = data,
                    isLoading = loading,
                    aksiError = vm.aksiError,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate("form?id=$id") },
                    onDelete = { vm.hapus(id) { navController.popBackStack() } }
                )
            }

            // ===== 3. Halaman Form Tambah / Edit =====
            composable(
                route = "form?id={id}",
                arguments = listOf(navArgument("id") {
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) { entry ->
                val id = entry.arguments?.getLong("id") ?: -1L
                val modeEdit = id != -1L
                var awal by remember { mutableStateOf<Pembelian?>(null) }
                var siap by remember { mutableStateOf(!modeEdit) }

                LaunchedEffect(id) {
                    vm.bersihkanAksiError()
                    if (modeEdit) {
                        awal = vm.ambilDetail(id)
                        siap = true
                    }
                }

                if (!siap) {
                    Box(
                        Modifier.fillMaxSize().background(BgScreen),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = BluePrimary) }
                } else {
                    FormPembelianScreen(
                        awal = awal,
                        isSaving = vm.isSaving,
                        errorMessage = vm.aksiError,
                        onBack = { navController.popBackStack() },
                        onSimpan = { req ->
                            if (modeEdit) {
                                vm.ubah(id, req) { navController.popBackStack() }
                            } else {
                                vm.tambah(req) { navController.popBackStack() }
                            }
                        }
                    )
                }
            }
        }
    }
}