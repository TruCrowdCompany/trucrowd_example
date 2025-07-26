plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "tech.trucrowd.gate"
    compileSdk = 35

    defaultConfig {
        applicationId = "tech.trucrowd.gateLibExample"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            pickFirsts.add("lib/x86/libc++_shared.so")
            pickFirsts.add("lib/x86_64/libc++_shared.so")
            pickFirsts.add("lib/armeabi-v7a/libc++_shared.so")
            pickFirsts.add("lib/arm64-v8a/libc++_shared.so")

            //excludes += "/lib/armeabi-v7a/*"      //exclude 32bit arm and leave arm64 bit only when uncommented
            excludes += "/lib/x64/*"
            excludes += "/lib/x86_64/*"
        }
    }
}

dependencies {

    implementation(files(".\\libs\\trucrowd.aar"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // SFE Toolkit 1.11.0
    val sfeToolkitVersion = "1.11.0"
    implementation("com.innovatrics.smartface.embedded.toolkit:core:$sfeToolkitVersion") {
        exclude(group = "*.java.dev.jna", module = "jna")
    }
    implementation("com.innovatrics.smartface.embedded.toolkit:face:$sfeToolkitVersion") {
        exclude(group = "*.java.dev.jna", module = "jna")
    }

    // SFE Toolkit solvers 1.11.0
    implementation("com.innovatrics.smartface.embedded.toolkit:face_detect_accurate_mask_onnxrt_solver:$sfeToolkitVersion")
    implementation("com.innovatrics.smartface.embedded.toolkit:face_landmarks_0_25_onnxrt_solver:$sfeToolkitVersion")
    implementation("com.innovatrics.smartface.embedded.toolkit:face_template_extract_fast_onnxrt_solver:$sfeToolkitVersion")
    implementation("com.innovatrics.smartface.embedded.toolkit:face_template_extract_balanced_onnxrt_solver:$sfeToolkitVersion")
    implementation("com.innovatrics.smartface.embedded.toolkit:face_liveness_passive_nearby_onnxrt_solver:$sfeToolkitVersion")

    val cameraxVersion = "1.4.2"
    implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-video:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")
    implementation("androidx.camera:camera-extensions:${cameraxVersion}")

    implementation("com.google.mlkit:face-detection:16.1.7")
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    implementation("org.opencv:opencv:4.11.0")
}