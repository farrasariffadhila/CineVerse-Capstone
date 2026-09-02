# ============================================================
# CineVerse :favorite (dynamic feature) — ProGuard/R8 rules
# Obfuscation aktif pada buildType debug & release (isMinifyEnabled = true).
# ============================================================

# Activity & Fragment dirujuk by-name dari AndroidManifest dan nav_graph :app,
# sehingga nama class-nya harus dipertahankan. Sisa isi module tetap di-obfuscate.
-keep class com.example.capstone.favorite.FavoriteActivity { *; }
-keep class com.example.capstone.favorite.FavoriteFragment { *; }

-keep public class * extends androidx.fragment.app.Fragment
-keepclassmembers public class * extends androidx.fragment.app.Fragment {
    public <init>();
}

# Koin module dynamic feature di-load saat runtime.
-dontwarn org.koin.**
-keep class org.koin.core.** { *; }
-keep class org.koin.android.** { *; }

# Play Feature Delivery
-keep class com.google.android.play.core.** { *; }
-dontwarn com.google.android.play.core.**
