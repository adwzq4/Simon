package edu.umsl.simon

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import kotlinx.android.synthetic.main.fragment_game_view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class GameActivity : AppCompatActivity() {
    private lateinit var model: GameModel
    private var viewFragment: GameViewFragment? = null
    private val runnable = Runnable { gameOver() }
    private var handler = Handler()
    private var difficulty = 1
    private var soundOn = false
    private var timeToPress: Long = 2000
    private var db: TopScoreDatabase? = null
    private var topScoreDao: TopScoreDao? = null
    private lateinit var highBeepMP: MediaPlayer
    private lateinit var mediumHighBeepMP: MediaPlayer
    private lateinit var mediumLowBeepMP: MediaPlayer
    private lateinit var lowBeepMP: MediaPlayer
    private lateinit var owMP: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)
        if (supportActionBar != null) { supportActionBar?.hide() }

        soundOn = this.intent.getBooleanExtra("soundOn", false)
        Log.d("sound", soundOn.toString())

        highBeepMP = MediaPlayer.create(this, R.raw.beep_high)
        mediumHighBeepMP = MediaPlayer.create(this, R.raw.beep_medium_high)
        mediumLowBeepMP = MediaPlayer.create(this, R.raw.beep_medium_low)
        lowBeepMP = MediaPlayer.create(this, R.raw.beep_low)
        owMP = MediaPlayer.create(this, R.raw.ow)

        difficulty = this.intent.getIntExtra("difficulty", 1)

        timeToPress /= difficulty

        model = ViewModelProvider(this).get(GameModel::class.java)

        model.lengthOfFlash /= difficulty

        GlobalScope.launch {
            db = TopScoreDatabase.getInstance(context = this@GameActivity)
            topScoreDao = db?.TopScoreDao()
            model.highScore.postValue(db?.TopScoreDao()?.getHighScore()?.score ?: 0)
        }

        Thread.sleep(500)

        model.currentScore.observe(this, Observer {
            currentScoreView.text = getString(R.string.current_score).format(model.currentScore.value)
        })
        model.highScore.observe(this, Observer{
            highScoreView.text = getString(R.string.high_score).format(model.highScore.value)
        })

        viewFragment = supportFragmentManager.findFragmentById(R.id.gameContainer) as? GameViewFragment
        if (viewFragment == null) {
            viewFragment = GameViewFragment()
            supportFragmentManager.beginTransaction().add(R.id.gameContainer, viewFragment!!).commit()
        }

        viewFragment?.listener = object: GameViewFragment.StateListener{
            override fun yellowButtonPressed() { checkButtonPress("yellow") }
            override fun greenButtonPressed() { checkButtonPress("green") }
            override fun blueButtonPressed() { checkButtonPress("blue") }
            override fun redButtonPressed() { checkButtonPress("red") }
        }

        model.listener = object: GameModel.Listener {
            override fun displaySequence(boolean: Boolean) {
                if (model.currentIndex < model.sequence.size) {
                    viewFragment?.highlightButton(model.sequence[model.currentIndex], boolean)
                }
            }

            override fun startPlayersTurn() { playersTurn() }

            override fun playBeep() { beep(model.sequence[model.currentIndex]) }
        }
    }

    override fun onStart() {
        super.onStart()
        when (model.whoseTurn) {
            "" -> {
                model.currentScore.value = 0
                simonsTurn()
            }
            "simon" -> viewFragment?.changeButtonClickability(false)
            "player" -> viewFragment?.changeButtonClickability(true)
            else -> gameOverDisplay()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewFragment?.listener = null
        handler.removeCallbacksAndMessages(null)
        model.handler?.removeCallbacksAndMessages(null)
    }

    fun checkButtonPress(color: String) {
        handler.removeCallbacks(runnable)
        if (model.sequence[model.currentIndex] == color) {
            beep(color)
            model.currentIndex++
            model.currentScore.value = model.currentScore.value?.plus(1)

            if (model.currentScore.value!! > model.highScore.value!!) {
                model.highScore.value = model.currentScore.value!!
            }

            if (model.currentIndex == model.sequence.size) { simonsTurn() }
            else { handler.postDelayed(runnable, timeToPress) }
        }
        else { gameOver() }
    }

    fun simonsTurn() {
        model.currentIndex = 0
        model.whoseTurn = "simon"
        viewFragment?.changeButtonClickability(false)
        model.addToSequence(difficulty)
        model.startSequence()
    }

    fun playersTurn() {
        viewFragment?.changeButtonClickability(true)
        model.currentIndex = 0
        model.whoseTurn = "player"
        handler.postDelayed(runnable, timeToPress)
    }

    private fun gameOver() {
        model.whoseTurn = "game_over"
        viewFragment?.listener = null
        if (soundOn) { owMP.start() }

        GlobalScope.launch {
            db = TopScoreDatabase.getInstance(context = this@GameActivity)
            topScoreDao = db?.TopScoreDao()
            db?.TopScoreDao()?.insert(
                RoomTopScore(
                    score = model.currentScore.value!!,
                    creationDate = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date())
                )
            )
            db?.TopScoreDao()?.deleteTopScore()
        }

        gameOverDisplay()
    }

    private fun gameOverDisplay() {
        viewFragment?.highlightButton(model.sequence[model.currentIndex], true)
        gameOverTextView.text = getString(R.string.gameOverText)

        val intent = Intent(this, HighScoresActivity::class.java)
        intent.putExtra("lastScore", model.currentScore.value)
        intent.putExtra("difficulty", difficulty)
        intent.putExtra("soundOn", soundOn)
        handler.postDelayed({
            startActivity(intent)
            model.listener = null
            finish()
        }, 3000)
    }

    fun beep(color: String){
        if (soundOn) when (color) {
            "blue" -> highBeepMP.start()
            "green" -> mediumHighBeepMP.start()
            "yellow" -> mediumLowBeepMP.start()
            else -> lowBeepMP.start()
        }
    }
}