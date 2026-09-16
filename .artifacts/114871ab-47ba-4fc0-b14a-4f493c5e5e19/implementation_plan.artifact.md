# Plan de Mejora: Mini Golf Pro

Este plan detalla las mejoras para convertir el "Borrador de Golf" en un juego más completo, añadiendo obstáculos, progresión de niveles y una física más refinada.

## User Review Required

> [!IMPORTANT]
> La implementación de obstáculos requerirá actualizar la lógica de rebotes en el `GolfEngine`. Esto podría aumentar la complejidad del cálculo de colisiones, pero hará que los niveles sean mucho más interesantes.

## Proposed Changes

### 1. Lógica y Motor (`logic`)

#### [MODIFY] [Level.kt](file:///C:/Users/logan/AndroidStudioProjects/borradordegolf/app/src/main/java/com/example/borradordegolf/logic/Level.kt)
- Añadir una lista de obstáculos (`List<Rect>`) a la clase `Level`.
- Definir obstáculos para los niveles 2 y 3.

#### [MODIFY] [GolfEngine.kt](file:///C:/Users/logan/AndroidStudioProjects/borradordegolf/app/src/main/java/com/example/borradordegolf/logic/GolfEngine.kt)
- Actualizar el constructor para aceptar una lista de obstáculos.
- Modificar `hitBall` para detectar colisiones con los nuevos obstáculos, no solo con las paredes exteriores.
- Implementar una reducción de fuerza en cada rebote para simular pérdida de energía.

### 2. Estado y Navegación (`ui`)

#### [MODIFY] [GolfViewModel.kt](file:///C:/Users/logan/AndroidStudioProjects/borradordegolf/app/src/main/java/com/example/borradordegolf/ui/GolfViewModel.kt)
- Añadir el nivel actual al `GolfState`.
- Implementar la función `loadLevel(levelIndex: Int)` para resetear el motor con los nuevos parámetros del nivel.
- Lógica para avanzar al siguiente nivel cuando `isHoleCompleted` sea true.

#### [MODIFY] [GolfScreen.kt](file:///C:/Users/logan/AndroidStudioProjects/borradordegolf/app/src/main/java/com/example/borradordegolf/ui/GolfScreen.kt)
- Dibujar los obstáculos en el `Canvas`.
- Añadir un diálogo o overlay de "Nivel Completado" con un botón para pasar al siguiente.
- Mostrar el número de nivel actual en el encabezado.

## Verification Plan

### Manual Verification
- **Prueba de Colisiones:** Verificar que la pelota rebota correctamente en los nuevos obstáculos internos.
- **Flujo de Niveles:** Completar el nivel 1 y verificar que se carga el nivel 2 con sus obstáculos correspondientes.
- **Física:** Observar que la pelota no rebota infinitamente y se siente "pesada".
