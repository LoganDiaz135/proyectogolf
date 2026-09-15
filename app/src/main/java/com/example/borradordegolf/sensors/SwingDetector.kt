package com.example.borradordegolf.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class SwingDetector(
    context: Context,
    private val onAimChanged: (directionRad: Float) -> Unit,
    private val onSwingDetected: (force: Float) -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private val SWING_THRESHOLD = 12f
    private var lastSwingTime = 0L
    private val COOLDOWN_MS = 800L

    // Para la dirección (Giroscopio)
    private var currentAngleRad = -Math.PI.toFloat() / 2f // Apuntando hacia arriba por defecto
    private var timestamp: Long = 0

    // Para la potencia (Acelerómetro)
    private var gravity = floatArrayOf(0f, 0f, 0f)
    private val alpha = 0.8f

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> handleAccelerometer(event)
            Sensor.TYPE_GYROSCOPE -> handleGyroscope(event)
        }
    }

    private fun handleAccelerometer(event: SensorEvent) {
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

        val x = event.values[0] - gravity[0]
        val y = event.values[1] - gravity[1]
        val z = event.values[2] - gravity[2]

        val magnitude = sqrt(x * x + y * y + z * z)

        if (magnitude > SWING_THRESHOLD) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastSwingTime > COOLDOWN_MS) {
                lastSwingTime = currentTime
                val force = magnitude * 3.5f
                onSwingDetected(force)
            }
        }
    }

    private fun handleGyroscope(event: SensorEvent) {
        if (timestamp != 0L) {
            val dt = (event.timestamp - timestamp) * 1.0f / 1000000000.0f
            // El eje Z del giroscopio mide la rotación en el plano del celular
            val angularVelocityZ = event.values[2]

            // Actualizamos el ángulo basado en la velocidad angular (integración)
            currentAngleRad -= angularVelocityZ * dt
            onAimChanged(currentAngleRad)
        }
        timestamp = event.timestamp
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
