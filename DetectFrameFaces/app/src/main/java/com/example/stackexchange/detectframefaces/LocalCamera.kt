package com.example.stackexchange.detectframefaces

import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView

class LocalCamera(context: Context,camera : Camera,cameraPreviewCallback: Camera.PreviewCallback) : SurfaceView(context),SurfaceHolder.Callback {
    lateinit var mholder : SurfaceHolder
    lateinit var camera : Camera
    init {
        this.camera = camera
        mholder = holder
        mholder.addCallback(this)
    }
    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {

    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
        var param : Camera.Parameters = camera.parameters

        if(this.resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE){
            param.set("orientation","portrait")
            camera.setDisplayOrientation(90)
            param.setRotation(90)
        }
        else{
            param.set("orientation","landscape")
            camera.setDisplayOrientation(0)
            param.setRotation(0)
        }
        camera.parameters = param
        camera.setPreviewDisplay(mholder)
        camera.startPreview()

    }


}