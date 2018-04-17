package uk.co.fanduel.highpoints.ui

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyZeroInteractions
import uk.co.fanduel.highpoints.api.PlayersApi
import uk.co.fanduel.highpoints.game.GamePrefsPersister
import uk.co.fanduel.highpoints.game.GameState
import uk.co.fanduel.highpoints.game.PlayerSelector
import uk.co.fanduel.highpoints.model.Image
import uk.co.fanduel.highpoints.model.Images
import uk.co.fanduel.highpoints.model.Player
import uk.co.fanduel.highpoints.model.Players

class MainPresenterTest {

    private val view = mock(MainView::class.java)
    private val gamePrefsPersister = mock(GamePrefsPersister::class.java)
    private val playersApi = mock(PlayersApi::class.java)
    private val playerSelector = mock(PlayerSelector::class.java)
    private val gameState = mock(GameState::class.java)
    private lateinit var presenter: MainPresenter

    @Before
    fun setup() {
        presenter = MainPresenter(
            view,
            gamePrefsPersister,
            playersApi,
            playerSelector,
            gameState,
            Schedulers.trampoline(),
            Schedulers.trampoline()
        )
    }

    @Test
    fun whenStartAndGetPlayersSucceedsAndEnoughPlayersAndInstructionsNotAcknowledgedThenViewShowsInstructions() {
        val players = Players(listOf(createPlayer(0), createPlayer(1)))
        val options = Pair(players.players[0], players.players[1])

        `when`(playersApi.getPlayers()).thenReturn(Observable.just(players))
        `when`(playerSelector.isMore()).thenReturn(true)
        `when`(gamePrefsPersister.instructionsAcknowledged).thenReturn(false)
        `when`(playerSelector.getNext()).thenReturn(options)

        presenter.start()

        verify(playerSelector).init(players.players)
        verify(view).showInstructions()
    }

    @Test
    fun whenStartAndGetPlayersSucceedsAndEnoughPlayersAndInstructionsAcknowledgedThenViewShowsInitialOptions() {
        val players = Players(listOf(createPlayer(0), createPlayer(1)))
        val options = Pair(players.players[0], players.players[1])

        `when`(playersApi.getPlayers()).thenReturn(Observable.just(players))
        `when`(playerSelector.isMore()).thenReturn(true)
        `when`(gamePrefsPersister.instructionsAcknowledged).thenReturn(true)
        `when`(playerSelector.getNext()).thenReturn(options)

        presenter.start()

        verify(playerSelector).init(players.players)
        verify(gameState).setOptions(options)
        verify(view).showInitialOptions(options)
        verify(view).showCorrectSoFar(0)
    }

    @Test
    fun whenStartAndGetPlayersSucceedsAndNotEnoughPlayersThenViewShowsNotEnoughPlayersError() {
        val players = Players(emptyList())

        `when`(playersApi.getPlayers()).thenReturn(Observable.just(players))
        `when`(playerSelector.isMore()).thenReturn(false)

        presenter.start()

        verify(playerSelector).init(players.players)
        verify(view).showNotEnoughPlayersError()
    }

    @Test
    fun whenStartAndGetPlayersFailsThenViewShowsLoadPlayersError() {
        `when`(playersApi.getPlayers()).thenReturn(Observable.error(Throwable()))

        presenter.start()

        verifyZeroInteractions(playerSelector)
        verify(view).showLoadPlayersError()
    }

    @Test
    fun whenStopThenModelAndViewUnaffected() {
        presenter.stop()

        verifyZeroInteractions(playersApi)
        verifyZeroInteractions(view)
    }

    @Test
    fun whenInstructionsAcknowledgedThenViewShowsInitialOptions() {
        val options = Pair(createPlayer(0), createPlayer(1))

        `when`(playerSelector.getNext()).thenReturn(options)

        presenter.onInstructionsAcknowledged()

        verify(gameState).setOptions(options)
        verify(view).showInitialOptions(options)
        verify(view).showCorrectSoFar(0)
    }

    @Test
    fun whenPlayerSelectedAndCorrectThenViewShowsSelectedCorrect() {
        val player = createPlayer(0)
        val correctSoFar = 5

        `when`(gameState.isCorrect(player)).thenReturn(true)
        `when`(gameState.getCorrectSoFar()).thenReturn(correctSoFar)

        presenter.onPlayerSelected(player)

        verify(view).showSelectedCorrect(player)
        verify(view).showCorrectSoFar(correctSoFar)
    }

    @Test
    fun whenPlayerSelectedAndCorrectAndGameIsCompleteThenViewShowsComplete() {
        val player = createPlayer(0)

        `when`(gameState.isCorrect(player)).thenReturn(true)
        `when`(gameState.isComplete()).thenReturn(true)

        presenter.onPlayerSelected(player)

        verify(view).showComplete()
    }

    @Test
    fun whenPlayerSelectedAndCorrectAndGameIsIncompleteThenViewSchedulesNext() {
        val player = createPlayer(0)

        `when`(gameState.isCorrect(player)).thenReturn(true)
        `when`(gameState.isComplete()).thenReturn(false)

        presenter.onPlayerSelected(player)

        verify(view).scheduleNext()
    }

    @Test
    fun whenPlayerSelectedAndIncorrectThenViewShowsSelectedIncorrectAndSchedulesNext() {
        val player = createPlayer(0)

        `when`(gameState.isCorrect(player)).thenReturn(false)

        presenter.onPlayerSelected(player)

        verify(view).showSelectedIncorrect(player)
        verify(view).scheduleNext()
    }

    @Test
    fun whenNextAndNoMorePlayersThenPlayerSelectorReset() {
        `when`(playerSelector.isMore()).thenReturn(false)

        presenter.onNext()

        verify(playerSelector).reset()
    }

    @Test
    fun whenNextThenViewShowsOptions() {
        val options = Pair(createPlayer(0), createPlayer(1))

        `when`(playerSelector.getNext()).thenReturn(options)

        presenter.onNext()

        verify(gameState).setOptions(options)
        verify(view).showOptions(options)
    }

    @Test
    fun whenResetThenStateAndViewReset() {
        val options = Pair(createPlayer(0), createPlayer(1))
        val correctSoFar = 3

        `when`(playerSelector.getNext()).thenReturn(options)
        `when`(gameState.getCorrectSoFar()).thenReturn(correctSoFar)

        presenter.onReset()

        verify(playerSelector).reset()
        verify(gameState).reset()
        verify(gameState).setOptions(options)
        verify(view).showOptions(options)
        verify(view).showCorrectSoFar(correctSoFar)
    }

    private fun createPlayer(index: Int): Player {
        return Player(Integer.toString(index), Integer.toString(index), 0.0, Images(Image("")))
    }
}