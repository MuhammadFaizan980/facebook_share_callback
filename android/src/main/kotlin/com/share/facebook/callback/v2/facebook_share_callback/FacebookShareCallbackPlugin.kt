package com.share.facebook.callback.v2.facebook_share_callback

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.io.File

/** FacebookShareCallbackPlugin */
class FacebookShareCallbackPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private var channel: MethodChannel? = null
    private var activity: Activity? = null
    private var callbackManager: CallbackManager? = null

    private var type: String? = null
    private var quote: String? = null
    private var url: String? = null
    private var uint8Image: ByteArray? = null
    private var imageName: String? = null

    override fun onAttachedToActivity(@NonNull binding: ActivityPluginBinding) {
        activity = binding.activity
        // Register activity result listener
        binding.addActivityResultListener { requestCode, resultCode, data ->
            callbackManager?.onActivityResult(requestCode, resultCode, data) ?: false
        }
    }

    override fun onDetachedFromActivityForConfigChanges() {
        // No implementation
    }

    override fun onReattachedToActivityForConfigChanges(@NonNull binding: ActivityPluginBinding) {
        activity = binding.activity
        // Re-register activity result listener
        binding.addActivityResultListener { requestCode, resultCode, data ->
            callbackManager?.onActivityResult(requestCode, resultCode, data) ?: false
        }
    }

    override fun onDetachedFromActivity() {
        activity = null
    }

    private fun onAttachedToEngine(messenger: BinaryMessenger) {
        channel = MethodChannel(messenger, "share_facebook_callback")
        channel?.setMethodCallHandler(this)
        callbackManager = CallbackManager.Factory.create()
    }

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        onAttachedToEngine(flutterPluginBinding.binaryMessenger)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel?.setMethodCallHandler(null)
        channel = null
        activity = null
        callbackManager = null
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method.equalsIgnoreCase("facebook_share")) {
            type = call.argument("type")
            quote = call.argument("quote")
            url = call.argument("url")
            uint8Image = call.argument("uint8Image")
            imageName = call.argument("imageName")
            when (type) {
                "ShareType.shareLinksFacebook" -> shareLinksFacebook(url, quote, result)
                "ShareType.sharePhotoFacebook" -> sharePhotoFacebook(uint8Image, quote, result)
                else -> result.notImplemented()
            }
        } else {
            result.notImplemented()
        }
    }

    private fun shareLinksFacebook(url: String?, quote: String?, result: Result) {
        val shareDialog = ShareDialog(activity)
        val content = ShareLinkContent.Builder()
            .setContentUrl(url?.let { Uri.parse(it) })
            .setQuote(quote)
            .build()

        if (ShareDialog.canShow(ShareLinkContent::class.java)) {
            shareDialog.registerCallback(callbackManager, object : FacebookCallback<Sharer.Result> {
                override fun onSuccess(sharerResult: Sharer.Result) {
                    println("--------------------success")
                    result.success("success")
                }

                override fun onCancel() {
                    println("-----------------cancel")
                    result.success("cancel")
                }

                override fun onError(error: FacebookException) {
                    println("---------------error: ${error.message}")
                    result.error("ERROR", error.message, null)
                }
            })
            shareDialog.show(content)
        } else {
            result.error("ERROR", "Cannot show ShareDialog for ShareLinkContent", null)
        }
    }

    private fun sharePhotoFacebook(uint8Image: ByteArray?, quote: String?, result: Result) {
        println("--------------------sharePhotoFacebook")
        val shareDialog = ShareDialog(activity)

        shareDialog.registerCallback(callbackManager, object : FacebookCallback<Sharer.Result> {
            override fun onSuccess(sharerResult: Sharer.Result?) {
                println("--------------------success")
                result.success(true)
            }

            override fun onCancel() {
                println("-----------------cancel")
                result.success(false)
            }

            override fun onError(error: FacebookException?) {
                println("---------------error: ${error?.message}")
                result.success(false)
            }
        })

        if (uint8Image == null) {
            result.error("ERROR", "Image data is null", null)
            return
        }

        val bitmap = BitmapFactory.decodeByteArray(uint8Image, 0, uint8Image.size)
        if (bitmap == null) {
            result.error("ERROR", "Failed to decode image", null)
            return
        }

        val photo = SharePhoto.Builder()
            .setBitmap(bitmap)
            .setCaption(quote)
            .build()

        val content = SharePhotoContent.Builder()
            .addPhoto(photo)
            .build()

        if (shareDialog.canShow(SharePhotoContent::class.java)) {
            shareDialog.show(content)
        } else {
            result.error("ERROR", "Cannot show ShareDialog for SharePhotoContent", null)
        }
    }
}