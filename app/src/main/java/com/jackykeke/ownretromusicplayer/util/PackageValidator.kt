package com.jackykeke.ownretromusicplayer.util

import android.Manifest
import android.Manifest.permission.MEDIA_CONTENT_CONTROL
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.XmlResourceParser
import android.os.Process
import android.os.Process.myUid
import android.support.v4.media.session.MediaSessionCompat
import android.util.Base64
import android.util.Log
import androidx.annotation.XmlRes
import androidx.media.MediaBrowserServiceCompat
import com.jackykeke.ownretromusicplayer.BuildConfig
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 *
 * @author keyuliang on 2022/9/28.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class PackageValidator(
    context: Context,
    @XmlRes xmlResId: Int
) {
    private val context: Context
    private val packageManager: PackageManager

    private val certificateWhitelist: Map<String, KnownCallerInfo>
    private val platformSignature: String

    private val callerChecked = mutableMapOf<String, Pair<Int, Boolean>>()

    init {

        val parser = context.resources.getXml(xmlResId)
        this.context = context.applicationContext
        this.packageManager = this.context.packageManager

        certificateWhitelist = buildCertificateWhitelist(parser)
        platformSignature = getSystemSignature()

    }

    /**
     * Checks whether the caller attempting to connect to a [MediaBrowserServiceCompat] is known.
     * See [MediaBrowserServiceCompat.onGetRoot] for where this is utilized.
     *
     * @param callingPackage The package name of the caller.
     * @param callingUid The user id of the caller.
     * @return `true` if the caller is known, `false` otherwise.
     *
    检查尝试连接到[MediaBrowserServiceCompat]的调用者是否已知。
    [MediaBrowserServiceCompat见。onGetRoot]用于使用它的地方。
    @param callingPackage调用者的包名。
    @param callingUid呼叫方用户id。
    如果已知调用者返回' true '，否则返回' false '。
     */
    fun isKnownCaller(callingPackage: String, callingUid: Int): Boolean {

        val (checkedUid,checkResult) = callerChecked[callingPackage]?:Pair(0,false)
        if (checkedUid == callingUid){
            return checkResult
        }

        /**
         * Because some of these checks can be slow, we save the results in [callerChecked] after
         * this code is run.
         *
         * In particular, there's little reason to recompute the calling package's certificate
         * signature (SHA-256) each call.
         *
         * This is safe to do as we know the UID matches the package's UID (from the check above),
         * and app UIDs are set at install time. Additionally, a package name + UID is guaranteed to
         * be constant until a reboot. (After a reboot then a previously assigned UID could be
         * reassigned.)
         * 因为其中一些检查可能比较慢，所以之后我们将结果保存在[callerChecked]中

        运行此代码。

        特别是，几乎没有理由重新计算调用包的证书

        签名(SHA-256)。

        这样做是安全的，因为我们知道UID匹配包的UID(从上面的检查)，

        和应用程序uid在安装时设置。此外，包名+ UID保证

        直到重新启动。(重新启动后，以前分配的UID可能是

        重新分配)。
         */

        // Build the caller info for the rest of the checks here.
        val callerPackageInfo = buildCallerInfo(callingPackage)?:throw IllegalStateException("Caller wasn't found in the system?")

        if (callerPackageInfo.uid != callingUid){
            throw IllegalStateException("Caller's package UID doesn't match caller's actual UID?")
        }

        val callerSignature = callerPackageInfo.signature
        val isPackageInWhitelist =certificateWhitelist[callingPackage]?.signatures?.first {
            it.signature == callerSignature
        }!=null

        val isCallerKnown = when {

            // If it's our own app making the call, allow it.
            callingUid == Process.myUid() -> true
            // If it's one of the apps on the whitelist, allow it.
            isPackageInWhitelist -> true
            // If the system is making the call, allow it.
            callingUid == Process.SYSTEM_UID -> true
            // If the app was signed by the same certificate as the platform itself, also allow it.
            callerSignature == platformSignature -> true
            /**
             * [MEDIA_CONTENT_CONTROL] permission is only available to system applications, and
             * while it isn't required to allow these apps to connect to a
             * [MediaBrowserServiceCompat], allowing this ensures optimal compatability with apps
             * such as Android TV and the Google Assistant.
             */
            callerPackageInfo.permissions.contains(MEDIA_CONTENT_CONTROL) -> true
            /**
             * This last permission can be specifically granted to apps, and, in addition to
             * allowing them to retrieve notifications, it also allows them to connect to an
             * active [MediaSessionCompat].
             * As with the above, it's not required to allow apps holding this permission to
             * connect to your [MediaBrowserServiceCompat], but it does allow easy comparability
             * with apps such as Wear OS.
             */
            callerPackageInfo.permissions.contains(Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE) -> true
            // If none of the pervious checks succeeded, then the caller is unrecognized.
            else -> false

        }

        if (!isCallerKnown){
            logUnknownCaller(callerPackageInfo)
        }
        callerChecked[callingPackage] = Pair(callingUid,isCallerKnown)
        return isCallerKnown
    }

    private fun logUnknownCaller(callerPackageInfo: CallerPackageInfo) {
        if (BuildConfig.DEBUG && callerPackageInfo.signature != null) {
            Log.i(TAG, "PackageValidator call" + callerPackageInfo.name + callerPackageInfo.packageName + callerPackageInfo.signature)
        }
    }

    private fun buildCallerInfo(callingPackage: String): CallerPackageInfo? {

        val packageInfo = getPackageInfo(callingPackage)?:return null

        val appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
        val uid =packageInfo.applicationInfo.uid
        val signature =  getSignature(packageInfo)

        val requestedPermissions = packageInfo.requestedPermissions
        val permissionFlags = packageInfo.requestedPermissionsFlags
        val activePermissions = mutableSetOf<String>()

        requestedPermissions?.forEachIndexed { index, permission ->
            if (permissionFlags[index] and PackageInfo.REQUESTED_PERMISSION_GRANTED !=0){
                activePermissions += permission
            }
        }

        return CallerPackageInfo(appName,callingPackage,uid,signature,activePermissions.toSet())
    }


    /**
     * Convenience class to hold all of the information about an app that's being checked
     * to see if it's a known caller.
     */
    private data class CallerPackageInfo(
        val name: String,
        val packageName: String,
        val uid: Int,
        val signature: String?,
        val permissions: Set<String>
    )

    private fun getSystemSignature(): String =
        getPackageInfo(ANDROID_PLATFORM)?.let { platformInfo ->
            getSignature(platformInfo)
        } ?: throw IllegalStateException("Platform signature not found")

    private fun getSignature(packageInfo: PackageInfo): String? {
        return if (packageInfo.signatures == null || packageInfo.signatures.size != 1) {
            null
        } else {
            val certificate = packageInfo.signatures[0].toByteArray()
            getSignatureSha256(certificate)
        }
    }


    /**
     * Looks up the [PackageInfo] for a package name.
     * This requests both the signatures (for checking if an app is on the whitelist) and
     * the app's permissions, which allow for more flexibility in the whitelist.
     *
     * @return [PackageInfo] for the package name or null if it's not found.
     */
    @Suppress("Deprecation")
    @SuppressLint("PackageManagerGetSignatures")
    private fun getPackageInfo(callingPackage: String): PackageInfo? =
        packageManager.getPackageInfo(
            callingPackage,
            PackageManager.GET_SIGNATURES or PackageManager.GET_PERMISSIONS
        )


    private fun buildCertificateWhitelist(parser: XmlResourceParser): Map<String, KnownCallerInfo> {

        val certificateWhitelist = LinkedHashMap<String, KnownCallerInfo>()
        try {
            var eventType = parser.next()
            while (eventType != XmlResourceParser.END_DOCUMENT) {

                if (eventType == XmlResourceParser.START_TAG) {
                    val callerInfo = when (parser.name) {
                        "signing_certificate" -> parseV1Tag(parser)
                        "signature" -> parseV2Tag(parser)
                        else -> null
                    }

                    callerInfo?.let { info ->
                        val packageName = info.packageName
                        val existingCallerInfo = certificateWhitelist[packageName]
                        if (existingCallerInfo != null) {
                            existingCallerInfo.signatures += callerInfo.signatures
                        } else {
                            certificateWhitelist[packageName] = callerInfo
                        }

                    }
                }
                eventType = parser.next()
            }
        } catch (xmlException: XmlPullParserException) {
            Log.e(TAG, "Could not read allowed callers from XML.", xmlException)
        } catch (ioException: IOException) {
            Log.e(TAG, "Could not read allowed callers from XML.", ioException)
        }
        return certificateWhitelist
    }

    private fun parseV2Tag(parser: XmlResourceParser): KnownCallerInfo {
        val name = parser.getAttributeValue(null, "name")
        val packageName = parser.getAttributeValue(null, "package")

        val callerSignatures = mutableSetOf<KnownSignature>()
        var eventType = parser.next()
        while (eventType != XmlResourceParser.END_TAG) {
            val isRelease = parser.getAttributeBooleanValue(null, "release", false)
            val signature =
                parser.nextText().replace(WHITESPACE_REGEX, "").lowercase(Locale.getDefault())
            callerSignatures += KnownSignature(signature, isRelease)

            eventType = parser.next()
        }
        return KnownCallerInfo(name, packageName, callerSignatures)
    }

    private fun parseV1Tag(parser: XmlResourceParser): KnownCallerInfo {

        val name = parser.getAttributeValue(null, "name")
        val packageName = parser.getAttributeValue(null, "package")
        val isRelease = parser.getAttributeBooleanValue(null, "release", false)
        val certificate = parser.nextText().replace(WHITESPACE_REGEX, "")
        val signature = getSignatureSha256(certificate)

        val callerSignature = KnownSignature(signature, isRelease)
        return KnownCallerInfo(name, packageName, mutableSetOf(callerSignature))
    }

    /**
     * Creates a SHA-256 signature given a Base64 encoded certificate.
     */
    private fun getSignatureSha256(certificate: String): String {
        return getSignatureSha256(Base64.decode(certificate, Base64.DEFAULT))
    }

    /**
     * Creates a SHA-256 signature given a certificate byte array.
     */
    private fun getSignatureSha256(certificate: ByteArray): String {
        val md: MessageDigest
        try {
            md = MessageDigest.getInstance("SHA256")
        } catch (noSuchAlgorithmException: NoSuchAlgorithmException) {
            Log.e(TAG, "No such algorithm: $noSuchAlgorithmException")
            throw RuntimeException("Could not find SHA256 hash algorithm", noSuchAlgorithmException)
        }
        md.update(certificate)

        // This code takes the byte array generated by `md.digest()` and joins each of the bytes
        // to a string, applying the string format `%02x` on each digit before it's appended, with
        // a colon (':') between each of the items.
        // For example: input=[0,2,4,6,8,10,12], output="00:02:04:06:08:0a:0c"
        return md.digest().joinToString(":") { String.format("%02x", it) }
    }

}

data class KnownCallerInfo(
    val name: String,
    val packageName: String,
    val signatures: MutableSet<KnownSignature>
)

data class KnownSignature(
    val signature: String,
    val release: Boolean
)

private const val TAG = "PackageValidator"
private const val ANDROID_PLATFORM = "android"
private val WHITESPACE_REGEX = "\\s|\\n".toRegex()
