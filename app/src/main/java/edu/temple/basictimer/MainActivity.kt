package edu.temple.basictimer

import android.R.attr.text
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

const val myKey = "countdown"
class MainActivity : AppCompatActivity() {

    var timerBinder: TimerService.TimerBinder? = null
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            timerBinder = service as TimerService.TimerBinder
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            timerBinder = null
        }

    }

    val displayTextView: TextView by lazy {
        findViewById<TextView>(R.id.textView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
        TimerInfo.currentTime.collect {
            displayTextView.text = it.toString()
        }
    }

        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )
        findViewById<Button>(R.id.startButton).setOnClickListener { button->
            //check status,
        // if paused then unpause, if unpaused then pause
            timerBinder?.let { it1 ->
                if(!it1.isRunning){
                    timerBinder?.start(100)
                    (button as Button).text = "pause"
                }
                else if (it1.isPaused) {
                    timerBinder?.pause()
                    (button as Button).text = "pause"
                }
                else if (!it1.isPaused){
                    timerBinder?.pause()
                    (button as Button).text = "resume"
                }

            }
        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            timerBinder?.stop()
            findViewById<Button>(R.id.startButton).text = "Start"
        }
    }
}