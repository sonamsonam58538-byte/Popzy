package com.example.popzy;

import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private FrameLayout gameArea;
    private TextView gameScoreText;
    private TextView gameTimerText;

    private int score = 0;
    private boolean gameOver = false;

    // Lower value = faster
    private int gameSpeed = 4000;

    private final Random random = new Random();
    private CountDownTimer timer;

    // Sound
    private MediaPlayer popSound;
    private MediaPlayer bombSound;

    // Object size
    private final int OBJECT_SIZE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        gameArea = findViewById(R.id.gameArea);
        gameScoreText = findViewById(R.id.gameScoreText);
        gameTimerText = findViewById(R.id.gameTimerText);

        // Make Score and Timer dark and clearly visible
        gameScoreText.setTextColor(Color.BLACK);
        gameTimerText.setTextColor(Color.BLACK);

        gameScoreText.setTypeface(null, Typeface.BOLD);
        gameTimerText.setTypeface(null, Typeface.BOLD);

        // Load sounds
        popSound = MediaPlayer.create(this, R.raw.pop);
        bombSound = MediaPlayer.create(this, R.raw.bomb);

        // Wait until gameArea has actual size
        gameArea.post(this::startGame);
    }

    // =========================
    // START GAME
    // =========================

    private void startGame() {

        gameOver = false;
        score = 0;

        // Reset speed
        gameSpeed = 4000;

        gameScoreText.setText("0");
        gameTimerText.setText("30");

        startTimer();
        createObjects();
    }

    // =========================
    // TIMER
    // =========================

    private void startTimer() {

        timer = new CountDownTimer(30000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                long seconds = millisUntilFinished / 1000;

                gameTimerText.setText(
                        String.valueOf(seconds)
                );
            }

            @Override
            public void onFinish() {

                if (!gameOver) {

                    gameTimerText.setText("0");

                    endGame("TIME UP!");
                }
            }

        }.start();
    }

    // =========================
    // CREATE OBJECTS
    // =========================

    private void createObjects() {

        if (gameOver) {
            return;
        }

        gameArea.postDelayed(() -> {

            if (!gameOver) {

                // 20% Bomb
                // 80% Balloon
                int chance = random.nextInt(100);

                if (chance < 20) {
                    createBomb();
                } else {
                    createBalloon();
                }

                createObjects();
            }

        }, 450);
    }

    // =========================
    // BALLOON
    // =========================

    private void createBalloon() {

        ImageView balloon = new ImageView(this);

        balloon.setImageResource(R.drawable.balloon);

        balloon.setScaleType(
                ImageView.ScaleType.CENTER_INSIDE
        );

        balloon.setAdjustViewBounds(true);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(
                        dpToPx(OBJECT_SIZE),
                        dpToPx(OBJECT_SIZE)
                );

        int objectSize = dpToPx(OBJECT_SIZE);

        int maxX =
                gameArea.getWidth() - objectSize;

        int randomX = random.nextInt(
                Math.max(maxX + 1, 1)
        );

        params.leftMargin = randomX;

        // Start below screen
        params.topMargin = gameArea.getHeight();

        gameArea.addView(balloon, params);

        balloon.setTranslationY(0);

        // Move upward
        float moveDistance =
                -(gameArea.getHeight() + objectSize);

        balloon.animate()
                .translationY(moveDistance)

                // Gradually increasing speed
                .setDuration(
                        gameSpeed + random.nextInt(1000)
                )

                .withEndAction(() -> {

                    if (balloon.getParent() != null) {
                        gameArea.removeView(balloon);
                    }

                })
                .start();

        // Balloon click
        balloon.setOnClickListener(v -> {

            if (gameOver) {
                return;
            }

            // POP SOUND
            if (popSound != null) {

                popSound.seekTo(0);
                popSound.start();
            }

            score++;

            gameScoreText.setText(
                    String.valueOf(score)
            );

            // Increase speed every 5 points
            if (score % 5 == 0 && gameSpeed > 1400) {

                gameSpeed -= 500;
            }

            // POP animation
            balloon.animate()
                    .scaleX(1.5f)
                    .scaleY(1.5f)
                    .alpha(0f)
                    .setDuration(150)

                    .withEndAction(() -> {

                        if (balloon.getParent() != null) {
                            gameArea.removeView(balloon);
                        }

                    })
                    .start();
        });
    }

    // =========================
    // BOMB
    // =========================

    private void createBomb() {

        ImageView bomb = new ImageView(this);

        bomb.setImageResource(R.drawable.bomb);

        bomb.setScaleType(
                ImageView.ScaleType.CENTER_INSIDE
        );

        bomb.setAdjustViewBounds(true);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(
                        dpToPx(OBJECT_SIZE),
                        dpToPx(OBJECT_SIZE)
                );

        int objectSize = dpToPx(OBJECT_SIZE);

        int maxX =
                gameArea.getWidth() - objectSize;

        int randomX = random.nextInt(
                Math.max(maxX + 1, 1)
        );

        params.leftMargin = randomX;

        // Start below screen
        params.topMargin = gameArea.getHeight();

        gameArea.addView(bomb, params);

        // Move upward
        float moveDistance =
                -(gameArea.getHeight() + objectSize);

        bomb.animate()
                .translationY(moveDistance)

                .setDuration(
                        gameSpeed + random.nextInt(1000)
                )

                .withEndAction(() -> {

                    if (bomb.getParent() != null) {
                        gameArea.removeView(bomb);
                    }

                })
                .start();

        // Bomb click
        bomb.setOnClickListener(v -> {

            if (gameOver) {
                return;
            }

            // BOMB SOUND
            if (bombSound != null) {

                bombSound.seekTo(0);
                bombSound.start();
            }

            gameOver = true;

            if (timer != null) {
                timer.cancel();
            }

            endGame("💥 BOOM! GAME OVER");
        });
    }

    // =========================
    // GAME OVER
    // =========================

    private void endGame(String message) {

        if (gameOver && message.equals("TIME UP!")) {
            return;
        }

        gameOver = true;

        if (timer != null) {
            timer.cancel();
        }

        gameArea.removeAllViews();

        // =========================
        // CENTER MESSAGE
        // =========================

        TextView gameOverText = new TextView(this);

        gameOverText.setText(
                message + "\n\nScore: " + score
        );

        gameOverText.setTextSize(28);

        // Dark text for light background
        gameOverText.setTextColor(Color.BLACK);

        gameOverText.setGravity(Gravity.CENTER);

        gameOverText.setTypeface(
                null,
                Typeface.BOLD
        );

        // Slight transparent white background
        gameOverText.setBackgroundColor(
                Color.argb(180, 255, 255, 255)
        );

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );

        params.gravity = Gravity.CENTER;

        gameArea.addView(
                gameOverText,
                params
        );

        // Return after 2 seconds
        gameArea.postDelayed(
                () -> finish(),
                2000
        );
    }

    // =========================
    // DP CONVERTER
    // =========================

    private int dpToPx(int dp) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return Math.round(dp * density);
    }

    // =========================
    // ACTIVITY DESTROY
    // =========================

    @Override
    protected void onDestroy() {

        if (timer != null) {
            timer.cancel();
        }

        if (popSound != null) {

            popSound.release();
            popSound = null;
        }

        if (bombSound != null) {

            bombSound.release();
            bombSound = null;
        }

        super.onDestroy();
    }
}