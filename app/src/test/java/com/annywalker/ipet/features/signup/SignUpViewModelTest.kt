package com.annywalker.ipet.features.signup

import com.annywalker.ipet.managers.FirebaseLoginManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var firebaseLoginManager: FirebaseLoginManager
    private lateinit var viewModel: SignUpViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        firebaseLoginManager = mockk()
        viewModel = SignUpViewModel(firebaseLoginManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onEmailChange updates uiState email`() = runTest {
        val newEmail = "test@example.com"
        viewModel.onEmailChange(newEmail)

        val uiState = viewModel.uiState.first()
        assertEquals(newEmail, uiState.email)
    }

    @Test
    fun `onPasswordChange updates uiState password`() = runTest {
        val newPassword = "secret123"
        viewModel.onPasswordChange(newPassword)

        val uiState = viewModel.uiState.first()
        assertEquals(newPassword, uiState.password)
    }

    @Test
    fun `signUp success updates uiState correctly`() = runTest {
        val email = "user@example.com"
        val password = "password"
        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)

        coEvery { firebaseLoginManager.signUp(email, password) } returns Result.success(true)

        viewModel.signUp()
        advanceUntilIdle()

        val uiState = viewModel.uiState.first()
        assertFalse(uiState.isLoading)
        assertTrue(uiState.isUserCreated)
        assertNull(uiState.errorMessage)

        coVerify(exactly = 1) { firebaseLoginManager.signUp(email, password) }
    }

    @Test
    fun `signUp failure updates uiState with error`() = runTest {
        val email = "user@example.com"
        val password = "password"
        val errorMsg = "Sign up failed"

        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)

        coEvery { firebaseLoginManager.signUp(email, password) } returns Result.failure(
            Exception(
                errorMsg
            )
        )

        viewModel.signUp()
        advanceUntilIdle()

        val uiState = viewModel.uiState.first()
        assertFalse(uiState.isLoading)
        assertFalse(uiState.isUserCreated)
        assertEquals(errorMsg, uiState.errorMessage)

        coVerify(exactly = 1) { firebaseLoginManager.signUp(email, password) }
    }
}
