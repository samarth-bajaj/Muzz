plugins {
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("app-plugin")
}

android {
    namespace = "com.test.muzz"
    defaultConfig {
        testInstrumentationRunnerArguments.putAll(
            mapOf(
                "testUser" to "user",
                "testPassword" to "password",
            ),
        )
    }
}

dependencies {
    api(libs.bundles.core)
    api(libs.timber)
    api(libs.kotlin.collections.immutable)
    api(libs.bundles.ui)
    api(libs.compose.foundation)
    api(libs.compose.foundation.layout)
    api(libs.compose.ui.util)
    api(libs.compose.runtime)
    api(libs.compose.runtime.lifecycle)

    implementation(libs.compose.paging)
    implementation(libs.bundles.ui)
    implementation(libs.androidx.core)
    implementation(libs.accompanist.system.ui.controller)
    implementation(libs.accompanist.webview)
    implementation(libs.compose.paging)
    implementation(libs.compose.constraint.layout)
    implementation(libs.compose.hilt.navigation)
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.javax.annotation)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    kapt(libs.room.compiler)
    implementation(libs.javajwt)
    implementation(libs.javax.annotation)

    // Hilt
    implementation(libs.hilt)
    kapt(libs.hilt.android.compiler)

    implementation(libs.glide.glide)
    kapt(libs.glide.compiler)

    testImplementation(libs.bundles.testUnit)

    androidTestImplementation(libs.bundles.testUi)
    kaptAndroidTest(libs.hilt.android.compiler)
}
