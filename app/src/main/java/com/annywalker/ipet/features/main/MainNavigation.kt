package com.annywalker.ipet.features.main

import OnBoardingScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.annywalker.ipet.core.designsystem.theme.LightColorScheme
import com.annywalker.ipet.features.alarms.PetAlarmsScreen
import com.annywalker.ipet.features.alarms.PetMedsViewModel
import com.annywalker.ipet.features.diary.PetMainViewModel
import com.annywalker.ipet.features.diary.PetSymptomScreen
import com.annywalker.ipet.features.login.LoginScreen
import com.annywalker.ipet.features.onboarding.OnboardingViewModel
import com.annywalker.ipet.features.reports.PetReportScreen
import com.annywalker.ipet.features.reports.PetReportViewModel
import com.annywalker.ipet.features.signup.SignUpScreen
import com.annywalker.ipet.features.signup.SignUpViewModel
import kotlinx.coroutines.launch

@Composable
fun MainNavigation(
    mainSharedViewModel: MainSharedViewModel = hiltViewModel(),
) {
    val mainState by mainSharedViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    when {
        mainState.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        !mainState.isUserLoggedIn && !mainState.isOnboardingStarted -> {
            AuthNavHost(
                snackbarHostState = snackbarHostState,
                onLoginSuccess = { mainSharedViewModel.reloadUserState() },
                mainSharedViewModel = mainSharedViewModel
            )
        }

        mainState.isUserLoggedIn && !mainState.isOnboardingComplete -> {
            OnboardingNavHost(
                onBoardingFinished = { mainSharedViewModel.reloadOnboardingState() },
                onBoardingStarted = { mainSharedViewModel.onBoardingStarted() }
            )
        }

        mainState.isUserLoggedIn && mainState.isOnboardingComplete -> {
            MainAppNavHost(
                snackbarHostState = snackbarHostState,
                logout = { mainSharedViewModel.logout() }
            )
        }
    }
}


@Composable
fun OnboardingNavHost(
    onBoardingFinished: () -> Unit,
    onBoardingStarted: () -> Unit,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "onboarding",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("onboarding") {
            val viewModel: OnboardingViewModel = hiltViewModel()
            OnBoardingScreen(
                viewModel = viewModel,
                onBoardingFinished = onBoardingFinished,
                onBoardingStarted = onBoardingStarted
            )
        }
    }
}

@Composable
fun AuthNavHost(
    snackbarHostState: SnackbarHostState,
    onLoginSuccess: () -> Unit,
    mainSharedViewModel: MainSharedViewModel
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("login") {
            LoginScreen(
                viewModel = mainSharedViewModel,
                onLoginSuccess = onLoginSuccess,
                openSignUpScreen = { navController.navigate("signup") }
            )
        }
        composable("signup") {
            val viewModel: SignUpViewModel = hiltViewModel()
            SignUpScreen(
                viewModel = viewModel,
                onSignUpSuccess = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Conta criada! Faça login")
                    }
                    navController.popBackStack()
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun MainAppNavHost(
    snackbarHostState: SnackbarHostState,
    logout: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                val viewModel: PetMainViewModel = hiltViewModel()
                PetSymptomScreen(viewModel = viewModel)
            }
            composable("meds") {
                val viewModel: PetMedsViewModel = hiltViewModel()
                PetAlarmsScreen(viewModel = viewModel)
            }
            composable("reports") {
                val viewModel: PetReportViewModel = hiltViewModel()
                PetReportScreen(
                    viewModel = viewModel,
                    logout = logout
                )
            }
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("home", "Diário", Icons.Default.HealthAndSafety),
        BottomNavItem("meds", "Alarmes", Icons.Default.Notifications),
        BottomNavItem("reports", "Pets", Icons.Default.Pets)
    )

    NavigationBar {
        val currentDestination =
            navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                selected = currentDestination == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(item.icon, contentDescription = item.label)
                },
                label = {
                    Text(item.label)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = LightColorScheme.primary,
                    selectedTextColor = LightColorScheme.primary,
                    unselectedIconColor = LightColorScheme.onSurfaceVariant,
                    unselectedTextColor = LightColorScheme.onSurfaceVariant,
                    indicatorColor = LightColorScheme.surfaceVariant
                )
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
