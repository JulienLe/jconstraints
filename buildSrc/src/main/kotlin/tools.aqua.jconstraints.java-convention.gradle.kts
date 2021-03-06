/*
 * Copyright 2015 United States Government, as represented by the Administrator
 *                of the National Aeronautics and Space Administration. All Rights Reserved.
 *           2017-2021 The jConstraints Authors
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR

plugins {
    id("tools.aqua.jconstraints.license-convention")
    `java-library`
    `maven-publish`
    id("com.github.sherter.google-java-format")
}

license {
    sourceSets = sourceSets.filter { it != project.sourceSets.main.get() }
    sourceSets = sourceSets.filter { it != project.sourceSets.test.get() }

    tasks {
        create("mainNonGenerated") {
            excludes += this@license.excludes
            includes += this@license.includes

            exclude { it.file.startsWith(project.buildDir) }

            files = project.sourceSets.main.get().allSource
        }
        create("testNonGenerated") {
            excludes += this@license.excludes
            includes += this@license.includes

            exclude { it.file.startsWith(project.buildDir) }

            files = project.sourceSets.test.get().allSource
        }
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

java.toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events(FAILED, STANDARD_ERROR, SKIPPED, PASSED)
        exceptionFormat = FULL
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set(provider { project.description?.split(' ')?.first() })
                description.set(provider { project.description })
            }
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            withType<MavenPublication> {
                pom {
                    url.set("https://github.com/tudo-aqua/jconstraints")
                    licenses {
                        license {
                            name.set("Apache-2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("jconstraints-authors")
                            name.set("The jConstraints Authors")
                        }
                    }
                    scm {
                        connection.set("https://github.com/tudo-aqua/jconstraints.git")
                        url.set("https://github.com/tudo-aqua/jconstraints")
                    }
                }
            }
        }
    }
}
