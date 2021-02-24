package com.fares.folderview

import android.graphics.Path

sealed class Shape {

    data class RoundRectangle(
        val topLeft: Float,
        val topRight: Float = topLeft,
        val bottomRight: Float = topLeft,
        val bottomLeft: Float = topLeft,
    ) : Shape() {
        fun draw(width: Float, height: Float) = Path().apply {
            moveTo(topLeft, 0f)
            lineTo(width - topRight, 0f)
            quadTo(width, 0f, width, topRight)
            lineTo(width, height - bottomRight)
            quadTo(
                width,
                height,
                width - bottomRight,
                height
            )
            lineTo(bottomLeft, height)
            quadTo(
                0f, height,
                0f,
                height - bottomLeft
            )
            lineTo(0f, topLeft)
            quadTo(0f, 0f, topLeft, 0f)
            close()
        }
    }

    data class RoundRectangleRadius(
        val radius: Int
    ) : Shape()


    data class Circular(val radius: Int) : Shape()
}
