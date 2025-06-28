package com.example.miaomiao.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object AddTransaction : Screen("add_transaction")
    object Statistics : Screen("statistics")
    object TransactionList : Screen("transaction_list")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object PersonalInfo : Screen("personal_info")
    object ExportBill : Screen("export_bill")
    object About : Screen("about")  // 添加关于我们路由
}