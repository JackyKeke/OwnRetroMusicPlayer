package com.jackykeke.ownretromusicplayer.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

/**
 * @author keyuliang on 2023/1/5.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
public class PopupBackground extends Drawable {

    private final int mPaddingEnd;

    private final int mPaddingStart;

    @NonNull
    private final Paint mPaint;

    @NonNull
    private final Path mPath = new Path();

    @NonNull
    private final Matrix mTempMatrix = new Matrix();

    public PopupBackground(@NonNull Context context, int color) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL);
        Resources resources = context.getResources();
        mPaddingStart = resources.getDimensionPixelOffset(me.zhanghai.android.fastscroll.R.dimen.afs_md2_popup_padding_start);
        mPaddingEnd = resources.getDimensionPixelOffset(me.zhanghai.android.fastscroll.R.dimen.afs_md2_popup_padding_end);
    }

    private static void pathArcTo(
            Path path,
            float centerX,
            float centerY,
            float radius,
            float startAngle,
            float sweepAngle
    ) {
        //将指定的圆弧作为新轮廓附加到路径。如果路径的起点与路径的当前最后一点不同，则会添加一个自动 lineTo() 以将当前轮廓连接到圆弧的起点。
        // 但是，如果路径为空，则我们使用弧的第一个点调用 moveTo()。
        //
        //参数：
        //startAngle – 圆弧开始的起始角度（以度为单位）
        //sweepAngle – 顺时针方向测量的扫描角度（以度为单位），经过 360 度处理。
        //forceMoveTo – 如果为真，则始终以圆弧开始新的轮廓
        path.arcTo(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius,
                startAngle,
                sweepAngle,
                false
        );
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
    }

    /*
        Drawable 返回此 Drawable 的不透明度/透明度。返回值是PixelFormat中的抽象格式常量之一： PixelFormat.UNKNOWN 、
        PixelFormat.TRANSLUCENT 、 PixelFormat.TRANSPARENT或PixelFormat.OPAQUE 。
        OPAQUE 可绘制对象是在其边界内绘制所有内容，完全覆盖可绘制对象后面的任何内容。
        TRANSPARENT drawable 是一种在其范围内不绘制任何内容，允许其后面的所有内容显示出来的对象。
        TRANSLUCENT 可绘制对象是处于任何其他状态的可绘制对象，其中可绘制对象将在其边界内绘制部分（但不是全部）内容，
        并且至少可绘制对象后面的一些内容是可见的。如果无法确定可绘制内容的可见性，则最安全/最佳的返回值为 TRANSLUCENT。
        一般来说，Drawable 应该尽可能保守它返回的值。例如，如果它包含多个子可绘制对象并且一次只显示其中一个，
        如果只有一个子对象是 TRANSLUCENT 而其他子对象是 OPAQUE，则应返回 TRANSLUCENT。您可以使用resolveOpacity方法将两个不透明度标准减少到适当的单个输出。
        请注意，返回值不一定考虑客户端通过setAlpha或setColorFilter方法应用的自定义 alpha 或颜色过滤器。
        一些子类，例如BitmapDrawable 、 ColorDrawable和GradientDrawable ，确实考虑了setAlpha的值，但一般行为取决于子类的实现。
    */
    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }


    @Override
    public void getOutline(@NonNull Outline outline) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !mPath.isConvex()) {
            // The outline path must be convex before Q, but we may run into floating point error
            // caused by calculation involving sqrt(2) or OEM implementation difference, so in this
            // case we just omit the shadow instead of crashing.
            super.getOutline(outline);
            return;
        }
        outline.setConvexPath(mPath);
    }

    @Override
    public void setAlpha(int alpha) {}

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {}

    @Override
    public boolean getPadding(@NonNull Rect padding) {
        if (needMirroring()) {
            padding.set(mPaddingEnd, 0, mPaddingStart, 0);
        } else {
            padding.set(mPaddingStart, 0, mPaddingEnd, 0);
        }
        return true;
    }

    @Override
    public boolean isAutoMirrored() {
        return true;
    }

    //当可绘制对象的解析布局方向发生变化时调用。
    @Override
    public boolean onLayoutDirectionChanged(int layoutDirection) {
        updatePath();
        return true;
    }

    //如果您根据边界而变化，请在您的子类中覆盖它以更改外观。
    @Override
    protected void onBoundsChange(@NonNull Rect bounds) {
        updatePath();
    }

    private boolean needMirroring() {
        return DrawableCompat.getLayoutDirection(this) == View.LAYOUT_DIRECTION_RTL;
    }


    private void updatePath(){

        mPath.reset();

        Rect bounds =getBounds();
        float width = bounds.width();
        float height = bounds.height();
        float r = height / 2;
        float sqrt2 = (float) Math.sqrt(2);
        // Ensure we are convex. 确保我们是凸的。
        width = Math.max(r+sqrt2 *r, width);
        pathArcTo(mPath,r,r,r,90,180);
        float o1X = width - sqrt2 * r;
        pathArcTo(mPath, o1X, r, r, -90, 45f);
        float r2 = r / 5;
        float o2X = width - sqrt2 * r2;
        pathArcTo(mPath, o2X, r, r2, -45, 90);
        pathArcTo(mPath, o1X, r, r, 45f, 45f);
        mPath.close();

        if (needMirroring()) {
            mTempMatrix.setScale(-1, 1, width / 2, 0);
        } else {
            mTempMatrix.reset();
        }
        mTempMatrix.postTranslate(bounds.left,bounds.top);
        mPath.transform(mTempMatrix);

    }
}