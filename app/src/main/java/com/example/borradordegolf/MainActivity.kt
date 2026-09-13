package com.example.borradordegolf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.borradordegolf.sensors.SwingDetector
import com.example.borradordegolf.ui.GolfScreen
import com.example.borradordegolf.ui.GolfViewModel
import com.example.borradordegolf.ui.theme.BorradorDeGolfTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.borradordegolf.ui.MenuScreen
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
                var enJuego by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (enJuego) {
                        GolfScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        MenuScreen(
                            onJugar = { enJuego = true },
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
