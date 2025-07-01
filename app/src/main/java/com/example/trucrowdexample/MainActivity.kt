package com.example.trucrowdexample

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import tech.trucrowd.gateCore.TruCrowdApiV1
import tech.trucrowd.gateCore.TruCrowdCallbacksV1
import tech.trucrowd.gateCore.setTruCrowdCallbacksV1
import tech.trucrowd.gateCore.startTruCrowdV1


class MainActivity : AppCompatActivity(), TruCrowdCallbacksV1 {
    var trucrowd: TruCrowdApiV1? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTruCrowdCallbacksV1(this)

        val btnStart = findViewById<View>(R.id.buttonStart) as Button
        btnStart.setOnClickListener {
            startTruCrowdV1(this)
        }
    }

    override fun onTruCrowdInit(api: TruCrowdApiV1){
        trucrowd = api

        //Examples of calling trucrowd functions
        if(trucrowd != null) {
            if (!trucrowd!!.isRegistered()) {
                trucrowd?.registerDevice("jvhjvbqJvFsbVf4oew", { connected: Boolean, error: String? -> {
                    if(!connected) Toast.makeText(this,
                    "Not Connected with Error: $error",
                    Toast.LENGTH_SHORT
                ).show()}})
            }
            Toast.makeText(
                this,
                "TruCrowd API version: ${trucrowd!!.getVersion()}",
                Toast.LENGTH_LONG
            ).show()
            trucrowd!!.setBanner(TruCrowdApiV1.BannerType.TOP, assets.open("topbanner.png"))
        }
    }

    override fun onTruCrowdExit(){
        trucrowd = null
    }

    override fun onTruCrowdFan(qrcode: Boolean, id: Int, customId: String, access: Boolean): Bundle {
        val ret = Bundle()
        val messageTop: String = if(qrcode) customId else id.toString()
        val messageBottomBig: String = "messageBottomBig"
        val messageBottomSmall: String = "messageBottomSmall"
        val messageMain: String = messageTop
        val messageRest: String = "messageRest"
        val messageDetail: String = "messageDetail"
        val allow: Boolean = qrcode

        ret.putString("messageTop", messageTop)
        ret.putString("messageBottomBig", messageBottomBig)
        ret.putString("messageBottomSmall", messageBottomSmall)
        ret.putString("messageMain", messageMain)
        ret.putString("messageRest", messageRest)
        ret.putString("messageDetail", messageDetail)
        ret.putBoolean("allow", allow)

        return ret
    }
}