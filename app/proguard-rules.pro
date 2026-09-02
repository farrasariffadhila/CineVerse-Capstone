# ============================================================
# CineVerse :app — ProGuard/R8 rules
# Obfuscation aktif pada buildType debug & release (isMinifyEnabled = true).
# Rule untuk model & library :core diwariskan otomatis lewat
# core/consumer-rules.pro, jadi file ini hanya memuat kebutuhan :app.
# ============================================================

# --- Model presentation (dipetakan dari domain model) ---
-keep class com.example.capstone.presentation.model.** { *; }

# --- Fragment di-instansiasi ulang by-name oleh FragmentManager & Navigation ---
-keep public class * extends androidx.fragment.app.Fragment
-keepclassmembers public class * extends androidx.fragment.app.Fragment {
    public <init>();
}

# --- Navigation + Play Feature Delivery (dynamic feature :favorite) ---
-keep class androidx.navigation.** { *; }
-keep class com.google.android.play.core.** { *; }
-dontwarn com.google.android.play.core.**

# Catatan: rule -keep untuk entry point dynamic feature :favorite TIDAK ditulis di
# sini. Module :app tidak bergantung pada :favorite (arah dependensinya terbalik),
# sehingga nama class tersebut tidak dapat di-resolve dari classpath :app dan
# dilaporkan sebagai "Unresolved class name" oleh inspeksi Shrinker Config file.
# Rule-nya berada di favorite/proguard-rules.pro, yang tetap digabungkan ke
# proses R8 milik base module ini.

# --- Koin (package sebenarnya org.koin, bukan io.insert.koin) ---
-dontwarn org.koin.**
-keep class org.koin.core.** { *; }
-keep class org.koin.android.** { *; }

# --- Glide ---
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public class * extends com.bumptech.glide.module.LibraryGlideModule
-keepclassmembers class * extends com.bumptech.glide.module.AppGlideModule {
    <init>(...);
}

# --- SQLCipher memuat native library lewat JNI ---
-keep class net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**

# --- Shimmer (dirujuk dari layout XML) ---
-keep class com.facebook.shimmer.** { *; }

# --- Buang log level verbose/debug dari APK release ---
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
}
