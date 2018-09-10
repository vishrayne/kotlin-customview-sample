package com.example.kotlincustomviewsample.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.kotlincustomviewsample.R

/**
 * Custom EmoView class
 */
class EmoView(
  context: Context,
  attrs: AttributeSet
) : View(context, attrs) {
  companion object {
    const val DEFAULT_FACE_COLOR = Color.YELLOW
    const val DEFAULT_EYES_COLOR = Color.BLACK
    const val DEFAULT_MOUTH_COLOR = Color.BLACK
    const val DEFAULT_BORDER_COLOR = Color.BLACK
    const val DEFAULT_BORDER_WIDTH = 4.0f
    const val HAPPY = 0L
    const val SAD = 1L
  }

  private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private var faceToggleColor = Color.YELLOW
  private var mouthToggleColor = Color.YELLOW
  private var eyesToggleColor = Color.YELLOW
  private var borderToggleColor = Color.YELLOW

  private var faceColor = Color.YELLOW
  private var eyesColor = Color.BLACK
  private var mouthColor = Color.BLACK
  private var borderColor = Color.BLACK

  private var facePaintColor = DEFAULT_FACE_COLOR
  private var mouthPaintColor = DEFAULT_MOUTH_COLOR
  private var borderPaintColor = DEFAULT_BORDER_COLOR
  private var eyesPaintColor = DEFAULT_EYES_COLOR

  private var isToggled = true

  private var borderWidth = 4.0f
  private var size = 320

  private val mouthPath: Path = Path()

  var mood = HAPPY
    set(state) {
      field = state
      invalidate()
    }

  init {
    context.theme.obtainStyledAttributes(attrs, R.styleable.EmotionalFaceView, 0, 0)
        .apply {
          mood = getInt(R.styleable.EmotionalFaceView_mood, HAPPY.toInt())
              .toLong()
          faceColor = getColor(R.styleable.EmotionalFaceView_faceColor, DEFAULT_FACE_COLOR)
          eyesColor = getColor(R.styleable.EmotionalFaceView_eyesColor, DEFAULT_EYES_COLOR)
          mouthColor = getColor(R.styleable.EmotionalFaceView_mouthColor, DEFAULT_MOUTH_COLOR)
          borderColor = getColor(R.styleable.EmotionalFaceView_borderColor, DEFAULT_BORDER_COLOR)

          mouthToggleColor = getColor(R.styleable.EmotionalFaceView_mouthToggleColor, mouthColor)
          eyesToggleColor = getColor(R.styleable.EmotionalFaceView_eyesToggleColor, eyesColor)
          borderToggleColor = getColor(R.styleable.EmotionalFaceView_borderToggleColor, borderColor)
          faceToggleColor = getColor(R.styleable.EmotionalFaceView_faceToggleColor, faceColor)

          borderWidth =
              getDimension(R.styleable.EmotionalFaceView_borderWidth, DEFAULT_BORDER_WIDTH)
          assignViewPaintColors()
        }
        .recycle()
  }

  private fun assignViewPaintColors() {
    facePaintColor = faceColor
    mouthPaintColor = mouthColor
    borderPaintColor = borderColor
    eyesPaintColor = eyesColor
  }

  override fun dispatchTouchEvent(event: MotionEvent): Boolean {
    when (event.action) {
      MotionEvent.ACTION_UP -> {
        Log.d("touch", "$event")
        toggleAllColors()
      }
    }
    return super.dispatchTouchEvent(event)
  }

  private fun toggleAllColors() {
    facePaintColor = if (isToggled) faceToggleColor else faceColor
    borderPaintColor = if (isToggled) borderToggleColor else borderColor
    mouthPaintColor = if (isToggled) mouthToggleColor else mouthColor
    eyesPaintColor = if (isToggled) eyesToggleColor else eyesColor
    isToggled = !isToggled
    invalidate()
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    Log.d("EmotionalFV", "OnDraw()")
    drawSmiley(canvas)
  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int
  ) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    size = Math.min(measuredWidth, measuredHeight)
    Log.d("EmotionalFV", "Measured min width|height [square]-> $size")
    setMeasuredDimension(size, size)
  }

  override fun onSaveInstanceState(): Parcelable {
    return Bundle().apply {
      putLong("mood", mood)
      putParcelable("super", super.onSaveInstanceState())
    }
  }

  override fun onRestoreInstanceState(state: Parcelable) {
    (state as Bundle).apply {
      mood = getLong("mood")
      super.onRestoreInstanceState(getParcelable("super"))
    }
  }

  private fun drawSmiley(canvas: Canvas) {
    val (cx, cy, radius) = Triple(size / 2f, size / 2f, size / 2f)
    drawFace(canvas, cx, cy, radius, facePaintColor)
    drawBorder(canvas, cx, cy, radius, borderPaintColor)
    drawEyes(canvas, eyesPaintColor)
    drawMouth(canvas, mouthPaintColor)
  }

  private fun drawFace(
    canvas: Canvas,
    cx: Float,
    cy: Float,
    radius: Float,
    itemColor: Int
  ) {
    paint.apply {
      color = itemColor
      style = Paint.Style.FILL
      canvas.drawCircle(cx, cy, radius, this)
    }
  }

  private fun drawBorder(
    canvas: Canvas,
    cx: Float,
    cy: Float,
    radius: Float,
    itemColor: Int
  ) {
    paint.apply {
      color = itemColor
      style = Paint.Style.STROKE
      strokeWidth = borderWidth
      canvas.drawCircle(cx, cy, radius - borderWidth, this)
    }
  }

  private fun drawEyes(
    canvas: Canvas,
    itemColor: Int
  ) {
    paint.apply {
      color = itemColor
      style = Paint.Style.FILL

      RectF(size * 0.32f, size * 0.33f, size * 0.43f, size * 0.50f).let { lRect ->
        canvas.drawOval(lRect, this)
      }

      RectF(size * 0.57f, size * 0.33f, size * 0.68f, size * 0.50f).let { rRect ->
        canvas.drawOval(rRect, this)
      }
    }
  }

  private fun drawMouth(
    canvas: Canvas,
    itemColor: Int
  ) {
    paint.apply {
      color = itemColor
      style = Paint.Style.FILL
      mouthPath.apply {
        reset()
        when (mood) {
          HAPPY -> drawHappyPath()
          else -> drawSadPath()
        }
      }
      canvas.drawPath(mouthPath, this)
    }
  }

  private fun Path.drawSadPath() {
    moveTo(size * 0.22f, size * 0.7f)
    quadTo(size * 0.50f, size * 0.50f, size * 0.78f, size * 0.70f)
    quadTo(size * 0.50f, size * 0.60f, size * 0.22f, size * 0.7f)
  }

  private fun Path.drawHappyPath() {
    moveTo(size * 0.22f, size * 0.6f)
    quadTo(size * 0.50f, size * 0.80f, size * 0.78f, size * 0.60f)
    quadTo(size * 0.50f, size * 0.90f, size * 0.22f, size * 0.60f)
  }
}