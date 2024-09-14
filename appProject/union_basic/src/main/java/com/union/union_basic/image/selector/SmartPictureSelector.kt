package com.union.union_basic.image.selector

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.engine.CompressEngine
import com.luck.picture.lib.engine.CropEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnCallbackListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.manager.PictureCacheManager
import com.luck.picture.lib.utils.DateUtils
import com.luck.picture.lib.utils.SdkVersionUtils
import com.union.union_basic.ext.showToast
import com.union.union_basic.logger.LoggerManager
import com.union.union_basic.utils.FileUtils
import com.yalantis.ucrop.UCrop
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File

/**
 * classname：SmartPictureSelector
 * desc: 图片相册选择统一调用入口
 */
object SmartPictureSelector {

    /**
     * funname:openCamera
     * parm:block:()--相机拍摄后返回图片
     *      isCrop：是否裁剪
     *      ratio：裁剪比例
     * desc: 打开相机
     */
    fun openCamera(activity: AppCompatActivity, isCrop: Boolean = false, ratio: List<Int> = listOf(1, 1),isCameraAroundState:Boolean=false,
        block: (paths: MutableList<String>) -> Unit) {
        PictureCacheManager.deleteAllCacheDirRefreshFile(activity)
        PictureSelector.create(activity).openCamera(SelectMimeType.ofImage()).isCameraAroundState(isCameraAroundState).setCompressEngine(CustomCompressEngine())
            .apply {
                if (isCrop) {
                    setCropEngine(ImageCropEngine().apply {
                        mRatio = ratio
                    })
                }
            }.forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>) {
                    val paths: MutableList<String> = ArrayList()
                    for (i in result.indices) {
                        val localMedia = result[i] //多选
                        if (localMedia.isCompressed) {
                            paths.add(localMedia.compressPath)
                        } else if (localMedia.isCut) {
                            paths.add(localMedia.cutPath)
                        } else {
                            paths.add(localMedia.path)
                        }
                    }
                    block(paths)
                }

                override fun onCancel() {
                    "取消选择".showToast()
                }
            })
    }

    /**
     * funname:openPicture
     * parm:block:()--相册选择后返回图片
     *      isCrop：是否裁剪
     *      ratio：裁剪比例
     *      maxSelectNum：最大选择张数
     * desc: 打开相册
     */
    fun openPicture(activity: AppCompatActivity, maxSelectNum: Int = 1, isCrop: Boolean = false,
        ratio: List<Int> = listOf(1, 1), block: (paths: MutableList<String>) -> Unit) {
        PictureCacheManager.deleteAllCacheDirRefreshFile(activity)
        PictureSelector.create(activity).openGallery(SelectMimeType.ofImage()).setImageSpanCount(3)
            .setQueryOnlyMimeType(PictureMimeType.ofJPEG(), PictureMimeType.ofPNG()).setMaxSelectNum(maxSelectNum)
            .setCompressEngine(CustomCompressEngine()).apply {
                if (isCrop) {
                    setCropEngine(ImageCropEngine().apply {
                        mRatio = ratio
                    })
                }
            }.isPreviewImage(true).isGif(true).setImageEngine(GlideEngine.createGlideEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>) {
                    val paths: MutableList<String> = ArrayList()
                    for (i in result.indices) {
                        val localMedia = result[i] //多选
                        when {
                            localMedia.isCompressed -> {
                                paths.add(localMedia.compressPath)
                            }
                            localMedia.isCut -> {
                                paths.add(localMedia.cutPath)
                            }
                            else -> {
                                val path = getRealPathFromUri(activity, localMedia.path.toUri())
                                paths.add(path ?: "")
                            }
                        }
                    }
                    block(paths)
                }

                override fun onCancel() {
                }
            })
    }

    fun getRealPathFromUri(context: Context, contentUri: Uri): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val columnIndex: Int = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA) ?: 0
            cursor?.moveToFirst()
            cursor?.getString(columnIndex)
        } catch (e: Exception) {
            ""
        } finally {
            cursor?.close()
        }
    }
}

class CustomCompressEngine : CompressEngine {

    override fun onStartCompress(context: Context, list: java.util.ArrayList<LocalMedia>,
        listener: OnCallbackListener<java.util.ArrayList<LocalMedia>>) {
        val compress: MutableList<Uri> = ArrayList()
        for (i in 0 until list.size) {
            val media = list[i]
            val availablePath = media.availablePath
            val uri: Uri =
                if (PictureMimeType.isContent(availablePath) || PictureMimeType.isHasHttp(availablePath)) Uri.parse(
                    availablePath) else Uri.fromFile(File(availablePath))
            compress.add(uri)
        }
        if (compress.size == 0) {
            listener.onCall(list)
            return
        }
        Luban.with(context).load(compress).ignoreBy(100)
            .filter { path -> PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(path) }
            .setRenameListener { filePath ->
                val indexOf = filePath.lastIndexOf(".")
                val postfix = if (indexOf != -1) filePath.substring(indexOf) else ".jpg"
                DateUtils.getCreateFileName("CMP_").toString() + postfix
            }.setCompressListener(object : OnCompressListener {
                override fun onStart() {}
                override fun onSuccess(index: Int, compressFile: File) {   // 压缩完构造LocalMedia对象
                    val media = list[index]
                    if (compressFile.exists() && !TextUtils.isEmpty(compressFile.absolutePath)) {
                        media.isCompressed = true
                        media.compressPath = compressFile.absolutePath
                        media.sandboxPath = if (SdkVersionUtils.isQ()) media.compressPath else null
                    } // 因为是多图压缩，所以判断压缩到最后一张时返回结果
                    if (index == list.size - 1) {
                        listener.onCall(list)
                    }
                }

                override fun onError(index: Int, e: Throwable?) {    // 压缩失败
                    if (index != -1) {
                        val media = list[index]
                        media.isCompressed = false
                        media.compressPath = null
                        media.sandboxPath = null
                        if (index == list.size - 1) {
                            listener.onCall(list)
                        }
                    }
                }
            }).launch()
    }
}

class ImageCropEngine : CropEngine {
    var mRatio: List<Int> = listOf(1, 1)
    override fun onStartCrop(fragment: Fragment, currentLocalMedia: LocalMedia,
        dataSource: java.util.ArrayList<LocalMedia>, requestCode: Int) {
        val currentCropPath = currentLocalMedia.availablePath
        val inputUri: Uri =
            if (PictureMimeType.isContent(currentCropPath) || PictureMimeType.isHasHttp(currentCropPath)) {
                Uri.parse(currentCropPath)
            } else {
                Uri.fromFile(File(currentCropPath))
            }
        val fileName = DateUtils.getCreateFileName("CROP_") + ".jpg"
        val destinationUri = Uri.fromFile(File(FileUtils.getSandboxPath(), fileName))
        val options: UCrop.Options = buildOptions(mRatio)
        val dataCropSource = java.util.ArrayList<String>()
        for (i in dataSource.indices) {
            val media = dataSource[i]
            dataCropSource.add(media.availablePath)
        }
        val uCrop = UCrop.of(inputUri, destinationUri, dataCropSource)
        uCrop.withOptions(options)
        uCrop.start(fragment.requireActivity(), fragment, requestCode)
    }

    /**
     * 配制UCrop，可根据需求自我扩展
     *
     * @return
     */
    private fun buildOptions(ratio: List<Int> = listOf(1, 1)): UCrop.Options {
        return UCrop.Options().apply {
            setCompressionQuality(80)
            withAspectRatio(ratio[0].toFloat(), ratio[1].toFloat())
            setFreeStyleCropEnabled(false)
            setCropOutputPathDir(FileUtils.getSandboxPath())
            setHideBottomControls(true)
            isCropDragSmoothToCenter(false)
            isForbidSkipMultipleCrop(false)
            setMaxScaleMultiplier(100f)
        }
    }
}