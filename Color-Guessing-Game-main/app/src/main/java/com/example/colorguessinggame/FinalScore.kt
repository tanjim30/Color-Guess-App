package com.example.colorguessinggame

import android.app.ActivityOptions
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import org.w3c.dom.Text

class FinalScore : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final_score)

        val introText = findViewById<TextView>(R.id.yourFinalScoreWas)

        val finScoreText = findViewById<TextView>(R.id.finalScoreText)
        finScoreText.setText(intent.getStringExtra("Score"))

        val rightAnswerText = findViewById<TextView>(R.id.rightAnswer)
        rightAnswerText.text = "Right answer was " + intent.getStringExtra("Right Answer").toString()

        val square = findViewById<TextView>(R.id.colorSquareFinal)
        val rC = intent.getStringExtra("Right Color").toString()
        if (rC != null) {
            square.setBackgroundColor(rC.toInt())
        }

        if (rC != null) {
            Log.i("Right Color", rC)
        }

        val newHS = findViewById<TextView>(R.id.newHighScore)
        if(intent.getStringExtra("Beat") == "true")
            newHS.setText("Congrats! You got a new high score!")
        else
            newHS.isVisible = false

        introText.typeface = Typeface.createFromAsset(assets, "crayons.ttf")
        finScoreText.typeface = Typeface.createFromAsset(assets, "crayons.ttf")
        newHS.typeface = Typeface.createFromAsset(assets, "crayons.ttf")
        rightAnswerText.typeface = Typeface.createFromAsset(assets, "crayons.ttf")

        when (this.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                introText.setTextColor(Color.WHITE)
                finScoreText.setTextColor(Color.WHITE)
                newHS.setTextColor(Color.WHITE)
            }
            Configuration.UI_MODE_NIGHT_NO -> {}
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {}
        }

        val restart = findViewById<Button>(R.id.restartButton)
        restart.setOnClickListener{ view ->
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            startActivity(Intent(this, MainActivity::class.java), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }
}