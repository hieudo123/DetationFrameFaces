package com.example.stackexchange.detectframefaces.facedetection

import android.app.ProgressDialog
import android.content.Context
import com.microsoft.projectoxford.face.FaceServiceRestClient
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.AsyncTask
import android.graphics.Bitmap
import com.microsoft.projectoxford.face.contract.Face
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import android.content.DialogInterface
import com.microsoft.projectoxford.face.FaceServiceClient

import com.microsoft.projectoxford.face.FaceServiceClient.*
import java.io.OutputStream


class FaceDetection(context: Context,impFaceDetection:ImpFaceDetection,faceServiceClient : FaceServiceClient ) {

    lateinit var context: Context

    lateinit var impFaceDetection:ImpFaceDetection
    lateinit var faceServiceClient : FaceServiceClient
    lateinit var detectionProcess : ProgressDialog

    init {
        this.context = context
        this.impFaceDetection = impFaceDetection
        detectionProcess = ProgressDialog(context)
        this.faceServiceClient = faceServiceClient

    }

    fun detectAndFrame(imageBitmap: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream as OutputStream?)
        val inputStream = ByteArrayInputStream(outputStream.toByteArray())

        val detectTask = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<InputStream, String, Array<Face>>() {
            internal var exceptionMessage = ""

            override fun doInBackground(vararg params: InputStream): Array<Face>? {
                try {
                    var list :Array<FaceServiceClient.FaceAttributeType>
                    list = arrayOf(FaceServiceClient.FaceAttributeType.Gender,FaceServiceClient.FaceAttributeType.Age)
                    publishProgress("Detecting...")
                    val result = faceServiceClient.detect(
                        params[0],
                        true,
                        false,list)// returnFaceAttributes:
                    // returnFaceLandmarks
                    if (result == null) {
                        publishProgress(
                            "Detection Finished. Nothing detected"
                        )
                        return null
                    }
                    publishProgress(
                        String.format(
                            "Detection Finished. %d face(s) detected",
                            result!!.size
                        )
                    )
                    return result
                } catch (e: Exception) {
                    exceptionMessage = String.format(
                        "Detection failed: %s", e.message
                    )
                    return null
                }

            }

            override fun onPreExecute() {
                //TODO: show progress dialog
                detectionProcess.show()
            }

            override fun onProgressUpdate(vararg progress: String) {
                //TODO: update progress
                detectionProcess.setMessage(progress[0])
            }

            override fun onPostExecute(result: Array<Face>?) {
                //TODO: update face frames
                detectionProcess.dismiss()

                if (exceptionMessage != "") {
                    showError(exceptionMessage)
                }
                if (result == null) return
                impFaceDetection.faceDetectionResult(result)
                imageBitmap.recycle()
            }
        }

        detectTask.execute(inputStream)
    }
    private fun showError(message: String) {
        AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id -> })
            .create().show()
    }
}