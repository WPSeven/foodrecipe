package com.waiphyo.foodrecipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import com.waiphyo.foodrecipe.navigation.NavigationSubGraphs
import com.waiphyo.foodrecipe.navigation.RecipeNavigation
import com.waiphyo.foodrecipe.ui.theme.FoodRecipeTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigationSubGraphs: NavigationSubGraphs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodRecipeTheme {
                Surface(modifier = Modifier.safeContentPadding()) {
                    RecipeNavigation(navigationSubGraphs = navigationSubGraphs)
                }
            }
        }
    }
}
