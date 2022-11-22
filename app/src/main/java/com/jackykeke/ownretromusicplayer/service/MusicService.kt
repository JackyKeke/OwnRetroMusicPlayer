package com.jackykeke.ownretromusicplayer.service

import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.bluetooth.BluetoothDevice
import android.content.*
import android.content.pm.ServiceInfo
import android.database.ContentObserver
import android.os.*
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.content.edit
import androidx.media.MediaBrowserServiceCompat
import androidx.preference.PreferenceManager
import code.name.monkey.retromusic.appwidgets.AppWidgetCircle
import com.jackykeke.ownretromusicplayer.appwidget.AppWidgetCard
import code.name.monkey.retromusic.appwidgets.AppWidgetClassic
import code.name.monkey.retromusic.appwidgets.AppWidgetSmall
import com.jackykeke.appthemehelper.util.VersionUtils
import com.jackykeke.ownretromusicplayer.*
import com.jackykeke.ownretromusicplayer.appwidget.AppWidgetBig
import com.jackykeke.ownretromusicplayer.appwidget.AppWidgetMD3
import com.jackykeke.ownretromusicplayer.appwidget.AppWidgetText
import com.jackykeke.ownretromusicplayer.auto.AutoMediaIDHelper
import com.jackykeke.ownretromusicplayer.auto.AutoMusicProvider
import com.jackykeke.ownretromusicplayer.extensions.toMediaSessionQueue
import com.jackykeke.ownretromusicplayer.extensions.uri
import com.jackykeke.ownretromusicplayer.model.Song
import com.jackykeke.ownretromusicplayer.model.Song.Companion.emptySong
import com.jackykeke.ownretromusicplayer.providers.HistoryStore
import com.jackykeke.ownretromusicplayer.providers.MusicPlaybackQueueStore
import com.jackykeke.ownretromusicplayer.providers.SongPlayCountStore
import com.jackykeke.ownretromusicplayer.service.notification.PlayingNotification
import com.jackykeke.ownretromusicplayer.service.playback.Playback
import com.jackykeke.ownretromusicplayer.util.MusicUtil
import com.jackykeke.ownretromusicplayer.util.PackageValidator
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil.crossFadeDuration
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil.playbackPitch
import com.jackykeke.ownretromusicplayer.util.PreferenceUtil.playbackSpeed
import com.jackykeke.ownretromusicplayer.volume.OnAudioVolumeChangedListener
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.koin.java.KoinJavaComponent.get
import java.lang.Runnable
import java.util.ArrayList

/**
 *
 * @author keyuliang on 2022/9/28.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class MusicService : MediaBrowserServiceCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    Playback.PlaybackCallbacks, OnAudioVolumeChangedListener {

    private val musicBind: IBinder = MusicBinder()

    @JvmField
    var nextPosition = -1

    @JvmField
    var pendingQuit = false

    private var uiThreadHandler: Handler? = null

    private lateinit var playbackManager: PlaybackManager

    val playback: Playback? get() = playbackManager.playback


    private var mPackageValidator: PackageValidator? = null

    private val mMusicProvider = get<AutoMusicProvider>(AutoMusicProvider::class.java)

    private lateinit var storage: PersistentStorage
    private var trackEndedByCrossfade = false
    private val serviceScope = CoroutineScope(Job() + Dispatchers.Main)

    inner class MusicBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }


    @JvmField
    var position = -1
    private val appWidgetBig = AppWidgetBig.instance
    private val appWidgetCard = AppWidgetCard.instance
    private val appWidgetClassic = AppWidgetClassic.instance
    private val appWidgetSmall = AppWidgetSmall.instance
    private val appWidgetText = AppWidgetText.instance
    private val appWidgetMd3 = AppWidgetMD3.instance
    private val appWidgetCircle = AppWidgetCircle.instance

    private val widgetIntentReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val command = intent.getStringExtra(EXTRA_APP_WIDGET_NAME)

            val ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            if (command != null) {
                when (command) {
                    AppWidgetClassic.NAME -> {
                        appWidgetClassic.performUpdate(this@MusicService, ids)
                    }
                    AppWidgetSmall.NAME -> {
                        appWidgetSmall.performUpdate(this@MusicService, ids)
                    }
                    AppWidgetBig.NAME -> {
                        appWidgetBig.performUpdate(this@MusicService, ids)
                    }
                    AppWidgetCard.NAME -> {
                        appWidgetCard.performUpdate(this@MusicService, ids)
                    }
                    AppWidgetText.NAME -> {
                        appWidgetText.performUpdate(this@MusicService, ids)
                    }
                    AppWidgetMD3.NAME -> {
                        appWidgetMd3.performUpdate(this@MusicService, ids)
                    }
                    AppWidgetCircle.NAME -> {
                        appWidgetCircle.performUpdate(this@MusicService, ids)
                    }
                }

            }

        }

    }


    private val bluetoothConnectedIntentFilter = IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED)
    private var bluetoothConnectedRegistered = false
    private val headsetReceiverIntentFilter =IntentFilter(Intent.ACTION_HEADSET_PLUG)
    private var headsetReceiverRegistered = false
    private var mediaSession: MediaSessionCompat? = null
    private lateinit var mediaStoreObserver: ContentObserver
    private var musicPlayerHandlerThread: HandlerThread? = null
    private var notHandledMetaChangedForCurrentTrack = false
    private var originalPlayingQueue = ArrayList<Song>()


    @JvmField
    var playingQueue = ArrayList<Song>()

    private var playerHandler : Handler? =null

    private var playingNotification : PlayingNotification? =null

    private var isForeground = false

    private val  updateFavoriteReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {

            isCurrentFavorite{
                isFavorite ->
                if (!isForeground){
                    playingNotification?.updateMetadata(currentSong){
                        playingNotification?.setPlaying(isPlaying)
                        playingNotification?.updateFavorite(isFavorite)
                        startForegroundOrNotify()
                    }

                }else{
                    playingNotification?.updateFavorite(isFavorite)
                    startForegroundOrNotify()
                }

                appWidgetCircle.notifyChange(this@MusicService, FAVORITE_STATE_CHANGED)
            }
        }
    }

    private fun startForegroundOrNotify() {
         if (playingNotification!=null && currentSong.id != -1L){
             if (isForeground && !isPlaying){

                 // This makes the notification dismissible
                 // We can't call stopForeground(false) on A12 though, which may result in crashes
                 // when we call startForeground after that e.g. when Alarm goes off,
                 if (!VersionUtils.hasS()){
                     stopForeground(false)
                     isForeground = false
                 }
             }

             if (!isForeground && isPlaying){
                 // Specify that this is a media service, if supported.
                if (VersionUtils.hasQ()){
                    startForeground(PlayingNotification.NOTIFICATION_ID,
                    playingNotification!!.build(),ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
                }else{
                    startForeground(
                        PlayingNotification.NOTIFICATION_ID,
                        playingNotification!!.build()
                    )
                }
                 isForeground = true

             }else{
                 // If we are already in foreground just update the notification
                 notificationManager?.notify(
                     PlayingNotification.NOTIFICATION_ID, playingNotification!!.build()
                 )
             }
         }
    }

    private var notificationManager: NotificationManager? = null

    private val lockScreenReceiver = object  : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
             val lockIntent  = Intent (context,LockScreenActivity::class.java)
            lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(lockIntent)
        }
    }

    private var queuesRestored = false

    var repeatMode = 0
        private set(value) {
            when(value){
                REPEAT_MODE_NONE, REPEAT_MODE_ALL, REPEAT_MODE_THIS ->{
                    field = value
                    PreferenceManager.getDefaultSharedPreferences(this).edit {
                        putInt(SAVED_REPEAT_MODE,value)
                    }
                    prepareNext()
                    handleAndSendChangeInternal(REPEAT_MODE_CHANGED)
                }
            }
        }

    private fun handleAndSendChangeInternal(what: String) {
        handleChangeInternal(what)
        sendChangeInternal(what)
    }

    private fun handleChangeInternal(what: String) {
        when(what){
            PLAY_STATE_CHANGED ->{
                updateMediaSessionPlaybackState()
                val isPlaying = isPlaying
                if (!isPlaying && songProgressMillis >0){
                    savePositionInTrack()
                }
                songPlayCountHelper.notifyPlayStateChanged(isPlaying)
                startForegroundOrNotify()
            }

            FAVORITE_STATE_CHANGED -> {
                isCurrentFavorite {
                    isFavorite ->
                    playingNotification?.updateFavorite(isFavorite)
                    startForegroundOrNotify()
                }
            }

            META_CHANGED -> {
                playingNotification?.updateMetadata(currentSong){
                    startForegroundOrNotify()
                }
                isCurrentFavorite {
                    isFavorite ->
                    playingNotification?.updateFavorite(isFavorite)
                    startForegroundOrNotify()
                }
                // We must call updateMediaSessionPlaybackState after the load of album art is completed
                // if we are loading it or it won't be updated in the notification
                updateMediaSessionMetaData(::updateMediaSessionPlaybackState)
                savePosition()
                savePositionInTrack()
                serviceScope.launch(IO) {
                    val currentSong= currentSong
                    HistoryStore.getInstance(this@MusicService).addSongId(currentSong.id)
                    if (songPlayCountHelper.shouldBumpPlayCount()){
                        SongPlayCountStore.getInstance(this@MusicService).bumpPlayCount(songPlayCountHelper.song.id)
                    }
                    songPlayCountHelper.notifySongChanged(currentSong)
                    storage.saveSong(currentSong)
                }
            }

            QUEUE_CHANGED -> {
                mediaSession?.setQueueTitle(getString(R.string.now_playing_queue))
                mediaSession?.setQueue(playingQueue.toMediaSessionQueue())
                updateMediaSessionMetaData(::updateMediaSessionPlaybackState) // because playing queue size might have changed
                saveQueues()
                if (playingQueue.size > 0) {
                    prepareNext()
                } else {
                    stopForegroundAndNotification()
                }
            }
        }
    }

    private fun saveQueues() {
         serviceScope.launch(IO) {
             MusicPlaybackQueueStore.getInstance(this@MusicService)
                 .saveQueues(playingQueue, originalPlayingQueue)
         }
    }

    private fun sendChangeInternal(what: String){
        sendBroadcast(Intent(what))
        appWidgetBig.notifyChange(this,what)
        appWidgetClassic.notifyChange(this, what)
        appWidgetSmall.notifyChange(this, what)
        appWidgetCard.notifyChange(this, what)
        appWidgetText.notifyChange(this, what)
        appWidgetMd3.notifyChange(this, what)
        appWidgetCircle.notifyChange(this, what)
    }


    private fun savePosition() {
        PreferenceManager.getDefaultSharedPreferences(this).edit {
            putInt(SAVED_POSITION, getPosition())
        }
    }
    private fun savePositionInTrack() {
         PreferenceManager.getDefaultSharedPreferences(this).edit {
             putInt(SAVED_POSITION_IN_TRACK,songProgressMillis)
         }
    }


    private fun isCurrentFavorite(completion: (isFavorite: Boolean) -> Unit ) {
         serviceScope.launch(IO) {
             val  isFavorite = MusicUtil.isFavorite(currentSong)
             withContext(Main){
                 completion(isFavorite)
             }
         }
    }

    private fun setupMediaSession(){
        val mediaButtonReceiverComponentName = ComponentName(applicationContext,
            MediaButtonIntentReceiver::class.java)

        val mediaButtonIntent =Intent(Intent.ACTION_MEDIA_BUTTON)
    }

    val isPlaying: Boolean
        get() = playbackManager.isPlaying


    val currentSong: Song
        get() = getSongAt(getPosition())

    private fun getPosition(): Int {
        return position
    }

    private fun setPosition(position: Int) {
        openTrackAndPrepareNextAt(position) {

        }
    }

    private var throttledSeekHandler: ThrottledSeekHandler? = null


    @Synchronized
    private fun openTrackAndPrepareNextAt(position: Int, completion: (success: Boolean) -> Unit) {

        this.position = position
        openCurrent {

        }
    }

    @Synchronized
    private fun openCurrent(completion: (success: Boolean) -> Unit) {

        val force = if (!trackEndedByCrossfade) {
            true
        } else {
            trackEndedByCrossfade = false
            false
        }
        playbackManager.setDataSource(currentSong, force) { success ->
            completion(success)
        }
    }

    fun switchToLocalPlayback() {
        playbackManager.switchToLocalPlayback(this::restorePlaybackState)
    }

    private fun restorePlaybackState(wasPlaying: Boolean, progress: Int) {

        playbackManager.setCallbacks(this)

        openTrackAndPrepareNextAt(position) { success ->
            if (success) {
                seek(progress)
                if (wasPlaying) {
                    play()
                } else {
                    pause()
                }
            }
        }
        playbackManager.setCrossFadeDuration(crossFadeDuration)

    }

    @Synchronized
    fun seek(millis: Int): Int {
        return try {
            val newPosition = playbackManager.seek(millis)
            throttledSeekHandler?.notifySeek()
            newPosition
        } catch (e: Exception) {
            -1
        }

    }

    private fun getSongAt(position: Int): Song {
        return if ((position >= 0) && (position < playingQueue.size)) {
            playingQueue[position]
        } else {
            emptySong
        }
    }


    private val isLastTrack: Boolean
        get() = getPosition() == playingQueue.size - 1

    var repeatMode = 0
        private set(value) {
            when (value) {
                REPEAT_MODE_NONE, REPEAT_MODE_ALL, REPEAT_MODE_THIS -> {
                    field = value
                    PreferenceManager.getDefaultSharedPreferences(this).edit {
                        putInt(SAVED_REPEAT_MODE, value)
                    }
                    prepareNext()
                    handleAndSendChangeInternal(REPEAT_MODE_CHANGED)
                }
            }
        }

    private fun prepareNext() {
        prepareNextImpl()
    }

    @Synchronized
    fun prepareNextImpl() {
        try {
            val nextPosition = getNextPosition(false)
            playbackManager.setNextDataSource(getSongAt(nextPosition).uri.toString())
            this.nextPosition = nextPosition
        } catch (ignored: Exception) {
        }
    }

    private fun getNextPosition(force: Boolean): Int {
        var position = getPosition() + 1
        when (repeatMode) {
            REPEAT_MODE_ALL -> if (isLastTrack) {
                position = 0
            }
            REPEAT_MODE_THIS -> if (force) {
                if (isLastTrack) {
                    position = 0
                }
            } else {
                position -= 1
            }
            REPEAT_MODE_NONE -> if (isLastTrack) {
                position -= 1
            }
            else -> if (isLastTrack) {
                position -= 1
            }
        }
        return position
    }


    val nextSong: Song?
        get() = if (isLastTrack && repeatMode == REPEAT_MODE_NONE) {
            null
        } else {
            getSongAt(getNextPosition(false))
        }


    override fun onCreate() {
        super.onCreate()

        uiThreadHandler = Handler(Looper.getMainLooper())

    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {

        // Check origin to ensure we're not allowing any arbitrary app to browse app contents
        // 检查来源，以确保我们不允许任何任意的应用程序浏览应用程序内容
        return if (!mPackageValidator!!.isKnownCaller(clientPackageName, clientUid)) {
            // Request from an untrusted package: return an empty browser root
            //来自不受信任的包的请求:返回一个空的浏览器根
            BrowserRoot(AutoMediaIDHelper.MEDIA_ID_EMPTY_ROOT, null)
        } else {
            /**
             * By default return the browsable root. Treat the EXTRA_RECENT flag as a special case
             * and return the recent root instead.
             */
            val isRecentRequest = rootHints?.getBoolean(BrowserRoot.EXTRA_RECENT) ?: false
            val browserRootPath =
                if (isRecentRequest) AutoMediaIDHelper.RECENT_ROOT else AutoMediaIDHelper.MEDIA_ID_ROOT
            BrowserRoot(browserRootPath, null)
        }
    }


    override fun onLoadChildren(
        parentId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        if (parentId == AutoMediaIDHelper.RECENT_ROOT) {
            result.sendResult(listOf(storage.recentSong()))
        } else {
            result.sendResult(mMusicProvider.getChildren(parentId, resources))
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {

        when (key) {
            PLAYBACK_SPEED, PLAYBACK_PITCH -> {

                updateMediaSessionPlaybackState()
                playbackManager.setPlaybackSpeedPitch(playbackSpeed, playbackPitch)
            }

            CROSS_FADE_DURATION -> {
                val progress = songProgressMillis
                val wasPlaying = isPlaying
                if (playbackManager.maybeSwitchToCrossFade(crossFadeDuration)) {
                    restorePlaybackState(wasPlaying, progress)
                } else {
                    playbackManager.setCrossFadeDuration(crossFadeDuration)
                }

            }

            ALBUM_ART_ON_LOCK_SCREEN, BLURRED_ALBUM_ART -> updateMediaSessionMetaData(::updateMediaSessionPlaybackState)
            COLORED_NOTIFICATION -> {
                playingNotification?.updateMetadata(currentSong) {
                    playingNotification?.setPlaying(isPlaying)
                    startForegroundOrNotify()
                }
            }
            CLASSIC_NOTIFICATION -> {
                updateNotification()
                playingNotification?.updateMetadata(currentSong) {
                    playingNotification?.setPlaying(isPlaying)
                    startForegroundOrNotify()
                }
            }
            TOGGLE_HEADSET -> registerHeadsetEvents()
        }
    }

    private fun updateMediaSessionMetaData(onCompletion: () -> Unit) {

        Log.i(TAG, "onResourceReady: ")
        val song = currentSong
        if (song.id == -1L) {
            mediaSession?.setMetadata(null)
            return
        }

    }

    override fun onTrackWentToNext() {
        TODO("Not yet implemented")
    }

    override fun onTrackEnded() {
        TODO("Not yet implemented")
    }

    override fun onTrackEndedWithCrossfade() {
        TODO("Not yet implemented")
    }

    override fun onPlayStateChanged() {
        TODO("Not yet implemented")
    }

    override fun onAudioVolumeChanged(currentVolume: Int, maxVolume: Int) {
        TODO("Not yet implemented")
    }

    val songProgressMillis: Int
        get() = playbackManager.songProgressMillis

    fun updateMediaSessionPlaybackState() {
        val stateBuilder = PlaybackStateCompat.Builder()
            .setActions(MEDIA_SESSION_ACTIONS)
            .setState(
                if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                songProgressMillis.toLong(),
                playbackSpeed
            )

        setCustomAction(stateBuilder)
    }

    private fun setCustomAction(stateBuilder: PlaybackStateCompat.Builder) {

        var repeatIcon = R.drawable.ic_repeat
        if (repeatMode == REPEAT_MODE_THIS) {
            repeatIcon = R.drawable.ic_repeat_one
        } else if (repeatMode == REPEAT_MODE_ALL) {
            repeatIcon = R.drawable.ic_repeat_white_circle
        }

        stateBuilder.addCustomAction(
            PlaybackStateCompat.CustomAction.Builder(
                CYCLE_REPEAT, getString(R.string.action_cycle_repeat), repeatIcon
            )
                .build()
        )

        val shuffleIcon =
            if (getShuffleMode() == SHUFFLE_MODE_NONE) R.drawable.ic_shuffle_off_circled
            else R.drawable.ic_shuffle_on_circled

        stateBuilder.addCustomAction(
            PlaybackStateCompat.CustomAction.Builder(
                TOGGLE_SHUFFLE, getString(R.string.action_toggle_shuffle), shuffleIcon
            )
                .build()
        )
    }

    @JvmField
    var shuffleMode = 0
    private val songPlayCountHelper = SongPlayCountHelper()

    private fun getShuffleMode(): Int {
        return shuffleMode
    }

    fun setShuffleMode(shuffleMode: Int) {
        PreferenceManager.getDefaultSharedPreferences(this).edit {
            putInt(SAVED_SHUFFLE_MODE, shuffleMode)
        }
        when (shuffleMode) {
            SHUFFLE_MODE_SHUFFLE -> {

            }
        }
    }

    fun runOnUiThread(runnable: Runnable?) {
        uiThreadHandler?.post(runnable!!)
    }


    companion object {
        val TAG: String = MusicService::class.java.simpleName
        const val RETRO_MUSIC_PACKAGE_NAME = "code.name.monkey.retromusic"
        const val MUSIC_PACKAGE_NAME = "com.android.music"
        const val ACTION_TOGGLE_PAUSE = "$RETRO_MUSIC_PACKAGE_NAME.togglepause"
        const val ACTION_PLAY = "$RETRO_MUSIC_PACKAGE_NAME.play"
        const val ACTION_PLAY_PLAYLIST = "$RETRO_MUSIC_PACKAGE_NAME.play.playlist"
        const val ACTION_PAUSE = "$RETRO_MUSIC_PACKAGE_NAME.pause"
        const val ACTION_STOP = "$RETRO_MUSIC_PACKAGE_NAME.stop"
        const val ACTION_SKIP = "$RETRO_MUSIC_PACKAGE_NAME.skip"
        const val ACTION_REWIND = "$RETRO_MUSIC_PACKAGE_NAME.rewind"
        const val ACTION_QUIT = "$RETRO_MUSIC_PACKAGE_NAME.quitservice"
        const val ACTION_PENDING_QUIT = "$RETRO_MUSIC_PACKAGE_NAME.pendingquitservice"
        const val INTENT_EXTRA_PLAYLIST = RETRO_MUSIC_PACKAGE_NAME + "intentextra.playlist"
        const val INTENT_EXTRA_SHUFFLE_MODE =
            "$RETRO_MUSIC_PACKAGE_NAME.intentextra.shufflemode"
        const val APP_WIDGET_UPDATE = "$RETRO_MUSIC_PACKAGE_NAME.appreciate"
        const val EXTRA_APP_WIDGET_NAME = RETRO_MUSIC_PACKAGE_NAME + "app_widget_name"

        // Do not change these three strings as it will break support with other apps (e.g. last.fm
        // scrobbling)
        const val META_CHANGED = "$RETRO_MUSIC_PACKAGE_NAME.metachanged"
        const val QUEUE_CHANGED = "$RETRO_MUSIC_PACKAGE_NAME.queuechanged"
        const val PLAY_STATE_CHANGED = "$RETRO_MUSIC_PACKAGE_NAME.playstatechanged"
        const val FAVORITE_STATE_CHANGED = "$RETRO_MUSIC_PACKAGE_NAME.favoritestatechanged"
        const val REPEAT_MODE_CHANGED = "$RETRO_MUSIC_PACKAGE_NAME.repeatmodechanged"
        const val SHUFFLE_MODE_CHANGED = "$RETRO_MUSIC_PACKAGE_NAME.shufflemodechanged"
        const val MEDIA_STORE_CHANGED = "$RETRO_MUSIC_PACKAGE_NAME.mediastorechanged"
        const val CYCLE_REPEAT = "$RETRO_MUSIC_PACKAGE_NAME.cyclerepeat"
        const val TOGGLE_SHUFFLE = "$RETRO_MUSIC_PACKAGE_NAME.toggleshuffle"
        const val TOGGLE_FAVORITE = "$RETRO_MUSIC_PACKAGE_NAME.togglefavorite"
        const val SAVED_POSITION = "POSITION"
        const val SAVED_POSITION_IN_TRACK = "POSITION_IN_TRACK"
        const val SAVED_SHUFFLE_MODE = "SHUFFLE_MODE"
        const val SAVED_REPEAT_MODE = "REPEAT_MODE"
        const val SHUFFLE_MODE_NONE = 0
        const val SHUFFLE_MODE_SHUFFLE = 1
        const val REPEAT_MODE_NONE = 0
        const val REPEAT_MODE_ALL = 1
        const val REPEAT_MODE_THIS = 2
        private const val MEDIA_SESSION_ACTIONS = (PlaybackStateCompat.ACTION_PLAY
                or PlaybackStateCompat.ACTION_PAUSE
                or PlaybackStateCompat.ACTION_PLAY_PAUSE
                or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                or PlaybackStateCompat.ACTION_STOP
                or PlaybackStateCompat.ACTION_SEEK_TO)
    }

}