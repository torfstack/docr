package com.torfstack.docr.views

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Category : Screen("category")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}