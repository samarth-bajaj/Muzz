package com.test.muzz.pages

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.test.muzz.MainActivity

class LoginPage(
    private val rule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
) {

    private val usernameField get() = rule.onNodeWithTag("login_username")
    private val passwordField get() = rule.onNodeWithTag("login_password")
    private val loginButton get() = rule.onNodeWithTag("login_button")
    private val errorText get() = rule.onNodeWithTag("login_error")

    fun login(username: String, password: String): LoginPage {
        usernameField.performTextClearance()
        usernameField.performTextInput(username)
        passwordField.performTextClearance()
        passwordField.performTextInput(password)
        loginButton.performClick()
        return this
    }

    fun assertErrorShown(): LoginPage {
        errorText.assertIsDisplayed()
        return this
    }
}
