package edu.umsl.simon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var difficulty: String = ""
    private var soundSwitchState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        soundSwitch.setOnClickListener { soundSwitchState = soundSwitch.isChecked }

        startButton.setOnClickListener{
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra(
                "difficulty",
                when (difficulty){
                    "Hard" -> 3
                    "Medium" -> 2
                    else -> 1
                }
            )
            intent.putExtra("soundOn", soundSwitchState)
            startActivity(intent)
        }

        highScoresButton.setOnClickListener{
            val intent = Intent(this, HighScoresActivity::class.java)
            startActivity(intent)
        }

        val spinner: Spinner = difficultyMenu
        spinner.onItemSelectedListener = this

        ArrayAdapter.createFromResource(
            this,
            R.array.difficulty_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) { difficulty = "Easy" }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) { difficulty = parent.getItemAtPosition(position).toString() }
    }
}