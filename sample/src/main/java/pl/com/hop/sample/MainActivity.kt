package pl.com.hop.sample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import pl.com.hop.components.HopLoadingButton

class MainActivity : AppCompatActivity() {

    private var loadingMap = mutableMapOf(R.id.btn1 to false, R.id.btn2 to false, R.id.btn3 to false)

    private val clickListener = View.OnClickListener { btn ->
        val isLoading = !requireNotNull(loadingMap[btn.id])
        (btn as HopLoadingButton).loading = isLoading
        loadingMap[btn.id] = isLoading
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadingMap.keys.forEach { id ->
            findViewById<View>(id).setOnClickListener(clickListener)
        }
    }
}
