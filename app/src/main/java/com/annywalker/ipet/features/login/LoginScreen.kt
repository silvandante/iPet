package com.annywalker.ipet.features.login

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialManagerCallback
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.annywalker.ipet.R
import com.annywalker.ipet.core.designsystem.dimensions.AppDimens
import com.annywalker.ipet.core.designsystem.typography.AppTypography
import com.annywalker.ipet.features.main.MainSharedViewModel

@Composable
fun LoginScreen(
    viewModel: MainSharedViewModel,
    onLoginSuccess: () -> Unit,
    openSignUpScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }

    if (uiState.isUserLoggedIn) {
        LaunchedEffect(Unit) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppDimens.spacingMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.welcome),
                style = AppTypography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text(stringResource(R.string.email)) },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.MailOutline, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text(stringResource(R.string.password)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = viewModel::login,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(AppDimens.spacingMedium)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(AppDimens.spacingMedium),
                        strokeWidth = AppDimens.spacingSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Login com Email")
                }
            }

            OutlinedButton(
                onClick = openSignUpScreen,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(AppDimens.spacingMedium)
            ) {
                Text(stringResource(R.string.create_email_account))
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = AppDimens.spacingMedium),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )

            OutlinedButton(
                onClick = {
                    val request = viewModel.createGoogleSignInRequest()
                    val executor = ContextCompat.getMainExecutor(context)
                    try {
                        credentialManager.getCredentialAsync(
                            context = context,
                            request = request,
                            cancellationSignal = null,
                            executor = executor,
                            callback = object :
                                CredentialManagerCallback<GetCredentialResponse, GetCredentialException> {
                                override fun onResult(result: GetCredentialResponse) {
                                    viewModel.loginWithGoogleCredential(result.credential)
                                }

                                override fun onError(e: GetCredentialException) {
                                    Log.e("LoginScreen", "Credential fetch failed: ${e.message}")
                                }
                            }
                        )
                    } catch (e: Exception) {
                        Log.e("LoginScreen", "Error launching credential manager", e)
                    }
                },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(AppDimens.spacingLarge)
            ) {
                Icon(Icons.Default.AccountCircle, contentDescription = null)
                Spacer(Modifier.width(AppDimens.spacingMedium))
                Text(stringResource(R.string.enter_google))
            }

            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = AppTypography.bodyMedium
                )
            }
        }
    }
}

