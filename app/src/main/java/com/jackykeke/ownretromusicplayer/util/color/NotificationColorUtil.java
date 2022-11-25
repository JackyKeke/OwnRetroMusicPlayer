package com.jackykeke.ownretromusicplayer.util.color;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Pair;

import androidx.annotation.ColorInt;

import java.util.WeakHashMap;

/**
 * @author keyuliang on 2022/11/25.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
public class NotificationColorUtil {

    private static final String TAG = "NotificationColorUtil";
    private static final boolean DEBUG = false;

    private static final Object sLock = new Object();
    private static NotificationColorUtil sInstance;

    private final ImageUtils mImageUtils = new ImageUtils();
    private final WeakHashMap<Bitmap, Pair<Boolean, Integer>> mGrayscaleBitmapCache =
            new WeakHashMap<>();

    private final int mGrayscaleIconMaxSize; // @dimen/notification_large_icon_width (64dp)

    private NotificationColorUtil(Context context) {
        mGrayscaleIconMaxSize = context.getResources().getDimensionPixelSize(androidx.core.R.dimen.notification_large_icon_width);

    }

    public static NotificationColorUtil getInstance(Context context) {
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new NotificationColorUtil(context);
            }
            return sInstance;
        }
    }


    /**
     * Clears all color spans of a text
     *
     * @param charSequence the input text
     * @return the same text but without color spans
     */
    public static CharSequence clearColorSpans(CharSequence charSequence) {

        if (charSequence instanceof Spanned) {
            Spanned ss = (Spanned) charSequence;
            Object[] spans = ss.getSpans(0, ss.length(), Object.class);
            SpannableStringBuilder builder = new SpannableStringBuilder(ss.toString());

            for (Object span : spans
            ) {
                Object resultSpan = span;
                if (resultSpan instanceof CharacterStyle) {
                    resultSpan = ((CharacterStyle) span).getUnderlying();
                }
                if (resultSpan instanceof TextAppearanceSpan) {
                    TextAppearanceSpan originalSpan = (TextAppearanceSpan) resultSpan;
                    if (originalSpan.getTextColor() != null) {
                        resultSpan = new TextAppearanceSpan(
                                originalSpan.getFamily(),
                                originalSpan.getTextStyle(),
                                originalSpan.getTextSize(),
                                null,
                                originalSpan.getLinkTextColor()
                        );
                    }

                } else if (resultSpan instanceof ForegroundColorSpan
                        || (resultSpan instanceof BackgroundColorSpan)) {
                    continue;
                } else {
                    resultSpan = span;
                }
                builder.setSpan(
                        resultSpan, ss.getSpanStart(span), ss.getSpanEnd(span), ss.getSpanFlags(span));
            }
            return builder;

        }

        return charSequence;

    }


    /**
     * Finds a suitable color such that there's enough contrast.
     *
     * @param color    the color to start searching from.
     * @param other    the color to ensure contrast against. Assumed to be lighter than {@param color}
     * @param findFg   if true, we assume {@param color} is a foreground, otherwise a background.
     * @param minRatio the minimum contrast ratio required.
     * @return a color with the same hue as {@param color}, potentially darkened to meet the contrast
     * ratio.
     */
    public static int findContrastColor(int color, int other, boolean findFg, double minRatio) {
        int fg = findFg ? color : other;
        int bg = findFg ? other : color;
        if (ColorUtilsFromCompat.calculateContrast(fg, bg) >= minRatio) {
            return color;
        }

        double[] lab = new double[3];
        ColorUtilsFromCompat.colorToLAB(findFg ? fg : bg, lab);

        double low = 0, high = lab[0];
        final double a = lab[1], b = lab[2];
        for (int i = 0; i < 15 && high - low > 0.00001; i++) {
            final double l = (low + high) / 2;
            if (findFg) {
                fg = ColorUtilsFromCompat.LABToColor(l, a, b);
            } else {
                bg = ColorUtilsFromCompat.LABToColor(l, a, b);
            }
            if (ColorUtilsFromCompat.calculateContrast(fg, bg) > minRatio) {
                low = l;
            } else {
                high = l;
            }
        }
        return ColorUtilsFromCompat.LABToColor(low, a, b);
    }

    /**
     * Framework copy of functions needed from android.support.v4.graphics.ColorUtils.
     */
    private static class ColorUtilsFromCompat {

        private static final double XYZ_WHITE_REFERENCE_X = 95.047;
        private static final double XYZ_WHITE_REFERENCE_Y = 100;
        private static final double XYZ_WHITE_REFERENCE_Z = 108.883;
        private static final double XYZ_EPSILON = 0.008856;
        private static final double XYZ_KAPPA = 903.3;

        private static final int MIN_ALPHA_SEARCH_MAX_ITERATIONS = 10;
        private static final int MIN_ALPHA_SEARCH_PRECISION = 1;

        private static final ThreadLocal<double[]> TEMP_ARRAY = new ThreadLocal<>();

        private ColorUtilsFromCompat() {
        }

        /**
         * Composite two potentially translucent colors over each other and returns the result.
         */
        public static int compositeColors(@ColorInt int foreground, @ColorInt int background) {
            int bgAlpha = Color.alpha(background);
            int fgAlpha = Color.alpha(foreground);
            int a = compositeAlpha(fgAlpha, bgAlpha);

            int r = compositeComponent(Color.red(foreground),fgAlpha,Color.red(background),bgAlpha,a);
            int g =
                    compositeComponent(Color.green(foreground), fgAlpha, Color.green(background), bgAlpha, a);
            int b =
                    compositeComponent(Color.blue(foreground), fgAlpha, Color.blue(background), bgAlpha, a);

            return Color.argb(a, r, g, b);

        }

        private static int compositeComponent(int fgC, int fgA, int bgC, int bgA, int a) {

            if (a == 0)return 0;

            return ((0XFF * fgC *fgA) + (bgC * bgA * (0xFF - fgA)))/(a*0XFF);
        }

        private static int compositeAlpha(int foregroundAlpha, int backgroundAlpha) {
            return 0XFF - ((0XFF - backgroundAlpha) * (0xFF - foregroundAlpha) / 0XFF);
        }


        public static boolean calculateContrast(int fg, int bg) {
        }
    }
}
