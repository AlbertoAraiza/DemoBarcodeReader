package test.isavanzados.demobarcodereader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnScan.setOnClickListener {
            val intent = Intent(this@MainActivity, ScanActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
            startActivity(intent)
        }
    }
}
