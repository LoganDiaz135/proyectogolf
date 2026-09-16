package com.example.borradordegolf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.borradordegolf.sensors.SwingDetector
import com.example.borradordegolf.ui.GolfScreen
import com.example.borradordegolf.ui.GolfViewModel
import com.example.borradordegolf.ui.InstructionsScreen
import com.example.borradordegolf.ui.LevelSelectScreen
import com.example.borradordegolf.ui.MenuScreen
import com.example.borradordegolf.ui.Screen
import com.example.borradordegolf.ui.theme.BorradorDeGolfTheme

class MainActivity : ComponentActivity() {
    private val viewModel: GolfViewModel by viewModels()
    private lateinit var swingDetector: SwingDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        swingDetector = SwingDetector(
            context = this,
            onAimChanged = { direction -> viewModel.onAimChanged(direction) },
            onSwingDetected = { force -> viewModel.onSwingDetected(force) }
        )

        enableEdgeToEdge()
        setContent {
            BorradorDeGolfTheme {
                var currentScreen by remember { mutableStateOf(Screen.MENU) }
                val unlockedLevels by viewModel.unlockedLevels.collectAsState()
                val completedLevels by viewModel.completedLevels.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentScreen) {
                        Screen.GAME -> GolfScreen(
                            viewModel = viewModel,
                            onBackToMenu = { currentScreen = Screen.MENU },
                            modifier = Modifier.padding(innerPadding)
                        )
                        Screen.LEVELS -> LevelSelectScreen(
                            unlockedLevels = unlockedLevels,
                            completedLevels = completedLevels,
                            onLevelSelected = { levelNumber ->
                                viewModel.loadLevel(levelNumber)
                                currentScreen = Screen.GAME
                            },
                            onBack = { currentScreen = Screen.MENU },
                            modifier = Modifier.padding(innerPadding)
                        )
                        Screen.MENU -> MenuScreen(
                            onPlay = {
                                viewModel.startFromProgress()
                                currentScreen = Screen.GAME
                            },
                            onLevels = { currentScreen = Screen.LEVELS },
                            onHowToPlay = { currentScreen = Screen.INSTRUCTIONS },
                            modifier = Modifier.padding(innerPadding)
                        )
                        Screen.INSTRUCTIONS -> InstructionsScreen(
                            onBack = { currentScreen = Screen.MENU },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        swingDetector.start()
    }

    override fun onPause() {
        super.onPause()
        swingDetector.stop()
    }
}