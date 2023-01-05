package com.jackykeke.appthemehelper.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.jackykeke.appthemehelper.R;

import java.lang.reflect.Field;

/**
 * @author keyuliang on 2022/9/15.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
public final class TintHelper {

    public static void colorHandles(@NonNull TextView view ,int color){
        try {
            Field editorField = TextView.class.getDeclaredField("mEditor");
        if (editorField.isAccessible()){
            editorField.setAccessible(true);
        }

        Object editor =editorField.get(view);
        Class<?> editorClass =editor.getClass();

        String[] handleNames ={"mSelectHandleLeft", "mSelectHandleRight", "mSelectHandleCenter"};
        String[] resNames = {"mTextSelectHandleLeftRes", "mTextSelectHandleRightRes", "mTextSelectHandleRes"};

            for (int i = 0; i < handleNames.length; i++) {
                Field handleField = editorClass.getDeclaredField(handleNames[i]);
                if (!handleField.isAccessible()) {
                    handleField.setAccessible(true);
                }

                Drawable handleDrawable = (Drawable) handleField.get(editor);

                if (handleDrawable == null) {
                    Field resField = TextView.class.getDeclaredField(resNames[i]);
                    if (!resField.isAccessible()) {
                        resField.setAccessible(true);
                    }
                    int resId = resField.getInt(view);
                    handleDrawable = view.getResources().getDrawable(resId);
                }

                if (handleDrawable != null) {
                    Drawable drawable = handleDrawable.mutate();
                    drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    handleField.set(editor, drawable);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void setTintAuto(final @NonNull View view, final @ColorInt int color,
                                   boolean background) {
        setTintAuto(view, color, background, ATHUtil.INSTANCE.isWindowBackgroundDark(view.getContext()));
    }

    public static void setTintAuto(final @NonNull View view, final @ColorInt int color, boolean background, final boolean isDark) {

        if (!background) {
            if (view instanceof FloatingActionButton) {
                setTint((FloatingActionButton) view, color, isDark);
            } else if (view instanceof RadioButton) {
                setTint((RadioButton) view, color, isDark);
            } else if (view instanceof SeekBar) {
                setTint((SeekBar) view, color, isDark);
            } else if (view instanceof ProgressBar) {
                setTint((ProgressBar) view, color);
            } else if (view instanceof EditText) {
                setTint((EditText) view, color, isDark);
            } else if (view instanceof CheckBox) {
                setTint((CheckBox) view, color, isDark);
            } else if (view instanceof ImageView) {
                setTint((ImageView) view, color);
            } else if (view instanceof MaterialSwitch) {
                setTint((MaterialSwitch) view, color, isDark);
            } else if (view instanceof SwitchCompat) {
                setTint((SwitchCompat) view, color, isDark);
            } else {
                background = true;
            }
        }
    }

    public static void setTint(@NonNull ImageView image, @ColorInt int color) {
        image.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    public static void setTint(@NonNull SwitchCompat switchView, @ColorInt int color, boolean useDarker) {
        if (switchView.getTrackDrawable() != null) {
            switchView.setTrackDrawable(modifySwitchDrawable(switchView.getContext(), switchView.getTrackDrawable(),
                    color,false,true,useDarker));
        }
    }

    private static Drawable modifySwitchDrawable(@NonNull Context context, @NonNull Drawable from, @ColorInt int tint,
                                                 boolean thumb, boolean compatSwitch, boolean useDarker) {
        ColorStateList sl=createSwitchDrawableTintList(context,tint,thumb,compatSwitch,useDarker);
        return createTintedDrawable(from,sl);
    }


    // This returns a NEW Drawable because of the mutate() call. The mutate() call is necessary
    // because Drawables with the same resource have shared states otherwise.
    @CheckResult
    @Nullable
    public static Drawable createTintedDrawable(@Nullable Drawable drawable, @NonNull ColorStateList sl) {
        if (drawable == null) {
            return null;
        }
        Drawable temp = DrawableCompat.wrap(drawable.mutate());
        temp.setTintList(sl);
        return temp;
    }

    private static ColorStateList createSwitchDrawableTintList(@NonNull Context context, @ColorInt int tint,
                                                               boolean thumb, boolean compatSwitch, boolean useDarker){
            int lightTint=ColorUtil.INSTANCE.blendColors(tint, Color.WHITE,0.4f);
            int darkerTint = ColorUtil.INSTANCE.shiftColor(tint,0.8f);
            if (useDarker){
                tint = (compatSwitch && !thumb) ? lightTint:darkerTint;
            }else {
                tint = (compatSwitch && !thumb) ? darkerTint : Color.WHITE;
            }

            int disabled;
            int normal;
            if (thumb){
                disabled =ContextCompat.getColor(context,
                        useDarker?R.color.ate_switch_thumb_disabled_dark:R.color.ate_switch_thumb_disabled_light);
                normal = ContextCompat.getColor(context,
                        useDarker ? R.color.ate_switch_thumb_normal_dark : R.color.ate_switch_thumb_normal_light);
            }else {
                disabled = ContextCompat.getColor(context,
                        useDarker ? R.color.ate_switch_track_disabled_dark : R.color.ate_switch_track_disabled_light);
                normal = ContextCompat.getColor(context,
                        useDarker ? R.color.ate_switch_track_normal_dark : R.color.ate_switch_track_normal_light);
            }

        // Stock switch includes its own alpha
        if (!compatSwitch){
            normal=ColorUtil.INSTANCE.stripAlpha(normal);
        }

        return new ColorStateList(
                new int[][]{
                        {-android.R.attr.state_enabled},
                         {android.R.attr.state_enabled, -android.R.attr.state_activated, -android.R.attr.state_checked},
                         {android.R.attr.state_enabled, android.R.attr.state_activated},
                         {android.R.attr.state_enabled, android.R.attr.state_checked}
                },
                new int[]{
                        disabled,
                        normal,
                        tint,
                        tint
                }
        );


    }

    public static void setTint(@NonNull CheckBox box, @ColorInt int color, boolean useDarker) {
        ColorStateList sl = new ColorStateList(new int[][]{
                new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_enabled, -android.R.attr.state_checked},
                new int[]{android.R.attr.state_enabled, android.R.attr.state_checked}
        }, new int[]{
                ContextCompat.getColor(box.getContext(),
                        useDarker ? R.color.ate_control_disabled_dark : R.color.ate_control_disabled_light),
                ContextCompat.getColor(box.getContext(),
                        useDarker ? R.color.ate_control_normal_dark : R.color.ate_control_normal_light),
                color
        });
        box.setButtonTintList(sl);
    }


    public static void setTint(@NonNull EditText editText, @ColorInt int color, boolean useDarker) {

        final ColorStateList editTextColorStateList = new ColorStateList(new int[][]{
                {-android.R.attr.state_enabled},
                {android.R.attr.state_enabled, -android.R.attr.state_pressed, -android.R.attr.state_focused},
                {}
        }, new int[]{
                ContextCompat.getColor(editText.getContext(),
                        useDarker ? R.color.ate_text_disabled_dark : R.color.ate_text_disabled_light),
                ContextCompat.getColor(editText.getContext(),
                        useDarker ? R.color.ate_control_normal_dark : R.color.ate_control_normal_light),
                color
        });
        if (editText instanceof AppCompatEditText) {
            ((AppCompatEditText) editText).setSupportBackgroundTintList(editTextColorStateList);
        } else {
            editText.setBackgroundTintList(editTextColorStateList);
        }
        setCursorTint(editText, color);
    }

    //光标颜色
    public static void setCursorTint(EditText editText, int color) {
        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);
            Drawable[] drawables = new Drawable[2];
            drawables[0] = ContextCompat.getDrawable(editText.getContext(), mCursorDrawableRes);
            drawables[0] = createTintedDrawable(drawables[0], color);
            drawables[1] = ContextCompat.getDrawable(editText.getContext(), mCursorDrawableRes);
            drawables[1] = createTintedDrawable(drawables[1], color);
            fCursorDrawable.set(editor, drawables);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This returns a NEW Drawable because of the mutate() call. The mutate() call is necessary
    // because Drawables with the same resource have shared states otherwise.
    @CheckResult
    @NonNull
    public static Drawable createTintedDrawable(@Nullable Drawable drawable, @ColorInt int color) {
        if (drawable == null) {
            return null;
        }
        drawable = DrawableCompat.wrap(drawable.mutate());
        drawable.setTintMode(PorterDuff.Mode.SRC_IN);
        drawable.setTint(color);
        return drawable;
    }

    public static void setTint(@NonNull ProgressBar progressBar, @ColorInt int color) {
        setTint(progressBar, color, false);
    }

    public static void setTint(@NonNull ProgressBar progressBar, @ColorInt int color, boolean skipIndeterminate) {
        ColorStateList sl = ColorStateList.valueOf(color);
        progressBar.setProgressTintList(sl);
        progressBar.setSecondaryProgressTintList(sl);
        if (!skipIndeterminate) {
            progressBar.setIndeterminateTintList(sl);
        }
    }

    public static void setTint(@NonNull SeekBar seekBar, @ColorInt int color, boolean useDarker) {
        final ColorStateList sl = getDisabledColorStateList(color, ContextCompat.getColor(seekBar.getContext(),
                useDarker ? R.color.ate_control_disabled_dark : R.color.ate_control_disabled_light));
        seekBar.setThumbTintList(sl);
        seekBar.setProgressTintList(sl);
    }

    private static ColorStateList getDisabledColorStateList(@ColorInt int normal, @ColorInt int disabled) {
        return new ColorStateList(new int[][]{
                {-android.R.attr.state_enabled},
                {android.R.attr.state_enabled}},
                new int[]{disabled,
                        normal});
    }

    public static void setTint(@NonNull RadioButton radioButton, @ColorInt int color, boolean useDarker) {
        ColorStateList sl = new ColorStateList(new int[][]{
                new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_enabled, -android.R.attr.state_checked},
                new int[]{android.R.attr.state_enabled, android.R.attr.state_checked}
        }, new int[]{
                ColorUtil.INSTANCE.stripAlpha(ContextCompat.getColor(radioButton.getContext(),
                        useDarker ? R.color.ate_control_disabled_dark : R.color.ate_control_disabled_light)),
                ContextCompat.getColor(radioButton.getContext(),
                        useDarker ? R.color.ate_control_normal_dark : R.color.ate_control_normal_light),
                color
        });

        radioButton.setButtonTintList(sl);
    }


    private static void setTint(FloatingActionButton view, int color, boolean isDark) {
        view.setImageTintList(ColorStateList.valueOf(color));
    }

    @CheckResult
    @NonNull
    public static Drawable createTintedDrawable(Context context,
                                                @DrawableRes int res, @ColorInt int color){
        Drawable drawable = ContextCompat.getDrawable(context,res);
        return createTintedDrawable(drawable, color);
    }


}
