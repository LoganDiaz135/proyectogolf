# Plan de Implementación: Mini Golf con Sensores

Este proyecto implementa un juego de Mini Golf para Android utilizando Kotlin y Jetpack Compose. La mecánica principal es el uso de sensores de movimiento (acelerómetro y giroscopio) para simular un golpe de golf (swing).

## User Review Required

> [!IMPORTANT]
> **Calibración de Sensores**: La sensibilidad del swing dependerá de la calidad del sensor del dispositivo. Se definirá un umbral base, pero podría requerir ajustes manuales según el dispositivo de prueba.

> [!NOTE]
> **Física Simplificada**: Según los requerimientos, no habrá animación de la pelota rodando. La pelota "saltará" a su posición final tras el golpe.

## Proposed Changes

### 1. Modelo y Lógica de Juego (Dominio)

#### [NEW] [GolfEngine.kt](file:///C:/Users/logan/AndroidStudioProjects/borradordegolf/app/src/main/java/com/example/borradordegolf/logic/GolfEngine.kt)
Encapsulará la lógica matemática:
- Cálculo de la posición final: `posFinal = posActual + (direccion * fuerza)`.
- Verificación de si la pelota entró al hoyo (basado en una distancia umbral).
- Manejo del contador de golpes y reinicio.

### 2. Integración de Sensores

#### [NEW] [SwingDetector.kt](file:///C:/Users/logan/AndroidStudioProjects/borradordegolf/app/src/main/java/com/example/borradordegolf/sensors/SwingDetector.kt)
Manejador de `SensorEventListener`:
- Escuchar `TYPE_LINEAR_ACCELERATION` para detectar la fuerza del golpe.
- Detectar el "pico" de movimiento para registrar un golpe válido.
- Emitir eventos de "Golpe Detectado" con magnitud y dirección.

### 3. Arquitectura de UI (ViewModel)

#### [NEW] [GolfViewModel.kt](file:///C:/Users/logan/AndroidStudioProjects/borradordegolf/app/src/main/java/com/example/borradordegolf/ui/GolfViewModel.kt)
- Mantener el estado de la aplicación (`GameState`).
- Procesar los datos crudos del `SwingDetector` y llamar al `GolfEngine`.
- Gestionar los estados de la UI: Instrucciones, Juego en curso, Hoyo completado.

### 4. Interfaz Gráfica (Jetpack Compose)

#### [MODIFY] [MainActivity.kt](file:///C:/Users/logan/AndroidStudioProjects/borradordegolf/app/src/main/java/com/example/borradordegolf/MainActivity.kt)
- Configurar el punto de entrada y permisos (si fueran necesarios, aunque los sensores básicos no suelen requerirlos).

#### [NEW] [GolfScreen.kt](file:///C:/Users/logan/AndroidStudioProjects/borradordegolf/app/src/main/java/com/example/borradordegolf/ui/GolfScreen.kt)
Componentes visuales:
- **Campo de Golf**: Un `Box` o `Canvas` que dibuje la pelota y el hoyo.
- **HUD**: Contador de golpes, número de hoyo y par.
- **Botón de Reinicio**: Acción para resetear el estado.
- **Diálogo de Instrucciones**: Explicación de cómo hacer el swing.

### 5. Pruebas Unitarias

#### [NEW] [GolfEngineTest.kt](file:///C:/Users/logan/AndroidStudioProjects/borradordegolf/app/src/test/java/com/example/borradordegolf/GolfEngineTest.kt)
- Verificar que la pelota se mueve correctamente según la fuerza.
- Validar que el hoyo se detecta cuando la distancia es mínima.
- Comprobar que el contador de golpes incrementa correctamente.

## Verification Plan

### Automated Tests
- Ejecutar `./gradlew test` para asegurar que la lógica matemática es correcta.

### Manual Verification
1. **Detección de Swing**: Mover el dispositivo bruscamente y verificar que el contador de golpes aumenta.
2. **Lógica de Juego**: Intentar "embocar" la pelota y verificar que aparezca el mensaje de éxito.
3. **Reinicio**: Presionar el botón de reinicio y confirmar que la pelota vuelve al inicio y el contador a cero.

## AI Usage Documentation (Extra)
Se redactará una carta en inglés (`AI_Usage_Letter.md`) detallando que se utilizó asistencia de IA para:
- Estructuración de la arquitectura MVVM.
- Implementación de la lógica de sensores.
- Generación de componentes de UI en Compose.
- Redacción de pruebas unitarias.
