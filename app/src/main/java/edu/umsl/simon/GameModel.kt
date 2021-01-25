package edu.umsl.simon

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameModel: ViewModel() {
    var sequence = ArrayList<String>()
    var currentScore = MutableLiveData<Int>()
    var highScore = MutableLiveData<Int>()
    var currentIndex: Int = 0
    var listener: Listener? = null
    var handler: Handler? = null
    var lengthOfFlash: Long = 1000
    var whoseTurn = ""

    interface Listener {
        fun displaySequence(boolean: Boolean)
        fun startPlayersTurn()
        fun playBeep()
    }

    fun startSequence(){
        if (handler == null) { handler = Handler() }
        handler?.postDelayed(runnable, 2000)
    }

    private fun triggerSequence() { handler?.post(runnable) }

    private var runnable: Runnable = Runnable{
        listener?.displaySequence(true)
        listener?.playBeep()
        handler?.postDelayed({ listener?.displaySequence(false) }, lengthOfFlash)

        if (currentIndex < sequence.size - 1) {
            handler?.postDelayed({
                currentIndex++
                triggerSequence()
                },
                lengthOfFlash + 250)
        }

        else {
            handler?.postDelayed({ listener?.startPlayersTurn()}, lengthOfFlash + 250)
        }
    }

    fun addToSequence(difficulty: Int){
        for (i in 0 until difficulty) {
            sequence.add(listOf("yellow", "blue", "green", "red").random())
        }
    }
}