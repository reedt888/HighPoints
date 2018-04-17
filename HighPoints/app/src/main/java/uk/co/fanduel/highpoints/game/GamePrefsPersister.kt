package uk.co.fanduel.highpoints.game

import android.content.Context
import android.content.SharedPreferences
import android.support.annotation.VisibleForTesting

class GamePrefsPersister(private val context: Context) {

    @VisibleForTesting
    companion object {

        @VisibleForTesting
        const val prefsKey = "GamePrefs"

        @VisibleForTesting
        const val instructionsAcknowledgedKey = "InstructionsAcknowledged"
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(prefsKey, Context.MODE_PRIVATE)
    }

    var instructionsAcknowledged: Boolean
        get() = prefs.getBoolean(instructionsAcknowledgedKey, false)
        set(value) {
            with(prefs.edit()) {
                putBoolean(instructionsAcknowledgedKey, value)
                apply()
            }
        }
}