package pl.com.hop.components

import android.animation.*
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.button.MaterialButton

/**
 * Copyright by Kamil Niezrecki
 */

private const val PATH_DASH_SEGMENTS = 2
private const val MOVE_ANIMATION_DURATION_MS = 2200
private const val PADDING = 40
const val END_GRADIENT = 200F
const val TINT_ALPHA = 112
const val VISIBILITY_ANIM_DURATION = 380L

class HopLoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.buttonStyle
) : MaterialButton(context, attrs, defStyleAttr) {

    var isLoading = false
        set(value) {
            if (value != field) {
                field = value
                updateAnimationState(value)
                updateButtonState(value)
            }
        }

    var animationInterpolator: TimeInterpolator = LinearInterpolator()
        set(value) {
            require(!isLoading) { "Don't change interpolator when animation is running" }
            field = value
        }

    private var lineWidth: Float = 0F
    private val pathMeasure = PathMeasure()
    private val rect = RectF()
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPath = Path()
    private val tintPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val segments = floatArrayOf(0F, 0F)

    private val hideAnimEndListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            stopLoadingAnimation()
        }
    }

    private val showAnimStartListener = object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            startLoadingAnimation()
        }
    }

    private var totalLength: Float = 0F
    private var primaryColor: Int = 0
    private var secondaryColor: Int = 0
    private var cachedLineWidth: Float = 0F
    private var lineSegmentSize: Float = 0F
    private var disableWhenLoading = false
    private var lineMoveAnimation: ValueAnimator? = null
    private var segmentAnimation: ValueAnimator? = null
    private var loadingText: String? = null
    private var buttonText: String? = null
    private var progressDuration: Long = 0
    private var isLoadingBeforeMeasure = false

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        val a = context.obtainStyledAttributes(attrs, R.styleable.HopLoadingButton, defStyleAttr, 0)

        try {
            disableWhenLoading =
                a.getBoolean(R.styleable.HopLoadingButton_disable_on_loading, false)
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
                a.getInteger(
                    R.styleable.HopLoadingButton_progress_duration,
                    MOVE_ANIMATION_DURATION_MS
                ).toLong()
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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isLoadingBeforeMeasure = isLoading && lineSegmentSize == 0F
    }

    override fun onDetachedFromWindow() {
        isLoading = false
        super.onDetachedFromWindow()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (measuredWidth > 0 && measuredHeight > 0) {
            totalLength = transformPath()
            if (isLoadingBeforeMeasure) {
                isLoadingBeforeMeasure = false
                reset()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isLoading) {
            canvas.drawPath(progressPath, paint)
        }
    }

    fun setPrimaryColor(color: Int) {
        primaryColor = color
        progressPaint.color = primaryColor
        onPrimaryColorChange(primaryColor)
        invalidate()
    }

    fun setSecondaryColor(color: Int) {
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
        require(isLoading)
        if (totalLength == 0F) {
            return
        }

        lineSegmentSize = totalLength / PATH_DASH_SEGMENTS
        segments[0] = lineSegmentSize
        segments[1] = lineSegmentSize

        segmentAnimation = ObjectAnimator.ofFloat(lineSegmentSize / 2F, 0F).apply {
            interpolator = FastOutSlowInInterpolator()
            repeatCount = ValueAnimator.INFINITE
            duration = (MOVE_ANIMATION_DURATION_MS / 2).toLong()
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener {
                val animValue = it.animatedValue as Float
                segments[0] = lineSegmentSize + animValue
                segments[1] = lineSegmentSize - animValue
            }
        }.also { animator ->
            animator.start()
        }

        lineMoveAnimation = ObjectAnimator.ofFloat((totalLength * PATH_DASH_SEGMENTS), 0F).apply {
            repeatCount = ValueAnimator.INFINITE
            duration = progressDuration
            interpolator = animationInterpolator
            addUpdateListener {
                paint.pathEffect = DashPathEffect(segments, it.animatedValue as Float)
                invalidate()
            }
        }.also { animator ->
            animator.start()
        }

        paint.color = primaryColor
    }

    private fun stopLoadingAnimation() {
        lineMoveAnimation?.let {
            it.cancel()
            lineMoveAnimation = null
        }

        segmentAnimation?.let {
            it.cancel()
            segmentAnimation = null
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
        if (isLoading) {
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
            addUpdateListener { l -> setLineWidth(l.animatedValue as Float) }
            addListener(hideAnimEndListener)
        }.start()
    }

    private fun show() {
        ObjectAnimator.ofFloat(0F, cachedLineWidth).apply {
            duration = VISIBILITY_ANIM_DURATION
            interpolator = AccelerateInterpolator()
            addUpdateListener { l -> setLineWidth(l.animatedValue as Float) }
            addListener(showAnimStartListener)
        }.start()
    }

    private fun transformPath(): Float {
        progressPath.reset()
        rect.set(0F, 0F, measuredWidth.toFloat(), measuredHeight.toFloat())
        val inset = adjustInset()
        rect.inset(inset, inset)
        progressPath.addRoundRect(
            rect,
            cornerRadius.toFloat(),
            cornerRadius.toFloat(),
            Path.Direction.CW
        )
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
