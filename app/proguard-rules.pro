# Rule model & library :core diwariskan lewat core/consumer-rules.pro

# Model presentation
-keep class com.example.capstone.presentation.model.** { *; }

# Fragment di-instansiasi by-name oleh FragmentManager & Navigation
-keep public class * extends androidx.fragment.app.Fragment
-keepclassmembers public class * extends androidx.fragment.app.Fragment {
    public <init>();
}

# Navigation & Play Feature Delivery
-keep class androidx.navigation.** { *; }
-keep class com.google.android.play.core.** { *; }
-dontwarn com.google.android.play.core.**

# Rule entry point :favorite ada di favorite/proguard-rules.pro

# Koin
-dontwarn org.koin.**
-keep class org.koin.core.** { *; }
-keep class org.koin.android.** { *; }

# Glide
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public class * extends com.bumptech.glide.module.LibraryGlideModule
-keepclassmembers class * extends com.bumptech.glide.module.AppGlideModule {
    <init>(...);
}

# SQLCipher (native lib via JNI)
-keep class net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**

# Shimmer (dirujuk dari layout XML)
-keep class com.facebook.shimmer.** { *; }

# Buang log verbose/debug dari release
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
}
