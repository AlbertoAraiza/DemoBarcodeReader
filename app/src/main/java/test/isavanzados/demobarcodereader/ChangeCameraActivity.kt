package test.isavanzados.demobarcodereader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ChangeCameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_camera)
        val intent = Intent(this, ScanActivity::class.java)
        startActivity(intent)
    }
}
