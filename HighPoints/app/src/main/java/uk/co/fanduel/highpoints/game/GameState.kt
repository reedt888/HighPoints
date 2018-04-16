package uk.co.fanduel.highpoints.game

import uk.co.fanduel.highpoints.model.Player

class GameState {

    // TODO: Generalise to support returning N players rather than just 2

    companion object {
        const val targetCorrect = 10
    }

    private var correctSoFar = 0
    private var options: Pair<Player, Player>? = null
    private var highFppg = 0.0

    fun getCorrectSoFar() = correctSoFar

    fun isComplete(): Boolean = correctSoFar >= targetCorrect

    fun setOptions(options: Pair<Player, Player>) {
        this.options = options
        highFppg = Math.max(options.first.fppg, options.second.fppg)
    }

    fun isCorrect(selected: Player): Boolean = selected.fppg == highFppg

    fun onCorrect() = correctSoFar++

    fun reset() {
        correctSoFar = 0
        options = null
        highFppg = 0.0
    }
}