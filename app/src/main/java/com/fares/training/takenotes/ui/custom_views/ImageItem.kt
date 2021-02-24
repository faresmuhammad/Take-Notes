package com.fares.folderview

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import com.fares.folderview.Shape
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

internal class ImageItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttrs) {


    var shape: Shape = Shape.RoundRectangle(64f, 0f, 64f, 0f)
        get() = field
        set(value) {
            field = value
            invalidate()
        }

    var placeHolderFillColor: Int = Color.GRAY
        set(value) {
            placeHolderPaint.color = value
            field = value
        }
    private val path = Path()
    private val placeHolderPaint = Paint().apply {
        isAntiAlias = true
        color = placeHolderFillColor
        style = Paint.Style.FILL
        isDither = true
    }
    private val rect = RectF()

    /**
     * Desired Dimensions
     */
    var scaleFactor: Double = 1.0
    var desiredWidth: Int = 0
        get() = field
        set(value) {
            field = (value * scaleFactor).toInt()
        }
    var desiredHeight: Int = 0
        get() = field
        set(value) {
            field = (value * scaleFactor).toInt()
        }


    fun setDesiredDimensions(width: Int, height: Int) {
        desiredWidth = (width * scaleFactor).toInt()
        desiredHeight = (height * scaleFactor).toInt()
    }




    override fun onDraw(canvas: Canvas?) {
        when (shape) {
            is Shape.Circular -> {
                // TODO: 2/6/2021 set the dimensions to be equal to fit the image to the circle 
                val shp = shape as Shape.Circular
                path.addCircle(width / 2f, height / 2f, shp.radius.toFloat(), Path.Direction.CW)
                canvas?.drawCircle(
                    width / 2f,
                    height / 2f,
                    shp.radius.toFloat(),
                    placeHolderPaint
                )
                canvas?.clipPath(path)
            }
            is Shape.RoundRectangle -> {
                val shp = shape as Shape.RoundRectangle
                canvas?.apply {
                    val path = shp.draw(width.toFloat(), height.toFloat())
                    drawPath(path, placeHolderPaint)
                    clipPath(path)
                }

            }
            is Shape.RoundRectangleRadius -> {
                val shp = shape as Shape.RoundRectangleRadius
                canvas?.apply {

                    path.addRoundRect(
                        rect,
                        shp.radius.toFloat(),
                        shp.radius.toFloat(),
                        Path.Direction.CW
                    )
                    drawRoundRect(
                        rect,
                        shp.radius.toFloat(),
                        shp.radius.toFloat(),
                        placeHolderPaint
                    )
                    clipPath(path)

                }

            }
        }

        super.onDraw(canvas)

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measureDimensions(desiredWidth, widthMeasureSpec)
        val height = measureDimensions(desiredHeight, heightMeasureSpec)
        rect.set(0f, 0f, width.toFloat(), height.toFloat())
        setMeasuredDimension(width, height)
    }


    private fun measureDimensions(size: Int, measureSpec: Int): Int {
        var result = 0
        val specSize = MeasureSpec.getSize(size)
        when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.EXACTLY -> {
                result = specSize
            }
            MeasureSpec.AT_MOST -> {
                result = maxOf(size, specSize)
            }
            MeasureSpec.UNSPECIFIED -> {
                result = size
            }
        }
        return result
    }


}