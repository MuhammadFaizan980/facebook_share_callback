package com.share.facebook.callback.v2.facebook_share_callback

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.BitmapFactory
import android.net.Uri
import android.graphics.Bitmap
import android.text.TextUtils

import androidx.annotation.NonNull
import androidx.core.content.FileProvider

import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog

import io.flutter.embedding.engine.plugins.activity.ActivityAware

import java.io.File

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import com.facebook.CallbackManager
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


import io.flutter.plugin.common.BinaryMessenger
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.MessageDialog
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView


/** FacebookShareCallbackPlugin */
class FacebookShareCallbackPlugin : FlutterPlugin, MethodCallHandler, ActivityAware,
    io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding.ActivityResultListener {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private var channel: MethodChannel? = null
    private var activity: Activity? = null
    private var callbackManager: CallbackManager? = null

    private var type: String? = null
    private var quote: String? = null
    private var url: String? = null
    private var uint8Image: ByteArray
    private var imageName: String? = null


    override fun onAttachedToActivity(@NonNull binding: ActivityPluginBinding) {
        binding.addActivityResultListener(this)
        activity = binding.getActivity()
    }


    override fun onDetachedFromActivityForConfigChanges() {
        // No implementation
    }


    override fun onReattachedToActivityForConfigChanges(@NonNull binding: ActivityPluginBinding) {
        binding.removeActivityResultListener(this)
        binding.addActivityResultListener(this)
    }


    override fun onDetachedFromActivity() {
        // activity = null;
        // binding.removeActivityResultListener(this);
    }

    private fun onAttachedToEngine(messenger: BinaryMessenger) {
        channel = MethodChannel(messenger, "share_facebook_callback")
        channel.setMethodCallHandler(this)
        callbackManager = CallbackManager.Factory.create()
    }

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        onAttachedToEngine(flutterPluginBinding.binaryMessenger)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        channel = null
        activity = null
    }


    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        System.out.println("--------------------onMethodCall")
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE)
        } else if (call.method.equalsIgnoreCase("facebook_share")) {
            type = call.argument("type")
            quote = call.argument("quote")
            url = call.argument("url")
//            uint8Image = call.argument("uint8Image")
            imageName = call.argument("imageName")
            when (type) {
                "ShareType.shareLinksFacebook" -> shareLinksFacebook(url, quote, result)
                "ShareType.sharePhotoFacebook" -> sharePhotoFacebook(uint8Image, quote, result)
                else -> result.notImplemented()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun shareLinksFacebook(url: String, quote: String, result2: Result) {
        val shareDialog = ShareDialog(activity)

        val content = ShareLinkContent.Builder()
            .setContentUrl(Uri.parse(url))
            .setQuote(quote)
            .build()

        if (ShareDialog.canShow(ShareLinkContent::class.java)) {
            shareDialog.registerCallback(callbackManager, object : FacebookCallback<Sharer.Result> {
                override fun onSuccess(result: Sharer.Result) {
                    println("--------------------success")
                    result2.success("success")
                }

                override fun onCancel() {
                    println("-----------------cancel")
                    result2.success("cancel")
                }

                override fun onError(error: FacebookException) {
                    println("---------------error")
                    result2.success("error")
                }
            })

            shareDialog.show(content)
        }
    }

    private fun sharePhotoFacebook(uint8Image: ByteArray, quote: String, result2: Result) {
        println("--------------------sharePhotoFacebook")

        val shareDialog = ShareDialog(activity)

        shareDialog.registerCallback(callbackManager, object : FacebookCallback<Sharer.Result> {
            override fun onSuccess(result: Sharer.Result?) {
                println("--------------------success")
                result2.success(true)
            }

            override fun onCancel() {
                println("-----------------cancel")
                result2.success(false)
            }

            override fun onError(error: FacebookException?) {
                println("---------------error")
                result2.success(false)
            }
        })

        val photo = SharePhoto.Builder()
            .setBitmap(BitmapFactory.decodeByteArray(uint8Image, 0, uint8Image.size))
            .setCaption(quote)
            .build()

        val content = SharePhotoContent.Builder()
            .addPhoto(photo)
            .build()

        if (shareDialog.canShow(SharePhotoContent::class.java)) {
            shareDialog.show(content)
        }
    }


}
