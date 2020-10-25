package com.app.mafia.helpers

import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View

class ExtendedDrawable(var view: View, var x: Float, var y: Float, var r: Float) : Drawable() {

    override fun draw(canvas: Canvas) {
        var path = Path()
        path.fillType = Path.FillType.EVEN_ODD;
        
        // This will create a full screen rectangle.
        path.addRect(RectF(0F, 0F, Global.screenX.toFloat(), Global.screenY.toFloat()), Path.Direction.CW);
        val position = IntArray(2)
        view.getLocationOnScreen(position)
        // This will punch a hole in it
        path.addCircle(position[0].toFloat(), position[1].toFloat(), r, Path.Direction.CCW);

        // Now draw everything on the Canvas
        var paint = Paint();
        paint.color = Color.parseColor("#80000000")
        canvas.drawPath(path, paint);
    }

    override fun setAlpha(p0: Int) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    override fun setColorFilter(p0: ColorFilter?) {
    }

}