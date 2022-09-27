package com.jackykeke.ownretromusicplayer

import android.content.Context
import android.content.ContextWrapper
import android.os.LocaleList
import com.jackykeke.appthemehelper.util.VersionUtils.hasNougat
import java.util.*

/**
 *
 * @author keyuliang on 2022/9/27.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class LanguageContextWrapper (base:Context?):ContextWrapper(base) {

    companion object{
        fun wrap(context: Context?,newLocale:Locale?):LanguageContextWrapper{

            if (context == null) return LanguageContextWrapper(context)

            val configuration = context.resources.configuration
            if (hasNougat()){
                configuration.setLocale(newLocale)
                val localeList= LocaleList(newLocale)
                LocaleList.setDefault(localeList)
                configuration.setLocales(localeList)
            }else{
                configuration.setLocale(newLocale)
            }
            return LanguageContextWrapper(context.createConfigurationContext(configuration))
        }
    }
}