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
import android.view.View
import com.example.kotlincustomviewsample.R

/**
 * Created by vishnu on 3/9/18.
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
  private var faceColor = Color.YELLOW
  private var eyesColor = Color.BLACK
  private var mouthColor = Color.BLACK
  private var borderColor = Color.BLACK

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
          borderWidth =
              getDimension(R.styleable.EmotionalFaceView_borderWidth, DEFAULT_BORDER_WIDTH)
        }
        .recycle()
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

    // face
    paint.apply {
      color = faceColor
      style = Paint.Style.FILL
      canvas.drawCircle(cx, cy, radius, this)
    }

    // border
    paint.apply {
      color = borderColor
      style = Paint.Style.STROKE
      strokeWidth = borderWidth
      canvas.drawCircle(cx, cy, radius - borderWidth, this)
    }

    // eyes
    paint.apply {
      color = eyesColor
      style = Paint.Style.FILL

      RectF(size * 0.32f, size * 0.33f, size * 0.43f, size * 0.50f).let { lRect ->
        canvas.drawOval(lRect, this)
      }

      RectF(size * 0.57f, size * 0.33f, size * 0.68f, size * 0.50f).let { rRect ->
        canvas.drawOval(rRect, this)
      }
    }

    // mouth
    paint.apply {
      color = mouthColor
      style = Paint.Style.FILL

      mouthPath.apply {
        reset()

        if (mood == HAPPY) {
          moveTo(size * 0.22f, size * 0.6f)
          quadTo(size * 0.50f, size * 0.80f, size * 0.78f, size * 0.60f)
          quadTo(size * 0.50f, size * 0.90f, size * 0.22f, size * 0.60f)
        } else {
          moveTo(size * 0.22f, size * 0.7f)
          quadTo(size * 0.50f, size * 0.50f, size * 0.78f, size * 0.70f)
          quadTo(size * 0.50f, size * 0.60f, size * 0.22f, size * 0.7f)
        }
      }

      canvas.drawPath(mouthPath, this)
    }
  }
}