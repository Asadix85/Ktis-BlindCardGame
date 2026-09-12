package com.example.ktis.ui.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.ktis.R

class KtisAudioManager(
    private val context: Context
) {

    private val soundPool: SoundPool

    private val vibrator: Vibrator

    private var cardDrawSoundId = 0
    private var cardPlaceSoundId = 0
    private var roundWinSoundId = 0
    private var tieSoundId = 0
    private var buttonClickSoundId = 0

    private var musicPlayer: MediaPlayer? = null

    private var soundEnabled = true
    private var musicEnabled = true
    private var vibrationEnabled = true

    init {

        val audioAttributes =
            AudioAttributes.Builder()
                .setUsage(
                    AudioAttributes.USAGE_GAME
                )
                .setContentType(
                    AudioAttributes.CONTENT_TYPE_SONIFICATION
                )
                .build()

        soundPool =
            SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build()

        vibrator =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                val vibratorManager =
                    context.getSystemService(
                        Context.VIBRATOR_MANAGER_SERVICE
                    ) as VibratorManager

                vibratorManager.defaultVibrator

            } else {

                @Suppress("DEPRECATION")
                context.getSystemService(
                    Context.VIBRATOR_SERVICE
                ) as Vibrator
            }

        loadSounds()
    }

    private fun loadSounds() {

        cardDrawSoundId =
            loadSoundIfAvailable(
                R.raw.card_draw
            )

        cardPlaceSoundId =
            loadSoundIfAvailable(
                R.raw.card_place
            )

        roundWinSoundId =
            loadSoundIfAvailable(
                R.raw.round_win
            )

        tieSoundId =
            loadSoundIfAvailable(
                R.raw.tie
            )

        buttonClickSoundId =
            loadSoundIfAvailable(
                R.raw.button_click
            )
    }

    private fun loadSoundIfAvailable(
        resourceId: Int
    ): Int {

        return try {

            val descriptor =
                context.resources
                    .openRawResourceFd(resourceId)

            if (descriptor == null) {
                return 0
            }

            val valid =
                descriptor.length > 0

            descriptor.close()

            if (!valid) {
                return 0
            }

            soundPool.load(
                context,
                resourceId,
                1
            )

        } catch (_: Exception) {

            0
        }
    }

    fun setSoundEnabled(
        enabled: Boolean
    ) {
        soundEnabled = enabled
    }

    fun setMusicEnabled(
        enabled: Boolean
    ) {

        musicEnabled = enabled

        if (enabled) {
            startMusic()
        } else {
            stopMusic()
        }
    }

    fun setVibrationEnabled(
        enabled: Boolean
    ) {
        vibrationEnabled = enabled
    }

    fun playCardDraw() {
        playSound(cardDrawSoundId)
        vibrate()
    }

    fun playCardPlace() {
        playSound(cardPlaceSoundId)
        vibrate()
    }

    fun playRoundWin() {
        playSound(roundWinSoundId)
        vibrate()
    }

    fun playTie() {
        playSound(tieSoundId)
        vibrate()
    }

    fun playButtonClick() {
        playSound(buttonClickSoundId)
    }

    private fun playSound(
        soundId: Int
    ) {

        if (!soundEnabled) {
            return
        }

        if (soundId == 0) {
            return
        }

        try {

            soundPool.play(
                soundId,
                1f,
                1f,
                1,
                0,
                1f
            )

        } catch (_: Exception) {
        }
    }

    private fun vibrate() {

        if (!vibrationEnabled) {
            return
        }

        if (!vibrator.hasVibrator()) {
            return
        }

        try {

            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O
            ) {

                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        35L,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )

            } else {

                @Suppress("DEPRECATION")
                vibrator.vibrate(35L)
            }

        } catch (_: Exception) {
        }
    }

    fun startMusic() {

        if (!musicEnabled) {
            return
        }

        if (musicPlayer != null) {
            return
        }

        try {

            val descriptor =
                context.resources
                    .openRawResourceFd(
                        R.raw.menu_music
                    )

            if (descriptor == null) {
                return
            }

            if (descriptor.length <= 0) {
                descriptor.close()
                return
            }

            val player =
                MediaPlayer()

            player.setDataSource(
                descriptor.fileDescriptor,
                descriptor.startOffset,
                descriptor.length
            )

            descriptor.close()

            player.isLooping = true

            player.setOnPreparedListener {
                if (musicEnabled) {
                    it.start()
                }
            }

            player.setOnCompletionListener {
                it.release()

                if (musicPlayer === it) {
                    musicPlayer = null
                }
            }

            player.prepareAsync()

            musicPlayer = player

        } catch (_: Exception) {

            musicPlayer?.release()
            musicPlayer = null
        }
    }

    fun stopMusic() {

        try {
            musicPlayer?.stop()
        } catch (_: Exception) {
        }

        musicPlayer?.release()
        musicPlayer = null
    }

    fun release() {

        stopMusic()

        soundPool.release()
    }
}