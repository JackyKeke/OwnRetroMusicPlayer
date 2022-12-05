package com.jackykeke.ownretromusicplayer.activities.base

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import com.google.android.material.snackbar.Snackbar
import com.jackykeke.appthemehelper.ThemeStore.Companion.accentColor
import com.jackykeke.appthemehelper.util.VersionUtils
import com.jackykeke.ownretromusicplayer.R
import com.jackykeke.ownretromusicplayer.extensions.accentColor
import com.jackykeke.ownretromusicplayer.extensions.rootView
import com.jackykeke.ownretromusicplayer.util.logD
import java.util.jar.Manifest

/**
 *
 * @author keyuliang on 2022/9/16.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
abstract class AbsBaseActivity : AbsThemeActivity() {


    private var hadPermissions: Boolean = false
    private var permissionDeniedMessage: String? = null
    private lateinit var permissions: Array<String>

    open fun getPermissionsToRequest(): Array<String> = arrayOf()

    private val snackBarContainer: View
        get() = rootView

    companion object {
        const val PERMISSION_REQUEST = 100
        const val BLUETOOTH_PERMISSION_REQUEST = 101
    }

    protected fun setPermissionDeniedMessage(message: String) {
        permissionDeniedMessage = message
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_MUSIC
        permissions = getPermissionsToRequest()
        hadPermissions = hasPermissions()
        permissionDeniedMessage = null
    }

    protected fun hasPermissions(): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_DENIED
            ) {
                return false
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        val hasPermissions = hasPermissions()
        if (hasPermissions != hadPermissions) {
            hadPermissions = hasPermissions
            if (VersionUtils.hasMarshmallow()) {
                onHasPermissionSChanged(hasPermissions)
            }
        }
    }

    private fun onHasPermissionSChanged(hasPermissions: Boolean) {
        logD(hasPermissions)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_MENU && event.action == KeyEvent.ACTION_UP) {
            showOverFlowMenu()
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    private fun showOverFlowMenu() {


    }

    protected open fun requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST) {

            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this@AbsBaseActivity,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) || ActivityCompat.shouldShowRequestPermissionRationale(
                            this@AbsBaseActivity,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        // User has deny from permission dialog
                        Snackbar.make(
                            snackBarContainer, permissionDeniedMessage!!, Snackbar.LENGTH_SHORT
                        )
                            .setAction(R.string.action_grant) { requestPermissions() }
                            .setActionTextColor(accentColor()).show()

                    } else {
                        // User has deny permission and checked never show permission dialog so you can redirect to Application settings page
                        Snackbar.make(
                            snackBarContainer,
                            permissionDeniedMessage!!,
                            Snackbar.LENGTH_INDEFINITE
                        )
                            .setAction(R.string.action_settings) {
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts(
                                    "package",
                                    this.packageName,
                                    null
                                )
                                intent.data = uri
                                startActivity(intent)
                            }.setActionTextColor(accentColor()).show()

                    }
                    return
                }
            }
            hadPermissions = true
            onHasPermissionSChanged(true)
        } else if (requestCode == BLUETOOTH_PERMISSION_REQUEST) {
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this@AbsBaseActivity, android.Manifest.permission.BLUETOOTH_CONNECT
                        )
                    ) {
                        // User has deny from permission dialog
                        Snackbar.make(
                            snackBarContainer,
                            R.string.permission_bluetooth_denied,
                            Snackbar.LENGTH_SHORT
                        )
                            .setAction(R.string.action_grant) {
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT),
                                    BLUETOOTH_PERMISSION_REQUEST
                                )
                            }
                            .setActionTextColor(accentColor()).show()
                    }
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(
                        v.windowToken,
                        0
                    )
                }
            }
        }


        return super.dispatchTouchEvent(ev)
    }

}