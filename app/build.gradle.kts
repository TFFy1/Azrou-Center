plugins {
    application
    id("org.javamodularity.moduleplugin") version "1.8.12"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "3.0.1"
}

repositories {
    mavenCentral()
}

dependencies {
    // JavaFX
    implementation("org.openjfx:javafx-controls:21")
    implementation("org.openjfx:javafx-fxml:21")
    
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    
    // Database
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")
    implementation("org.hibernate.orm:hibernate-core:6.4.4.Final")
    implementation("org.hibernate.orm:hibernate-community-dialects:6.4.4.Final")
    implementation("com.zaxxer:HikariCP:5.1.0")
    
    // Utils
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    implementation("net.coobird:thumbnailator:0.4.20")
    implementation("org.apache.pdfbox:pdfbox:3.0.1")
    implementation("org.mindrot:jbcrypt:0.4")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.10.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.10.0")
    testImplementation("org.testfx:testfx-core:4.0.17")
    testImplementation("org.testfx:testfx-junit5:4.0.17")
}

application {
    mainModule.set("azrou.app")
    mainClass.set("azrou.app.App")
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(
        "--add-opens", "azrou.app/azrou.app.service=org.junit.platform.commons",
        "--add-opens", "azrou.app/azrou.app.repo=org.junit.platform.commons",
        "--add-opens", "azrou.app/azrou.app.ui.login=org.junit.platform.commons",
        "--add-opens", "azrou.app/azrou.app.ui.students=org.junit.platform.commons",
        "--add-opens", "azrou.app/azrou.app.ui.students=org.testfx",
        "--add-opens", "azrou.app/azrou.app.ui.login=org.testfx",
        "--add-opens", "azrou.app/azrou.app.ui.login=org.testfx.core"
    )
}

tasks.named<JavaCompile>("compileTestJava") {
    options.compilerArgs.addAll(listOf(
        "--add-modules", "org.junit.jupiter.api",
        "--add-reads", "azrou.app=org.junit.jupiter.api",
        "--add-reads", "azrou.app=org.mockito"
    ))
}

jlink {
    imageZip.set(project.file("${buildDir}/image-zip/AzrouCenterApp.zip"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "AzrouCenterApp"
    }
    jpackage {
        installerOptions.add("--win-per-user-install")
        installerOptions.add("--win-dir-chooser")
        installerOptions.add("--win-menu")
        installerOptions.add("--win-shortcut")
        imageOptions.add("--win-console") // Keep console for debugging if needed, or remove for production
        installerType = "msi" // or "exe"
    }
}
