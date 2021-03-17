package com.jepster.fancontroller

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

import androidx.annotation.ColorInt
import androidx.core.content.withStyledAttributes

import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.PI

private const val RADIUS_OFFSET_LABEL = 30
private const val RADIUS_OFFSET_INDICATOR = -35

class DialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var radius: Float = 0.0f
    private var fanSpeed: FanSpeed = FanSpeed.OFF
    private val pointPosition: PointF = PointF(0.0f, 0.0f)
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }
    @ColorInt
    private var fanSpeedLowColor: Int = 0
    @ColorInt
    private var fanSpeedMidColor: Int = 0
    @ColorInt
    private var fanSpeedHiColor: Int = 0

    init {
        isClickable = true
        contentDescription = resources.getText(fanSpeed.label)
        context.withStyledAttributes(attrs, R.styleable.DialView) {
            fanSpeedLowColor = getColor(R.styleable.DialView_fanColor1, 0)
            fanSpeedMidColor = getColor(R.styleable.DialView_fanColor2, 0)
            fanSpeedHiColor = getColor(R.styleable.DialView_fanColor3, 0)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = (min(w, h) / 2.0f * 0.8f)
    }

    private fun PointF.computeXYForSpeed(pos: FanSpeed, radius: Float) {
        val startAngle = PI * (9/8.0f)
        val angle = startAngle + pos.ordinal * (PI / 4)
        x = (radius * cos(angle)).toFloat() + width / 2
        y = (radius * sin(angle)).toFloat() + height / 2
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = when (fanSpeed) {
            FanSpeed.OFF -> Color.GRAY
            FanSpeed.LOW -> fanSpeedLowColor
            FanSpeed.MEDIUM -> fanSpeedMidColor
            FanSpeed.HIGH -> fanSpeedHiColor
        }
        canvas?.drawCircle((width / 2f), (height / 2f), radius, paint)
        
        val markerRadius = radius + RADIUS_OFFSET_INDICATOR
        pointPosition.computeXYForSpeed(fanSpeed, markerRadius)
        paint.color = Color.BLACK
        canvas?.drawCircle(pointPosition.x, pointPosition.y, radius/12f, paint)

        val labelRadius = radius + RADIUS_OFFSET_LABEL
        for (i in FanSpeed.values()) {
            pointPosition.computeXYForSpeed(i, labelRadius)
            val label = resources.getString(i.label)
            canvas?.drawText(label, pointPosition.x, pointPosition.y, paint)
        }
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true
        fanSpeed = fanSpeed.next()
        contentDescription = resources.getText(fanSpeed.label)
        invalidate()
        return true
    }
}
