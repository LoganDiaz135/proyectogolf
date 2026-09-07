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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GolfScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
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
