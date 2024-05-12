package com.torfstack.docr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.torfstack.docr.views.CategoryDetailView
import com.torfstack.docr.views.CategoryView
import com.torfstack.docr.views.Screen


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Navigation()
        }
    }

    @Composable
    fun Navigation() {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            composable(route = Screen.Home.route) {
                CategoryView(navController = navController)
            }
            composable(
                route = Screen.Category.route + "/{categoryId}",
                arguments = listOf(navArgument("categoryId") { type = NavType.StringType }),
            ) {
                CategoryDetailView(
                    navController = navController,
                    categoryId = it.arguments?.getString("categoryId")!!
                )
            }
        }
    }
}
