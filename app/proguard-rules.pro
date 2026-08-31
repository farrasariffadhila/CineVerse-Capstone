# CineVerse ProGuard Rules for Capstone Akhir

# Keep Models and Serialization
-keep class com.example.capstone.core.data.source.remote.response.** { *; }
-keep class com.example.capstone.core.data.source.local.entity.** { *; }
-keep class com.example.capstone.core.domain.model.** { *; }
-keep class com.example.capstone.presentation.model.** { *; }

# Gson Rules
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Retrofit & OkHttp
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Room & SQLCipher
-dontwarn androidx.room.paging.**
-dontwarn net.sqlcipher.**
-keep class androidx.room.** { *; }
-keep class net.sqlcipher.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.RoomDatabase$Callback
-dontwarn androidx.sqlite.db.**

# Koin
-dontwarn io.insert.koin.**
-keep class io.insert.koin.** { *; }

# Glide
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public class * extends com.bumptech.glide.module.LibraryGlideModule
-keep class com.bumptech.glide.** { *; }

# Shimmer
-keep class com.facebook.shimmer.** { *; }

# Navigation Dynamic Feature
-keep class androidx.navigation.** { *; }
-keep class com.example.capstone.favorite.** { *; }