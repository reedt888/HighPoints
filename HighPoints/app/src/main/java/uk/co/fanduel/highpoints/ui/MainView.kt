package uk.co.fanduel.highpoints.ui

import uk.co.fanduel.highpoints.model.Player

interface MainView {

    // TODO: Generalise to support returning N players rather than just 2

    fun showInstructions()

    fun showInitialOptions(options: Pair<Player, Player>)

    fun showOptions(options: Pair<Player, Player>)

    fun scheduleNext()

    fun showSelectedCorrect(selected: Player)

    fun showSelectedIncorrect(selected: Player)

    fun showCorrectSoFar(correctSoFar: Int)

    fun showComplete()

    fun showNotEnoughPlayersError()

    fun showLoadPlayersError()
}