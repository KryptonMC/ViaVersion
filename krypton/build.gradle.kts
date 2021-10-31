plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("kapt") version "1.6.0-RC"
}

dependencies {
    implementation(projects.viaversionCommon)
    compileOnly(libs.kotlin)
    compileOnly(libs.kryptonApi)
    compileOnly(libs.krypton)
    kapt(libs.kryptonAP)
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "16"
        freeCompilerArgs = listOf("-Xjvm-default=enable")
    }
}
