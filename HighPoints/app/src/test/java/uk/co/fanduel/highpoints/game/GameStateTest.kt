package uk.co.fanduel.highpoints.game

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test
import uk.co.fanduel.highpoints.model.Image
import uk.co.fanduel.highpoints.model.Images
import uk.co.fanduel.highpoints.model.Player

class GameStateTest {

    private val gameState = GameState()

    @Test
    fun whenIsCorrectLessThan10TimesThenIsMoreTurnsReturnsTrue() {
        assertThat(gameState.isMoreTurns(), `is`(true))

        for (count in 1..9) {
            gameState.onCorrect()
            assertThat(gameState.isMoreTurns(), `is`(true))
        }
    }

    @Test
    fun whenIsCorrect10TimesThenIsMoreTurnsReturnsFalse() {
        for (count in 1..10) {
            gameState.onCorrect()
        }

        assertThat(gameState.isMoreTurns(), `is`(false))
    }

    @Test
    fun whenIsCorrectMoreThan10TimesThenIsMoreTurnsReturnsFalse() {
        for (count in 1..17) {
            gameState.onCorrect()
        }

        assertThat(gameState.isMoreTurns(), `is`(false))
    }

    @Test
    fun whenNoOptionsSetThenIsCorrectReturnsFalse() {
        assertThat(gameState.isCorrect(createPlayer(0, 1.23)), `is`(false))
    }

    @Test
    fun whenSpecifiedPlayerIsFirstOptionAndHasLowestFppgThenIsCorrectReturnsFalse() {
        val player1 = createPlayer(0, 1.23)
        val player2 = createPlayer(1, 1.24)
        gameState.setOptions(Pair(player1, player2))
        assertThat(gameState.isCorrect(player1), `is`(false))
    }

    @Test
    fun whenSpecifiedPlayerIsSecondOptionAndHasLowestFppgThenIsCorrectReturnsFalse() {
        val player1 = createPlayer(0, 1.24)
        val player2 = createPlayer(1, 1.23)
        gameState.setOptions(Pair(player1, player2))
        assertThat(gameState.isCorrect(player2), `is`(false))
    }

    @Test
    fun whenSpecifiedPlayerIsFirstOptionAndHasHighestFppgThenIsCorrectReturnsTrue() {
        val player1 = createPlayer(0, 1.24)
        val player2 = createPlayer(1, 1.23)
        gameState.setOptions(Pair(player1, player2))
        assertThat(gameState.isCorrect(player1), `is`(true))
    }

    @Test
    fun whenSpecifiedPlayerIsSecondOptionAndHasHighestFppgThenIsCorrectReturnsTrue() {
        val player1 = createPlayer(0, 1.23)
        val player2 = createPlayer(1, 1.24)
        gameState.setOptions(Pair(player1, player2))
        assertThat(gameState.isCorrect(player2), `is`(true))
    }

    @Test
    fun whenBothOptionsHaveSameFppgThenIsCorrectReturnsTrueForBothPlayers() {
        val player1 = createPlayer(0, 1.23)
        val player2 = createPlayer(1, 1.23)
        gameState.setOptions(Pair(player1, player2))
        assertThat(gameState.isCorrect(player1), `is`(true))
        assertThat(gameState.isCorrect(player2), `is`(true))
    }

    @Test
    fun whenSpecifiedPlayerHasHighestFppgButWasNotAnOptionThenIsCorrectStillReturnsTrue() {
        gameState.setOptions(Pair(createPlayer(0, 1.23), createPlayer(1, 1.24)))
        assertThat(gameState.isCorrect(createPlayer(3, 1.24)), `is`(true))
    }

    @Test
    fun whenResetThenStateCleared() {
        for (count in 1..10) {
            gameState.onCorrect()
        }

        val player1 = createPlayer(0, 1.24)
        val player2 = createPlayer(1, 1.23)
        gameState.setOptions(Pair(player1, player2))

        assertThat(gameState.isMoreTurns(), `is`(false))
        assertThat(gameState.isCorrect(player1), `is`(true))

        gameState.reset()

        assertThat(gameState.isMoreTurns(), `is`(true))
        assertThat(gameState.isCorrect(player1), `is`(false))
    }

    private fun createPlayer(index: Int, fppg: Double): Player {
        return Player(Integer.toString(index), Integer.toString(index), fppg, Images(Image("")))
    }
}