package code.name.monkey.retromusic.model.smartplaylist


import com.jackykeke.ownretromusicplayer.App
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.model.Song
import kotlinx.parcelize.Parcelize

@Parcelize
class ShuffleAllPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.action_shuffle_all),
    iconRes = R.drawable.ic_shuffle
) {
    override fun songs(): List<Song> {
        return songRepository.songs()
    }
}