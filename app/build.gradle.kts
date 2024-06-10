import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.libsDirectory

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "OscarGrC.tfccliente"
    compileSdk = 34

    defaultConfig {
        applicationId = "OscarGrC.tfccliente"
        minSdk = 29
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
    buildFeatures {
        viewBinding = true
    }

}

dependencies {
    //Firebase
    // FirebaseUI for Firebase Realtime Database
    implementation("com.google.firebase:firebase-database-ktx")
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation ("com.firebaseui:firebase-ui-database:8.0.2")
    // FirebaseUI for Cloud Firestore
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.2")
    // FirebaseUI for Firebase Auth
    implementation ("com.firebaseui:firebase-ui-auth:8.0.2")
    // FirebaseUI for Cloud Storage
    implementation ("com.firebaseui:firebase-ui-storage:8.0.2")
    //datastore
    implementation("androidx.datastore:datastore:1.1.1")
    //glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    //fragmentos
    implementation("androidx.fragment:fragment-ktx:1.7.1")
    //RecyclerView
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    //QR
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    //Redsys desde libreria + complementos necesarios como gson para Json o CardView
    implementation(files("libs/redsys-sdk-inApp-2.3.0.aar"))
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.google.code.gson:gson:2.9.0")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    implementation ("androidx.cardview:cardview:1.0.0")
    //Navegacion
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}