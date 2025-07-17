plugins {
    alias(libs.plugins.android.application) apply false
    id("org.jetbrains.kotlin.android") version "1.9.21" apply false // CORRIGIDO PARA 1.9.21
    id("org.jetbrains.compose") version "1.5.11" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}