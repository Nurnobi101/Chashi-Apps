package com.example.chashi

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.*
import com.example.chashi.ml.MobilenetV110224Quant
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.util.*

@Suppress("DEPRECATION")
class Problem : AppCompatActivity(), TextToSpeech.OnInitListener {
    lateinit var bitmap: Bitmap
    lateinit var imgview:ImageView
    lateinit var textToSpeech: TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problem)
        var btn :Button = findViewById(R.id.btn)
        var textView : TextView = findViewById(R.id.textView)
        textToSpeech = TextToSpeech(this,this)
        btn.setOnClickListener {
            var ch : String = textView.text.toString()
            textToSpeech.speak(ch,TextToSpeech.QUEUE_FLUSH,null)
        }
        val Logout1: Switch = findViewById(R.id.Logout1)
        Logout1.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val helpBtn: Button = findViewById(R.id.helpBtn)
        helpBtn.setOnClickListener {
            val intent = Intent(this,Helpline::class.java)
            startActivity(intent)
        }
        imgview = findViewById(R.id.imageView)
        val fileName = "label.text"
        val inputString = application.assets.open(fileName).bufferedReader().use { it.readText() }
        var townList = inputString.split('\n')

        var tv:TextView = findViewById(R.id.textView)


        var select:Button = findViewById(R.id.button)
        select.setOnClickListener(View.OnClickListener {
            var intent:Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            startActivityForResult(intent,100)
        })

        var predict:Button = findViewById(R.id.button2)
        predict.setOnClickListener(View.OnClickListener {

            var resize: Bitmap = Bitmap.createScaledBitmap(bitmap, 224,224,true)
            val model = MobilenetV110224Quant.newInstance(this)

            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)

            var tbuffer = TensorImage.fromBitmap(resize)
            var byteBuffer = tbuffer.buffer

            inputFeature0.loadBuffer(byteBuffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            var n = getMax(outputFeature0.floatArray)

            tv.setText(townList[n])

            model.close()

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        imgview.setImageURI(data?.data)

        var uri: Uri?= data?.data
        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,uri)
    }
    fun getMax(arr:FloatArray) : Int{
        var ind = 0
        var min = 0.0f

        for(i in 0..1000)
        {
            if(arr[i]>min)
            {
                ind = i
                min = arr[i]
            }

        }
        return ind

    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val res: Int = textToSpeech.setLanguage(Locale.forLanguageTag("bn_BD"))
            if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(this,"নির্দেশনাগুলো অনুসরণ করুন ", Toast.LENGTH_LONG).show()
            }
        }
        else{
            Toast.makeText(this,"Failed to initialize", Toast.LENGTH_LONG).show()
        }
    }

}