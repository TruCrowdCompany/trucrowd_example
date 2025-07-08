package tech.trucrowd.gate

import android.content.ContentValues.TAG
import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.innovatrics.smartface.embedded.toolkit.face.Template
import tech.trucrowd.gateCore.OfflineMode
import tech.trucrowd.gateCore.TruCrowdApiV1
import tech.trucrowd.gateCore.TruCrowdCallbacksV1
import tech.trucrowd.gateCore.setTruCrowdCallbacksV1
import tech.trucrowd.gateCore.startTruCrowdV1
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream


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
            //example how to register device after install as an alternative to qrcode scanning
            /*if (!trucrowd!!.isRegistered()) {
                trucrowd?.registerDevice(
                    "jvhjvbqJvFsbVf4oew",
                    { connected: Boolean, error: String? ->
                        {
                            if (!connected)
                                Log.e(TAG, "Not Connected with Error: $error.")
                        }
                    })
            }*/

            //get TruCrowd api version, should be 1
            val version = trucrowd!!.getVersion()
            Log.d(TAG, "TruCrowd API version: $version.")

            //set top banner to an image from assets
            trucrowd!!.setBanner(TruCrowdApiV1.BannerType.TOP, assets.open("topbanner.png"))

            //set to use onTruCrowdFan callback instead of server script
            trucrowd!!.setUseFanCallback(true)

            //generate offline file from csv and pics in assets folder
            //use device explorer to get offline file from /data/data/tech.trucrowd.gateLibExample/files/offlineGenerated.tmp
            val result = generateOfflineFile(filesDir, assets)
            if (result != "SUCCESS")
                Log.e(TAG, result)

            //loads offline file from assets folder
            trucrowd!!.setOfflineFile(assets.open("offlineGenerated.tmp"))

            //sets offline mode to COMBINED to search in the offline file first and if not found online on the server
            trucrowd!!.setOfflineMode(OfflineMode.COMBINED)
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

    fun generateOfflineFile(filesDir: File, assets: AssetManager) : String {
        val sfeOfflineDataVersion = 1
        val sfeOfflineTemplateSize = 522
        val fileNameIn = "fans.csv"
        val fileNameOut = "offlineGenerated.tmp"

        if(trucrowd == null) {
            val err = "ERROR: TruCrowd api not initialized."
            Log.e(TAG, err)
            return err
        }

        try {
            val lstIds = ArrayList<Int>()
            val lstCustomIds = ArrayList<String>()
            val lstTemplates = ArrayList<Template>()

            val file = assets.open(fileNameIn);
            val reader = BufferedReader(file.bufferedReader(Charsets.UTF_8))
            var line: String?
            var count = 0

            //reader.readLine() //skip header

            while ((reader.readLine().also { line = it }) != null) {
                ++count
                val parts = line!!.split(",")
                val idstr = parts[0].replace("\"", "")
                val id = idstr.toInt()
                val customId = parts[1].replace("\"", "")
                val faceFile = parts[2].replace("\"", "")

                lstIds.add(id)
                lstCustomIds.add(customId)

                val template = trucrowd!!.generateTemplateFromAssetPic(faceFile, assets)
                if(template != null) {
                    lstTemplates.add(template)
                } else {
                    val err = "ERROR: Could not generate template for file $faceFile on line ${count}."
                    Log.e(TAG, err)
                    return err
                }
            }

            if(lstIds.size != lstCustomIds.size || lstCustomIds.size != lstTemplates.size){
                val err = "ERROR: Sizes doesnt match: lstIds.size ${lstIds.size} lstCustomIds.size ${lstCustomIds.size} lstTemplates.size ${lstTemplates.size}."
                Log.e(TAG, err)
                return err
            }

            if(lstIds.size <= 0){
                val err = "ERROR: Sizes are zero: lstIds.size ${lstIds.size} lstCustomIds.size ${lstCustomIds.size} lstTemplates.size ${lstTemplates.size}."
                Log.e(TAG, err)
                return err
            }

            val fileOut = FileOutputStream(File(filesDir, fileNameOut))
            val dataOut = DataOutputStream(fileOut)

            dataOut.writeInt(sfeOfflineDataVersion)
            dataOut.writeInt(lstTemplates.size)

            for(i in lstTemplates.indices){
                val id = lstIds[i]
                val customId = lstCustomIds[i]
                val template = lstTemplates[i]

                if(template.size != sfeOfflineTemplateSize){
                    val err = "ERROR: Template sizes are different: template.size ${template.size} Settings.sfeOfflineTemplateSize ${sfeOfflineTemplateSize}."
                    Log.e(TAG, err)
                    return err
                }

                val customIdToBytes = customId.toByteArray()
                dataOut.writeInt(id)
                dataOut.writeInt(customIdToBytes.size)
                dataOut.write(customIdToBytes)
                dataOut.write(template)
            }

            dataOut.flush()
            dataOut.close()
            fileOut.flush()
            fileOut.close()
            file.close()

        } catch (e: Exception){
            Log.e(TAG, e.toString())
            return "ERROR: ${e.toString()}"
        }

        return "SUCCESS"
    }
}