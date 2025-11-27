package com.example.teacherstore

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.teacherstore.fakes.FakeApiViewModel
import com.example.teacherstore.fakes.FakeCartViewModel
import com.example.teacherstore.fakes.FakeUsuarioViewModel
import com.example.teacherstore.ui.screens.HomeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun muestraCatalogoEnPantalla() {

        val userVM = FakeUsuarioViewModel()
        val cartVM = FakeCartViewModel()
        val apiVM = FakeApiViewModel()

        composeRule.setContent {
            HomeScreen(
                vm = userVM,
                cartVM = cartVM,
                apiVM = apiVM,
                onNavigateToProfile = {},
                onNavigateToCart = {},
                onLogoutNavigate = {},
                onOpenDrawer = {}
            )
        }

        composeRule
            .onNodeWithText("Catálogo")
            .assertExists()
    }
}
