/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service.overlay

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager

class AudioFocusController(context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val focusChangeListener: AudioManager.OnAudioFocusChangeListener =
        object: AudioManager.OnAudioFocusChangeListener {
            override fun onAudioFocusChange(focusChange: Int) {
                if (focusChange == AudioManager.AUDIOFOCUS_GAIN) return
                focusRequest?.let { audioManager.requestAudioFocus(it) }
            }
        }

    private val focusRequest =
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).apply {
            setAudioAttributes(AudioAttributes.Builder().run {
                setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                build()
            })
            setAcceptsDelayedFocusGain(true)
            setOnAudioFocusChangeListener(focusChangeListener)
        }.build()

    fun request() = focusRequest?.let { audioManager.requestAudioFocus(it) }

    fun abandon() = focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }

}
