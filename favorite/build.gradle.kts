plugins {
    alias(libs.plugins.android.dynamic.feature)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.capstone.favorite"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        // Catatan: Android Gradle Plugin MELARANG dynamic feature module
        // menyetel `isMinifyEnabled = true` dan akan menggagalkan build dengan pesan:
        //   "Dynamic feature modules cannot set minifyEnabled to true.
        //    To enable minification for a dynamic feature module,
        //    set minifyEnabled to true in the base module."
        // Obfuscation module ini dijalankan oleh R8 milik base module (:app),
        // yang sudah mengaktifkan isMinifyEnabled = true pada debug DAN release.
        // `proguardFiles` di bawah tetap dibaca dan digabungkan ke proses R8 base module.
        release {
            isMinifyEnabled = false
            // getDefaultProguardFile() tidak boleh dipakai di dynamic feature
            // (sudah disertakan oleh base module :app).
            proguardFiles("proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
            // getDefaultProguardFile() tidak boleh dipakai di dynamic feature
            // (sudah disertakan oleh base module :app).
            proguardFiles("proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":app"))
    implementation(project(":core"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.swiperefreshlayout)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.koin.core)
    implementation(libs.koin.android)

    implementation(libs.glide)
    implementation(libs.facebook.shimmer)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.play.feature.delivery)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
