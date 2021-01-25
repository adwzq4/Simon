package edu.umsl.simon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_game_view.*
import kotlinx.android.synthetic.main.fragment_game_view.view.*

class GameViewFragment : Fragment() {
    interface StateListener{
        fun yellowButtonPressed()
        fun greenButtonPressed()
        fun blueButtonPressed()
        fun redButtonPressed()
    }

    var listener: StateListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game_view, container, false)

        view.yellowButton.setOnClickListener{ listener?.yellowButtonPressed() }
        view.greenButton.setOnClickListener{ listener?.greenButtonPressed() }
        view.blueButton.setOnClickListener{ listener?.blueButtonPressed() }
        view.redButton.setOnClickListener{ listener?.redButtonPressed() }

        return view
    }

    fun changeButtonClickability(boolean: Boolean){
        yellowButton.isClickable = boolean
        redButton.isClickable = boolean
        greenButton.isClickable = boolean
        blueButton.isClickable = boolean
    }

    fun highlightButton(color: String, boolean: Boolean){
            when (color){
                "yellow" -> yellowButton.isPressed = boolean
                "blue" -> blueButton.isPressed = boolean
                "green" -> greenButton.isPressed = boolean
                else -> redButton.isPressed = boolean
            }
    }
}