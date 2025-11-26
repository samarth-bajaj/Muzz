package com.test.muzz.tests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.test.muzz.BaseTest
import com.test.muzz.TestCredentials
import com.test.muzz.di.RepositoryModule
import com.test.muzz.pages.LoginPage
import com.test.muzz.pages.ProfilesPage
import com.test.muzz.features.muzz.data.model.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.delay
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(RepositoryModule::class)
@RunWith(AndroidJUnit4::class)
class ProfilesErrorTests : BaseTest() {

    @Module
    @InstallIn(SingletonComponent::class)
    object FailingProfileModule {
        @Provides
        @Singleton
        fun provideFailingProfileRepository(): ProfileRepository = object : ProfileRepository {
            override suspend fun getProfiles() : List<com.test.muzz.features.muzz.data.model.Profile> {
                delay(500)
                throw IllegalStateException("Failed to profiles")
            }
        }
    }

    @Before
    fun loginFirst() {
        LoginPage(composeRule).login(username = TestCredentials.username, password = TestCredentials.password)
    }

    @Test
    fun showsErrorAndRetryWhenProfilesFailToLoad() {
        val profilesPage = ProfilesPage(composeRule)
        val errorNodes = composeRule.onAllNodesWithTag("error_state")
        composeRule.waitUntil(timeoutMillis = 10_000) {
            errorNodes.fetchSemanticsNodes().isNotEmpty()
        }
        errorNodes[0].assertIsDisplayed()
        composeRule.onNodeWithText("Failed to profiles", substring = true).assertIsDisplayed()
        profilesPage.assertRetryVisible()
    }
}
