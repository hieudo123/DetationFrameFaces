package com.example.stackexchange.detectframefaces

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.hardware.Camera
import android.hardware.camera2.CameraCaptureSession
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import com.example.stackexchange.detectframefaces.facedetection.FaceDetection
import com.example.stackexchange.detectframefaces.facedetection.ImpFaceDetection
import com.example.stackexchange.detectframefaces.utils.AppUtils
import com.microsoft.projectoxford.face.FaceServiceClient
import com.microsoft.projectoxford.face.FaceServiceRestClient
import com.microsoft.projectoxford.face.contract.Face
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.icu.util.Measure
import android.util.Log
import android.view.View
import java.io.ByteArrayOutputStream


class VideoCallActivity :AppCompatActivity(),ImpFaceDetection, Camera.PreviewCallback {
    override fun onPreviewFrame(data: ByteArray?,camera:  Camera?) {
        var parameters : Camera.Parameters = camera!!.parameters
        var out : ByteArrayOutputStream = ByteArrayOutputStream()
        var yuvImage : YuvImage = YuvImage(data,parameters.previewFormat,
            parameters.getPreviewSize().width,
            parameters.getPreviewSize().height, null)
        yuvImage.compressToJpeg(Rect(0,0,parameters.getPreviewSize().width, parameters.getPreviewSize().height), 90, out)

        var imageBytes = out.toByteArray()
        bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size);
        Log.e("AAA","$bitmap")
        detection.detectAndFrame(bitmap)
        out.flush()
        out.close()
    }
    val PICK_IMAGE = 1
    lateinit var detection: FaceDetection
    lateinit var bitmap : Bitmap
    lateinit var camera : Camera
    lateinit var frameLocalVideoView : FrameLayout
    lateinit var localCamera: LocalCamera
    var cameraId =0
    val apiEndpoint = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0"
    val subscriptionKey ="0cc9676c2d4946349f6a1e076729b70c"
    var faceServiceClient : FaceServiceClient = FaceServiceRestClient(apiEndpoint,subscriptionKey)

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_localvideo_view)
        super.onCreate(savedInstanceState)
        detection = FaceDetection(this,this,faceServiceClient)
        frameLocalVideoView = findViewById(R.id.frame_localCamera)
        for (i in 0 until Camera.getNumberOfCameras()) {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(i, info)
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i
                break
            }
        }
        camera = Camera.open(cameraId)
        localCamera = LocalCamera(this,camera,this)
        frameLocalVideoView.addView(localCamera)
        camera.setPreviewCallback(this)
    }
    fun loadBitmapFromView(v: View): Bitmap {
        val b = Bitmap.createBitmap(v.layoutParams.width, v.layoutParams.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)
        return b
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            var uri: Uri = data.data!!
            try{
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uri)
                iv_Image.setImageBitmap(bitmap)
                detection.detectAndFrame(bitmap)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
    override fun faceDetectionResult(result: Array<Face>) {

        iv_Image.setImageBitmap(AppUtils.drawFaceRectanglesOnBitmap(bitmap,result))
    }
}