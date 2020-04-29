package test.isavanzados.demobarcodereader

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_scan.*


class ScanActivity : AppCompatActivity() {
    var ctx : Context? = null
    var detector: BarcodeDetector? = null
    var cameraSource: CameraSource? = null

    private val REQUEST_CAMERA_PERMISSION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        ctx = this

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            askCameraPermision()
        } else {
            setupControls()
        }


        btnChangeCamera.setOnClickListener{
            if (checkCameraFront(this)) {
                if (cameraSource != null) {
                    cameraSource!!.release()
                }
                if (cameraFacing == CameraSource.CAMERA_FACING_FRONT)
                    cameraFacing = CameraSource.CAMERA_FACING_BACK
                else
                    cameraFacing = CameraSource.CAMERA_FACING_FRONT
                finish()
                startActivity(intent)
            }else{
                Toasty.info(this, "Este dispoitivo no tiene camara frontal")
            }
        }
    }

    private fun setupControls(){
        detector = BarcodeDetector.Builder(this).build()
        cameraSource = CameraSource.Builder(this, detector).setFacing(cameraFacing).setAutoFocusEnabled(true).build()
        svCamera.holder.addCallback(surfaceHolderCallback)
        detector?.setProcessor(processor)
    }

    private fun askCameraPermision(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.isNotEmpty()){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val i = Intent(this, ScanActivity::class.java)
                startActivity(i)
                finish()
            }else{
                Toasty.info(this, "Permiso no otorgado").show()
            }
        }
    }


    fun checkCameraFront(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
    }

    val processor = object : Detector.Processor<Barcode>{
        override fun release() {}

        override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
            if (detections != null && detections.detectedItems.size()!=0){
                runOnUiThread {
                    try {
                        cameraSource?.release()
                        val codes: SparseArray<Barcode> = detections.detectedItems
                        val code = codes.valueAt(0)
                        Toasty.success(ctx!!, code.displayValue).show()
                        //val task = MyAsyncTask(ctx!!)
                        //task.execute(code.displayValue)
                        Log.e("Scanner", code.displayValue)
                    }catch (e:Exception){
                        e.printStackTrace()
                        finish()
                    }
                }
            }
        }
    }
    val surfaceHolderCallback = object: SurfaceHolder.Callback{
        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            cameraSource!!.stop()
        }

        override fun surfaceCreated(holder: SurfaceHolder?) {
            try{
                cameraSource!!.start(holder)
            }catch (e:Exception){
                Toasty.error(applicationContext, "Algo salio mal " + e.message).show()
                e.printStackTrace()
            }
        }
    }


    companion object{
        var cameraFacing = CameraSource.CAMERA_FACING_BACK
    }
}
