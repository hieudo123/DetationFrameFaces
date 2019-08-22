package com.example.stackexchange.detectframefaces

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.example.stackexchange.detectframefaces.facedetection.FaceDetection
import com.example.stackexchange.detectframefaces.facedetection.ImpFaceDetection
import com.example.stackexchange.detectframefaces.utils.AppUtils
import com.microsoft.projectoxford.face.FaceServiceClient
import com.microsoft.projectoxford.face.FaceServiceRestClient
import com.microsoft.projectoxford.face.contract.Face
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException


class MainActivity : AppCompatActivity(), View.OnClickListener,ImpFaceDetection {

    val PICK_IMAGE = 1
    lateinit var detection: FaceDetection
    lateinit var bitmap : Bitmap
    val apiEndpoint = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0"
    val subscriptionKey ="0cc9676c2d4946349f6a1e076729b70c"
    var faceServiceClient : FaceServiceClient = FaceServiceRestClient(apiEndpoint,subscriptionKey)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        detection = FaceDetection(this,this,faceServiceClient)
        tv_pickImage.setOnClickListener(this)
        tv_startLocalCamera.setOnClickListener(this)
    }
    override fun faceDetectionResult(result: Array<Face>) {
        iv_Image.setImageBitmap(AppUtils.drawFaceRectanglesOnBitmap(bitmap,result))
    }
    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.tv_pickImage->{
                val intent : Intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("image/*")
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE)
            }
            R.id.tv_startLocalCamera->{
                val intent : Intent = Intent(this,VideoCallActivity::class.java)
                startActivity(intent)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            var uri:Uri = data.data!!
            try{
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uri)
                iv_Image.setImageBitmap(bitmap)
                detection.detectAndFrame(bitmap)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }

    }
}
