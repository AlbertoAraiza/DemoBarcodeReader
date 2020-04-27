package test.isavanzados.demobarcodereader

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.View
import android.view.Window
import android.view.WindowManager
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
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        supportActionBar?.hide()
        ctx = this

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            askCameraPermision()
        } else {
            setupControls()
        }
    }

    private fun setupControls(){
        detector = BarcodeDetector.Builder(this).build()
        cameraSource = CameraSource.Builder(this, detector).setAutoFocusEnabled(true).build()
        svCamera.holder.addCallback(surfaceCallback)
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

    private val surfaceCallback = object : SurfaceHolder.Callback{
        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            cameraSource?.stop()
        }

        override fun surfaceCreated(holder: SurfaceHolder?) {
            try{
                cameraSource?.start(holder)
            }catch (e:Exception){
                Toasty.info(applicationContext, "Algo salio mal").show()
                e.printStackTrace()
            }
        }
    }

    private val processor = object : Detector.Processor<Barcode>{
        override fun release() {}

        override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
            if (detections != null && detections.detectedItems.size()!=0){
                runOnUiThread {
                    try {
                        cameraSource?.release()
                        val codes: SparseArray<Barcode> = detections.detectedItems
                        val code = codes.valueAt(0)
                        val task = MyAsyncTask(ctx!!)
                        task.execute(code.displayValue)
                        Log.e("Scanner", code.displayValue)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    companion object {
        class MyAsyncTask internal constructor(context: Context) : AsyncTask<String, String, String?>() {
            private val ctx: Context = context

            override fun doInBackground(vararg params: String?): String? {
                var msg = ""
                try {

                }catch (e:Exception){
                    e.printStackTrace()
                    msg = "error"
                }
                return msg
            }

            override fun onPostExecute(result: String?) {
                if (!result.isNullOrEmpty())
                    Toasty.info(ctx, result).show()
                val i = Intent(ctx,MainActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                ContextCompat.startActivity(ctx, i, null)
            }
        }
    }
}
