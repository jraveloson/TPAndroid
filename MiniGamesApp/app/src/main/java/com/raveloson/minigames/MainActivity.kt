package com.raveloson.minigames

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.raveloson.minigames.ui.home.HomeScreen
import com.raveloson.minigames.ui.theme.MiniGamesAppTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import com.raveloson.minigames.ui.reaction.ReactionScreen

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
    var isPlaying by remember {
        mutableStateOf(false)
    }
    if (isPlaying) {
        ReactionScreen(onBackClick = { isPlaying = false })
    }
    else {
        HomeScreen(onPlayClick = { isPlaying = true })
    }
}