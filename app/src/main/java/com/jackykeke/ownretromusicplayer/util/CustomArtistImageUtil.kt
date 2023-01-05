package com.jackykeke.ownretromusicplayer.util

import android.content.Context
import android.content.SharedPreferences
import com.jackykeke.ownretromusicplayer.App
import com.jackykeke.ownretromusicplayer.model.Artist
import java.io.File
import java.util.*

/**
 *
 * @author keyuliang on 2023/1/5.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class CustomArtistImageUtil private constructor(context: Context){

    private val mPreferences: SharedPreferences = context.applicationContext.getSharedPreferences(
        CUSTOM_ARTIST_IMAGE_PREFS,
        Context.MODE_PRIVATE
    )

    fun hasCustomArtistImage(artist: Artist):Boolean{
        return mPreferences.getBoolean(getFileName(artist),false)
    }


    companion object{
        private const val CUSTOM_ARTIST_IMAGE_PREFS = "custom_artist_image"
        private const val FOLDER_NAME = "/custom_artist_images/"

        private var sInstance: CustomArtistImageUtil? = null

        fun getInstance(context: Context):CustomArtistImageUtil{
            if (sInstance==null){
                sInstance = CustomArtistImageUtil(context.applicationContext)
            }
            return sInstance!!
        }

        fun getFileName(artist: Artist):String{
            var artistName = artist.name
            // replace everything that is not a letter or a number with _
            // 用 _ 替换所有不是字母或数字的内容
            artistName= artistName.replace("[^a-zA-Z0-9]".toRegex(),"_")
            return String.format(Locale.US,"#%d#%s.jpeg", artist.id, artistName)
        }

        @JvmStatic
        fun getFile(artist: Artist): File {
            val dir = File(App.getContext().filesDir, FOLDER_NAME)
            return File(dir, getFileName(artist))
        }
    }


}