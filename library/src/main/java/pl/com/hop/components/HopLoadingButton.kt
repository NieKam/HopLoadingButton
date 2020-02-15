package pl.com.hop.components

import android.animation.*
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import com.google.android.material.button.MaterialButton

/**
 * Copyright by Kamil Niezrecki
 */

private const val PATH_DASH_SEGMENTS = 2
private const val MOVE_ANIMATION_DURATION_MS = 2400
private const val PADDING = 40
const val END_GRADIENT = 200F
const val TINT_ALPHA = 112
const val VISIBILITY_ANIM_DURATION = 380L


class HopLoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = androidx.appcompat.R.attr.buttonStyle
) : MaterialButton(context, attrs, defStyleAttr) {

    var loading = false
        set(value) {
            field = value
            updateAnimationState(value)
            updateButtonState(value)
        }

    var animationInterpolator: TimeInterpolator = LinearInterpolator()
        set(value) {
            require(!loading) { "Don't change interpolator when animation is running" }
            field = value
        }

    private var lineWidth: Float = 0F
    private val pathMeasure = PathMeasure()
    private val rect = RectF()
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPath = Path()
    private val tintPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var totalLength: Float = 0F
    private var primaryColor: Int = 0
    private var secondaryColor: Int = 0
    private var cachedLineWidth: Float = 0F
    private var lineSegmentSize: Float = 0F
    private var disableWhenLoading = false
    private var lineMoveAnimation: ValueAnimator? = null
    private var loadingText: String? = null
    private var buttonText: String? = null
    private var progressDuration: Long = 0

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        val a = context.obtainStyledAttributes(attrs, R.styleable.HopLoadingButton, defStyleAttr, 0)

        try {
            disableWhenLoading = a.getBoolean(R.styleable.HopLoadingButton_disable_on_loading, false)
            loadingText = a.getString(R.styleable.HopLoadingButton_loading_text)
            buttonText = a.getString(R.styleable.HopLoadingButton_android_text)
            primaryColor = a.getColor(
                R.styleable.HopLoadingButton_primary_color,
                context.resources.getDefaultColor()
            )
            secondaryColor = a.getColor(
                R.styleable.HopLoadingButton_secondary_color,
                context.resources.getDefaultSecondaryColor()
            )
            lineWidth = a.getDimension(
                R.styleable.HopLoadingButton_line_width,
                context.resources.getDefaultStrokeWidth()
            )
            progressDuration =
                a.getInteger(R.styleable.HopLoadingButton_progress_duration, MOVE_ANIMATION_DURATION_MS).toLong()
            cachedLineWidth = lineWidth
        } finally {
            a.recycle()
        }

        progressPaint.initLinePaint(lineWidth, primaryColor)
        tintPaint.apply {
            initLinePaint(lineWidth, secondaryColor)
            alpha = TINT_ALPHA
        }
        paint.apply {
            initLinePaint(lineWidth, primaryColor)
            shader = getShader(primaryColor, secondaryColor)
        }
        setPadding(PADDING, PADDING, PADDING, PADDING)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        loading = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (measuredWidth > 0 && measuredHeight > 0) {
            totalLength = transformPath()
            lineSegmentSize = totalLength / PATH_DASH_SEGMENTS
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (loading) {
            canvas.drawPath(progressPath, paint)
        }
    }

    fun setFirstColor(color: Int) {
        primaryColor = color
        progressPaint.color = primaryColor
        onPrimaryColorChange(primaryColor)
        invalidate()
    }

    fun setSecondColor(color: Int) {
        secondaryColor = color
        tintPaint.let {
            it.color = secondaryColor
            it.alpha = TINT_ALPHA
        }
        onSecondaryColorChange(secondaryColor)
        invalidate()
    }

    fun getLoadingineWidth(): Int {
        return lineWidth.pxToDp
    }

    private fun setLineWidth(width: Float) {
        lineWidth = width

        progressPaint.strokeWidth = lineWidth
        tintPaint.strokeWidth = lineWidth
        paint.strokeWidth = lineWidth
        transformPath()
        invalidate()
    }

    private fun updateAnimationState(isLoading: Boolean) {
        if (isLoading) {
            show()
        } else {
            hide()
        }
    }

    private fun updateButtonState(isLoading: Boolean) {
        if (disableWhenLoading) {
            this.isEnabled = !isLoading
        }

        loadingText?.let {
            if (isLoading) {
                this.text = it
            } else {
                this.text = buttonText
            }
        }
    }

    private fun startLoadingAnimation() {
        require(loading)

        lineSegmentSize = totalLength / PATH_DASH_SEGMENTS
        lineMoveAnimation = ObjectAnimator.ofFloat((totalLength * PATH_DASH_SEGMENTS), 0F).apply {
            repeatCount = ValueAnimator.INFINITE
            duration = progressDuration
            interpolator = animationInterpolator
            addUpdateListener {
                paint.pathEffect = DashPathEffect(
                    floatArrayOf(lineSegmentSize, lineSegmentSize),
                    it.animatedValue as Float
                )
                invalidate()
            }
        }.also {
            it.start()

        }
        paint.color = primaryColor
    }

    private fun stopLoadingAnimation() {
        lineMoveAnimation?.let {
            it.cancel()
            lineMoveAnimation = null
        }
    }

    private fun onPrimaryColorChange(color: Int) {
        paint.shader = getShader(color, secondaryColor)
        reset()
    }

    private fun onSecondaryColorChange(color: Int) {
        paint.shader = getShader(primaryColor, color)
        reset()
    }

    private fun getShader(color1: Int, color2: Int): LinearGradient {
        return LinearGradient(
            0F,
            0F,
            END_GRADIENT,
            END_GRADIENT,
            color1,
            color2,
            Shader.TileMode.MIRROR
        )
    }

    private fun reset() {
        if (loading) {
            stopLoadingAnimation()
            startLoadingAnimation()
        }
    }

    private fun hide() {
        // Save original value
        cachedLineWidth = lineWidth

        ObjectAnimator.ofFloat(lineWidth, 0F).apply {
            duration = VISIBILITY_ANIM_DURATION
            interpolator = AccelerateInterpolator()
            addUpdateListener { l ->
                setLineWidth(l.animatedValue as Float)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    stopLoadingAnimation()
                }
            })
        }.start()
    }

    private fun show() {

        ObjectAnimator.ofFloat(0F, cachedLineWidth).apply {
            duration = VISIBILITY_ANIM_DURATION
            interpolator = AccelerateInterpolator()
            addUpdateListener { l ->
                setLineWidth(l.animatedValue as Float)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    startLoadingAnimation()
                }
            })
        }.start()
    }

    private fun transformPath(): Float {
        progressPath.reset()
        rect.set(0F, 0F, measuredWidth.toFloat(), measuredHeight.toFloat())
        val inset = adjustInset()
        rect.inset(inset, inset)
        progressPath.addRoundRect(rect, cornerRadius.toFloat(), cornerRadius.toFloat(), Path.Direction.CW)
        pathMeasure.setPath(progressPath, false)
        return pathMeasure.length
    }

    private fun adjustInset(): Float {
        val maxWidth = 4.dpToPx
        return if (lineWidth >= maxWidth) {
            lineWidth / 3
        } else {
            0F
        }
    }
}
