import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.annywalker.ipet.core.designsystem.typography.AppTypography
import com.annywalker.ipet.features.onboarding.OnboardingUiState
import com.annywalker.ipet.features.onboarding.OnboardingViewModel
import com.annywalker.ipet.features.onboarding.PermissionStep

@Composable
fun OnBoardingScreen(
    viewModel: OnboardingViewModel,
    onBoardingFinished: () -> Unit,
    onBoardingStarted: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    val notificationsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            viewModel.nextPermission(Manifest.permission.POST_NOTIFICATIONS)
        }

    val exactAlarmSettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.nextPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    }

    fun launchExactAlarmSettings() {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        exactAlarmSettingsLauncher.launch(intent)
    }

    LaunchedEffect(Unit) {
        onBoardingStarted()
    }

    when (val current = state) {
        is OnboardingUiState.Loading -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        is OnboardingUiState.Permissions -> {
            val permissions = current.permissions
            val currentIndex = current.currentIndex

            val currentPermission = permissions.getOrNull(currentIndex)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(64.dp))

                currentPermission?.let { permission ->
                    PermissionStep(permissionInfo = permission, onRequest = {
                        when (permission.permission) {
                            Manifest.permission.SCHEDULE_EXACT_ALARM -> {
                                launchExactAlarmSettings()
                            }

                            Manifest.permission.POST_NOTIFICATIONS -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notificationsLauncher.launch(permission.permission)
                                } else {
                                    viewModel.nextPermission(permission.permission)
                                }
                            }

                            else -> {
                                viewModel.nextPermission(permission.permission)
                            }
                        }
                    })
                }

                Spacer(modifier = Modifier.height(64.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    permissions.forEachIndexed { index, _ ->
                        val isSelected = index == currentIndex
                        val dot = if (isSelected) "●" else "○"
                        Text(
                            text = dot,
                            style = AppTypography.headlineMedium,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }

        is OnboardingUiState.Finished -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            onBoardingFinished()
        }
    }
}