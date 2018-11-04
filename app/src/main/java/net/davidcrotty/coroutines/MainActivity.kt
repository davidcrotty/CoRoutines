package net.davidcrotty.coroutines

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Objective: Run some co-routines.
 * Network some co-routines.
 * Discover if main thread callback is not needed when using default (blocking vs non blocking CPU ops)
 * Testability of co-routines on JVM and ART.
 */
class MainActivity : AppCompatActivity() {

    val job = Job()
    val coroutineContext: CoroutineContext
        get() = Dispatchers.Main //just this works, is it sharing //w the UI thread?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainThreadWorkSliced()
        Toast.makeText(this, "Act launched", Toast.LENGTH_SHORT).show()
    }

    fun mainThreadWorkSliced() {
        GlobalScope.launch(coroutineContext) { //background thread pool, main by default!
            Log.d("scope", "Thread: ${Thread.currentThread().name}") //should be background
            delay(5000) // has suspended this portion of the thread, but is running on the main thread. This is a fibre
            return@launch Toast.makeText(this@MainActivity, "GlobalScope", Toast.LENGTH_SHORT).show() //yield is ui thread
        }
    }

    override fun onPause() {
        super.onPause()
        job.cancel()
    }
}
