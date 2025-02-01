plugins {
	id("org.jetbrains.kotlin.jvm") version "1.9.25"
	id("org.jetbrains.kotlin.plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.asciidoctor.jvm.convert") version "3.3.2"
	id("org.jetbrains.kotlin.plugin.jpa") version "1.9.25"
}

group = "com.moz1mozi"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
}

repositories {
	mavenCentral()
}

val snippetsDir = file("build/generated-snippets")

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
	annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jakarta")
	annotationProcessor("jakarta.annotation:jakarta.annotation-api")
	annotationProcessor("jakarta.persistence:jakarta.persistence-api")

	implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.add("-Xjsr305=strict")
	}
}

val generated = "build/generated/querydsl"

tasks.clean {
	delete("src/main/generated", generated)
}

tasks.withType<JavaCompile> {
	options.generatedSourceOutputDirectory.set(file(generated))
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

noArg {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.test {
	outputs.dir(snippetsDir)
	useJUnitPlatform()
}

tasks.named<org.asciidoctor.gradle.jvm.AsciidoctorTask>("asciidoctor") {
	inputs.dir(snippetsDir)
	dependsOn(tasks.test)
}
