# ============================================================
# CineVerse :core — ProGuard/R8 rules
# Obfuscation aktif untuk buildType debug & release (isMinifyEnabled = true).
# Aturan di bawah menjaga class yang diakses lewat refleksi / codegen
# (Gson, Retrofit, Room, SQLCipher, Koin) agar tetap berfungsi setelah di-obfuscate.
# ============================================================

# --- Atribut yang wajib dipertahankan untuk generic & annotation ---
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# ============================================================
# PUBLIC API :core
# :core diminifikasi terpisah dari :app, jadi R8 tidak bisa melihat
# pemakaian dari module konsumen. Seluruh permukaan API yang dipakai
# :app dan :favorite harus dipertahankan namanya, sedangkan implementasi
# internal (data.**, repository impl, data source, NetworkBoundResource)
# tetap di-shrink & di-obfuscate.
# ============================================================
-keep class com.example.capstone.core.di.** { *; }
-keep class com.example.capstone.core.domain.** { *; }
-keep class com.example.capstone.core.utils.** { *; }
-keep class com.example.capstone.core.databinding.** { *; }
-keep class com.example.capstone.core.R { *; }
-keep class com.example.capstone.core.R$* { *; }

# --- Data layer: DTO/Entity/Domain model dibaca via refleksi (Gson & Room) ---
-keep class com.example.capstone.core.data.source.remote.response.** { *; }
-keep class com.example.capstone.core.data.source.local.entity.** { *; }
-keep class com.example.capstone.core.domain.model.** { *; }

# --- Gson ---
-dontwarn com.google.gson.**
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# --- Retrofit & OkHttp ---
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# --- Room & SQLCipher (database encryption) ---
-dontwarn androidx.room.paging.**
-dontwarn androidx.sqlite.db.**
-dontwarn net.sqlcipher.**
-keep class androidx.room.** { *; }
-keep class net.sqlcipher.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.RoomDatabase$Callback { *; }

# --- Koin (dependency injection berbasis refleksi konstruktor) ---
-dontwarn org.koin.**
-keep class org.koin.** { *; }

# --- Glide ---
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public class * extends com.bumptech.glide.module.LibraryGlideModule
-keep class com.bumptech.glide.** { *; }

# --- Kotlin coroutines ---
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }

# --- Desugaring ---
# Saat :core diminifikasi terpisah, R8 belum melihat implementasi
# StringConcatFactory (di-desugar belakangan pada level :app).
-dontwarn java.lang.invoke.StringConcatFactory
