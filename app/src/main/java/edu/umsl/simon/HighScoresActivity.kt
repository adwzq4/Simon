package edu.umsl.simon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_high_scores.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HighScoresActivity : AppCompatActivity() {
    private var db: TopScoreDatabase? = null
    private var topScoreDao: TopScoreDao? = null
    private var topScoreList: List<RoomTopScore>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_scores)

        GlobalScope.launch {
            db = TopScoreDatabase.getInstance(context = this@HighScoresActivity)
            topScoreDao = db?.TopScoreDao()

            if (db?.TopScoreDao()?.getTopScores() == null) {
                val s1 = RoomTopScore(score = 0, creationDate = "--/--/----")
                with(topScoreDao){ this?.insert(s1) }
            }

            topScoreList = db?.TopScoreDao()?.getTopScores()
        }

        Thread.sleep(500)

        newGameButton.setOnClickListener{
            val intent = Intent(this, GameActivity::class.java)
            val difficulty = this.intent.getIntExtra("difficulty", 1)
            val soundOn = this.intent.getBooleanExtra("soundOn", false)
            intent.putExtra("difficulty", difficulty)
            intent.putExtra("soundOn", soundOn)
            startActivity(intent)
            finish()
        }

        val lastScore = this.intent.getIntExtra("lastScore", 0)
        lastScoreView.text = getString(R.string.last_score).format(lastScore)

        highScoresRecyclerView.layoutManager = LinearLayoutManager(this)
        highScoresRecyclerView.adapter = HighScoresAdapter()
    }

    inner class HighScoresAdapter : RecyclerView.Adapter<HighScoresAdapter.ScoreHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HighScoresAdapter.ScoreHolder {
            val inflater = LayoutInflater.from(this@HighScoresActivity)
            val itemView = inflater.inflate(R.layout.top_score_layout, parent, false)
            return ScoreHolder(itemView)
        }

        override fun getItemCount(): Int = topScoreList?.size ?: 1

        override fun onBindViewHolder(holder: ScoreHolder, position: Int) {
            val topScore = topScoreList?.get(position)
            holder.bindScore(topScore, position)
        }

        inner class ScoreHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView){
            private val scoreView: TextView = itemView.findViewById(R.id.scoreView)
            private val rankView: TextView = itemView.findViewById(R.id.rankView)
            private val dateView: TextView = itemView.findViewById(R.id.dateView)

            fun bindScore(score: RoomTopScore?, position: Int){
                rankView.text = (position + 1).toString()
                scoreView.text = score!!.score.toString()
                dateView.text = score.creationDate
            }
        }
    }
}