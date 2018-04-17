package uk.co.fanduel.highpoints.game

import android.content.Context
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class GamePrefsPersisterTest {

    private lateinit var gamePrefsPersister: GamePrefsPersister

    @Before
    fun setup() {
        gamePrefsPersister = GamePrefsPersister(RuntimeEnvironment.application)
    }

    @Test
    fun whenGetInstructionsAcknowledgedThenReturnsFalseByDefault() {
        assertThat(gamePrefsPersister.instructionsAcknowledged, `is`(false))
    }

    @Test
    fun whenGetInstructionsAcknowledgedThenReturnsWhatWasSet() {
        gamePrefsPersister.instructionsAcknowledged = true
        assertThat(gamePrefsPersister.instructionsAcknowledged, `is`(true))

        gamePrefsPersister.instructionsAcknowledged = false
        assertThat(gamePrefsPersister.instructionsAcknowledged, `is`(false))
    }

    @Test
    fun whenSetInstructionsAcknowledgedThenPersistedToSharedPrefs() {
        val prefs = RuntimeEnvironment.application.getSharedPreferences(GamePrefsPersister.prefsKey, Context.MODE_PRIVATE)
        assertThat(prefs.getBoolean(GamePrefsPersister.instructionsAcknowledgedKey, false), `is`(false))

        gamePrefsPersister.instructionsAcknowledged = true

        assertThat(prefs.getBoolean(GamePrefsPersister.instructionsAcknowledgedKey, false), `is`(true))
    }
}