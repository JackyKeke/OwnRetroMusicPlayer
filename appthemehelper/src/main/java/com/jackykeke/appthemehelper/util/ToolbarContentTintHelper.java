package com.jackykeke.appthemehelper.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SearchView;

import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.WindowDecorActionBar;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.view.menu.BaseMenuPresenter;
import androidx.appcompat.view.menu.ListMenuItemView;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.view.menu.MenuPresenter;
import androidx.appcompat.view.menu.ShowableListMenu;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ToolbarWidgetWrapper;

import com.jackykeke.appthemehelper.ThemeStore;
import com.jackykeke.appthemehelper.common.ATHToolbarActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * @author keyuliang on 2022/9/15.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
public final class ToolbarContentTintHelper {

    public static void handleOnPrepareOptionsMenu(Activity activity, Toolbar toolbar) {
        handleOnPrepareOptionsMenu(activity, toolbar,ThemeStore.Companion.accentColor(activity));
    }

    private static void handleOnPrepareOptionsMenu(Activity activity, Toolbar toolbar, int widgetColor) {
        InternalToolbarContentTintUtil.applyOverflowMenuTint(activity,toolbar,widgetColor);

    }

    public static class InternalToolbarContentTintUtil {

        public static void setOverflowButtonColor(@NonNull Activity activity,
                                                  final @ColorInt int color) {
            String overflowDescription = activity
                    .getString(androidx.appcompat.R.string.abc_action_menu_overflow_description);
            ViewGroup decrView = (ViewGroup) activity.getWindow().getDecorView();
            ViewTreeObserver viewTreeObserver = decrView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ArrayList<View> outViews = new ArrayList<>();
                    decrView.findViewsWithText(outViews, overflowDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                    if (outViews.isEmpty())
                        return;

                    AppCompatImageView overflow = (AppCompatImageView) outViews.get(0);
                    overflow.setImageDrawable(TintHelper.createTintedDrawable(overflow.getDrawable(), color));
                    ViewUtil.INSTANCE.removeOnGlobalLayoutListener(decrView, this);
                }
            });
        }

        public static void tintMenu(@NonNull Toolbar toolbar, @Nullable Menu menu, final @ColorInt int color) {

            try {
                final Field field = Toolbar.class.getDeclaredField("mCollapseIcon");
                field.setAccessible(true);
                Drawable collapseIcon = (Drawable) field.get(toolbar);
                if (collapseIcon != null) {
                    field.set(toolbar, TintHelper.createTintedDrawable(collapseIcon, color));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (menu != null && menu.size() > 0) {
                for (int i = 0; i < menu.size(); i++) {
                    MenuItem item = menu.getItem(i);
                    if (item.getIcon() != null) {
                        item.setIcon(TintHelper.createTintedDrawable(item.getIcon(), color));
                    }

                    // Search view theming
                    if (item.getActionView() != null && (item.getActionView() instanceof SearchView ||
                            item.getActionView() instanceof androidx.appcompat.widget.SearchView)) {
                        SearchViewTintUtil.setSearchViewContentColor(item.getActionView(), color);
                    }
                }
            }
        }

        public static final class SearchViewTintUtil {

            public static void setSearchViewContentColor(View searchView, final @ColorInt int color) {
                if (searchView == null)
                    return;

                final Class<?> cls = searchView.getClass();

                try {
                    Field mSearchSrcTextViewField = cls.getDeclaredField("mSearchSrcTextView");
                    mSearchSrcTextViewField.setAccessible(true);
                    EditText mSearchSrcTextView = (EditText) mSearchSrcTextViewField.get(searchView);
                    mSearchSrcTextView.setTextColor(color);
                    mSearchSrcTextView.setHintTextColor(ColorUtil.INSTANCE.adjustAlpha(color, 0.5f));
                    TintHelper.setCursorTint(mSearchSrcTextView, color);

                    Field field = cls.getDeclaredField("mSearchButton");
                    tintImageView(searchView, field, color);
                    field = cls.getDeclaredField("mGoButton");
                    tintImageView(searchView, field, color);
                    field = cls.getDeclaredField("mCloseButton");
                    tintImageView(searchView, field, color);
                    field = cls.getDeclaredField("mVoiceButton");
                    tintImageView(searchView, field, color);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private SearchViewTintUtil() {
            }

            private static void tintImageView(Object target, Field field, int color) throws Exception {
                field.setAccessible(true);
                ImageView imageView = (ImageView) field.get(target);
                if (imageView.getDrawable() != null) {
                    imageView.setImageDrawable(TintHelper.createTintedDrawable(imageView.getDrawable(), color));
                }
            }
        }

        public static void applyOverflowMenuTint(final @NonNull Context context, final Toolbar toolbar,
                                                 final @ColorInt int color) {

            if (toolbar == null)
                return;
            toolbar.post(new Runnable() {
                @Override
                public void run() {

                    try {

                        Field f1 = Toolbar.class.getDeclaredField("mMenuView");
                        f1.setAccessible(true);
                        ActionMenuView actionMenuView = (ActionMenuView) f1.get(toolbar);
                        Field f2 = ActionMenuView.class.getDeclaredField("mPresenter");
                        f2.setAccessible(true);

                        // Actually ActionMenuPresenter
                        BaseMenuPresenter presenter = (BaseMenuPresenter) f2.get(actionMenuView);
                        Field f3 = presenter.getClass().getDeclaredField("mOverflowPopup");
                        f3.setAccessible(true);

                        MenuPopupHelper overflowMenuPopupHelper = (MenuPopupHelper) f3.get(presenter);
                        setTintForMenuPopupHelper(context, overflowMenuPopupHelper, color);
                        Field f4 = presenter.getClass().getDeclaredField("mActionButtonPopup");
                        f4.setAccessible(true);

                        MenuPopupHelper subMenuPopupHelper = (MenuPopupHelper) f4.get(presenter);
                        setTintForMenuPopupHelper(context, subMenuPopupHelper, color);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        public static void setTintForMenuPopupHelper(final @NonNull Context context,
                                                     @Nullable MenuPopupHelper menuPopupHelper, final @ColorInt int color) {

            try {

                if (menuPopupHelper != null) {
                    @SuppressLint("RestrictedApi") final ListView listView = ((ShowableListMenu) menuPopupHelper.getPopup()).getListView();
                    listView.getViewTreeObserver()
                            .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {

                                    try {

                                        Field checkboxField = ListMenuItemView.class.getDeclaredField("mCheckBox");
                                        checkboxField.setAccessible(true);
                                        Field radioButtonField = ListMenuItemView.class.getDeclaredField("mRadioButton");
                                        radioButtonField.setAccessible(true);

                                        final boolean isDark = !ColorUtil.INSTANCE.isColorLight(
                                                ATHUtil.INSTANCE.resolveColor(context, android.R.attr.windowBackground));

                                        for (int i = 0; i < listView.getChildCount(); i++) {

                                            View v = listView.getChildAt(i);
                                            if (!(v instanceof ListMenuItemView)) {
                                                continue;
                                            }
                                            ListMenuItemView iv = (ListMenuItemView) v;
                                            CheckBox check = (CheckBox) checkboxField.get(iv);
                                            if (check != null) {
                                                TintHelper.setTint(check, color, isDark);
                                                check.setBackground(null);
                                            }

                                            RadioButton radioButton = (RadioButton) radioButtonField.get(iv);
                                            if (radioButton != null) {
                                                TintHelper.setTint(radioButton, color, isDark);
                                                radioButton.setBackground(null);
                                            }

                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                                }
                            });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    /**
     * Use this method to colorize toolbar icons to the desired target color
     * 使用此方法将工具栏图标着色为所需的目标颜色
     *
     * @param toolbarView       toolbar view being colored
     * @param toolbarIconsColor the target color of toolbar icons
     * @param activity          reference to activity needed to register observers
     */

    public static void colorizeToolbar(Toolbar toolbarView, int toolbarIconsColor, Activity activity) {

        final PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(toolbarIconsColor, PorterDuff.Mode.MULTIPLY);

        for (int i = 0; i < toolbarView.getChildCount(); i++) {
            final View v = toolbarView.getChildAt(i);

            //Step 1 : Changing the color of back button (or open drawer button).
            // 1更改后退按钮（或打开抽屉按钮）的颜色。
            if (v instanceof ImageButton) {
                //Action Bar back button
                ((ImageButton) v).getDrawable().setColorFilter(colorFilter);
            }

            if (v instanceof ActionMenuView) {
                //Step 2: Changing the color of any ActionMenuViews - icons that are not back button, nor text, nor overflow menu icon.
                //Colorize the ActionViews -> all icons that are NOT: back button | overflow menu
                // 第 2 步：更改任何 ActionMenuViews 的颜色 - 不是后退按钮、文本或溢出菜单图标的图标。为 ActionViews 着色 -> 所有不是的图标：后退按钮 |溢出菜单

                for (int j = 0; j < ((ActionMenuView) v).getChildCount(); j++) {
                    final View innerView = ((ActionMenuView) v).getChildAt(j);
                    if (innerView instanceof ActionMenuItemView) {
                        for (int k = 0; k < ((ActionMenuItemView) innerView).getCompoundDrawables().length; k++) {
                            if (((ActionMenuItemView) innerView).getCompoundDrawables()[k] != null) {
                                int finalK = k;
                                innerView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((ActionMenuItemView) innerView).getCompoundDrawables()[finalK].setColorFilter(colorFilter);
                                    }
                                });
                            }
                        }
                    }

                }
            }

            //Step 3: Changing the color of title and subtitle.
            toolbarView.setTitleTextColor(ATHUtil.INSTANCE.resolveColor(activity, android.R.attr.textColorPrimary));
            toolbarView.setSubtitleTextColor(ATHUtil.INSTANCE.resolveColor(activity, android.R.attr.textColorSecondary));

            //Step 4: Changing the color of the Overflow Menu icon.
            setOverflowButtonColor(toolbarView, toolbarIconsColor);
        }
    }


    private static class ATHMenuPresenterCallback implements MenuPresenter.Callback {

        private final int mColor;
        private final Context mContext;
        private final MenuPresenter.Callback mParentCb;
        private final Toolbar mToolbar;


        public ATHMenuPresenterCallback(Context context, int color, MenuPresenter.Callback parentcb, Toolbar toolbar) {
            this.mColor = color;
            this.mContext = context;
            this.mParentCb = parentcb;
            this.mToolbar = toolbar;
        }

        @Override
        public void onCloseMenu(@NonNull MenuBuilder menu, boolean allMenusAreClosing) {
            if (mParentCb != null) {
                mParentCb.onCloseMenu(menu, allMenusAreClosing);
            }
        }

        @Override
        public boolean onOpenSubMenu(@NonNull MenuBuilder subMenu) {
            InternalToolbarContentTintUtil.applyOverflowMenuTint(mContext, mToolbar, mColor);
            return mParentCb != null && mParentCb.onOpenSubMenu(subMenu);
        }
    }

    private static void setOverflowButtonColor(final Toolbar toolbar, final int color) {
        Drawable drawable = toolbar.getOverflowIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Nullable
    public static Toolbar getSupportActionBarView(@Nullable ActionBar ab) {
        if (!(ab instanceof WindowDecorActionBar))
            return null;
        try {
            WindowDecorActionBar decorAb = (WindowDecorActionBar) ab;

            Field field = WindowDecorActionBar.class.getDeclaredField("mDecorToolbar");
            field.setAccessible(true);
            ToolbarWidgetWrapper wrapper = (ToolbarWidgetWrapper) field.get(decorAb);
            field = ToolbarWidgetWrapper.class.getDeclaredField("mToolbar");
            field.setAccessible(true);
            return (Toolbar) field.get(wrapper);
        } catch (Throwable t) {
            throw new RuntimeException(
                    "Failed to retrieve Toolbar from AppCompat support ActionBar: " + t.getMessage(), t);
        }

    }

    public static void handleOnCreateOptionsMenu(
            @NonNull Context context,
            @NonNull Toolbar toolbar,
            @NonNull Menu menu,
            int toolbarColor) {
        setToolbarContentColorBasedOnToolbarColor(context, toolbar, menu, toolbarColor);
    }

    private static void setToolbarContentColorBasedOnToolbarColor(Context context, Toolbar toolbar, Menu menu, int toolbarColor) {
        setToolbarContentColorBasedOnToolbarColor(context, toolbar, menu, toolbarColor, ThemeStore.Companion.accentColor(context));
    }

    private static void setToolbarContentColorBasedOnToolbarColor(Context context, Toolbar toolbar, Menu menu,
                                                                  int toolbarColor, final @ColorInt int menuWidgetColor) {

        setToolbarContentColor(context, toolbar, menu, toolbarContentColor(context, toolbarColor),
                toolbarTitleColor(context, toolbarColor), toolbarSubtitleColor(context, toolbarColor),
                menuWidgetColor);
    }


    @CheckResult
    @ColorInt
    public static int toolbarContentColor(@NonNull Context context, @ColorInt int toolbarColor) {
        if (ColorUtil.INSTANCE.isColorLight(toolbarColor)) {
            return toolbarSubtitleColor(context, toolbarColor);
        }
        return toolbarTitleColor(context, toolbarColor);
    }

    @CheckResult
    @ColorInt
    private static int toolbarTitleColor(Context context, int toolbarColor) {
        return MaterialValueHelper
                .getPrimaryTextColor(context, ColorUtil.INSTANCE.isColorLight(toolbarColor));
    }

    @CheckResult
    @ColorInt
    private static int toolbarSubtitleColor(Context context, int toolbarColor) {
        return MaterialValueHelper.getSecondaryTextColor(context,ColorUtil.INSTANCE.isColorLight(toolbarColor));
    }

    private ToolbarContentTintHelper() {
    }


    public static void setToolbarContentColor(@NonNull Context context,
                                              Toolbar toolbar,
                                              @Nullable Menu menu,
                                              final @ColorInt int toolbarContentColor,
                                              final @ColorInt int titleTextColor,
                                              final @ColorInt int subtitleTextColor,
                                              final @ColorInt int menuWidgetColor) {

        if (toolbar == null)
            return;
        if (menu == null) {
            menu = toolbar.getMenu();
        }

        toolbar.setTitleTextColor(titleTextColor);
        toolbar.setSubtitleTextColor(subtitleTextColor);

        if (toolbar.getNavigationIcon() != null) {
            // Tint the toolbar navigation icon (e.g. back, drawer, etc.)
            toolbar.setNavigationIcon(TintHelper.createTintedDrawable(toolbar.getNavigationIcon(), toolbarContentColor));
        }

        InternalToolbarContentTintUtil.tintMenu(toolbar, menu, toolbarContentColor);
        InternalToolbarContentTintUtil.applyOverflowMenuTint(context, toolbar, menuWidgetColor);

        if (context instanceof Activity) {
            InternalToolbarContentTintUtil.setOverflowButtonColor((Activity) context, toolbarContentColor);
        }


    }

}
