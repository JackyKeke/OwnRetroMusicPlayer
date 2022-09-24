package code.name.monkey.retromusic.model.smartplaylist


import com.jackykeke.ownretromusicplayer.App
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.model.Song
import kotlinx.parcelize.Parcelize
import org.koin.core.component.KoinComponent

@Parcelize
class HistoryPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.history),
    iconRes = R.drawable.ic_history
), KoinComponent {

    override fun songs(): List<Song> {
        return topPlayedRepository.recentlyPlayedTracks()
    }
}