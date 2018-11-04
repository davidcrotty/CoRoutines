package net.davidcrotty.coroutines

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

/**
 * Objective: Run some co-routines.
 * Network some co-routines.
 * Discover if main thread callback is not needed when using default (blocking vs non blocking CPU ops)
 * Testability of co-routines on JVM and ART.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
