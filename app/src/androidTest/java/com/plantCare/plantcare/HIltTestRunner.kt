package com.plantCare.plantcare

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

class HiltTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        // We swap the default app for HiltTestApplication
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}