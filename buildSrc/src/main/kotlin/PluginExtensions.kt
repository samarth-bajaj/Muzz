@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

private val Project.baseAndroidExtension: BaseExtension
    get() = extensions.findByName("android") as BaseExtension

val Project.androidAppExtension: BaseAppModuleExtension
    get() = extensions.getByType()

fun applyAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    target: Project
) {
    target.plugins.apply("org.jetbrains.kotlin.plugin.compose")

    commonExtension.apply {
        buildFeatures {
            compose = true
        }
    }
}

fun Project.applyUiTests() {
    baseAndroidExtension.run {
        defaultConfig {
            testInstrumentationRunner = "com.test.muzz.CustomTestRunner"
        }
    }
}

fun Project.applyCommonAndroidConfig() {
    baseAndroidExtension.run {
        compileSdkVersion(35)
        defaultConfig {
            buildFeatures.buildConfig = true
            targetSdk = 35
            minSdk = 28
            versionCode = 1
            versionName = "1.0.0"
            consumerProguardFiles("consumer-rules.pro")
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
                freeCompilerArgs.set(listOf("-Xcontext-receivers"))
            }
        }

        buildTypes {
            findByName("release")?.run {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
                consumerProguardFiles("consumer-rules.pro")
            }
            findByName("debug")?.run {
                isMinifyEnabled = false
            }
        }

        testOptions {
            unitTests.isReturnDefaultValues = true
        }

        packagingOptions {
            resources.excludes.add("META-INF/AL2.0")
            resources.excludes.add("META-INF/LGPL2.1")
            resources.excludes.add("**/*.proto")
        }
    }
}

fun Project.applyAndroidAppPlugin() {
    plugins.apply("com.android.application")
}

fun Project.applyCommonPlugins() {
    with(plugins) {
        apply("kotlin-android")
        apply("org.jmailen.kotlinter")
        apply("kotlin-parcelize")
    }
}

fun Project.applyHilt() {
    val libs = this.extensions.getByType<VersionCatalogsExtension>().named("libs")
    dependencies {
        add("implementation", libs.findLibrary("hilt").get())
        add("kapt", libs.findLibrary("hilt.compiler").get())
        add("kapt", libs.findLibrary("hilt.android.compiler").get())
    }
}

fun Project.applyAppSigningConfig() {
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val keystoreProperties = Properties()
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))

    baseAndroidExtension.run {
        signingConfigs {
            create("local") {
                if (!keystorePropertiesFile.exists()) {
                    logger.warn("Signature file doesn't exist!!")
                    return@create
                }
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
            }
        }

        buildTypes {
            findByName("release")?.run {
                isMinifyEnabled = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
                signingConfig = signingConfigs.getByName("local")
            }
            findByName("debug")?.run {
                isMinifyEnabled = false
                signingConfig = signingConfigs.getByName("local")
            }
        }
    }
}
