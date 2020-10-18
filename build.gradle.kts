/*
 * Copyright © 2020 Pavel Kakolin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

plugins {
  kotlin("multiplatform") version "1.4.10"
  id("org.jmailen.kotlinter") version "3.2.0"
}

repositories {
  maven("https://dl.bintray.com/ktgpio/ktgpio/")
  mavenCentral()
}

val nativeLibs = configurations.create("nativeLibs") {
  isTransitive = false
}

dependencies {
  nativeLibs("io.ktgp:native-libs:${project.findProperty("ktgpio.version")}")
}

val unzip = tasks.register<Copy>("unzip") {
  from(zipTree(nativeLibs.singleFile))
  into("lib")
}


kotlin {
//  val native = linuxArm64("native")
  val native = linuxArm32Hfp("native")

  sourceSets.all {
    languageSettings.useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation("io.ktgp:core:${project.findProperty("ktgpio.version")}")
        implementation(kotlin("stdlib"))
      }
    }
  }

  configure(listOf(native)) {
    binaries.executable()
    binaries.all {
      linkTask.dependsOn(unzip)
    }
  }
}
