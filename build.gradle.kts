import org.gradle.kotlin.dsl.developmentOnly
import org.springframework.boot.gradle.tasks.run.BootRun
plugins {
	java
	id("org.springframework.boot") version "4.1.0"
	id("io.spring.dependency-management") version "1.1.7"
	id ("org.openjfx.javafxplugin") version "0.1.0"
}

group = "ca.lizardwizard"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

javafx {

    version = "25.0.2"
    modules = listOf("javafx.controls", "javafx.web")
}


dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("org.springframework.boot:spring-boot-starter-thymeleaf-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	val mainSourceSet = project.extensions.getByType<JavaPluginExtension>().sourceSets.getByName("main")
	sourceResources(mainSourceSet)
}
