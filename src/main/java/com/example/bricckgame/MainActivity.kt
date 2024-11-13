package com.example.bricckgame



import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var scoreText: TextView
    private lateinit var paddle: View
    private lateinit var ball: View
    private lateinit var brickContainer: LinearLayout
    private var ballX = 0f
    private var ballY = 0f
    private var ballSpeedX = 0f
    private var ballSpeedY = 0f
    private var paddleX = 0f
    private var score = 0
    private val brickRows = 9
    private val brickColumns = 10
    private val brickWidth = 100
    private val brickHeight = 40
    private val brickMargin = 4
    private var isBallLaunched = false
    private var lives = 1



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scoreText = findViewById(R.id.scoreText)
        paddle = findViewById(R.id.paddle)
        ball = findViewById(R.id.ball)
        brickContainer = findViewById(R.id.brickContainer)

        val newgame = findViewById<Button>(R.id.newgame)


        newgame.setOnClickListener {
            initializeBricks()
            start()
            //  movepaddle()
            newgame.visibility = View.INVISIBLE

            val backButton = findViewById<Button>(R.id.back)
            backButton.visibility = View.INVISIBLE

            val btn_but = findViewById<Button>(R.id.back)
            btn_but.setOnClickListener {

                val Intent = Intent(this, MainActivity2::class.java)
                startActivity(Intent)
            }
        }
    }

    private var isFirstTime = true // check the first time playing

    private fun initializeBricks() {
        // Clear existing bricks not the first time play
        if (!isFirstTime) {
            brickContainer.removeAllViews()
        }

        val brickWidthWithMargin = (brickWidth + brickMargin).toInt()

        for (row in 0 until brickRows) {
            val rowLayout = LinearLayout(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            rowLayout.layoutParams = params

            for (col in 0 until brickColumns) {
                val brick = View(this)
                val brickParams = LinearLayout.LayoutParams(brickWidth, brickHeight)
                brickParams.setMargins(brickMargin, brickMargin, brickMargin, brickMargin)
                brick.layoutParams = brickParams
                brick.setBackgroundResource(R.drawable.gradiant2)
                rowLayout.addView(brick)
            }

            brickContainer.addView(rowLayout)
        }

        isFirstTime = false // Update it's not the first time anymore
    }

    private fun moveBall() {
        ballX += ballSpeedX
        ballY += ballSpeedY

        ball.x = ballX
        ball.y = ballY
    }

    private fun movePaddle(x: Float) {
        paddleX = x - paddle.width / 2
        paddle.x = paddleX
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun checkCollision() {
        // Check walls
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()

        if (ballX <= 0 || ballX + ball.width >= screenWidth) {
            ballSpeedX *= -1
        }

        if (ballY <= 0) {
            ballSpeedY *= -1
        }

        // Check paddle
        if (ballY + ball.height >= paddle.y && ballY + ball.height <= paddle.y + paddle.height
            && ballX + ball.width >= paddle.x && ballX <= paddle.x + paddle.width
        ) {
            ballSpeedY *= -1
            score++
            scoreText.text = "Score: $score"
        }


        if (ballY + ball.height >= screenHeight) {

            resetBallPosition() // Example: Reset the ball to its initial position
        }

        // Check bricks
        for (row in 0 until brickRows) {
            val rowLayout = brickContainer.getChildAt(row) as LinearLayout

            val rowTop = rowLayout.y + brickContainer.y
            val rowBottom = rowTop + rowLayout.height

            for (col in 0 until brickColumns) {
                val brick = rowLayout.getChildAt(col) as View

                if (brick.visibility == View.VISIBLE) {
                    val brickLeft = brick.x + rowLayout.x
                    val brickRight = brickLeft + brick.width
                    val brickTop = brick.y + rowTop
                    val brickBottom = brickTop + brick.height

                    if (ballX + ball.width >= brickLeft && ballX <= brickRight
                        && ballY + ball.height >= brickTop && ballY <= brickBottom
                    ) {
                        brick.visibility = View.INVISIBLE
                        ballSpeedY *= -1
                        score++
                        scoreText.text = "Score: $score"
                        return
                    }
                }
            }
        }

        // paddle misses the ball
        if (ballY + ball.height >= screenHeight - 100) {
            // Reduce the number of lives
            lives--

            if (lives > 0 ) {
                Toast.makeText(this, "$lives balls left ", Toast.LENGTH_SHORT).show()
            }

            paddle.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {
                        movePaddle(event.rawX)
                    }
                }
                true
            }

            if (lives <= 0) {

                gameOver()
            } else {

                resetBallPosition()
                start()
            }
        }

    }

    private fun resetBallPosition() {
        // Reset the ball position
        val displayMetrics = resources.displayMetrics
        val screenDensity = displayMetrics.density

        val screenWidth = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()

        ballX = screenWidth / 2 - ball.width / 2
        ballY = screenHeight / 2 - ball.height / 2 +525

        ball.x = ballX
        ball.y = ballY

        // Reset the ball speed
        ballSpeedX = 2 * screenDensity
        ballSpeedY = 2 * screenDensity

        paddleX = screenWidth / 2 - paddle.width / 2
        paddle.x = paddleX
    }

    private fun gameOver() {
        // Display a game over message
        val currentScore = score
        updateHighScore(currentScore) // Update and store high score

        scoreText.text = "Game Over\nScore: $currentScore\nHigh Score: ${getHighScore()}"
        score = 0
        val newgame = findViewById<Button>(R.id.newgame)
        newgame.visibility = View.VISIBLE

        val backButton = findViewById<Button>(R.id.back)
        backButton.visibility = View.VISIBLE

        resetBricks()
    }



    @SuppressLint("ClickableViewAccessibility")
    private fun movepaddle() {

        paddle.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    movePaddle(event.rawX)
                }
            }
            true
        }
    }


    private fun start() {

        resetBricks()
        movepaddle()
        val displayMetrics = resources.displayMetrics
        val screenDensity = displayMetrics.density

        val screenWidth = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()

        paddleX = screenWidth / 2 - paddle.width / 2
        paddle.x = paddleX

        ballX = screenWidth / 2 - ball.width / 2
        ballY = screenHeight / 2 - ball.height / 2

        val brickHeightWithMargin = (brickHeight + brickMargin * screenDensity).toInt()

        ballSpeedX = 3 * screenDensity
        ballSpeedY = -3 * screenDensity

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = Long.MAX_VALUE
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { animation ->
            moveBall()
            checkCollision()
        }
        animator.start()
    }



    private fun resetBricks() {
        for (row in 0 until brickRows) {
            val rowLayout = brickContainer.getChildAt(row) as LinearLayout
            for (col in 0 until brickColumns) {
                val brick = rowLayout.getChildAt(col) as View
                brick.visibility = View.VISIBLE
            }
        }
    }

    private val HIGH_SCORE_KEY = "high_score"

    // high score from SharedPreferences
    private fun getHighScore(): Int {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        return sharedPreferences.getInt(HIGH_SCORE_KEY, 0) // Default high score is 0 if not found
    }

    //update and store the high score in SharedPreferences
    private fun updateHighScore(score: Int) {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val currentHighScore = getHighScore()
        if (score > currentHighScore) {
            editor.putInt(HIGH_SCORE_KEY, score)
            editor.apply()
        }
    }

}



