package com.example.stackexchange.detectframefaces.utils
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.microsoft.projectoxford.face.contract.Face


class AppUtils {
    companion object{
        fun drawFaceRectanglesOnBitmap(
            originalBitmap: Bitmap, faces: Array<Face>?
        ): Bitmap {
            val bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            paint.setAntiAlias(true)
            paint.setStyle(Paint.Style.STROKE)
            paint.setColor(Color.RED)
            paint.setStrokeWidth(10f)
            if (faces != null) {
                for (face in faces) {
                    val faceRectangle = face.faceRectangle
                    canvas.drawRect(
                        faceRectangle.left.toFloat(),
                        faceRectangle.top.toFloat(),
                        (faceRectangle.left + faceRectangle.width).toFloat(),
                        (faceRectangle.top + faceRectangle.height).toFloat(),
                        paint
                    )
                }
            }
            return bitmap
        }
    }
}