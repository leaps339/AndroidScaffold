plugins {
    id("com.android.library")
    id ("kotlin-android")
    id ("kotlin-parcelize")
//    id 'maven-publish'
//    id 'maven'
}

android {
    compileSdk = 29

    defaultConfig {
        minSdk = 20
        targetSdk = 29
//        versionCode = 1
//        versionName = "1.0"

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
//    kotlinOptions {
//        jvmTarget = JavaVersion.VERSION_1_8
//    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.21")
    api("com.squareup.wire:wire-runtime:3.0.2")

    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.0")
    api("androidx.core:core-ktx:1.3.2")
    api("androidx.collection:collection-ktx:1.1.0")
    implementation("androidx.appcompat:appcompat:1.2.0")

    api("androidx.lifecycle:lifecycle-common-java8:2.2.0")
    api("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0")

    api("com.squareup.okhttp3:okhttp:3.12.12")
    api("com.squareup.okio:okio:2.9.0")
}

//task androidSourcesJar(type: Jar) {
//    classifier = 'sources'
//    from android.sourceSets.main.java.srcDirs
//}
//
//artifacts {
//    archives androidSourcesJar
//}

//def MAVEN_PATH = "$MAVEN_PATH"
//def ARTIFACT_ID = 'ik-svga-player'
//def VERSION_NAME = '1.0.8'
//def GROUP_ID = "$POM_GROUP_ID"
//uploadArchives {
//    repositories {
//        mavenDeployer {
//            repository(url: MAVEN_PATH) {
//                authentication(userName: "$MAVEN_USER_NAME", password: "$MAVEN_USER_PWD")
//            }
//            pom.project {
//                groupId GROUP_ID
//                artifactId ARTIFACT_ID
//                version VERSION_NAME
//                packaging 'aar'
//            }
//        }
//    }
//}