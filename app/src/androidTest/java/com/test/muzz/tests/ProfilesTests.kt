package com.test.muzz.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.test.muzz.BaseTest
import com.test.muzz.TestCredentials
import com.test.muzz.pages.LoginPage
import com.test.muzz.pages.ProfilesPage
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ProfilesTests : BaseTest() {

    @Before
    fun loginFirst() {
        LoginPage(composeRule).login(username = TestCredentials.username, password = TestCredentials.password)
        ProfilesPage(composeRule).awaitLoaded()
    }

    @Test
    fun likingAllProfilesFinishesDeck() {
        val profilesPage = ProfilesPage(composeRule)

        profilesPage.like(times = 5)
            .assertFinished()
    }

    @Test
    fun mixPassAndLikeFinishesDeck() {
        val profilesPage = ProfilesPage(composeRule)

        profilesPage.pass(times = 2)
            .like(times = 3)
            .assertFinished()
    }

    @Test
    fun profilesLoadSuccessfullyWhenOnline() {
        ProfilesPage(composeRule).awaitLoaded()
    }
}
