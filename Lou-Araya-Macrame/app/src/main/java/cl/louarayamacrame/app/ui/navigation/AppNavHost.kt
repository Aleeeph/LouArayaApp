package cl.louarayamacrame.app.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.louarayamacrame.app.ui.screens.HomeScreen
import cl.louarayamacrame.app.ui.screens.LoginScreen
import cl.louarayamacrame.app.ui.screens.PetDetailScreen
import cl.louarayamacrame.app.ui.screens.PetFormScreen
import cl.louarayamacrame.app.ui.screens.ProfileScreen
import cl.louarayamacrame.app.ui.screens.RegisterScreen
import cl.louarayamacrame.app.ui.screens.RemindersScreen
import cl.louarayamacrame.app.ui.screens.SplashScreen
import cl.louarayamacrame.app.viewmodel.SessionState
import cl.louarayamacrame.app.viewmodel.SessionViewModel

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val REMINDERS = "reminders"
    const val PROFILE = "profile"
    const val PET_FORM = "pet_form?petId={petId}"
    const val PET_DETAIL = "pet_detail/{petId}"

    fun petForm(id: Long = -1L) = "pet_form?petId=$id"
    fun petDetail(id: Long) = "pet_detail/$id"
}

private data class TabItem(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    TabItem(Routes.HOME, "Mascotas", Icons.Rounded.Pets),
    TabItem(Routes.REMINDERS, "Recordatorios", Icons.Rounded.Notifications),
    TabItem(Routes.PROFILE, "Perfil", Icons.Rounded.Person)
)

@Composable
fun AppNavHost(sessionVm: SessionViewModel) {
    val navController = rememberNavController()
    val session by sessionVm.session.collectAsStateWithLifecycle()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = tabs.any { it.route == currentRoute }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(tween(300)) { it } + fadeIn(),
                exit = slideOutVertically(tween(200)) { it } + fadeOut()
            ) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = backStack?.destination?.hierarchy?.any { it.route == tab.route } == true,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
            enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 10 } },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(300)) },
            popExitTransition = { fadeOut(tween(200)) + slideOutHorizontally(tween(300)) { it / 10 } }
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(
                    ready = session !is SessionState.Loading,
                    onFinished = {
                        val destination = if (session is SessionState.LoggedIn) Routes.HOME else Routes.LOGIN
                        navController.navigate(destination) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoggedIn = {
                        navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    },
                    onGoRegister = { navController.navigate(Routes.REGISTER) }
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onRegistered = {
                        navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    onAddPet = { navController.navigate(Routes.petForm()) },
                    onOpenPet = { navController.navigate(Routes.petDetail(it)) }
                )
            }
            composable(Routes.REMINDERS) { RemindersScreen() }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    onLoggedOut = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }
            composable(
                route = Routes.PET_FORM,
                arguments = listOf(navArgument("petId") { type = NavType.LongType; defaultValue = -1L })
            ) { entry ->
                PetFormScreen(
                    petId = entry.arguments?.getLong("petId") ?: -1L,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.PET_DETAIL,
                arguments = listOf(navArgument("petId") { type = NavType.LongType })
            ) { entry ->
                val petId = entry.arguments?.getLong("petId") ?: -1L
                PetDetailScreen(
                    petId = petId,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Routes.petForm(it)) }
                )
            }
        }
    }
}
