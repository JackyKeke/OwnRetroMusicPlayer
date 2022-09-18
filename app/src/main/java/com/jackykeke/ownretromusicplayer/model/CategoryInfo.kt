package com.jackykeke.ownretromusicplayer.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jackykeke.ownretromusicplayer.R
import kotlinx.parcelize.Parcelize

/**
 *
 * @author keyuliang on 2022/9/16.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
@Parcelize
data class CategoryInfo( val category: Category,var visible:Boolean) : Parcelable{

    enum class Category(
        val id:Int,
        @StringRes val stringRes: Int,
        @DrawableRes val icon:Int
    ){
        Home(R.id.action_home,)

    }

}
