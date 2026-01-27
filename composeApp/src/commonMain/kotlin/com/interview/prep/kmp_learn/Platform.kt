package com.interview.prep.kmp_learn

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform