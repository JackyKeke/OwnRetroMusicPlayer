package com.jackykeke.ownretromusicplayer.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.databinding.ListItemViewNoCardBinding
import com.jackykeke.ownretromusicplayer.extensions.hide
import com.jackykeke.ownretromusicplayer.extensions.show

/**
 *
 * @author keyuliang on 2023/7/3.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class ListItemView @JvmOverloads constructor(
    context: Context,
    attrs:AttributeSet?=null,
    defStyleAttr:Int = -1
): FrameLayout(context, attrs, defStyleAttr){

    private var binding = ListItemViewNoCardBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )


    init {

        context.withStyledAttributes(attrs, R.styleable.ListItemView){
            if (hasValue(R.styleable.ListItemView_listItemIcon)){
                binding.icon.setImageDrawable(getDrawable(R.styleable.ListItemView_listItemIcon))
            }else{
                binding.icon.hide()
            }

            binding.title.text = getText(R.styleable.ListItemView_listItemTitle)
            if (hasValue(R.styleable.ListItemView_listItemSummary)) {
                binding.summary.text = getText(R.styleable.ListItemView_listItemSummary)
            } else {
                binding.summary.hide()
            }
        }
    }

    fun setSummary(appVersion:String){
        binding.summary.show()
        binding.summary.text = appVersion
    }
}