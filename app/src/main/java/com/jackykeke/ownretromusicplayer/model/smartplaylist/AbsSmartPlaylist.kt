package code.name.monkey.retromusic.model.smartplaylist

import androidx.annotation.DrawableRes
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.model.AbsCustomPlaylist

abstract class AbsSmartPlaylist(
    name: String,
    @DrawableRes val iconRes: Int = R.drawable.ic_queue_music
) : AbsCustomPlaylist(
    id = PlaylistIdGenerator(name, iconRes),
    name = name
)