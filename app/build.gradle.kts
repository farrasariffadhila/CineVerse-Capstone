plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.capstone"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.capstone"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    dynamicFeatures += ":favorite"

    buildTypes {
        release {
            // Obfuscation + code shrinking (R8) untuk build produksi.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Obfuscation juga diaktifkan pada mode debug agar aplikasi yang
            // dijalankan/diuji sudah berjalan di atas kode yang ter-obfuscate.
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

    lint {
        // Ikut menganalisis :core dan :favorite dalam satu laporan.
        checkDependencies = true
        abortOnError = true
        // Aspek yang menjadi catatan reviewer diangkat menjadi ERROR supaya
        // regresi langsung menggagalkan pipeline CI, bukan lolos sebagai warning.
        error += listOf(
            "Overdraw",
            "UnusedResources",
            "DisableBaselineAlignment",
            "InefficientWeight",
            "UselessParent",
            "HardcodedText",
            "ContentDescription",
            "SetTextI18n",
            "SmallSp",
        )
        htmlReport = true
        xmlReport = true
    }
}

dependencies {
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
    implementation(libs.androidx.navigation.dynamic.features.fragment)
    implementation(libs.play.feature.delivery)

    debugImplementation(libs.leakcanary.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}