package test.isavanzados.demobarcodereader

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.AndroidXMapFragment;
import androidx.fragment.app.FragmentActivity;
import com.here.android.mpa.cluster.ClusterLayer
import com.here.android.mpa.common.GeoPosition
import com.here.android.mpa.common.PositioningManager
import com.here.android.mpa.mapping.MapMarker
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_here.*
import java.io.File
import java.lang.ref.WeakReference
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.concurrent.fixedRateTimer

class HereActivity : FragmentActivity() {
    private var posManager :PositioningManager? = null
    private var paused = false
    private var map :Map? = null
    private var positionListener :PositioningManager.OnPositionChangedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    private fun initialize(){
        setContentView(R.layout.activity_here)
        val finalMapFragment = supportFragmentManager.findFragmentById(R.id.mapfragment) as AndroidXMapFragment
        val success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(
            applicationContext.getExternalFilesDir(null).toString() + File.separator + ".here-maps"
        )

        if (!success){
            Toasty.error(this, "Unable to set isolated disk cache path.").show()
        }else{
            finalMapFragment.init(object :OnEngineInitListener{
                override fun onEngineInitializationCompleted(error :OnEngineInitListener.Error?) {
                    if (error == OnEngineInitListener.Error.NONE){
                        map = finalMapFragment.map!!

                        positionListener = object :PositioningManager.OnPositionChangedListener{
                            override fun onPositionFixChanged(
                                method: PositioningManager.LocationMethod?,
                                status: PositioningManager.LocationStatus?
                            ) {

                            }

                            override fun onPositionUpdated(
                                locationMethod: PositioningManager.LocationMethod,
                                position: GeoPosition?,
                                isMapMatched: Boolean
                            ) {
                                if (!paused) {
                                    tvTitle.text = position!!.coordinate.toString()
                                    map!!.setCenter(position.coordinate, Map.Animation.BOW, Map.MOVE_PRESERVE_ZOOM_LEVEL, Map.MOVE_PRESERVE_ORIENTATION, Map.MOVE_PRESERVE_TILT)
                                }
                            }
                        }
                        posManager = PositioningManager.getInstance()
                        posManager?.addListener(WeakReference(positionListener))
                        // display position indicator
                        map!!.positionIndicator.isVisible = true
                        map!!.setLandmarksVisible(true)
                        map?.setCenter(GeoCoordinate(21.8806055,-102.2986796),Map.Animation.NONE,17.0, Map.MOVE_PRESERVE_ORIENTATION, 35f)
                        if(posManager!!.start(PositioningManager.LocationMethod.GPS_NETWORK))
                            Toasty.info(this@HereActivity, "Posmanafer Initialized").show()
                        else
                            Toasty.error(this@HereActivity, "Posmanafer couldn't initialize").show()
                    }else{
                        Toasty.error(this@HereActivity, "ERROR: Cannot initialize AndroidXMapFragment: " + error?.details).show()
                    }
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        paused = false
        if (posManager!=null){
            posManager!!.start(PositioningManager.LocationMethod.GPS_NETWORK)
        }
    }

    override fun onPause() {
        if (posManager!=null){
            posManager!!.stop()
        }
        super.onPause()
        paused = true
    }

    override fun onDestroy() {
        if (posManager!=null){
            posManager!!.removeListener(positionListener!!)
        }
        map = null
        super.onDestroy()
    }
}
