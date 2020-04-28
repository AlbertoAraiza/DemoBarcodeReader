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
import com.here.android.mpa.mapping.MapMarker
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_here.*
import java.io.File
import kotlin.concurrent.fixedRateTimer

class HereActivity : FragmentActivity() {

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
                        val map = finalMapFragment.map!!
                        val geoLocation = GeoCoordinate(21.8761704,-102.2771799)
                        val mapMarker = MapMarker(geoLocation)
                        val clusterLayer = ClusterLayer()

                        clusterLayer.addMarker(mapMarker)
                        clusterLayer.addMarker(MapMarker(GeoCoordinate(21.8806055,-102.2986796)))
                        map.setSafetySpotsVisible(true)
                        map.setLandmarksVisible(true)
                        map.addClusterLayer(clusterLayer)
                        //21.8761704,-102.2771799
                        map.setCenter(geoLocation, Map.Animation.BOW, 17.8, Map.MOVE_PRESERVE_ORIENTATION,45f)
                    }else{
                        Toasty.error(this@HereActivity, "ERROR: Cannot initialize AndroidXMapFragment: " + error?.details).show()
                    }
                }
            })
        }
    }
}
