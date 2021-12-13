plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("kapt") version "1.6.0"
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
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjvm-default=enable")
    }
}
