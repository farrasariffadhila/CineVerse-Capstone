# ============================================================
# CineVerse :core — consumer ProGuard rules
# Aturan ini otomatis diwariskan ke module yang meng-konsumsi :core
# (:app dan :favorite), sehingga tiap module tidak perlu menduplikasi rule.
# ============================================================

-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Model publik :core yang dibaca via refleksi oleh Gson (remote) & Room (local).
-keep class com.example.capstone.core.data.source.remote.response.** { *; }
-keep class com.example.capstone.core.data.source.local.entity.** { *; }
-keep class com.example.capstone.core.domain.model.** { *; }

# Room memuat implementasi database lewat Class.forName("<Database>_Impl").
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.RoomDatabase$Callback { *; }

# Gson membaca nama field lewat anotasi @SerializedName.
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Retrofit membaca anotasi HTTP pada method interface via refleksi.
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
