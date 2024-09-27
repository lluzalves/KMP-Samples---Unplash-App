import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp.kmp)
    alias(libs.plugins.sqlitedelightPlugin)
    alias(libs.plugins.test.logger)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                    allWarningsAsErrors = false
                    freeCompilerArgs.add("-opt-in=androidx.compose.material3.ExperimentalMaterial3Api")
                }
            }
        }
    }

    sqldelight {
        databases {
            create("KmpAppDatabase") {
                packageName.set("com.daniel.myapplication.database")
            }
            linkSqlite = true
        }
    }

    ksp {
        arg("KOIN_CONFIG_CHECK", "true")
        arg("KOIN_DEFAULT_MODULE", "false")
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.ui)
            implementation(compose.runtime)
            implementation(compose.components.resources)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.material)
            implementation(compose.components.uiToolingPreview)
            api(libs.koin.annotations)
            implementation(libs.koin.core)
            implementation(libs.koin)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.logging)
            implementation(libs.kotlin.serialization)
            implementation(libs.ktor.json)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.koin.annotations)
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            implementation(libs.koin.annotations)
            api(libs.ktor.client.okhttp)
            api(libs.sqldelight.db.android)
        }

        iosMain.dependencies {
            implementation(libs.koin.annotations)
            implementation(libs.ktor.client.darwin)
            api(libs.sqldelight.db.ios)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.mockk.common)
        }
    }

    testlogger {
        theme = com.adarshr.gradle.testlogger.theme.ThemeType.STANDARD
        showStandardStreams = true
        showPassed = true
        showFailed = true
        showSkipped = true
        showExceptions = true
        showStackTraces = true
    }

    task("testClasses")

    tasks.withType<Test> {
        testLogging {
            events("passed", "failed", "skipped")
            showStandardStreams = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            displayGranularity = 1
        }
        reports {
            junitXml.required.set(true)  // Enable JUnit XML report generation
            html.required.set(true)      // Enable HTML report generation
        }
    }
}
android {
    namespace = "com.daniel.myapplication"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
