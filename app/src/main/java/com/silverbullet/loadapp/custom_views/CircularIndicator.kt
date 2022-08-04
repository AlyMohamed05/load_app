package com.silverbullet.loadapp.custom_views

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.silverbullet.loadapp.R

private const val DEFAULT_STROKE_WIDTH = 40f
private const val DEFAULT_INDICATOR_SIZE = 200f
private const val DEFAULT_DURATION = 1000
private const val DEFAULT_INDICATOR_COLOR = Color.GRAY
private const val DEFAULT_LOADING_COLOR = Color.BLACK
private const val FULL_LOAD_VALUE = 360f

class CircularIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    // Indicator dimensions and attributes
    private var strokeWidth = Float.MIN_VALUE
    private var duration = Long.MIN_VALUE
    private var indicatorColor = Int.MIN_VALUE
    private var loadingColor = Int.MIN_VALUE

    // Center of the canvas
    private var centerX = 0f
    private var centerY = 0f

    private val oval = RectF()
    private lateinit var paint: Paint

    private var currentProgress = 0f
    private lateinit var animator: Animator

    init {
        setAttributes(attrs)
        initAnimator()
        initializePaint()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w.toFloat() / 2f
        centerY = h.toFloat() / 2f
        setOvalCoordinates()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }
        drawIndicatorBackground(canvas)
        drawIndicatorProgress(canvas)
    }

    fun playAnimation() {
        animator.start()
    }

    private fun drawIndicatorBackground(canvas: Canvas) {
        paint.apply {
            color = indicatorColor
            strokeCap = Paint.Cap.SQUARE
        }
        canvas.drawArc(oval, 0f, 360f, false, paint)
    }

    private fun drawIndicatorProgress(canvas: Canvas) {
        paint.apply {
            color = loadingColor
            strokeCap = Paint.Cap.ROUND
        }
        canvas.drawArc(
            oval,
            270f,
            FULL_LOAD_VALUE * currentProgress,
            false, paint
        )
    }

    private fun setOvalCoordinates() {
        val radius = DEFAULT_INDICATOR_SIZE / 2
        oval.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
    }

    private fun initializePaint() {
        paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = this@CircularIndicator.strokeWidth
        }
    }

    private fun initAnimator() {
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = this@CircularIndicator.duration
            addUpdateListener {
                currentProgress = it.animatedValue as Float
                invalidate()
            }
        }
    }

    private fun setAttributes(attrs: AttributeSet?) {
        context.withStyledAttributes(attrs, R.styleable.CircularIndicator) {
            duration = getInt(R.styleable.CircularIndicator_duration, DEFAULT_DURATION).toLong()
            strokeWidth = getFloat(
                R.styleable.CircularIndicator_indicatorWidth,
                DEFAULT_STROKE_WIDTH
            )
            indicatorColor = getColor(
                R.styleable.CircularIndicator_indicatorColor,
                DEFAULT_INDICATOR_COLOR
            )
            loadingColor = getColor(
                R.styleable.CircularIndicator_loadingColor,
                DEFAULT_LOADING_COLOR
            )
            visibility = getInt(R.styleable.CircularIndicator_visibility, VISIBLE)
        }
    }
}
