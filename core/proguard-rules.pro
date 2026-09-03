-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Public API :core (dipakai :app & :favorite)
-keep class com.example.capstone.core.di.** { *; }
-keep class com.example.capstone.core.domain.** { *; }
-keep class com.example.capstone.core.utils.** { *; }
-keep class com.example.capstone.core.databinding.** { *; }
-keep class com.example.capstone.core.R { *; }
-keep class com.example.capstone.core.R$* { *; }

# Model dibaca via refleksi (Gson & Room)
-keep class com.example.capstone.core.data.source.remote.response.** { *; }
-keep class com.example.capstone.core.data.source.local.entity.** { *; }
-keep class com.example.capstone.core.domain.model.** { *; }

# Gson
-dontwarn com.google.gson.**
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Retrofit & OkHttp
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

# Room & SQLCipher
-dontwarn androidx.room.paging.**
-dontwarn androidx.sqlite.db.**
-dontwarn net.sqlcipher.**
-keep class androidx.room.** { *; }
-keep class net.sqlcipher.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.RoomDatabase$Callback { *; }

# Koin
-dontwarn org.koin.**
-keep class org.koin.** { *; }

# Glide
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public class * extends com.bumptech.glide.module.LibraryGlideModule
-keep class com.bumptech.glide.** { *; }

# Coroutines
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }

# Desugaring
-dontwarn java.lang.invoke.StringConcatFactory
