import com.google.protobuf.gradle.*

plugins {
    kotlin("jvm") version "2.3.0-RC"
    id("com.google.protobuf") version "0.9.5"
    application
}

application {
    // Define the main class for the application plugin
    mainClass.set("main.kotlin.GrpcClient")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.20")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.10.2")

    implementation("io.grpc:grpc-kotlin-stub:1.5.0")
    implementation("io.grpc:grpc-netty-shaded:1.77.0")
    implementation("io.grpc:grpc-protobuf:1.77.0")
    implementation("io.grpc:grpc-stub:1.77.0")

    implementation("com.google.protobuf:protobuf-java:4.33.0")
    compileOnly("org.apache.tomcat:annotations-api:6.0.53")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.33.0"
    }
    plugins {
        // Plugin to generate Kotlin gRPC stubs
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.77.0"
        }
        // Plugin for generating Kotlin message classes (optional, Java classes are Kotlin compatible)
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.5.0:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}

// Add generated sources to the source sets so the Kotlin compiler can find them
sourceSets["main"].java.srcDirs("build/generated/source/proto/main/grpc")
sourceSets["main"].java.srcDirs("build/generated/source/proto/main/grpckt")
sourceSets["main"].java.srcDirs("build/generated/source/proto/main/java")