plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.contactsapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.contactsapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("debugCustom") {
            storeFile = file("/Users/alicjapuzio/keystore/contactsprovider.jks")
            storePassword = "password"
            keyAlias = "ContactsKey"
            keyPassword = "password"
        }
    }


    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debugCustom")
        }
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}