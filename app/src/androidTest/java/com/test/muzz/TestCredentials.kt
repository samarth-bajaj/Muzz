package com.test.muzz

import androidx.test.platform.app.InstrumentationRegistry

object TestCredentials {
    private val args = InstrumentationRegistry.getArguments()

    val username: String =
        args.getString("testUser")
            ?: throw IllegalStateException("Instrumentation arg 'testUser' not set")
    val password: String =
        args.getString("testPassword")
            ?: throw IllegalStateException("Instrumentation arg 'testPassword' not set")
}
