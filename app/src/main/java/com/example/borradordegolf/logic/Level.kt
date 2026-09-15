package com.example.borradordegolf.logic

data class Level(
    val number: Int,
    val start: Point,
    val hole: Point,
    val holeRadius: Float,
    val par: Int
)

val LEVELS = listOf(
    Level(1, Point(50f, 90f), Point(50f, 10f), 5f, 2),
    Level(2, Point(20f, 90f), Point(80f, 15f), 4f, 3),
    Level(3, Point(10f, 50f), Point(90f, 50f), 3f, 4)
)