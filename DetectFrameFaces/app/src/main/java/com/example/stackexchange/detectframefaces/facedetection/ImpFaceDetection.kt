package com.example.stackexchange.detectframefaces.facedetection

import com.microsoft.projectoxford.face.contract.Face

interface ImpFaceDetection {
    fun faceDetectionResult(result: Array<Face>)
}