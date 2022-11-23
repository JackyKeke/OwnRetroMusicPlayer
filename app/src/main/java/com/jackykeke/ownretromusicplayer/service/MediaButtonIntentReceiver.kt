package com.jackykeke.ownretromusicplayer.service

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.PowerManager
import android.util.Log
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.media.session.MediaButtonReceiver
import com.jackykeke.ownretromusicplayer.BuildConfig
import com.jackykeke.ownretromusicplayer.service.MusicService.*
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.ACTION_PAUSE
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.ACTION_PLAY
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.ACTION_REWIND
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.ACTION_SKIP
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.ACTION_STOP
import com.jackykeke.ownretromusicplayer.service.MusicService.Companion.ACTION_TOGGLE_PAUSE

/**
 *
 * @author keyuliang on 2022/11/23.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
public class MediaButtonIntentReceiver :MediaButtonReceiver(){

    companion object {
        val TAG: String = MediaButtonIntentReceiver::class.java.simpleName
        private val DEBUG = BuildConfig.DEBUG
        private const val MSG_HEADSET_DOUBLE_CLICK_TIMEOUT = 2

        private const val DOUBLE_CLICK = 400

        private var wakeLock: PowerManager.WakeLock? = null
        private var mClickCounter = 0
        private var mLastClickTime: Long = 0

        private val mHandler = object :Handler(Looper.getMainLooper()){

            override fun handleMessage(msg: Message) {

                when(msg.what){

                    MSG_HEADSET_DOUBLE_CLICK_TIMEOUT ->{
                        val clickCount = msg.arg1

                        if (DEBUG) Log.v(TAG, "Handling headset click, count = $clickCount")
                        val command = when(clickCount){
                            1 -> ACTION_TOGGLE_PAUSE
                            2 -> ACTION_SKIP
                            3 -> ACTION_REWIND
                            else -> null
                        }
                        if (command != null) {
                            val context = msg.obj as Context
                            startService(context, command)
                        }
                    }
                }
                releaseWakeLockIfHandlerIdle()
            }
        }

        private fun startService(context: Context, command: String) {

            val intent = Intent(context,MusicService::class.java)
            intent.action = command
            try {
                // IMPORTANT NOTE: (kind of a hack)
                // on Android O and above the following crashes when the app is not running
                // there is no good way to check whether the app is running so we catch the exception
                // we do not always want to use startForegroundService() because then one gets an ANR
                // if no notification is displayed via startForeground()
                // according to Play analytics this happens a lot, I suppose for example if command = PAUSE
                //重要提示:(一种hack)
                //
                //在Android O及以上系统中，当应用程序未运行时，以下程序将崩溃
                //
                //没有好的方法来检查应用程序是否在运行，所以我们捕获了异常
                //
                //我们并不总是想使用startForegroundService()，因为这样就会得到ANR
                //
                //如果没有通过start前台()显示通知
                //
                //根据游戏分析，这种情况经常发生，例如if command = PAUSE
                context.startService(intent)
            } catch (ignored: IllegalStateException) {
                ContextCompat.startForegroundService(context, intent)
            }
        }

        private fun releaseWakeLockIfHandlerIdle() {
             if (mHandler.hasMessages(MSG_HEADSET_DOUBLE_CLICK_TIMEOUT)) {
                 if (DEBUG) Log.v(TAG, "Handler still has messages pending, not releasing wake lock")
                 return
             }
            if (wakeLock != null) {
                if (DEBUG) Log.v(TAG, "Releasing wake lock")
                wakeLock!!.release()
                wakeLock = null
            }
        }

        //媒体按钮的事件分发
        fun handleIntent(context: Context, intent: Intent): Boolean {
            println("Intent Action: ${intent.action}")
            val intentAction = intent.action
            if (Intent.ACTION_MEDIA_BUTTON == intentAction){
                val event = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)?:return false

                val keycode = event.keyCode
                val action  = event.action
                val eventTime = if (event.eventTime !=0L)
                    event.eventTime else
                        System.currentTimeMillis()

                var command :String ?=null
                when(keycode){
                    KeyEvent.KEYCODE_MEDIA_STOP -> command = ACTION_STOP

                    KeyEvent.KEYCODE_HEADSETHOOK,
                    KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> command = ACTION_TOGGLE_PAUSE

                    KeyEvent.KEYCODE_MEDIA_NEXT -> command = ACTION_SKIP
                    KeyEvent.KEYCODE_MEDIA_PREVIOUS -> command = ACTION_REWIND
                    KeyEvent.KEYCODE_MEDIA_PAUSE -> command = ACTION_PAUSE
                    KeyEvent.KEYCODE_MEDIA_PLAY -> command = ACTION_PLAY
                }

                if (command != null){

                    if (action == KeyEvent.ACTION_DOWN) {
                        if (event.repeatCount == 0) {
                            // Only consider the first event in a sequence, not the repeat events,
                            // so that we don't trigger in cases where the first event went to
                            // a different app (e.g. when the user ends a phone call by
                            // long pressing the headset button)

                            // The service may or may not be running, but we need to send it
                            // a command.
                            if (keycode == KeyEvent.KEYCODE_HEADSETHOOK || keycode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                                if (eventTime - mLastClickTime >= DOUBLE_CLICK) {
                                    mClickCounter = 0
                                }

                                mClickCounter++
                                if (DEBUG) Log.v(TAG, "Got headset click, count = $mClickCounter")
                                mHandler.removeMessages(MSG_HEADSET_DOUBLE_CLICK_TIMEOUT)

                                val msg = mHandler.obtainMessage(
                                    MSG_HEADSET_DOUBLE_CLICK_TIMEOUT, mClickCounter, 0, context
                                )

                                val delay = (if (mClickCounter < 3) DOUBLE_CLICK else 0).toLong()
                                if (mClickCounter >= 3) {
                                    mClickCounter = 0
                                }
                                mLastClickTime = eventTime
                                acquireWakeLockAndSendMessage(context, msg, delay)
                            } else {
                                startService(context, command)
                            }
                            return true
                        }
                    }


                }


            }
            return false
        }

        private fun acquireWakeLockAndSendMessage(context: Context, msg: Message, delay: Long) {
            if(wakeLock == null){
                val appContext = context.applicationContext
                val pm = appContext.getSystemService<PowerManager>()
                wakeLock = pm?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "RetroMusicApp:Wakelock headset button")
                //设置这个WakeLock是否被引用计数。
                //唤醒锁默认情况下是引用计数的。如果唤醒锁是引用计数的，那么对acquire()的每次调用必须与对release()的相同数量的调用相平衡。如果一个唤醒锁没有被引用计数，那么一个release()调用就足以撤销之前所有acquire()调用的效果。
                //参数:
                //value - True表示唤醒锁引用计数，false表示唤醒锁不引用计数。
                wakeLock!!.setReferenceCounted(false)
            }

            if(DEBUG) Log.v(TAG, "Acquiring wake lock and sending " + msg.what)

            wakeLock!!.acquire(1000)
            mHandler.sendMessageDelayed(msg,delay)

        }

    }

    override fun onReceive(context: Context, intent: Intent) {

        if (DEBUG)
            Log.v(TAG,"Received intent: $intent")

        if (handleIntent(context, intent) && isOrderedBroadcast) {
            abortBroadcast()
        }


    }
}