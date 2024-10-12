plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.andoirdsecondhandtradingsystem"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.andoirdsecondhandtradingsystem"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)


    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0-alpha01")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3") // 确保使用合适的版本
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation(libs.androidx.navigation.compose)//导航
    implementation("com.google.android.material:material:1.5.0")
    implementation ("androidx.emoji2:emoji2-emojipicker:1.4.0-beta05")
    implementation(libs.androidx.emoji2.bundled)
    implementation(libs.androidx.compose.material)
    implementation(libs.material)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.paging.compose.android)//表情包



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation ("io.coil-kt:coil-compose:2.0.0")
    implementation ("io.coil-kt:coil-compose:2.0.0")
    implementation ("io.coil-kt:coil-compose:1.3.2")
    implementation ("androidx.compose.material3:material3:1.0.0-beta01")
    implementation ("androidx.compose.foundation:foundation:1.1.0-alpha03")
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.24.7-alpha")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.google.code.gson:gson:2.8.7")
    implementation ("io.coil-kt:coil-compose:1.2.2")
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.14.0")
    implementation ("androidx.compose.ui:ui:1.0.0")
    implementation ("androidx.compose.material3:material3:1.0.0")
    implementation ("androidx.activity:activity-compose:1.3.1")
    implementation ("androidx.navigation:navigation-compose:2.4.0-alpha10")
// 网络请求框架 okhttp3
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
// 用来解析json
    implementation ("com.google.code.gson:gson:2.9.0")
    implementation ("com.google.accompanist:accompanist-swiperefresh:<version>")
    implementation("io.coil-kt:coil-compose:2.0.0")


}