package code.name.monkey.retromusic.model.smartplaylist


import com.jackykeke.ownretromusicplayer.App
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.model.Song
import kotlinx.parcelize.Parcelize

@Parcelize
class LastAddedPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.last_added),
    iconRes = R.drawable.ic_library_add
) {
    override fun songs(): List<Song> {
        return lastAddedRepository.recentSongs()
    }
}