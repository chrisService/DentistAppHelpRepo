package com.dentify.dentify.util

import android.graphics.*
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget


fun <T> ImageView.loadCircularImage(
    model: T,
    borderSize: Float = 0F,
    borderColor: Int = Color.WHITE
) {
    Glide.with(context)
        .asBitmap()
        .load(model)
        .apply(RequestOptions.circleCropTransform())
        .into(object : BitmapImageViewTarget(this) {
            override fun setResource(resource: Bitmap?) {
                setImageDrawable(
                    resource?.run {
                        RoundedBitmapDrawableFactory.create(
                            resources,
                            if (borderSize > 0) {
                                createBitmapWithBorder(borderSize, borderColor)
                            } else {
                                this
                            }
                        ).apply {
                            isCircular = true
                        }
                    }
                )
            }
        })
}

private fun Bitmap.createBitmapWithBorder(borderSize: Float, borderColor: Int): Bitmap {
    val borderOffset = (borderSize * 2).toInt()
    val halfWidth = width / 2
    val halfHeight = height / 2
    val circleRadius = Math.min(halfWidth, halfHeight).toFloat()
    val newBitmap = Bitmap.createBitmap(
        width + borderOffset,
        height + borderOffset,
        Bitmap.Config.ARGB_8888
    )

    val centerX = halfWidth + borderSize
    val centerY = halfHeight + borderSize

    val paint = Paint()
    val canvas = Canvas(newBitmap).apply {
        drawARGB(0, 0, 0, 0)
    }

    paint.isAntiAlias = true
    paint.style = Paint.Style.FILL
    canvas.drawCircle(centerX, centerY, circleRadius, paint)

    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, borderSize, borderSize, paint)

    paint.xfermode = null
    paint.style = Paint.Style.STROKE
    paint.color = borderColor
    paint.strokeWidth = borderSize
    canvas.drawCircle(centerX, centerY, circleRadius, paint)
    return newBitmap
}