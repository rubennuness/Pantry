import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

// Read SerpAPI key from local.properties (preferred) or Gradle property SERPAPI_KEY
val serpApiKey: String = run {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        val props = Properties()
        file.inputStream().use { props.load(it) }
        props.getProperty("SERPAPI_KEY") ?: providers.gradleProperty("SERPAPI_KEY").orNull ?: ""
    } else providers.gradleProperty("SERPAPI_KEY").orNull ?: ""
}

// EAN-Search configuration
val eanSearchToken: String = run {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        val props = Properties()
        file.inputStream().use { props.load(it) }
        props.getProperty("EAN_SEARCH_TOKEN") ?: providers.gradleProperty("EAN_SEARCH_TOKEN").orNull ?: ""
    } else providers.gradleProperty("EAN_SEARCH_TOKEN").orNull ?: ""
}
val eanSearchBaseUrl: String = run {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        val props = Properties()
        file.inputStream().use { props.load(it) }
        props.getProperty("EAN_SEARCH_BASE_URL") ?: "https://api.ean-search.org/api"
    } else "https://api.ean-search.org/api"
}

android {
    namespace = "com.smartgrocery.pantry"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.smartgrocery.pantry"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "SERPAPI_KEY", "\"${serpApiKey}\"")
        buildConfigField("String", "EAN_SEARCH_TOKEN", "\"${eanSearchToken}\"")
        buildConfigField("String", "EAN_SEARCH_BASE_URL", "\"${eanSearchBaseUrl}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-Xjvm-default=all",
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    // Compose compiler is now applied via plugin `org.jetbrains.kotlin.plugin.compose` (Kotlin 2.0+)

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coil.compose)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.mlkit.text.recognition)
    implementation(libs.coroutines.android)
    implementation(libs.okhttp)
}

