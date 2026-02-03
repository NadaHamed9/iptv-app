plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.iptv"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.iptv"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
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

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.leanback)
    implementation(libs.glide)

    // Add these two lines for your layout:
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.leanback:leanback:1.0.0")
    implementation(libs.androidx.fragment.ktx)
    // Media3 ExoPlayer for video playback
//    implementation("androidx.media3:media3-exoplayer:1.2.1")
//    implementation("androidx.media3:media3-ui:1.2.1")
//    implementation("androidx.media3:media3-exoplayer-dash:1.2.1")

    // Correct Media3 1.4.1 dependencies
//    implementation("androidx.media3:media3-exoplayer:1.4.1")
//    implementation("androidx.media3:media3-exoplayer-dash:1.4.1")
//    implementation("androidx.media3:media3-datasource:1.4.1")
//    implementation("androidx.media3:media3-exoplayer-rtsp:1.4.1")
//    implementation("com.github.K0ntrv:media3-ffmpeg:1.4.1")

    //implementation("org.jellyfin.media3:media3-ffmpeg-decoder:1.4.1")
    // ✅ Media3 1.4.1 Core
    val media3Version = "1.4.1"
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-ui:$media3Version")
    implementation("androidx.media3:media3-common:$media3Version")
    implementation("androidx.media3:media3-datasource:$media3Version")
    implementation("androidx.media3:media3-exoplayer-rtsp:$media3Version")

    implementation("org.videolan.android:libvlc-all:3.5.1")

}
