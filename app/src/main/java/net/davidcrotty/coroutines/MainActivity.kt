package net.davidcrotty.coroutines

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
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
        get() = Dispatchers.Main //everything ran on the UI thread, distributed via fibres

    private val client = Retrofit.Builder().client(
        OkHttpClient()
    )
    .baseUrl("https://swapi.co/api/")
    .addConverterFactory(
        GsonConverterFactory.create()
    )
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .build()
    .create(SWAPI::class.java)

    interface SWAPI {
        @GET("people/1")
        fun people() : Deferred<People>
    }

    data class People(val name: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        networkCall()
        Toast.makeText(this, "Act launched", Toast.LENGTH_SHORT).show()
    }

    fun networkCall() {
        GlobalScope.launch(coroutineContext) {
            Log.d("scope", "Thread: ${Thread.currentThread().name}") //main thread
            val person = try {
                crappyCall()
            } catch (e : java.lang.Exception) {
                "Couldnt find a person"
            }
            Toast.makeText(this@MainActivity, "Received call ${person}", Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun crappyCall() : String {
        val person = client.people().await()
        return person.name
    }

    fun mainThreadWorkSliced() {
        GlobalScope.launch(coroutineContext) { //background thread pool, main by default!
            Log.d("scope", "Thread: ${Thread.currentThread().name}") //main thread
//            delay(5000) // has suspended this portion of the thread, but is running on the main thread. This is a fibre
            count(0..10)
            return@launch Toast.makeText(this@MainActivity, "GlobalScope", Toast.LENGTH_SHORT).show() //yield is ui thread
        }
    }

    suspend fun count(range: IntRange) {
        for(i in range) {
            Log.d("scope", "Count: ${Thread.currentThread().name}") //should be background
            delay(1000)
        }
    }

    override fun onPause() {
        super.onPause()
        job.cancel()
    }
}
