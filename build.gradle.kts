import java.time.Duration

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.3.1" apply false
    id("com.android.library") version "7.3.1" apply false
    id ("org.jetbrains.kotlin.android") version "1.8.21" apply false
    id ("com.google.protobuf") version "0.9.3" apply false
}

tasks.register("hangingTask") {
    doLast {
        Thread.sleep(100000)
    }
    timeout.set(Duration.ofMillis(500))
}