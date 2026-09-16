package com.example.borradordegolf.logic

data class Obstacle(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

data class Level(
    val number: Int,
    val start: Point,
    val hole: Point,
    val holeRadius: Float,
    val par: Int,
    val obstacles: List<Obstacle> = emptyList()
)

val LEVELS = listOf(
    // Nivel 1: pelota y hoyo prácticamente alineados, sin obstáculos.
    Level(
        number = 1,
        start = Point(50f, 90f),
        hole = Point(50f, 15f),
        holeRadius = 5f,
        par = 2
    ),
    // Nivel 2: pelota y hoyo desalineados, con una pared que obliga a rodear.
    Level(
        number = 2,
        start = Point(20f, 90f),
        hole = Point(80f, 15f),
        holeRadius = 4f,
        par = 3,
        obstacles = listOf(
            Obstacle(left = 40f, top = 35f, right = 60f, bottom = 100f)
        )
    ),
    // Nivel 3: provisional, estructura lista para diseñar después.
    Level(
        number = 3,
        start = Point(15f, 85f),
        hole = Point(85f, 15f),
        holeRadius = 4f,
        par = 4,
        obstacles = listOf(
            Obstacle(left = 35f, top = 45f, right = 65f, bottom = 60f)
        )
    )
)