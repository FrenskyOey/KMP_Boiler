package com.interview.prep.kmp_learn

class DesktopPlatform : Platform {
    override val name: String = "Desktop (JVM)"
}

actual fun getPlatform(): Platform = DesktopPlatform()
