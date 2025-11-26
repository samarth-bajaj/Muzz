package com.test.muzz.tests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.test.muzz.BaseTest
import com.test.muzz.TestCredentials
import com.test.muzz.pages.LoginPage
import com.test.muzz.pages.ProfilesPage
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginTests : BaseTest() {

    @Test
    fun loginFailsWithWrongCredentials() {
        val loginPage = LoginPage(composeRule)

        loginPage.login(username = "wrong", password = "creds")
            .assertErrorShown()
    }

    @Test
    fun loginScreenIsShownOnFirstOpen() {
        composeRule.onNodeWithTag("login_username").assertIsDisplayed()
        composeRule.onNodeWithTag("login_password").assertIsDisplayed()
        composeRule.onNodeWithTag("login_button").assertIsDisplayed()
    }

    @Test
    fun loginSucceedsWithValidCredentials() {
        val loginPage = LoginPage(composeRule)
        val profilesPage = ProfilesPage(composeRule)

        loginPage.login(username = TestCredentials.username, password = TestCredentials.password)
        profilesPage.awaitLoaded()
    }

    @Test
    fun whenPreviouslyLoggedIn_appOpensToProfiles() {
        val loginPage = LoginPage(composeRule)
        val profilesPage = ProfilesPage(composeRule)

        loginPage.login(username = TestCredentials.username, password = TestCredentials.password)
        profilesPage.awaitLoaded()

        composeRule.activityRule.scenario.recreate()

        profilesPage.awaitLoaded()
    }

    /**
     * Simulates a new session without cold force-stop by recreating the activity.
     * This is the closest I can get without persisted login state in app code.
     */
    @Test
    fun whenPreviouslyLoggedIn_newSession_staysOnProfiles() {
        val loginPage = LoginPage(composeRule)
        val profilesPage = ProfilesPage(composeRule)

        loginPage.login(username = TestCredentials.username, password = TestCredentials.password)
        profilesPage.awaitLoaded()

        composeRule.activityRule.scenario.recreate()

        profilesPage.awaitLoaded()
    }
}
