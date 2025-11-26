package com.test.muzz.pages

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.test.muzz.MainActivity

class ProfilesPage(
    private val rule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
) {

    private val profileCards get() = rule.onAllNodesWithTag("profile_card")
    private val likeButton get() = rule.onNodeWithContentDescription("Like Profile")
    private val passButton get() = rule.onNodeWithContentDescription("Pass Profile")
    private val finishedStates get() = rule.onAllNodesWithTag("finished_state")
    private val retryButtons get() = rule.onAllNodesWithTag("button_retry")

    fun awaitLoaded(): ProfilesPage {
        // Wait until either a profile card or the finished state/error is present,
        // and loading spinner is gone.
        rule.waitUntil(timeoutMillis = 15_000) {
            val hasCard = profileCards.fetchSemanticsNodes().isNotEmpty()
            val hasFinished = finishedStates.fetchSemanticsNodes().isNotEmpty()
            val hasRetry = retryButtons.fetchSemanticsNodes().isNotEmpty()
            hasCard || hasFinished || hasRetry
        }
        when {
            profileCards.fetchSemanticsNodes().isNotEmpty() -> profileCards[0].assertIsDisplayed()
            finishedStates.fetchSemanticsNodes().isNotEmpty() -> finishedStates[0].assertIsDisplayed()
            retryButtons.fetchSemanticsNodes().isNotEmpty() -> retryButtons[0].assertIsDisplayed()
        }
        return this
    }

    fun like(times: Int = 1): ProfilesPage {
        repeat(times) { likeButton.performClick() }
        return this
    }

    fun pass(times: Int = 1): ProfilesPage {
        repeat(times) { passButton.performClick() }
        return this
    }

    fun assertFinished(): ProfilesPage {
        finishedStates[0].assertIsDisplayed()
        return this
    }

    fun assertRetryVisible(): ProfilesPage {
        retryButtons[0].assertIsDisplayed()
        return this
    }
}
