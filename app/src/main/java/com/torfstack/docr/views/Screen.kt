package com.torfstack.docr.views

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Category : Screen("category") {
        fun withArgs(categoryId: String, version: Int): String {
            return buildString {
                append(route)
                append("/$categoryId")
                append("/$version")
            }
        }
    }
}