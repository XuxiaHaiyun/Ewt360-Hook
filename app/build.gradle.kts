
plugins {
    id("com.android.application")
    
}

android {
    namespace = "com.xxhy.ewt360hook"
    compileSdk = 33
    
    defaultConfig {
        applicationId = "com.xxhy.ewt360hook"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        
        vectorDrawables { 
            useSupportLibrary = true
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
        
    }
    
}

dependencies {
    compileOnly(files("libs/xposed-api-82.jar"))
    implementation(files("libs/joor-0.9.15.jar"))
}
