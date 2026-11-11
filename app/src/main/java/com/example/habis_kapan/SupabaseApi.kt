package com.example.habis_kapan

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Konfigurasi Supabase.
 * PROJECT_URL cukup URL project saja (tanpa /rest/v1/), karena BASE_URL dibuat otomatis.
 * API_KEY = publishable key (aman dipakai di aplikasi). JANGAN pakai "secret key".
 */
object SupabaseConfig {
    const val PROJECT_URL = "https://jkfocovprasnzsurpgct.supabase.co"
    const val API_KEY = "sb_publishable_teZYWfl0wt6_r3XPBi98ow_hz-FsDOc"

    // Otomatis jadi: https://jkfocovprasnzsurpgct.supabase.co/rest/v1/
    val BASE_URL: String = PROJECT_URL.trim().trimEnd('/') + "/rest/v1/"
}

/** Endpoint CRUD ke tabel "pembelian" lewat PostgREST Supabase */
interface SupabaseApi {

    // READ - semua data (terbaru di atas)
    @GET("pembelian")
    suspend fun getAll(
        @Query("select") select: String = "*",
        @Query("order") order: String = "tanggal.desc,id.desc"
    ): List<Pembelian>

    // READ - detail 1 data. Isi id dengan "eq.5"
    @GET("pembelian")
    suspend fun getById(
        @Query("id") id: String,
        @Query("select") select: String = "*"
    ): List<Pembelian>

    // INSERT
    @Headers("Prefer: return=representation")
    @POST("pembelian")
    suspend fun insert(@Body body: PembelianRequest): List<Pembelian>

    // UPDATE. Isi id dengan "eq.5"
    @Headers("Prefer: return=representation")
    @PATCH("pembelian")
    suspend fun update(
        @Query("id") id: String,
        @Body body: PembelianRequest
    ): List<Pembelian>

    // DELETE. Isi id dengan "eq.5"
    @DELETE("pembelian")
    suspend fun delete(@Query("id") id: String)
}

object SupabaseClient {
    val api: SupabaseApi by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val builder = chain.request().newBuilder()
                    .addHeader("apikey", SupabaseConfig.API_KEY)
                // Key model baru ("sb_publishable_...") tidak dipakai sebagai Bearer
                if (!SupabaseConfig.API_KEY.startsWith("sb_")) {
                    builder.addHeader("Authorization", "Bearer ${SupabaseConfig.API_KEY}")
                }
                chain.proceed(builder.build())
            }
            .build()

        Retrofit.Builder()
            .baseUrl(SupabaseConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseApi::class.java)
    }
}