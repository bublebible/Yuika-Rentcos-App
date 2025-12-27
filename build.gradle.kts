// File: build.gradle.kts (Project Level)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    // INI YANG KURANG! Kita harus kasih tahu versinya di sini:
    id("com.google.gms.google-services") version "4.4.2" apply false

}