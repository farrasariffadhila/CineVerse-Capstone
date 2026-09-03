# Entry point dirujuk by-name dari manifest & nav_graph :app
-keep class com.example.capstone.favorite.FavoriteActivity { *; }
-keep class com.example.capstone.favorite.FavoriteFragment { *; }

-keep public class * extends androidx.fragment.app.Fragment
-keepclassmembers public class * extends androidx.fragment.app.Fragment {
    public <init>();
}

# Koin
-dontwarn org.koin.**
-keep class org.koin.core.** { *; }
-keep class org.koin.android.** { *; }

# Play Feature Delivery
-keep class com.google.android.play.core.** { *; }
-dontwarn com.google.android.play.core.**
