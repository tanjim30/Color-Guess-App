package com.example.colorguessinggame

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.collections.ArrayList
import kotlin.math.abs


class GameScreen : AppCompatActivity() {
    lateinit var rightAnswertoSend : String
    lateinit var closestColorVal: String

    private lateinit var job : Job

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_screen)

        // what difficulty
        val mode = intent.getStringExtra("Mode")

        // Read in txt file of colors and their values
        val file : String = if(mode == "Easy")
            "colorsEasy.txt"
        else {
            "colorsHard.txt"
        }

        // load high score
        val highScoreField = findViewById<TextView>(R.id.highScoreText)
        highScoreField.typeface = Typeface.createFromAsset(assets, "crayons.ttf")

        val highScoreFileName : String = when (mode) {
            "Easy" -> "UserScoreEasy.txt"
            "Hard" -> "UserScoreHard.txt"
            else -> "UserScoreImpossible.txt"
        }

        val highScoreFile = File(this.filesDir, highScoreFileName)

        var highScore = 0

        // if first time playing game
        if(!highScoreFile.exists()) {
            highScoreFile.createNewFile()
            this.openFileOutput(highScoreFileName, Context.MODE_PRIVATE).use {
                it.write("0".toByteArray())
            }
            highScoreField.text = "High Score: 0"
        }
        else{
            // read in from file
            this.openFileInput(highScoreFileName).use { stream ->
                val text = stream.bufferedReader().use {
                    it.readText()
                }
                Log.i("from high score file", "LOADED: $text")

                highScore = text.toInt()
                highScoreField.text = "High Score: $highScore"
            }
        }

//        Log.i("file dir.", highScore.toString())

        // square that changes color
        val square = findViewById<Button>(R.id.colorSquare)

        // get option buttons
        val option1 = findViewById<Button>(R.id.option1)
        val option2 = findViewById<Button>(R.id.option2)
        val option3 = findViewById<Button>(R.id.option3)
        val option4 = findViewById<Button>(R.id.option4)


        val bufferReader = application.assets.open(file).bufferedReader()
        val data = bufferReader.use {
            it.readText()
        }
//        Log.i("DATA FILE", data)

        // colorNames stores names of colors, colorValues stores names values of colors
        val colorNames = ArrayList<String>()
        val colorValues = ArrayList<Int>()
        val line = data.split("\n")
        for(l in line){
            val s = l.split("\t")
            if(s.size >= 2) {
//                Log.i("split 1", s[0])
//                Log.i("split 2", s[1])

                colorNames.add(s[0])
                val rgbSplit = s[1].split(" ")
                val redS = rgbSplit[0].trim()
                val greenS = rgbSplit[1].trim()
                val blueS = rgbSplit[2].trim()
//                Log.i("r", redS)
//                Log.i("g", greenS)
//                Log.i("b", blueS)

                val red : Int = if(redS  == "0")
                    0
                else
                    redS.toInt()

                val green : Int = if(greenS == "0")
                    0
                else
                    greenS.toInt()

                val blue : Int = if(blueS == "0")
                    0
                else
                    blueS.toInt()

                val cToAdd = Color.rgb(red, green, blue)
//                Log.i("Converted to color", cToAdd.toString())
                colorValues.add(cToAdd)
            }
        }
//        Log.i("Check for size", colorValues.size.toString() + " " + colorNames.size.toString())

        // do it once to begin game
        var colors = oneGameLoop(colorValues, colorNames, square)
        var rightChoice = setOptions(option1, option2, option3, option4, colorNames, colors)

        var score = 0

        // changes as user gets right answers
        val scoreField = findViewById<TextView>(R.id.scoreText)
        scoreField.typeface = Typeface.createFromAsset(assets, "crayons.ttf")

        // for debugging, set invisible for now
        val colorDebugText = findViewById<TextView>(R.id.colorDebug)
        colorDebugText.setText(rightChoice.toString())
        colorDebugText.isVisible = false

        var timeLeft = 5

        var wrongAnswerPressed = false

        option1.setOnClickListener{ view ->
            if(rightChoice == 1) {
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                score += 1
                scoreField.text = "Score: $score"
                colors = oneGameLoop(colorValues, colorNames, square)
                rightChoice = setOptions(option1, option2, option3, option4, colorNames, colors)
                timeLeft = 6
            }
            else {
                view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                gameOver(score, highScore, highScoreFileName)
                wrongAnswerPressed = true
            }


//            Log.i("list of options", colors.toString())
            colorDebugText.text = colorNames[colors[0]]
        }
        option2.setOnClickListener{ view ->
            if(rightChoice == 2) {
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                score += 1
                scoreField.text = "Score: $score"
                colors = oneGameLoop(colorValues, colorNames, square)
                rightChoice = setOptions(option1, option2, option3, option4, colorNames, colors)
                timeLeft = 6
            }
            else {
                view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                gameOver(score, highScore, highScoreFileName)
                wrongAnswerPressed = true
            }


//            Log.i("list of options", colors.toString())
            colorDebugText.setText(colorNames[colors[0]])
        }
        option3.setOnClickListener{ view ->
            if(rightChoice == 3) {
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                score += 1
                scoreField.text = "Score: $score"
                colors = oneGameLoop(colorValues, colorNames, square)
                rightChoice = setOptions(option1, option2, option3, option4, colorNames, colors)
                timeLeft = 6
            }
            else {
                view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                gameOver(score, highScore, highScoreFileName)
                wrongAnswerPressed = true
            }


//            Log.i("list of options", colors.toString())
            colorDebugText.text = colorNames[colors[0]]
        }
        option4.setOnClickListener{ view ->
            if(rightChoice == 4) {
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                score += 1
                scoreField.text = "Score: $score"
                colors = oneGameLoop(colorValues, colorNames, square)
                rightChoice = setOptions(option1, option2, option3, option4, colorNames, colors)
                timeLeft = 6
            }
            else {
                view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                gameOver(score, highScore, highScoreFileName)
                wrongAnswerPressed = true
            }

//            Log.i("list of options", colors.toString())
            colorDebugText.text = colorNames[colors[0]]
        }

        val timerField = findViewById<TextView>(R.id.timerText)
        timerField.typeface = Typeface.createFromAsset(assets, "crayons.ttf")
        // timer stuff
        if(mode == "Impossible"){
            timerField.text = timeLeft.toString()
            job = lifecycleScope.launch {
                var timerValid = true
                while(timerValid){
                    delay(1000)
                    this@GameScreen.findViewById<ConstraintLayout>(R.id.gamescreenlayout).performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    timeLeft -= 1
                    timerField.text = timeLeft.toString()
                    if(timeLeft == 0 && !wrongAnswerPressed) {
                        Log.i("WRONG ANSWER", wrongAnswerPressed.toString())
                        timerValid = false
                        gameOver(score, highScore, highScoreFileName)
                    }
                }
            }
        }

        // set colors for score and high score
        when (this.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                highScoreField.setTextColor(Color.WHITE)
                scoreField.setTextColor(Color.WHITE)
                timerField.setTextColor(Color.WHITE)
            }
            Configuration.UI_MODE_NIGHT_NO -> {}
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {}
        }
    }

    private fun getNewColor(): Int {
        var red = (0..255).random()
        var green = (0..255).random()
        var blue = (0..255).random()
        return Color.rgb(red, green, blue)
    }

    private fun gameOver(curScore: Int, highScore: Int, highScoreFileName: String) {
        val intent = Intent(this, FinalScore::class.java)
        intent.putExtra("Score", curScore.toString())
        intent.putExtra("Right Answer", rightAnswertoSend)
        intent.putExtra("Right Color", closestColorVal.toString())

        if(curScore > highScore){
            this.openFileOutput(highScoreFileName, Context.MODE_PRIVATE).use {
                it.write(curScore.toString().toByteArray())
            }
            intent.putExtra("Beat","true")
        }

        else
            intent.putExtra("Beat","false")

        if(highScoreFileName == "UserScoreImpossible.txt")
            job.cancel()

        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    private fun oneGameLoop(colorValues: ArrayList<Int>, colorNames: ArrayList<String>, square: Button): ArrayList<Int> {
        fun List<Int>.closestValue(value: Int) = minByOrNull { abs(value - it) }

        var newCol = getNewColor()
        var closestVal = colorValues.closestValue(newCol)
        if (closestVal != null) {
            square.setBackgroundColor(closestVal)
            closestColorVal = closestVal.toString()
        }
//        Log.i("Check for nearest", "old val: $newCol closest val: $closestVal")

        // find index of nearest value
        var indexOfClosest = 0
        for(i in 0 until colorNames.size)
            if(colorValues[i] == closestVal)
                indexOfClosest = i

//        Log.i("Color of closest", colorNames[indexOfClosest])
//        Log.i("Index of right answer", indexOfClosest.toString())

        // correct answer choice
        var rightAnswer = colorNames[indexOfClosest]
        Log.i("user entry", "right answer is $rightAnswer")
        rightAnswertoSend = rightAnswer

        // contains indices of all options, including right answer
        var colors = ArrayList<Int>()
        colors.add(indexOfClosest)

        // select other 'wrong' colors
        while(colors.size != 4){
            var toAdd = colorValues.closestValue(getNewColor())

            indexOfClosest = 0
            for(i in 0 until colorNames.size)
                if(colorValues[i] == toAdd)
                    indexOfClosest = i

            if(!colors.contains(indexOfClosest))
                colors.add(indexOfClosest)
        }

//        Log.i("list of options", colors.toString())
        return colors
    }

    // set option choices to new colors
    private fun setOptions(
        option1: Button,
        option2: Button,
        option3: Button,
        option4: Button,
        colorNames: ArrayList<String>,
        colors: ArrayList<Int>
    ): Int {
        var rightChoice = (1..4).random()
        when(rightChoice) {
            1 -> {
                option1.text = colorNames[colors[0]]
                option2.text = colorNames[colors[1]]
                option3.text = colorNames[colors[2]]
                option4.text = colorNames[colors[3]]
            }

            2 -> {
                option1.text = colorNames[colors[3]]
                option2.text = colorNames[colors[0]]
                option3.text = colorNames[colors[1]]
                option4.text = colorNames[colors[2]]
            }

            3 -> {
                option1.text = colorNames[colors[2]]
                option2.text = colorNames[colors[3]]
                option3.text = colorNames[colors[0]]
                option4.text = colorNames[colors[1]]
            }

            4 -> {
                option1.text = colorNames[colors[1]]
                option2.text = colorNames[colors[2]]
                option3.text = colorNames[colors[3]]
                option4.text = colorNames[colors[0]]
            }
        }
        return rightChoice
    }

    override fun onBackPressed() {
//        startActivity(Intent(this, MainActivity::class.java), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }
}