package com.jackykeke.ownretromusicplayer.util

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.edit
import androidx.core.content.getSystemService
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.jackykeke.appthemehelper.util.VersionUtils
import com.jackykeke.ownretromusicplayer.*
import com.jackykeke.ownretromusicplayer.extensions.getIntRes
import com.jackykeke.ownretromusicplayer.extensions.getStringOrDefault
import com.jackykeke.ownretromusicplayer.helper.SortOrder
import com.jackykeke.ownretromusicplayer.model.CategoryInfo
import com.jackykeke.ownretromusicplayer.util.theme.ThemeMode

/**
 *
 * @author keyuliang on 2022/9/16.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object PreferenceUtil {


     private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext())

    val defaultCategories = listOf(
        CategoryInfo(CategoryInfo.Category.Home, true),
        CategoryInfo(CategoryInfo.Category.Songs, true),
        CategoryInfo(CategoryInfo.Category.Albums, true),
        CategoryInfo(CategoryInfo.Category.Artists, true),
        CategoryInfo(CategoryInfo.Category.Playlists, true),
        CategoryInfo(CategoryInfo.Category.Genres, false),
        CategoryInfo(CategoryInfo.Category.Folder, false),
        CategoryInfo(CategoryInfo.Category.Search, false)
    )

    var libraryCategory: List<CategoryInfo>
        get() {
            val gson = Gson()
            val collectionType = object : TypeToken<List<CategoryInfo>>() {}.type

            val data = sharedPreferences.getStringOrDefault(
                LIBRARY_CATEGORIES,
                gson.toJson(defaultCategories, collectionType)
            )
            return try {
                Gson().fromJson(data, collectionType)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                return defaultCategories
            }
        }
        set(value) {
            val collectionType = object : TypeToken<List<CategoryInfo?>?>() {}.type
            sharedPreferences.edit {
                putString(
                    LIBRARY_CATEGORIES,
                    Gson().toJson(value, collectionType)
                )
            }
        }


    var artistDetailSongSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ARTIST_DETAIL_SONG_SORT_ORDER,
            SortOrder.ArtistSongSortOrder.SONG_A_Z
        )
        set(value) = sharedPreferences.edit { putString(ARTIST_DETAIL_SONG_SORT_ORDER, value) }

    var playlistSortOrder
        get() = sharedPreferences.getStringOrDefault(
            PLAYLIST_SORT_ORDER,
            SortOrder.PlaylistSortOrder.PLAYLIST_A_Z
        )
        set(value) = sharedPreferences.edit { putString(PLAYLIST_SORT_ORDER, value) }


    var songSortOrder
        get() = sharedPreferences.getStringOrDefault(
            SONG_SORT_ORDER,
            SortOrder.SongSortOrder.SONG_A_Z
        )
        set(value) = sharedPreferences.edit {
            putString(SONG_SORT_ORDER, value)
        }

    val isWhiteList: Boolean
        get() = sharedPreferences.getBoolean(WHITELIST_MUSIC, false)

    var isInitializedBlacklist
        get() = sharedPreferences.getBoolean(
            INITIALIZED_BLACKLIST, false
        )
        set(value) = sharedPreferences.edit {
            putBoolean(INITIALIZED_BLACKLIST, value)
        }

    val filterLength get() = sharedPreferences.getInt(FILTER_SONG, 20)

    var albumSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ALBUM_SORT_ORDER,
            SortOrder.AlbumSortOrder.ALBUM_A_Z
        )
        set(value) = sharedPreferences.edit {
            putString(ALBUM_SORT_ORDER, value)
        }

    val albumSongSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ALBUM_SONG_SORT_ORDER,
            SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST
        )

    var albumDetailSongSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ALBUM_DETAIL_SONG_SORT_ORDER,
            SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST
        )
        set(value) = sharedPreferences.edit { putString(ALBUM_DETAIL_SONG_SORT_ORDER, value) }

    val genreSortOrder
        get() = sharedPreferences.getStringOrDefault(
            GENRE_SORT_ORDER,
            SortOrder.GenreSortOrder.GENRE_A_Z
        )

    val lastAddedCutoff: Long
        get() {
            val calendarUtil = CalendarUtil()
            val interval =
                when (sharedPreferences.getStringOrDefault(LAST_ADDED_CUTOFF, "this_month")) {
                    "today" -> calendarUtil.elapsedToday
                    "this_week" -> calendarUtil.elapsedWeek
                    "past_three_months" -> calendarUtil.getElapsedMonths(3)
                    "this_year" -> calendarUtil.elapsedYear
                    "this_month" -> calendarUtil.elapsedMonth
                    else -> calendarUtil.elapsedMonth
                }
            return (System.currentTimeMillis() - interval) / 1000

        }

    val isFullScreenMode
        get() = sharedPreferences.getBoolean(
            TOGGLE_FULL_SCREEN, false
        )

    fun getRecentlyPlayedCutoffTimeMillis(): Long {
        val calendarUtil = CalendarUtil()
        val interval: Long = when (sharedPreferences.getString(RECENTLY_PLAYED_CUTOFF, "")) {

            "today" -> calendarUtil.elapsedToday
            "this_week" -> calendarUtil.elapsedWeek
            "past_three_months" -> calendarUtil.getElapsedMonths(3)
            "this_year" -> calendarUtil.elapsedYear
            "this_month" -> calendarUtil.elapsedMonth
            else -> calendarUtil.elapsedMonth
        }
        return System.currentTimeMillis() - interval
    }

    private val isBlackMode
        get() = sharedPreferences.getBoolean(BLACK_THEME, false)

    fun getGeneralThemeValue(isSystemDark: Boolean): ThemeMode {
        val themeMode: String = sharedPreferences.getStringOrDefault(GENERAL_THEME, "auto")
        return if (isBlackMode && isSystemDark && themeMode != "light") {
            ThemeMode.DARK
        } else {
            if (isBlackMode && themeMode == "dark") {
                ThemeMode.BLACK
            } else {
                when (themeMode) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    "auto" -> ThemeMode.AUTO
                    else -> ThemeMode.AUTO
                }
            }
        }
    }

    val materialYou
        get() = sharedPreferences.getBoolean(MATERIAL_YOU, VersionUtils.hasS())

    val isScreenOnEnabled
        get() = sharedPreferences.getBoolean(KEEP_SCREEN_ON, false)

    val isCustomFont
        get() = sharedPreferences.getBoolean(CUSTOM_FONT, false)

    val languageCode
        get() = sharedPreferences.getString(LANGUAGE_NAME, "auto") ?: "auto"

    var albumArtistsOnly
        get() = sharedPreferences.getBoolean(
            ALBUM_ARTISTS_ONLY,
            false
        )
        set(value) = sharedPreferences.edit { putBoolean(ALBUM_ARTISTS_ONLY, value) }

    val isExpandPanel get() = sharedPreferences.getBoolean(EXPAND_NOW_PLAYING_PANEL, false)

    val isIgnoreMediaStoreArtwork
        get() = sharedPreferences.getBoolean(
            IGNORE_MEDIA_STORE_ARTWORK,
            false
        )

    val crossFadeDuration get() = sharedPreferences.getInt(CROSS_FADE_DURATION, 0)

    val isCrossfadeEnabled get() = crossFadeDuration > 0

    val isAudioFocusEnabled
        get() = sharedPreferences.getBoolean(
            MANAGE_AUDIO_FOCUS, false
        )

    var playbackSpeed
        get() = sharedPreferences
            .getFloat(PLAYBACK_SPEED, 1F)
        set(value) = sharedPreferences.edit { putFloat(PLAYBACK_SPEED, value) }

    var playbackPitch
        get() = sharedPreferences
            .getFloat(PLAYBACK_PITCH, 1F)
        set(value) = sharedPreferences.edit { putFloat(PLAYBACK_PITCH, value) }

    val isGapLessPlayback
        get() = sharedPreferences.getBoolean(
            GAP_LESS_PLAYBACK, false
        )

    var audioFadeDuration
        get() = sharedPreferences
            .getInt(AUDIO_FADE_DURATION, 0)
        set(value) = sharedPreferences.edit { putInt(AUDIO_FADE_DURATION, value) }

    var isClassicNotification
        get() = sharedPreferences.getBoolean(CLASSIC_NOTIFICATION, false)
        set(value) = sharedPreferences.edit { putBoolean(CLASSIC_NOTIFICATION, value) }


    var isColoredNotification
        get() = sharedPreferences.getBoolean(
            COLORED_NOTIFICATION, true
        )
        set(value) = sharedPreferences.edit {
            putBoolean(COLORED_NOTIFICATION, value)
        }

    val isHeadsetPlugged
        get() = sharedPreferences.getBoolean(
            TOGGLE_HEADSET, false
        )

    fun registerOnSharedPreferenceChangedListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

    fun unregisterOnSharedPreferenceChangedListener(
        changeListener: SharedPreferences.OnSharedPreferenceChangeListener,
    ) = sharedPreferences.unregisterOnSharedPreferenceChangeListener(changeListener)

    val isAlbumArtOnLockScreen
        get() = sharedPreferences.getBoolean(
            ALBUM_ART_ON_LOCK_SCREEN, false
        )

    val isBlurredAlbumArt
        get() = sharedPreferences.getBoolean(
            BLURRED_ALBUM_ART, false
        )

    val isBluetoothSpeaker
        get() = sharedPreferences.getBoolean(
            BLUETOOTH_PLAYBACK, false
        )

    val pauseHistory: Boolean
        get() = sharedPreferences.getBoolean(
            PAUSE_HISTORY,
            false
        )

    val isVolumeVisibilityMode
        get() = sharedPreferences.getBoolean(
            TOGGLE_VOLUME, false
        )

    val isPauseOnZeroVolume get() = sharedPreferences.getBoolean(PAUSE_ON_ZERO_VOLUME, false)


    val isAdaptiveColor
        get() = sharedPreferences.getBoolean(
            ADAPTIVE_COLOR_APP, false
        )

    var safSdCardUri
        get() = sharedPreferences.getStringOrDefault(SAF_SDCARD_URI, "")
        set(value) = sharedPreferences.edit {
            putString(SAF_SDCARD_URI, value)
        }


    var songGridSize
        get() = sharedPreferences.getInt(
            SONG_GRID_SIZE,
            App.getContext().getIntRes(R.integer.default_list_columns)
        )
        set(value) = sharedPreferences.edit {
            putInt(SONG_GRID_SIZE, value)
        }

    var songGridSizeLand
        get() = sharedPreferences.getInt(
            SONG_GRID_SIZE_LAND,
            App.getContext().getIntRes(R.integer.default_grid_columns_land)
        )
        set(value) = sharedPreferences.edit {
            putInt(SONG_GRID_SIZE_LAND, value)
        }

    private val autoDownloadImagesPolicy
        get() = sharedPreferences.getStringOrDefault(
            AUTO_DOWNLOAD_IMAGES_POLICY,
            "only_wifi"
        )

    @Suppress("deprecation")
    fun isAllowedToDownloadMetadata(context: Context):Boolean{
        return when (autoDownloadImagesPolicy){
            "always" -> true
            "only_wifi" -> {
                val connectivityManager = context.getSystemService<ConnectivityManager>()
                if (VersionUtils.hasMarshmallow()){
                    val network = connectivityManager?.activeNetwork
                    val capabilities = connectivityManager?.getNetworkCapabilities(network)
                    capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                }else{
                    val netInfo = connectivityManager?.activeNetworkInfo
                    netInfo != null && netInfo.type == ConnectivityManager.TYPE_WIFI && netInfo.isConnectedOrConnecting
                }
            }
            "never" -> false
            else -> false
        }
    }

}