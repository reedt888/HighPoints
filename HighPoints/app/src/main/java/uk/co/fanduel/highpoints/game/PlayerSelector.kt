package uk.co.fanduel.highpoints.game

import uk.co.fanduel.highpoints.model.Player

class PlayerSelector(private val allPlayers: List<Player>) {

    // TODO: Generalise to support returning N players rather than just 2

    private val remainingPlayers = mutableListOf<Player>()

    init {
        reset()
    }

    fun isMore(): Boolean = remainingPlayers.size >= 2

    fun getNext(): Pair<Player, Player> {
        val pair = Pair(remainingPlayers[0], remainingPlayers[1])
        remainingPlayers.removeAt(0)
        remainingPlayers.removeAt(0)
        return pair
    }

    fun reset() {
        remainingPlayers.clear()
        remainingPlayers.addAll(allPlayers)
        remainingPlayers.shuffle()
    }
}