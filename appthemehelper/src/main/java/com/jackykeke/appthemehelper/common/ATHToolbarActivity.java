package com.jackykeke.appthemehelper.common;

import android.graphics.Color;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.jackykeke.appthemehelper.ATHActivity;
import com.jackykeke.appthemehelper.util.ATHUtil;
import com.jackykeke.appthemehelper.util.ToolbarContentTintHelper;

/**
 * @author keyuliang on 2022/9/16.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
public class ATHToolbarActivity extends ATHActivity {

    private Toolbar toolbar;

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        this.toolbar= toolbar;
        super.setSupportActionBar(toolbar);
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        ToolbarContentTintHelper.handleOnPrepareOptionsMenu(this,getATHToolbar());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        Toolbar toolbar =getATHToolbar();
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(this,toolbar,menu,getToolbarBackgroundColor(toolbar));
        return super.onCreateOptionsMenu(menu);
    }

    public static int getToolbarBackgroundColor(@NonNull Toolbar toolbar) {
        if (toolbar!=null){
            return ATHUtil.INSTANCE.resolveColor(toolbar.getContext(), com.google.android.material.R.attr.colorSurface);

        }
        return Color.BLACK;
    }

    protected Toolbar getATHToolbar() {
        return toolbar;
    }
}
