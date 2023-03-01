package com.example.trafficlight

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible


class MainActivity : Activity() {

    private lateinit var startStopBtn: Button
    private lateinit var pauseBtn: Button
    private lateinit var timer: CountDownTimer
    private lateinit var remainingTimer: CountDownTimer
    private lateinit var countTime: TextView
    private lateinit var input: EditText
    private lateinit var switch: SwitchCompat
    private var buttons = ArrayList<Button>()
    private var counter = 1
    private var timeMs: Long = 2000
    private var remainingMs: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        startStopBtn = findViewById(R.id.buttonStart)
        pauseBtn = findViewById(R.id.buttonPause)
        countTime = findViewById(R.id.textTimer)
        input = findViewById(R.id.editTimer)
        switch = findViewById(R.id.switch1)

        buttons.add(findViewById(R.id.buttonRed))
        buttons.add(findViewById(R.id.buttonYellow))
        buttons.add(findViewById(R.id.buttonGreen))

        timer = createTimer(timeMs)
        remainingTimer = createTimer(timeMs)

        setSwitchListener()
    }


    fun startStop(view: View) {
        if (startStopBtn.text == resources.getString(R.string.start)) {
            startStopBtn.text = resources.getString(R.string.stop)
            start()
        } else {
            startStopBtn.text = resources.getString(R.string.start)
            stop()
        }
    }

    fun pauseUnpause(view: View) {
        if (pauseBtn.text == "Pause") {
            pause()
        } else {
            unpause()
        }
    }

    private fun changeTimer() {
        input.text.ifEmpty { input.setText("2000") }
        timeMs = input.text.toString().toLong()
        timer = createTimer(timeMs)
    }

    private fun unpause() {
        input.isVisible = false
        remainingTimer = createTimer(remainingMs).start()
        pauseBtn.text = resources.getString(R.string.pause)
        changeTimer()
    }

    private fun pause() {
        input.isVisible = true
        timer.cancel()
        remainingTimer.cancel()
        if (countTime.text == resources.getString(R.string.zero)) {
            Toast.makeText(applicationContext, "Nothing to Pause", Toast.LENGTH_SHORT).show()
        } else pauseBtn.text = resources.getString(R.string.unpause)
    }

    private fun start() {
        input.isVisible = false
        if (!switch.isChecked) {
            buttons[0].isPressed = true
            countTime.setTextColor(resources.getColor(R.color.red))
        }
        changeTimer()
        timer.start()
        pauseBtn.text = resources.getString(R.string.pause)
    }

    private fun stop() {
        input.isVisible = true
        timer.cancel()
        remainingTimer.cancel()
        countTime.setTextColor(resources.getColor(R.color.grey))
        countTime.text = resources.getString(R.string.zero)
        pauseBtn.text = resources.getString(R.string.pause)
        buttons.forEach { it.isPressed = false }
        counter = 1
    }

    private fun setSwitchListener() {
        switch.setOnClickListener {
            if (switch.isChecked) {
                buttons[0].isPressed = false
                buttons[2].isPressed = false
                counter = 1
//                countTime.setTextColor(resources.getColor(R.color.yellow))
            }
        }
    }

    private fun changeLight() {
        if (switch.isChecked) {          // Мигающий желтый
            buttons[1].isPressed = !buttons[1].isPressed
        } else {                         // Стандартный режим
            if (counter < buttons.size) {
                buttons[counter].isPressed = true
                if (counter != 0) {
                    buttons[counter - 1].isPressed = false
                }
                counter += 1
            } else {
                buttons[counter - 1].isPressed = false
                buttons[0].isPressed = true
                counter = 1
            }
        }
    }

    private fun changeTimerColor() {
        if (buttons[0].isPressed) {
            countTime.setTextColor(resources.getColor(R.color.red))
        } else if (buttons[2].isPressed) {
            countTime.setTextColor(resources.getColor(R.color.green))
        } else {
            countTime.setTextColor(resources.getColor(R.color.yellow))
        }
    }

    private fun createTimer(ms: Long): CountDownTimer {
        val timer = object : CountDownTimer(ms, 100) {
            override fun onTick(millisUntilFinished: Long) {
                countTime.text = (millisUntilFinished / 100).toString()
                remainingMs = millisUntilFinished
            }

            override fun onFinish() {
                changeLight()
                changeTimerColor()
                timer.start()
            }
        }
        return timer
    }
}