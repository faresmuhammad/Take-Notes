package com.fares.training.takenotes.ui.custom_views

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import com.fares.folderview.ImageItem
import com.fares.folderview.Shape
import timber.log.Timber

class MultiImagesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val radius = 64f


    private val topLeftItem: ImageItem by lazy {
        createImageView().apply {
            shape = Shape.RoundRectangle(radius, 0f, 0f, 0f)
        }
    }
    private val topRightItem: ImageItem by lazy {
        createImageView().apply {
            shape = Shape.RoundRectangle(0f, radius, 0f, 0f)
        }
    }
    private val bottomLeftItem: ImageItem by lazy {
        createImageView().apply {
            shape = Shape.RoundRectangle(0f, 0f, 0f, radius)
        }
    }
    private val bottomRightItem: ImageItem by lazy {
        createImageView().apply {
            shape = Shape.RoundRectangle(0f, 0f, radius, 0f)
        }
    }

    private val imagesList = mutableListOf<Uri>()
    private var spaceBetween = 5
    var desiredSize = 400
    private var childWidth: Int
    private var childHeight: Int


    init {


        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            foregroundGravity = Gravity.CENTER

        }
//        childWidth = (desiredSize - 2 * (510f / 8467) * desiredSize - spaceBetween).toInt() / 2
        childWidth = (desiredSize - spaceBetween) / 2
        childHeight = (desiredSize - spaceBetween) / 2
//            (desiredSize - (1614f / 8467) * desiredSize - (430f / 8467) * desiredSize - spaceBetween).toInt() / 2
        addView(topLeftItem)
        addView(topRightItem)
        addView(bottomLeftItem)
        addView(bottomRightItem)


    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = resolveSize(desiredSize, widthMeasureSpec)
        val height = resolveSize(desiredSize, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {


        Timber.d("Children count: $childCount")
        Timber.d("left: $left")
        Timber.d("top: $top")
        Timber.d("right: $right")
        Timber.d("bottom: $bottom")
        Timber.d("child width: $childWidth")
        Timber.d("child height: $childHeight")


        val parentTop = paddingTop
        val parentLeft = paddingStart
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val width = childWidth
            val height = childHeight
            var childLeft: Int
            var childTop: Int
            Timber.d("child dim $i: ($width, $height)")
            when (i) {
                0 -> {
                    childLeft = parentLeft
                    childTop = parentTop
                    child.layout(
                        childLeft,
                        childTop,
                        childLeft + width,
                        childTop + height
                    )
                }
                1 -> {
                    childLeft = parentLeft + width + spaceBetween
                    childTop = parentTop
                    child.layout(
                        childLeft,
                        childTop,
                        childLeft + width,
                        childTop + height
                    )
                }
                2 -> {
                    childLeft = parentLeft
                    childTop = parentTop + height + spaceBetween
                    child.layout(
                        childLeft,
                        childTop,
                        childLeft + width,
                        childTop + height
                    )
                }
                3 -> {
                    childLeft = parentLeft + width + spaceBetween
                    childTop = parentTop + height + spaceBetween
                    child.layout(
                        childLeft,
                        childTop,
                        childLeft + width,
                        childTop + height
                    )
                }
            }
        }


    }

    private fun createImageView(): ImageItem = ImageItem(context).apply {
        setDesiredDimensions(childWidth, childHeight)
        placeHolderFillColor = Color.TRANSPARENT
        scaleType = ImageView.ScaleType.CENTER_CROP
        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

    }


    fun addImage(imageUri: Uri) {
        imagesList.add(imageUri)
        setImages()
    }

    private fun setImages() = when (imagesList.size) {
        1 -> topLeftItem.setImageURI(imagesList[0])
        2 -> {
            topLeftItem.setImageURI(imagesList[0])
            topRightItem.setImageURI(imagesList[1])
        }
        3 -> {
            topLeftItem.setImageURI(imagesList[0])
            topRightItem.setImageURI(imagesList[1])
            bottomLeftItem.setImageURI(imagesList[2])
        }
        else -> {
            topLeftItem.setImageURI(imagesList[0])
            topRightItem.setImageURI(imagesList[1])
            bottomLeftItem.setImageURI(imagesList[2])
            bottomRightItem.setImageURI(imagesList[3])
        }
    }
}