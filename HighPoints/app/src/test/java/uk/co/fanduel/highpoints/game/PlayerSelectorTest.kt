package uk.co.fanduel.highpoints.game

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.notNullValue
import org.junit.Assert.fail
import org.junit.Test
import uk.co.fanduel.highpoints.model.Image
import uk.co.fanduel.highpoints.model.Images
import uk.co.fanduel.highpoints.model.Player

class PlayerSelectorTest {

    // TODO:
    // - Consider test that assert random results are different as there is a chance they
    //   could fail, especially with small numbers of players
    // - Add tests for mis-use, e.g. init() not called / called multiple times (need for these
    //   tests suggests a design issue)

    private val playerSelector = PlayerSelector()

    @Test
    fun whenNoPlayersThenIsMoreReturnsFalse() {
        playerSelector.init(createPlayers(0))
        assertThat(playerSelector.isMore(), `is`(false))
    }

    @Test
    fun whenOnePlayerThenIsMoreReturnsFalse() {
        playerSelector.init(createPlayers(1))
        assertThat(playerSelector.isMore(), `is`(false))
    }

    @Test
    fun whenTwoPlayerThenIsMoreReturnsTrue() {
        playerSelector.init(createPlayers(2))
        assertThat(playerSelector.isMore(), `is`(true))
    }

    @Test
    fun whenMoreThanTwoPlayerThenIsMoreReturnsTrue() {
        playerSelector.init(createPlayers(17))
        assertThat(playerSelector.isMore(), `is`(true))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun whenNoPlayersThenGetNextThrowsException() {
        playerSelector.init(createPlayers(0))
        playerSelector.getNext()
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun whenOnePlayerThenGetNextThrowsException() {
        playerSelector.init(createPlayers(1))
        playerSelector.getNext()
    }

    @Test
    fun whenTwoPlayersThenGetNextReturnsDifferentPlayers() {
        playerSelector.init(createPlayers(2))
        val players = playerSelector.getNext()

        assertThat(players, `is`(notNullValue()))
        assertThat(players.first, `is`(notNullValue()))
        assertThat(players.second, `is`(notNullValue()))
        assertThat(players.first, not(equalTo(players.second)))
    }

    @Test
    fun whenManyPlayersThenGetNextReturnsCorrectNumberOfPairs() {
        playerSelector.init(createPlayers(9))
        playerSelector.getNext()
        playerSelector.getNext()
        playerSelector.getNext()
        playerSelector.getNext()
        try {
            playerSelector.getNext()
            fail()
        } catch (_: IndexOutOfBoundsException) {
        }
    }

    @Test
    fun whenGetNextThenReturnsPlayersInDifferentOrderToOriginal() {
        val originalPlayers = createPlayers(6)
        playerSelector.init(originalPlayers)

        val nextPlayers = mutableListOf<Player>()

        val next1 = playerSelector.getNext()
        nextPlayers.add(next1.first)
        nextPlayers.add(next1.second)

        val next2 = playerSelector.getNext()
        nextPlayers.add(next2.first)
        nextPlayers.add(next2.second)

        val next3 = playerSelector.getNext()
        nextPlayers.add(next3.first)
        nextPlayers.add(next3.second)

        assertThat(nextPlayers.toList(), not(equalTo(originalPlayers)))
    }

    @Test
    fun whenGetNextAfterResetThenReturnsPlayersInDifferent() {
        val originalPlayers = createPlayers(6)
        playerSelector.init(originalPlayers)

        val nextPlayers1 = mutableListOf<Player>()

        val next1 = playerSelector.getNext()
        nextPlayers1.add(next1.first)
        nextPlayers1.add(next1.second)

        val next2 = playerSelector.getNext()
        nextPlayers1.add(next2.first)
        nextPlayers1.add(next2.second)

        val next3 = playerSelector.getNext()
        nextPlayers1.add(next3.first)
        nextPlayers1.add(next3.second)

        playerSelector.reset()

        val nextPlayers2 = mutableListOf<Player>()

        val next4 = playerSelector.getNext()
        nextPlayers2.add(next4.first)
        nextPlayers2.add(next4.second)

        val next5 = playerSelector.getNext()
        nextPlayers2.add(next5.first)
        nextPlayers2.add(next5.second)

        val next6 = playerSelector.getNext()
        nextPlayers2.add(next6.first)
        nextPlayers2.add(next6.second)

        assertThat(nextPlayers1.toList(), not(equalTo(nextPlayers2.toList())))
    }

    private fun createPlayers(count: Int): List<Player> {
        val players = mutableListOf<Player>()
        for (index in 0 until count) {
            players.add(createPlayer(index))
        }
        return players.toList()
    }

    private fun createPlayer(index: Int): Player {
        return Player(Integer.toString(index), Integer.toString(index), 0.0, Images(Image("")))
    }
}