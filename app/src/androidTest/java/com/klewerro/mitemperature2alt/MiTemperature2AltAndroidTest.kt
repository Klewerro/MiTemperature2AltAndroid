package com.klewerro.mitemperature2alt

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.klewerro.mitemperature2alt.persistence.ThermometerDatabase
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

abstract class MiTemperature2AltAndroidTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var db: ThermometerDatabase

    protected lateinit var context: Context

    @Before
    open fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        hiltRule.inject()
        db.clearAllTables()
    }
}
