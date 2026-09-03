# Diwariskan ke :app dan :favorite

-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Model dibaca via refleksi oleh Gson & Room
-keep class com.example.capstone.core.data.source.remote.response.** { *; }
-keep class com.example.capstone.core.data.source.local.entity.** { *; }
-keep class com.example.capstone.core.domain.model.** { *; }

# Room memuat <Database>_Impl lewat Class.forName
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.RoomDatabase$Callback { *; }

# Gson
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Retrofit
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
