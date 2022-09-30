package com.jackykeke.ownretromusicplayer.auto

/**
 *
 * @author keyuliang on 2022/9/28.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
object AutoMediaIDHelper {


        // Media IDs used on browseable items of MediaBrowser
        val MEDIA_ID_EMPTY_ROOT = "__EMPTY_ROOT__"
        val MEDIA_ID_ROOT = "__ROOT__"
        val MEDIA_ID_MUSICS_BY_SEARCH = "__BY_SEARCH__" // TODO

        val MEDIA_ID_MUSICS_BY_HISTORY = "__BY_HISTORY__"
        val MEDIA_ID_MUSICS_BY_TOP_TRACKS = "__BY_TOP_TRACKS__"
        val MEDIA_ID_MUSICS_BY_SUGGESTIONS = "__BY_SUGGESTIONS__"
        val MEDIA_ID_MUSICS_BY_PLAYLIST = "__BY_PLAYLIST__"
        val MEDIA_ID_MUSICS_BY_ALBUM = "__BY_ALBUM__"
        val MEDIA_ID_MUSICS_BY_ARTIST = "__BY_ARTIST__"
        val MEDIA_ID_MUSICS_BY_ALBUM_ARTIST = "__BY_ALBUM_ARTIST__"
        val MEDIA_ID_MUSICS_BY_GENRE = "__BY_GENRE__"
        val MEDIA_ID_MUSICS_BY_SHUFFLE = "__BY_SHUFFLE__"
        val MEDIA_ID_MUSICS_BY_QUEUE = "__BY_QUEUE__"
        val RECENT_ROOT = "__RECENT__"



    private val CATEGORY_SEPARATOR = "__/__"
    private val LEAF_SEPARATOR = "__|__"

    /**
     * Create a String value that represents a playable or a browsable media.
     * <p/>
     * Encode the media browseable categories, if any, and the unique music ID, if any,
     * into a single String mediaID.
     * <p/>
     * MediaIDs are of the form <categoryType>__/__<categoryValue>__|__<musicUniqueId>, to make it
     * easy to find the category (like genre) that a music was selected from, so we
     * can correctly build the playing queue. This is specially useful when
     * one music can appear in more than one list, like "by genre -> genre_1"
     * and "by artist -> artist_1".
     *
     * @param mediaID    Unique ID for playable items, or null for browseable items.
     * @param categories Hierarchy of categories representing this item's browsing parents.
     * @return A hierarchy-aware media ID.
     */

     fun createMediaID(mediaID: String?, vararg categories: String?): String {
        val sb = StringBuilder()
        if (categories != null) {
            for (i in 0 until categories.size) {
                require(isValidCategory(categories[i])) { "Invalid category: " + categories[i] }
                sb.append(categories[i])
                if (i < categories.size - 1) {
                    sb.append(CATEGORY_SEPARATOR)
                }
            }
        }
        if (mediaID != null) {
            sb.append(LEAF_SEPARATOR)
                .append(mediaID)
        }
        return sb.toString()
    }

    fun extractCategory(mediaID: String): String? {
        val pos: Int =
            mediaID.indexOf(LEAF_SEPARATOR)
        return if (pos >= 0) {
            mediaID.substring(0, pos)
        } else mediaID
    }

    fun extractMusicID(mediaID: String): String? {
        val pos: Int =
            mediaID.indexOf(LEAF_SEPARATOR)
        return if (pos >= 0) {
            mediaID.substring(pos + LEAF_SEPARATOR.length)
        } else null
    }

    fun isBrowseable(mediaID: String): Boolean {
        return !mediaID.contains(LEAF_SEPARATOR)
    }

    private fun isValidCategory(category: String?): Boolean {
        return category == null ||
                !category.contains(CATEGORY_SEPARATOR) && !category.contains(
            LEAF_SEPARATOR
        )
    }

}