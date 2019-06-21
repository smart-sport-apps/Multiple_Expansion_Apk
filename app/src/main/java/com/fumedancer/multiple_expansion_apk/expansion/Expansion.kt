package com.fumedancer.multiple_expansion_apk.expansion

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast

import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader

import java.io.File
import android.os.storage.OnObbStateChangeListener
import android.os.storage.StorageManager


object Expansion {

    private var TAG = "FILE_HELPER"
    private var pathToObb = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/obb/"


    fun mountExpansion(context: Context, fileName: String, mountedListener: OnMountedFileListener) {

        val filepathToObb = pathToObb + fileName

        if(File(filepathToObb).exists()){

            Log.d(TAG, "START MOUNTING FILE $fileName")

        }else{

            Log.d(TAG, "FILE $fileName NOT EXIST")
            mountedListener.onMountedFailed()
            return

        }

        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        storageManager.mountObb(filepathToObb, null, object : OnObbStateChangeListener() {
            override fun onObbStateChange(path: String, state: Int) {
                super.onObbStateChange(path, state)
                if (state == MOUNTED) {

                    Log.d(TAG, "MOUNT SUCCESS: $fileName")
                    mountedListener.onMountSuccess(storageManager.getMountedObbPath(filepathToObb))

                } else {

                    Log.d(TAG, "MOUNT FAILED: $fileName, reason: ${getMountStatusDescription(state)}")
                    if(state == 24) mountedListener.onMountSuccess(storageManager.getMountedObbPath(filepathToObb))
                    else mountedListener.onMountedFailed()

                }
            }
        })

    }

    fun checkExpansionPack(url:String, fileName: String,  context: Context,  callback: OnExpansionCheckListener) {

        if(isExpansionPackDownloaded(fileName)){

            Log.d(TAG, "FILE CHECKED $fileName")
            callback.fileExists()

        }else {

            downloadExpansion(url, fileName, context, object : ExpansionDownloadListener {

                override fun onExpansionDownloaded() {

                    Log.d(TAG, "FILE CHECKED $fileName")
                    callback.fileExists()

                }

                override fun onError() {

                    Log.d(TAG, "TROUBLE WITH FILE $fileName")
                    callback.fileTrouble()

                }

            })

        }

    }

    // Could be upgraded with getting status of the download process
    fun downloadExpansion(url: String, saveFileName: String, context: Context, downloadListener: ExpansionDownloadListener){

        PRDownloader.initialize(context)

        val downloadId = PRDownloader.download(url, pathToObb, saveFileName)
                .build()
                .setOnStartOrResumeListener { }
                .setOnPauseListener { }
                .setOnCancelListener { }
                .setOnProgressListener { }
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {

                        Log.d(TAG, "FILE DOWNLOADED: $saveFileName")
                        downloadListener.onExpansionDownloaded()

                    }

                    override fun onError(error: Error) {

                        Log.d(TAG, "FILE NOT DOWNLOADED: $saveFileName")
                        Toast.makeText(context, "Expansion file $saveFileName not downloaded", Toast.LENGTH_LONG).show()

                    }

                })

    }

    fun isExpansionPackDownloaded(filename: String): Boolean{

        return File(pathToObb, filename).exists()

    }

    private fun getMountStatusDescription(statusId: Int): String {

        when (statusId) {
            1 -> return " The OBB file is Mounted successfully"
            2 -> return " The OBB file is Unmounted successfully"
            20 -> return " Internal Error!"
            21 -> return " Could Not Mount current OBB file!"
            22 -> return " Could Not Unmount current OBB file!"
            23 -> return " The OBB file is not mounted, so it can not unmount!"
            24 -> return " Error! The OBB file is already mounted!"
            25 -> return " Error! Your Application has no permission to access current OBB file"
            -1 -> return " The OBB file wanted to be mounted is Not Assigned yet!!!"
            else -> return " Unknown Error!"
        }

    }


}
