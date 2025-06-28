package com.example.miaomiao

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.miaomiao.navigation.Screen
import com.example.miaomiao.ui.components.BottomNavigationBar
import com.example.miaomiao.ui.screens.*
import com.example.miaomiao.ui.theme.MiaomiaoTheme
import com.example.miaomiao.viewmodel.MainViewModel
import com.example.miaomiao.viewmodel.MainViewModelFactory
import com.example.miaomiao.ui.screens.ExportBillScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiaomiaoTheme {
                MiaomiaoApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiaomiaoApp() {
    val navController = rememberNavController()
    val application = androidx.compose.ui.platform.LocalContext.current.applicationContext as MiaomiaoApplication
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(
            application.transactionRepository, 
            application.userRepository,
            application.networkTimeService // 添加这行
        )
    )
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    
    if (isLoggedIn) {
        MainAppContent(navController = navController, viewModel = viewModel)
    } else {
        AuthNavigation(navController = navController, viewModel = viewModel)
    }
}

@Composable
fun AuthNavigation(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    // 登录成功后会自动切换到主应用
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = viewModel,
                onRegisterSuccess = {
                    // 注册成功后跳转到登录页面
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    
    // 需要显示底部导航的页面
    val bottomNavRoutes = listOf(
        Screen.Home.route,
        Screen.Statistics.route,
        Screen.TransactionList.route,
        Screen.Profile.route
    )
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                BottomNavigationBar(
                    currentRoute = currentRoute ?: Screen.Home.route,
                    onNavigate = { route ->
                        if (route == Screen.AddTransaction.route) {
                            navController.navigate(route)
                        } else {
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(viewModel = viewModel)
            }
            
            composable(Screen.Statistics.route) {
                StatisticsScreen(viewModel = viewModel)
            }
            
            composable(Screen.AddTransaction.route) {
                AddTransactionScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.TransactionList.route) {
                TransactionListScreen(viewModel = viewModel)
            }
            
            // 在ProfileScreen的composable中添加onNavigateToAbout参数
            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onNavigateToPersonalInfo = {
                        navController.navigate(Screen.PersonalInfo.route)
                    },
                    onNavigateToExportBills = {
                        navController.navigate(Screen.ExportBill.route)
                    },
                    onNavigateToAbout = {
                        navController.navigate(Screen.About.route)  // 添加关于我们导航
                    },
                    onLogout = {
                        viewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
            
            // 添加AboutScreen的composable
            composable(Screen.About.route) {
                AboutScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.PersonalInfo.route) {
                PersonalInfoScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.ExportBill.route) {
                ExportBillScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MiaomiaoTheme {
        Greeting("Android")
    }
}

