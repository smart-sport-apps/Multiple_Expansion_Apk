package com.fumedancer.multiple_expansion_apk.exampleApp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

import com.fumedancer.multiple_expansion_apk.R
import com.fumedancer.multiple_expansion_apk.expansion.Expansion
import com.fumedancer.multiple_expansion_apk.expansion.ExpansionDownloadListener
import com.fumedancer.multiple_expansion_apk.expansion.OnExpansionCheckListener
import com.fumedancer.multiple_expansion_apk.expansion.OnMountedFileListener

class MainActivity : AppCompatActivity() {

    val url = "http://192.168.1.111/"

    lateinit var downloadButton: Button
    lateinit var mountButton: Button
    lateinit var textView: TextView

    val expansionFileList = arrayOf(

            "pack1.1.com.heyclay.heyclayr.obb",
            "pack2.1.com.heyclay.heyclayr.obb",
            "pack3.1.com.heyclay.heyclayr.obb",
            "pack4.1.com.heyclay.heyclayr.obb",
            "pack5.1.com.heyclay.heyclayr.obb",
            "pack6.1.com.heyclay.heyclayr.obb",
            "pack7.1.com.heyclay.heyclayr.obb",
            "pack8.1.com.heyclay.heyclayr.obb",
            "pack9.1.com.heyclay.heyclayr.obb",
            "pack10.1.com.heyclay.heyclayr.obb"

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun init(){

        downloadButton = findViewById(R.id.button_download)
        mountButton = findViewById(R.id.button_mount)
        textView = findViewById(R.id.textView)

        downloadButton.setOnClickListener{downloadExpansions()}
        mountButton.setOnClickListener{mountExpansion()}

    }

    private fun downloadExpansions() {

        expansionFileList.forEach {

            Expansion.checkExpansionPack(url, it, applicationContext, object: OnExpansionCheckListener{

                override fun fileExists() {

                }

                override fun fileTrouble() {

                }
            })

        }

    }

    private fun mountExpansion(){

        expansionFileList.forEach {

            Expansion.mountExpansion(applicationContext, it, object: OnMountedFileListener{

                override fun onMountSuccess(mountedFilePath: String) {

                }

                override fun onMountedFailed() {

                }

            })

        }

    }

}
