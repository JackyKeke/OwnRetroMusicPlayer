package code.name.monkey.retromusic.model.smartplaylist


import com.jackykeke.ownretromusicplayer.App
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.model.Song
import kotlinx.parcelize.Parcelize

@Parcelize
class NotPlayedPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.not_recently_played),
    iconRes = R.drawable.ic_audiotrack
) {
    override fun songs(): List<Song> {
        return topPlayedRepository.notRecentlyPlayedTracks()
    }
}