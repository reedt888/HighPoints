package uk.co.fanduel.highpoints.ui

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import uk.co.fanduel.highpoints.api.PlayersApi
import uk.co.fanduel.highpoints.game.GamePrefsPersister
import uk.co.fanduel.highpoints.game.GameState
import uk.co.fanduel.highpoints.game.PlayerSelector
import uk.co.fanduel.highpoints.model.Player

class MainPresenter(
    private val view: MainView,
    private val gamePrefsPersister: GamePrefsPersister,
    private val playersApi: PlayersApi,
    private val playerSelector: PlayerSelector = PlayerSelector(),
    private val gameState: GameState = GameState(),
    private val subscribeOn: Scheduler = Schedulers.io(),
    private val observeOn: Scheduler = AndroidSchedulers.mainThread()
) {

    // TODO:
    // - Consider moving schedulers code to a separate object to remove Android from presenter
    // - Generalise to support returning N players rather than just 2
    // - Pre-fetch images for future options, e.g fetch options 3,4 when displaying 1,2

    private var disposable: Disposable? = null

    fun start() {
        try {
            playersApi.getPlayers()
                .subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .subscribe({
                    init(it)
                }, {
                    showLoadFailed()
                })
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    fun stop() {
        disposable?.dispose()
    }

    fun onInstructionsAcknowledged() {
        gamePrefsPersister.instructionsAcknowledged = true
        showInitialOptions()
    }

    fun onPlayerSelected(selected: Player) {
        if (gameState.isCorrect(selected)) {
            onCorrect(selected)
        } else {
            onIncorrect(selected)
        }
    }

    fun onNext() {
        if (!playerSelector.isMore()) {
            playerSelector.reset()
        }

        val options = playerSelector.getNext()
        gameState.setOptions(options)
        view.showNextOptions(options)
    }

    fun onReset() {
        playerSelector.reset()
        gameState.reset()

        val options = playerSelector.getNext()

        gameState.setOptions(options)
        view.showInitialOptions(options)
        view.showCorrectSoFar(gameState.getCorrectSoFar())
    }

    private fun init(allPlayers: List<Player>) {
        playerSelector.init(allPlayers)
        if (playerSelector.isMore()) {
            initWithPlayers()
        } else {
            showNotEnoughPlayers()
        }
    }

    private fun initWithPlayers() {
        if (!gamePrefsPersister.instructionsAcknowledged) {
            showInstructions()
        } else {
            showInitialOptions()
        }
    }

    private fun showInstructions() = view.showInstructions()

    private fun showInitialOptions() {
        val options = playerSelector.getNext()
        gameState.setOptions(options)
        view.showInitialOptions(options)
        view.showCorrectSoFar(0)
    }

    private fun showNotEnoughPlayers() = view.showNotEnoughPlayersError()

    private fun showLoadFailed() = view.showLoadPlayersError()

    private fun onCorrect(selected: Player) {
        gameState.onCorrect()

        view.showSelectedCorrect(selected)
        view.showCorrectSoFar(gameState.getCorrectSoFar())

        if (gameState.isComplete()) {
            view.showComplete()
        } else {
            view.scheduleNext()
        }
    }

    private fun onIncorrect(selected: Player) {
        view.showSelectedIncorrect(selected)
        view.scheduleNext()
    }
}