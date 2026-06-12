package com.raveloson.minigames

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.raveloson.minigames.ui.home.HomeScreen
import com.raveloson.minigames.ui.wordgame.WordGameScreen
import com.raveloson.minigames.ui.reaction.ReactionScreen
import com.raveloson.minigames.ui.theme.MiniGamesAppTheme
import androidx.compose.material3.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniGamesAppTheme {
                MiniGamesApp()
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
    MiniGamesAppTheme {
        Greeting("Android")
    }
}

@Composable
fun MiniGamesApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(
                onReactionClick = { navController.navigate(Reaction) },
                onWordGameClick = { navController.navigate(WordGame) }
            )
        }
        composable<Reaction> {
            ReactionScreen(onBackClick = { navController.popBackStack() })
        }
        composable<WordGame> {
            WordGameScreen(onBackClick = { navController.popBackStack() })
        }
    }
}